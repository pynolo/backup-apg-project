package it.giunti.apg.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeUtility;

public class B64 {

	public static String encode(String value) throws MessagingException, IOException {
		byte[] b = (byte[]) value.getBytes("UTF-8"); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		OutputStream b64os = MimeUtility.encode(baos, "base64");
		b64os.write(b);
		b64os.close();
		return new String(baos.toByteArray());
	}

	public static String decode(String s) throws MessagingException,
			IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(s.getBytes());
		InputStream b64is = MimeUtility.decode(bais, "base64");
		byte[] tmp = new byte[s.length()];
		int n = b64is.read(tmp);
		byte[] res = new byte[n];
		System.arraycopy(tmp, 0, res, 0, n);
		String value = new String(res, "UTF-8");
		return value;
	}
}
