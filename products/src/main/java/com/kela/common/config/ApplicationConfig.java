package com.kela.common.config;

public final class ApplicationConfig {

	private String url600001506 = null;
	
	private static ApplicationConfig myinstance = null;

	public static ApplicationConfig getInstance() {
		System.out.println("in ApplicationConfig getInstance");
		if (myinstance == null) {
			myinstance = new ApplicationConfig();
		}
		return myinstance;
	}

	private ApplicationConfig() {

	}

	public String getHTTPS_SERV_URL() {
		return url600001506;
	}

	public void setHttpsUrl600001506(String hTTPS_SERV_URL) {
		url600001506 = hTTPS_SERV_URL;
	}

	public static ApplicationConfig getMyinstance() {
		return myinstance;
	}

	public static void setMyinstance(ApplicationConfig myinstance) {
		ApplicationConfig.myinstance = myinstance;
	}
}
