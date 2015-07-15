package com.viewfunction.activityEngine.activityView.common;

import java.util.List;

import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface CustomStructure {
	public String getStructureName();	
	public void setStructureName(String structureName);		
	public String getStructureId() throws ActivityEngineRuntimeException;
	public void setStructureId(String structureId);	
	public List<CustomStructure> getSubCustomStructures()throws ActivityEngineRuntimeException, ActivityEngineDataException;	
	public CustomStructure getSubCustomStructure(String structureName) throws ActivityEngineRuntimeException, ActivityEngineDataException;	
	public boolean addSubCustomStructure(String structureName) throws ActivityEngineRuntimeException;	
	public boolean deleteSubCustomStructure(String structureName) throws ActivityEngineRuntimeException;	
	public boolean addCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public boolean updateCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public boolean deleteCustomAttribute(String attributeName) throws ActivityEngineRuntimeException, ActivityEngineDataException;
	public CustomAttribute getCustomAttribute(String attributeName) throws ActivityEngineRuntimeException;
	public List<CustomAttribute> getCustomAttributes() throws ActivityEngineRuntimeException;	
	
}