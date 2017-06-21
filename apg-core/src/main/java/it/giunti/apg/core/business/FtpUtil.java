package it.giunti.apg.core.business;

import it.giunti.apg.core.ConfigUtil;
import it.giunti.apg.core.persistence.SessionFactory;
import it.giunti.apg.shared.BusinessException;

import java.io.File;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.hibernate.HibernateException;
import org.hibernate.Session;

public class FtpUtil {

	private FtpConfig ftpConfig = null;
	
	public FtpUtil(String idSocieta) throws BusinessException {
		Session ses = SessionFactory.getSession();
		try {
			this.ftpConfig = getFtpConfig(ses, idSocieta);
		} catch (HibernateException e) {
			throw new BusinessException(e.getMessage(), e);
		} finally {
			ses.close();
		}
	}
	
	public FtpUtil(Session ses, String idSocieta) throws BusinessException {
		this.ftpConfig = getFtpConfig(ses, idSocieta);
	}
	
	public static FtpConfig getFtpConfig(Session ses, String idSocieta) 
			throws BusinessException {
		FtpConfig ftpConfig = ConfigUtil.loadFtpBySocieta(ses, idSocieta);
		return ftpConfig;
	}

	public String fileTransfer(File localFile, String ftpSubDir, String remoteFileName)
			throws IOException {
		String remoteNameAndDir = ftpConfig.getDir();
		if (remoteNameAndDir.length() > 0) remoteNameAndDir += "/";
		if (ftpSubDir == null) ftpSubDir = "";
		if (ftpSubDir.length() > 0) remoteNameAndDir += ftpSubDir+"/";
		
		remoteNameAndDir += remoteFileName;
		remoteNameAndDir = remoteNameAndDir.replace("//", "/");
		FtpBusiness.upload(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUsername(),
				ftpConfig.getPassword(), remoteNameAndDir, localFile);
		return ftpConfig.getHost();
	}
	
	public boolean checkDirectoryExists(String dirPath) throws IOException {
		//Connection
		FTPClient ftpClient = new FTPClient();
        ftpClient.connect(ftpConfig.getHost());
        int returnCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(returnCode)) {
            throw new IOException("Could not connect");
        }
        boolean loggedIn = ftpClient.login(ftpConfig.getUsername(),
        		ftpConfig.getPassword());
        if (!loggedIn) {
            throw new IOException("Could not login");
        }
        //Check directory
        boolean exists = true;
	    ftpClient.changeWorkingDirectory(dirPath);
	    returnCode = ftpClient.getReplyCode();
	    if (returnCode == 550) {
	        exists = false;
	    }
	    //Disconnection
	    if (ftpClient != null && ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
	    return exists;
	}
	
