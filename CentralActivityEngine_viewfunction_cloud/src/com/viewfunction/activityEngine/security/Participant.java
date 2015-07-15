package com.viewfunction.activityEngine.security;

import java.util.List;

import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.activityView.common.ParticipantTask;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface Participant{		
	
	public static final String PARTICIPANT_TYPE_USER="PARTICIPANT_TYPE_USER";
	public static final String PARTICIPANT_TYPE_GROUP="PARTICIPANT_TYPE_GROUP";	
	
	//Gets the name of the Activity Space associated with the Participant object. 
	public String getActivitySpaceName();	
	public void setActivitySpaceName(String theActivitySpaceName);		
	
	//Sets the "short" or "logon" name of a participant user or group. 
	public void setDisplayName(String theDisplayName);
	//Gets the participant display name
	public String getDisplayName();
	
	//Returns the name of a participant user or group
	public String getParticipantName();
	//Indicates if the current instance of Participant is a group. 	
	public boolean isGroup();
	//Get roles this participant is in
	public Role[] getRoles() throws ActivityEngineRuntimeException;
	public boolean isInRole(String roleName) throws ActivityEngineRuntimeException;	
	
	public List<ParticipantTask> fetchParticipantTasks() throws ActivityEngineProcessException;	
	
	public boolean addCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public boolean updateCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public boolean deleteCustomAttribute(String attributeName) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public CustomAttribute getCustomAttribute(String attributeName) throws ActivityEngineRuntimeException;
	public List<CustomAttribute> getCustomAttributes() throws ActivityEngineRuntimeException;	
	
	public List<CustomStructure> getSubCustomStructures()throws ActivityEngineRuntimeException, ActivityEngineDataException;	
	public CustomStructure getSubCustomStructure(String structureName) throws ActivityEngineRuntimeException, ActivityEngineDataException;	
	public boolean addSubCustomStructure(String structureName) throws ActivityEngineRuntimeException;	
	public boolean deleteSubCustomStructure(String structureName) throws ActivityEngineRuntimeException;
	
	public String getDocumentsFolderPath();
}