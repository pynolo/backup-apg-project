package it.giunti.apg.core.business;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtpBusiness {
	
	static private Logger LOG = LoggerFactory.getLogger(FtpBusiness.class);

	/**
	 * Upload a file to a FTP server. A FTP URL is generated with the following
	 * syntax: ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 *            , FTP server address (optional port ':portNumber').
	 * @param user
	 *            , Optional user name to login.
	 * @param password
	 *            , Optional password for user.
	 * @param remoteNameAndDir
	 *            , Destination file name on FTP server (with optional preceding
	 *            relative path, e.g. "myDir/myFile.txt").
	 * @param source
	 *            , Source file to upload.
	 * @throws MalformedURLException
	 *             , IOException on error.
	 */
	public static void upload(String ftpServer, String port, String user, String password,
			String remoteNameAndDir, File source) throws MalformedURLException,
			IOException {
		if (ftpServer != null && remoteNameAndDir != null && source != null) {
			StringBuffer sb = new StringBuffer("ftp://");
			// check for authentication else assume its anonymous access.
			if (user != null && password != null) {
				sb.append(user);
				sb.append(':');
				sb.append(password);
				sb.append('@');
			}
			sb.append(ftpServer);
			sb.append(':');
			sb.append(port);
			sb.append('/');
			sb.append(remoteNameAndDir);
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append(";type=i");

			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;
			try {
				URL url = new URL(sb.toString());
				URLConnection urlc = url.openConnection();

				bos = new BufferedOutputStream(urlc.getOutputStream());
				bis = new BufferedInputStream(new FileInputStream(source));

				int i;
				// read byte by byte until end of stream
				while ((i = bis.read()) != -1) {
					bos.write(i);
				}
			} finally {
				if (bis != null)
					try {
						bis.close();
					} catch (IOException ioe) {
						LOG.error(ioe.getMessage(), ioe);
						throw ioe;
					}
				if (bos != null)
					try {
						bos.close();
					} catch (IOException ioe) {
						LOG.error(ioe.getMessage(), ioe);
						throw ioe;
					}
			}
		} else {
			throw new IOException("FTP input not available.");
		}
	}

	/**
	 * Download a file from a FTP server. A FTP URL is generated with the
	 * following syntax: ftp://user:password@host:port/filePath;type=i.
	 * 
	 * @param ftpServer
	 *            , FTP server address (optional port ':portNumber').
	 * @param user
	 *            , Optional user name to login.
	 * @param password
	 *            , Optional password for user.
	 * @param remoteNameAndDir
	 *            , Name of file to download (with optional preceeding relative
	 *            path, e.g. one/two/three.txt).
	 * @param destination
	 *            , Destination file to save.
	 * @throws MalformedURLException
	 *             , IOException on error.
	 */
	public static void download(String ftpServer, String port, String user, String password,
			String remoteNameAndDir, File destination)
			throws MalformedURLException, IOException {
		int BUFFERLENGTH = 4096;
		if (ftpServer != null && remoteNameAndDir != null
				&& destination != null) {
			StringBuffer sb = new StringBuffer("ftp://");
			// check for authentication else assume its anonymous access.
			if (user != null && password != null) {
				sb.append(user);
				sb.append(':');
				sb.append(password);
				sb.append('@');
			}
			sb.append(ftpServer);
			sb.append(':');
			sb.append(port);
			sb.append('/');
			sb.append(remoteNameAndDir);
			/*
			 * type ==> a=ASCII mode, i=image (binary) mode, d= file directory
			 * listing
			 */
			sb.append(";type=i");
			BufferedInputStream bis = null;
			FileOutputStream fos = null;
			byte[] buffer = new byte[BUFFERLENGTH];
			int read = 0;
			try {
				URL url = new URL(sb.toString());
				URLConnection urlc = url.openConnection();

				bis = new BufferedInputStream(urlc.getInputStream());
				fos = new FileOutputStream(destination);
				do {
					read = bis.read(buffer, 0, BUFFERLENGTH);
					if (read > -1) {
						fos.write(buffer, 0, read);
					}
				} while (read > -1);
			} finally {
				if (bis != null)
					try {
						bis.close();
					} catch (IOException ioe) {
						LOG.error(ioe.getMessage(), ioe);
						throw ioe;
					}
				if (fos != null)
					try {
						fos.close();
					} catch (IOException ioe) {
						LOG.error(ioe.getMessage(), ioe);
						throw ioe;
					}
			}
		} else {
			throw new IOException("FTP input not available.");
		}
	}
	
}
