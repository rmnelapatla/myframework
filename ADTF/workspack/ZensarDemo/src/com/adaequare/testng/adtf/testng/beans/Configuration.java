package com.adaequare.testng.adtf.testng.beans;

import java.io.Serializable;

public class Configuration implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3624221966977060769L;
	private String configName;
	private String browserType;
	private String browserVersion;
	private String host;
	private String port;
	private String osVersion;
	
	

	public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getBrowserType() {
		return browserType;
	}

	public void setBrowserType(String browserType) {
		this.browserType = browserType;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}
	
	@Override
	public String toString() {
		return "[" + browserType + " <" + browserVersion + ">, "  + host +":"+port+ " @ platform "+osVersion+"    ]";
	
	}

}
