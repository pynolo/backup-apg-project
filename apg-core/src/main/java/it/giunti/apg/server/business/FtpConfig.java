package it.giunti.apg.server.business;

public class FtpConfig {
	private String host = null;
	private String port = null;
	private String username = null;
	private String password = null;
	private String dir = null;
	
	public FtpConfig() {}
	
	public FtpConfig(String host, String port, String username, String password, String dir) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.dir = dir;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getDir() {
		return dir;
	}
	
}
