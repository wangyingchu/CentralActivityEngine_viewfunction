package com.viewfunction.activityEngine.security;

import java.util.List;

import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface Role {
	//Gets the name of the activity space in which this role is defined. 
    public String getActivitySpaceName();
 
    //Gets the original role name. 
    public String getRoleName();
    //Gets the role description. 
    public String getDescription();
    public void setDescription(String desc);
    //Gets the current name for the role. 
    public String getDisplayName();
    public void setDisplayName(String displayName);    
    
    //Participant operation
    //get all Participants belongs to this role
    public Participant[] getParticipants() throws ActivityEngineRuntimeException;
    public boolean addParticipant(String participantName) throws ActivityEngineRuntimeException;    
    public boolean removeParticipant(String participantName) throws ActivityEngineRuntimeException;    
    public boolean containParticipant(String participantName) throws ActivityEngineRuntimeException;
    
    public RoleQueue[] getRelatedRoleQueues() throws ActivityEngineRuntimeException;
    public boolean addInRoleQueue(String queueName) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
    public boolean removeFromRoleQueue(String queueName) throws ActivityEngineRuntimeException, ActivityEngineActivityException; 
    
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