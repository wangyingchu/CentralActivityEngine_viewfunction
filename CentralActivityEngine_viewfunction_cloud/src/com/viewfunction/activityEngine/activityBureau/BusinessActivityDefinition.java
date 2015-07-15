package com.viewfunction.activityEngine.activityBureau;

import java.util.Map;

import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;

public interface BusinessActivityDefinition {
	//name of a BusinessActivity
	public String getActivityType();
	public String getActivitySpaceName();
	public boolean isEnabled();		
	public String getActivityDescription();
	public boolean setActivityDescription(String activityDescription);	
	public String[] getActivityCategories();
	public boolean setActivityCategories(String[] activityCategories);
	public boolean addActivityCategory(String activityCategory);
	public boolean removeActivityCategory(String activityCategory);
		
	//contented data object
	public DataFieldDefinition[] getActivityDataFields();
	public boolean setActivityDataFields(DataFieldDefinition[] contentedDataFields);
	public boolean resetActivityDataFields(DataFieldDefinition[] contentedDataFields);
	public boolean addActivityDataFields(DataFieldDefinition dataField);
	public boolean removeActivityDataFields(String dataFieldName);
	public boolean containsActivityDataField(String dataFieldName);
	//Define exposed DataFields
	public boolean setActivityStepExposedDataFields(String stepName,DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException;	
	public boolean addActivityStepExposedDataField(String stepName,DataFieldDefinition exposedDataField) throws ActivityEngineDataException;
	public boolean removeActivityStepExposedDataField(String stepName,String dataFieldName) throws ActivityEngineDataException, ActivityEngineProcessException;		
	public boolean setLaunchPointExposedDataFields(DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException;	
	public boolean addLaunchPointExposedDataField(DataFieldDefinition exposedDataField) throws ActivityEngineDataException;
	public boolean removeLaunchPointExposedDataField(String dataFieldName) throws ActivityEngineDataException;	
	public DataFieldDefinition[] getLaunchPointExposedDataFields()throws ActivityEngineDataException;
	//activity launch roles and participants
	public boolean setActivityLaunchRoles(String[] roleNames);
	public boolean addActivityLaunchRole(String roleName);
	public boolean removeActivityLaunchRole(String roleName);
	public String[] getActivityLaunchRoles();	
	public boolean setActivityLaunchParticipants(String[] participantNames);
	public boolean addActivityLaunchParticipant(String participantName);
	public boolean removeActivityLaunchParticipant(String participantName);
	public String[] getActivityLaunchParticipants();	
	
	public Map<String,DataFieldDefinition[]> getActivityStepsExposedDataField();
	public boolean containsExposedActivityStep(String stepName);	
	public void setExposedSteps(String[] steps);
	public String[] getExposedSteps();	
	
	public Map<String,String> getActivityStepRoleMap();		
	public boolean setActivityStepRelatedRole(String stepDefineKey,String roleName) throws ActivityEngineProcessException;
	public Role getActivityStepRelatedRole(String stepDefineKey) throws ActivityEngineProcessException, ActivityEngineRuntimeException;
	
	//Define Process Define resource
	public boolean setDefinitionResource(Object definitionResource);
	public Object getDefinitionResource();
	public String getRosterName();
	public boolean isDefinitionResourceUpdated();
	public void setDefinitionResourceUpdated(boolean updateFlag);
	
	//Define Process operation data
	public String getLaunchDecisionPointAttributeName();
	public void setLaunchDecisionPointAttributeName(String attributeName);
	public String getLaunchUserIdentityAttributeName();
	public void setLaunchUserIdentityAttributeName(String attributeName);
	public String[] getLaunchDecisionPointChoiseList();	
	public void setLaunchDecisionPointChoiseList(String[] optionNameList);
	public String[] getLaunchProcessVariableList();
	public void setLaunchProcessVariableList(String[] variableNameList);	
	
	public String getStepDecisionPointAttributeName(String stepName);
	public boolean setStepDecisionPointAttributeName(String stepName,String attributeName) throws ActivityEngineDataException;		
	public String getStepUserIdentityAttributeName(String stepName);
	public boolean setStepUserIdentityAttributeName(String stepName,String attributeName) throws ActivityEngineDataException;	
	public String[] getStepDecisionPointChoiseList(String stepName);	
	public boolean setStepDecisionPointChoiseList(String stepName,String[] optionNameList) throws ActivityEngineDataException;		
	public String[] getStepProcessVariableList(String stepName);	
	public boolean setStepProcessVariableList(String stepName,String[] variableNameList) throws ActivityEngineDataException;
}