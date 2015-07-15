package com.viewfunction.activityEngine.activityView.common;

import java.util.Date;

import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface ParticipantTask {
	
	public String getActivityType();
	public String getRoleName() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;	
	
	public String getActivityStepName();	
	public String getStepDescription();	
	
	public Date getCreateTime();
	public Date getDueDate();
		
	public String getStepAssignee();	
	public String getStepOwner();
		
	public ActivityStep getActivityStep();
}