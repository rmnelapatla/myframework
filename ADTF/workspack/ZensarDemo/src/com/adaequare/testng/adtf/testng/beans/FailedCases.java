package com.adaequare.testng.adtf.testng.beans;

import java.io.Serializable;

public class FailedCases implements Serializable {

	private static final long serialVersionUID = -8081681135110929606L;
	private String testCaseTime;
	private int failCount;
	private String timeStamp;
	
	
	public String getTestCaseTime() {
		return testCaseTime;
	}
	public void setTestCaseTime(String testCaseTime) {
		this.testCaseTime = testCaseTime;
	}
	public int getFailCount() {
		return failCount;
	}
	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
}
