package com.viewfunction.activityEngine.extension;

public class ActivityStepMonitorRuntimeInfo {
	
	private String activityStepMonitorClassName;
	private String relatedProcessDefinitionId;
	private String relatedProcessObjectId;
	private int monitorRetryTimes;
	private String monitorDefinitionKey;
	private String monitorStepId;
	
	public String getActivityStepMonitorClassName() {
		return activityStepMonitorClassName;
	}
	public void setActivityStepMonitorClassName(
			String activityStepMonitorClassName) {
		this.activityStepMonitorClassName = activityStepMonitorClassName;
	}
	public String getRelatedProcessDefinitionId() {
		return relatedProcessDefinitionId;
	}
	public void setRelatedProcessDefinitionId(String relatedProcessDefinitionId) {
		this.relatedProcessDefinitionId = relatedProcessDefinitionId;
	}
	public String getRelatedProcessObjectId() {
		return relatedProcessObjectId;
	}
	public void setRelatedProcessObjectId(String relatedProcessObjectId) {
		this.relatedProcessObjectId = relatedProcessObjectId;
	}
	public int getMonitorRetryTimes() {
		return monitorRetryTimes;
	}
	public void setMonitorRetryTimes(int monitorRetryTimes) {
		this.monitorRetryTimes = monitorRetryTimes;
	}
	public String getMonitorDefinitionKey() {
		return monitorDefinitionKey;
	}
	public void setMonitorDefinitionKey(String monitorDefinitionKey) {
		this.monitorDefinitionKey = monitorDefinitionKey;
	}
	public String getMonitorStepId() {
		return monitorStepId;
	}
	public void setMonitorStepId(String monitorStepId) {
		this.monitorStepId = monitorStepId;
	}
}