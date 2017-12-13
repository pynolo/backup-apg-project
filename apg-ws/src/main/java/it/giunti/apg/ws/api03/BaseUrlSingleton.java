package it.giunti.apg.ws.api03;

import javax.servlet.http.HttpServletRequest;

public class BaseUrlSingleton {

	private static BaseUrlSingleton instance = null;
	private String baseUrl = null;
	
	private BaseUrlSingleton() {}
	
	public static BaseUrlSingleton get() {
		if (instance == null) instance = new BaseUrlSingleton();
		return instance;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public void setBaseUrl(HttpServletRequest servletRequest) {
        String scheme = servletRequest.getScheme();
        String serverName = servletRequest.getServerName();
        int portNumber = servletRequest.getServerPort();
        String contextPath = servletRequest.getContextPath();
        baseUrl = scheme+"://"+serverName+":"+portNumber+contextPath;
	}
}
