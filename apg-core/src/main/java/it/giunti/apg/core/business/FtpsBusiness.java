package it.giunti.apg.core.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPSClient;

public class FtpsBusiness {
	//private static final String KEYSTORE_PASS = "*****";
	//private static final String KEYSTORE_FILE_NAME = "keystore/file/path/name";

	//private static final String PROTOCOL = "TLS"; // SSL/TLS

	public static final String upload(String host, String userName, String password,
			File localFile, String remoteDir, String remoteFileName)
					throws IOException {
		java.io.ByteArrayOutputStream log = new java.io.ByteArrayOutputStream();
		//TODO java.io.PrintWriter logPrinter = new java.io.PrintWriter(log);
		PrintWriter logPrinter = new PrintWriter(System.out);
		FTPSClient ftps = new FTPSClient(false);
		ftps.addProtocolCommandListener(new PrintCommandListener(logPrinter));
		ftps.connect(host);
		ftps.getReplyCode();
		ftps.execPBSZ(0); // RFC2228 requires that the PBSZ subcommand be issued prior to the PROT subcommand. However, TLS/SSL handles blocking of data, so '0' is the only value accepted.
		ftps.execPROT("C"); // P(rivate) needs certs. E and S: '536 Requested PROT level not opzorted by mechanism.'. C is default, but has clear text data channel - http://www.nabble.com/TLS-for-FTP-td6645485.html
		ftps.login(userName,password);
		ftps.changeWorkingDirectory(remoteDir);
		InputStream fileStream = new FileInputStream(localFile);
		ftps.storeFile(remoteFileName, fileStream);
		fileStream.close();
		return log.toString();
	}

	//public static final void upload(String keystoreFilePath, String keystorePass,
	//		String host, String userName, String password,
	//		File localFile, String remoteDir, String remoteFileName)
	//				throws FileException {
	//	FTPSClient ftps = null;
	//	try {
	//		ftps = new FTPSClient(PROTOCOL, false);
	//		ftps.setRemoteVerificationEnabled(false);
	//		SSLContext sslContext = getSSLContext(keystoreFilePath, keystorePass);
	//		FTPSSocketFactory sf = new FTPSSocketFactory(sslContext);
	//		ftps.setSocketFactory(sf);
	//		ftps.setBufferSize(1000);
	//		ftps.setFileType(FTPSClient.BINARY_FILE_TYPE);
	//		KeyManager keyManager = getKeyManagers(keystoreFilePath, keystorePass)[0];
	//		TrustManager trustManager = getTrustManagers(keystoreFilePath, keystorePass)[0];
	//		ftps.setControlEncoding("UTF-8");
	//
	//		ftps.setKeyManager(keyManager);
	//		ftps.setTrustManager(trustManager);
	//
	//		ftps.addProtocolCommandListener(new PrintCommandListener(
	//				new PrintWriter(System.out)));
	//
	//		ftps.connect(host, 21);
	//
	//		//System.out.println("Connected to " + host + ".");
	//		int reply = ftps.getReply();
	//
	//		if (!FTPReply.isPositiveCompletion(reply)) {
	//			ftps.disconnect();
	//			throw new FileException("FTP server refused connection.");
	//		}
	//		if (!ftps.login(userName, password)) {
	//			ftps.logout();
	//		}
	//
	//		ftps.pwd();
	//		ftps.changeWorkingDirectory(remoteDir);
	//
	//		//Rimuove il vecchio file se esiste
	//		try {
	//			ftps.deleteFile(remoteFileName);
	//		} catch (Exception e) {/*non c'è nulla da eliminare*/}
	//
	//		InputStream localFileStream = new FileInputStream(localFile);
	//		ftps.storeFile(remoteFileName, localFileStream);
	//
	//
	//	} catch (IOException e) {
	//		if (ftps.isConnected()) {
	//			try {
	//				ftps.disconnect();
	//			} catch (IOException ioe) {/*do nothing*/}
	//		}
	//		throw new FileException("Could not connect to server.", e);
	//	} catch (UnrecoverableKeyException e) {
	//		throw new FileException(e.getMessage(), e);
	//	} catch (NoSuchAlgorithmException e) {
	//		throw new FileException(e.getMessage(), e);
	//	} catch (KeyStoreException e) {
	//		throw new FileException(e.getMessage(), e);
	//	} catch (CertificateException e) {
	//		throw new FileException(e.getMessage(), e);
	//	} catch (KeyManagementException e) {
	//		throw new FileException(e.getMessage(), e);
	//	} finally {
	//		if (ftps.isConnected()) {
	//			try {
	//				ftps.disconnect();
	//			} catch (IOException ioe) {/*do nothing*/}
	//		}
	//	}
	//} // end main
	//
	//private static SSLContext getSSLContext(String keystorePath, String keystorePass) throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, UnrecoverableKeyException, IOException {
	//	TrustManager[] tm = getTrustManagers(keystorePath, keystorePass);
	//	//System.out.println("Init SSL Context");
	//	SSLContext sslContext = SSLContext.getInstance("SSLv3");
	//	sslContext.init(null, tm, null);
	//
	//	return sslContext;
	//}
	//private static KeyManager[] getKeyManagers(String keystorePath, String keystorePass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
	//	KeyStore ks = KeyStore.getInstance("JKS");
	//	ks.load(new FileInputStream(keystorePath), keystorePass.toCharArray());
	//
	//	KeyManagerFactory tmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	//	tmf.init(ks, keystorePass.toCharArray());
	//
	//	return tmf.getKeyManagers();
	//}
	//private static TrustManager[] getTrustManagers(String keystorePath, String keystorePass) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException {
	//	KeyStore ks = KeyStore.getInstance("JKS");
	//	ks.load(new FileInputStream(keystorePath), keystorePass.toCharArray());
	//
	//	TrustManagerFactory tmf = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	//	tmf.init(ks);
	//
	//	return tmf.getTrustManagers();
	//}
}
