package com.viewfunction.activityEngine.activityView;

import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface Queue {	
	public static final String QUEUE_TYPE_USERQUEUE="QUEUE_TYPE_USERQUEUE";
	public static final String QUEUE_TYPE_PROCESSQUEUE="QUEUE_TYPE_PROCESSQUEUE";	
	
	public boolean setExposedDataFields(DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException;	
	public DataFieldDefinition[] getExposedDataFields() throws ActivityEngineRuntimeException, ActivityEngineActivityException;	
	
	//Gets the name of the activity space in which this role is defined. 
    public String getActivitySpaceName();
    //Gets the original role name. 
    public String getQueueName();
    //Gets the role description.    
    public String getDescription();   
    //Gets the current name for the role. 
    public String getDisplayName();   
    
    public String getQueueType();    
}