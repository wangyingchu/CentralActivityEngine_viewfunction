package com.viewfunction.activityEngine.activityView;

import java.util.List;

import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
public interface Roster {	
	//Gets the name of the activity space in which this role is defined. 
    public String getActivitySpaceName();
    //Gets the original role name. 
    public String getRosterName();
    //Gets the role description.    
    public String getDescription();
    public void setDescription(String desc);
    //Gets the current name for the role. 
    public String getDisplayName();
    public void setDisplayName(String displayName);    
   
    
    public String[] getContainedActivityTypes() throws ActivityEngineRuntimeException, ActivityEngineActivityException;
    public boolean addActivityType(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
    public boolean removeActivityType(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
    
    public boolean setExposedDataFields(DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException;	
	public DataFieldDefinition[] getExposedDataFields() throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean addExposedDataField(DataFieldDefinition exposedDataField) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean removeExposedDataField(String dataFieldName) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean updateExposedDataField(DataFieldDefinition exposedDataField) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException;
	
	public List<BusinessActivity> fetchBusinessActivitys() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineProcessException;	
}