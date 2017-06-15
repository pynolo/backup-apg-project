package it.giunti.apg.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyReader {

	private static Logger LOG = LoggerFactory.getLogger(PropertyReader.class);

	public static String getApgTitle() {
		return readProperty("apg.title");
	}
	
	public static String getApgStatus() {
		return readProperty("apg.status");
	}
	
	public static String getApgMenuImage() {
		return readProperty("apg.menu");
	}
	
	public static String getApgLoginImage() {
		return readProperty("apg.login");
	}
	
	public static String readProperty(String propertyName) {
		String value = "";
		try {
			InputStream configStream = new PropertyReader().getClass().getResourceAsStream(ServerConstants.PROPERTY_FILE);
			BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream, "UTF-8"));
			Properties props = new Properties();
			props.load(configReader);
			value = props.getProperty(propertyName);
		} catch (IOException e) { // catch exception in case properties file does not exist
			LOG.error(e.getMessage(), e);
		}
		return value;
	}

	public static String getApgVersion() {
		String value = "";
		try {
			InputStream configStream = new PropertyReader().getClass().getResourceAsStream(ServerConstants.VERSION_FILE);
			BufferedReader configReader = new BufferedReader(new InputStreamReader(configStream, "UTF-8"));
			Properties props = new Properties();
			props.load(configReader);
			value = props.getProperty("version");
		} catch (IOException e) { // catch exception in case properties file does not exist
			LOG.error(e.getMessage(), e);
		}
		return value;
	}
	
}