	// String ftpFolder;
	// FTPClient ftpClient;
	//
	// public FtpUtil(String ftpHost, String ftpFolder, String ftpUsername,
	// String ftpPassword) throws IOException {
	// this.ftpFolder=ftpFolder;
	// ftpClient = new FTPClient();
	// ftpClient.connect(ftpHost);
	// logger.debug("connect: "+ftpClient.getReplyString());
	// ftpClient.login(ftpUsername, ftpPassword);
	// logger.debug("login con user "+ftpUsername+": "+ftpClient.getReplyString());
	// int reply = ftpClient.getReplyCode();
	//
	// if(!FTPReply.isPositiveCompletion(reply)) {
	// ftpClient.disconnect();
	// logger.error("FTP server refused connection.");
	// throw new IOException("FTP server refused connection.");
	// }
	// ftpClient.setRemoteVerificationEnabled(true);
	// if (ftpFolder!=null) {
	// if (!ftpFolder.equals("")) {
	// ftpClient.changeWorkingDirectory(ftpFolder);
	// logger.info("changeWorkingDirectory: "+ftpClient.getReplyString());
	// }
	// }
	// //ftpClient.enterRemotePassiveMode();
	// //logger.debug("enterLocalPassiveMode: "+ftpClient.getReplyString());
	// ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
	// logger.debug("setFileType BINARY:"+ftpClient.getReplyString());
	// }
	//
	// public OutputStream secureStoreFileStream(String destFilename) throws
	// IOException {
	// return secureStoreFileStream(destFilename, 10, 100);
	// }
	//
	// /** workaround to get the storeFileStream in case of inconstancy
	// * @param strDestFileNameFull stored filename (including folders)
	// * @param intTestMaxNb amount of tries to call storeFileStream(...)
	// reasonable value: 10
	// * @param intSleepTime The sleep time between to calls of
	// storeFileStream(...) reasonable value: 100
	// * @return the OutputStream or null if it still couldn't be retrieved
	// after the tries
	// * @throws IOException if problem occurs in storeFileStream(...)
	// */
	// public OutputStream secureStoreFileStream(String destFilename, int
	// intTestMaxNb, int intSleepTime)
	// throws IOException {
	// OutputStream out = null;
	// String filePath = /*"/"+ftpFolder+"/"+*/destFilename;
	//
	// ftpClient.deleteFile(filePath);
	// logger.debug("Delete file: "+ftpClient.getReplyString());
	// ftpClient.sendCommand("PASV");
	// logger.debug("Set passive mode: "+ftpClient.getReplyString());
	// ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
	// logger.debug("Set file transfer mode: "+ftpClient.getReplyString());
	//
	// for(int intTest = 0; intTest < intTestMaxNb && out == null; intTest++) {
	// try {
	// out = ftpClient.storeFileStream(filePath);
	// } catch (Exception e) {
	// logger.warn("Acquiring stream - ERROR: "+e.getMessage());
	// e.printStackTrace();
	// }
	// logger.debug("Acquiring stream: "+ftpClient.getReplyString());
	// if (out==null) {
	// logger.info(filePath+" non aperto - tentativo "+intTest);
	// } else {
	// logger.info(filePath+" aperto OutputStream - "+(intTest+1)+"° tentativo");
	// }
	// try { Thread.sleep(intSleepTime); }
	// catch(InterruptedException iex) {}
	// }
	// if (out==null) {
	// throw new
	// IOException("Non è possibile scrivere su "+destFilename+" "+ftpClient.getReplyString());
	// }
	// logger.debug("Remote outputStream acquired");
	// return out;
	// }
	//
	// public void disconnect() throws IOException {
	// logger.info("Chiusura stream FTP in corso");
	// ftpClient.completePendingCommand();
	// logger.info("Chiusura FTP");
	// ftpClient.disconnect();
	// logger.info("FTP chiuso con successo");
	// }

	// public boolean simpleStoreFile(String filename, InputStream in) throws
	// IOException {
	// ftpClient.deleteFile(filename);
	// logger.debug("Delete file: "+ftpClient.getReplyString());
	// ftpClient.sendCommand("PASV");
	// logger.debug("Set passive mode: "+ftpClient.getReplyString());
	// ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
	// logger.debug("Set file transfer mode: "+ftpClient.getReplyString());
	//
	// Boolean done = ftpClient.storeFile(filename, in);
	// logger.debug("File transferred: "+done.toString());
	// logger.debug("File transfer: "+ftpClient.getReplyString());
	// return done;
	// }

	// public static String completeFileTransfer(File localFile, String
	// remoteFileName) throws IOException {
	// String ftpHost = ServerConstants.FTP_HOST;
	// //Trasferimento FTP
	// FtpUtil ftp = new FtpUtil(ftpHost, ServerConstants.FTP_DIR_BASE,
	// ServerConstants.FTP_USER, ServerConstants.FTP_PASSWORD);
	// //Soluzione veloce: ftp.simpleStoreFile(remoteFileName, new
	// FileInputStream(localFile));
	// //Soluzione robusta:
	// InputStream fileInputStream = new FileInputStream(localFile);
	// OutputStream ftpOutputStream = ftp.secureStoreFileStream(remoteFileName);
	// byte[] buffer = new byte[4096];
	// int len;
	// while ((len = fileInputStream.read(buffer)) != -1) {
	// ftpOutputStream.write(buffer, 0, len);
	// }
	// ftpOutputStream.flush();
	// fileInputStream.close();
	// ftpOutputStream.close();
	// ftp.disconnect();
	// return ftpHost;
	// }

}
