package com.viewfunction.activityEngine.activityView.common;

public class ActivityStepDefinition {
	private String stepName;
	private String stepId;
	private String stepDescription;
	private String stepRole;
	private String stepUserIdentityAttribute;
	private String[] stepProcessVariables;
	
	public String getStepName() {
		return stepName;
	}
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	public String getStepId() {
		return stepId;
	}
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	public String getStepDescription() {
		return stepDescription;
	}
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}
	public String getStepRole() {
		return stepRole;
	}
	public void setStepRole(String stepRole) {
		this.stepRole = stepRole;
	}
	public String getStepUserIdentityAttribute() {
		return stepUserIdentityAttribute;
	}
	public void setStepUserIdentityAttribute(String stepUserIdentityAttribute) {
		this.stepUserIdentityAttribute = stepUserIdentityAttribute;
	}
	public String[] getStepProcessVariables() {
		return stepProcessVariables;
	}
	public void setStepProcessVariables(String[] stepProcessVariables) {
		this.stepProcessVariables = stepProcessVariables;
	}
}