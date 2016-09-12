package com.viewfunction.activityEngine.activityBureau;

import java.util.List;

import com.viewfunction.activityEngine.activityView.common.ActivityComment;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public interface BusinessActivity {
	//get this activity's process instance
	public ProcessObject getActivityProcessObject() throws ActivityEngineProcessException;
	//get all business data of this activity
	public ActivityData[] getActivityData() throws ActivityEngineRuntimeException, ActivityEngineDataException, ActivityEngineActivityException;	
	public boolean setActivityData(ActivityData[] activityDataArray) throws ActivityEngineRuntimeException, ActivityEngineDataException;	
	//get business data of pointed step
	public ActivityData[] getStepActivityData(String stepName) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;
	//get special activity data
	public ActivityData[] getActivityData(DataFieldDefinition[] dataFieldDefinitionArray) throws ActivityEngineRuntimeException,ActivityEngineDataException;
	//get definition of this activity
	public BusinessActivityDefinition getActivityDefinition() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;
	//get unique instanceID of this activity 
	public String getActivityId();			
	//Gets the roster name of this activity. 
	public String getRosterName() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;
	//Comment management
	public void addComment(ActivityComment activityComment) throws ActivityEngineProcessException;
	public List<ActivityComment> getComments() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;	
	//Activity status
	public boolean isFinished() throws ActivityEngineProcessException;
	//get ActivityStep
	public ActivityStep getCurrentActivityStepByStepName(String activityStepName) throws ActivityEngineProcessException;
	public List<ActivityStep> getCurrentActivitySteps() throws ActivityEngineProcessException;
	//get document content root folder path
	public String getDocumentsFolderPath();
	
	public Integer getActivityDefinitionVersion()throws ActivityEngineProcessException;
	public boolean isSuspendedActivity() throws ActivityEngineProcessException;
}