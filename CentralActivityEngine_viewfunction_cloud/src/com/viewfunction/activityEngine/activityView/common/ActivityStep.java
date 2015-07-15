package com.viewfunction.activityEngine.activityView.common;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessStep;

public interface ActivityStep {
	
	public String getActivityType();	
	public String getActivityStepName();
	public String getParentActivityStepId();
	public String getActivityStepDefinitionKey();
	public String getActivityStepId();
	public String getActivityId();
	public Role getRelatedRole() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;
	public BusinessActivity getBusinessActivity();
	//process data
	public Date getCreateTime();		
	public String getStepAssignee();
	public String getStepDescription();
	public String getStepOwner();
	public Date getDueDate();	
	//finished activity step should have finish time property
	public Date getFinishTime();		
	//content data
	public ActivityData[] getActivityStepData() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;	
	//Activity data
	public ProcessStep getActivityProcessStep();
	public ProcessObject getActivityProcessObject() throws ActivityEngineProcessException;	
	
	public boolean handleActivityStep(String activeParticipantName) throws ActivityEngineProcessException;
	public boolean saveActivityStep(String activeParticipantName) throws ActivityEngineProcessException;// save processObject data and contentObjectData
	public boolean completeActivityStep(String activeParticipantName) throws ActivityEngineProcessException;
	public boolean completeActivityStep(String activeParticipantName,Map<String,Object> processVariables) throws ActivityEngineProcessException;	
	public boolean returnActivityStep() throws ActivityEngineProcessException;
	public boolean reassignActivityStep(String newActiveParticipantName) throws ActivityEngineProcessException;
	
	//Comment management
	public void addComment(ActivityComment activityComment);
	public List<ActivityComment> getComments() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;	
	
	//child activityStep	
	public ActivityStep createChildActivityStep(String childStepAssignee,String childStepName,String childStepDescription,Date childStepDueDate) throws ActivityEngineProcessException;
	public boolean deleteChildActivityStepByStepId(String stepId) throws ActivityEngineProcessException;
	public boolean deleteChildActivitySteps() throws ActivityEngineProcessException;
	public List<ActivityStep> getChildActivitySteps() throws ActivityEngineProcessException;
	public boolean isAllChildActivityStepsFinished() throws ActivityEngineProcessException;	
	public boolean hasParentActivityStep() throws ActivityEngineProcessException;
	public boolean hasChildActivityStep() throws ActivityEngineProcessException;
	public ActivityStep getParentActivityStep() throws ActivityEngineProcessException;

}