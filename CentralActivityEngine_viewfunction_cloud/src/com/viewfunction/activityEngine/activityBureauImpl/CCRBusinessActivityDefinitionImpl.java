package com.viewfunction.activityEngine.activityBureauImpl;

import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.common.ActivityStepDefinition;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStepDefinition;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCRBusinessActivityDefinitionImpl implements BusinessActivityDefinition,Serializable{	
	private static final long serialVersionUID = 4253617946152726919L;
	
	private String activityType;
	private String activitySpaceName;
	private String activityDescription;	
	private DataFieldDefinition[] containedDataFieldDefineArray;
	private HashMap<String,DataFieldDefinition> containedDataFieldDefineMap;	
	private HashMap<String,HashMap<String, DataFieldDefinition>> conatinedStepExposedDataField;
	private String[] exposedStepsArray;
	private boolean isEnabled=true;
	private Object definitionResource;
	private String rosterName;
	private HashMap<String,String> containedStepRoleMap;	
	private String[] launchDecisionPointChoiseList;
	private String[] launchProcessVariableList;
	private HashMap<String,String[]> stepDecisionPointChoiseListMap;
	private HashMap<String,String[]> stepProcessVariableListMap;
	private String launchDecisionPointAttributeName;	
	private HashMap<String,String> stepDecisionPointAttributeNameMap;	
	private String launchUserIdentityAttributeName;
	private HashMap<String,String> stepUserIdentityAttributeNameMap;
	private boolean definitionResourceUpdated=false;
	private HashMap<String,DataFieldDefinition> launchPointDataFieldDefineMap;
	private HashMap<String,String> activityLaunchRolesMap;
	private HashMap<String,String> activityLaunchParticipantsMap;
	private HashMap<String,String> activityCategoriesMap;
	private long metaConfigurationVersion;
		
	public CCRBusinessActivityDefinitionImpl(String activityType,String activitySpaceName){
		this.activityType=activityType;
		this.activitySpaceName=activitySpaceName;
		containedDataFieldDefineMap=new HashMap<String,DataFieldDefinition>();
		conatinedStepExposedDataField=new HashMap<String,HashMap<String,DataFieldDefinition>>();
		stepDecisionPointChoiseListMap=new HashMap<String,String[]>();
		stepProcessVariableListMap=new HashMap<String,String[]>();
		stepDecisionPointAttributeNameMap=new HashMap<String,String>();
		stepUserIdentityAttributeNameMap=new HashMap<String,String>();
		containedStepRoleMap=new HashMap<String,String>();		
		launchPointDataFieldDefineMap=new HashMap<String,DataFieldDefinition>();
		activityLaunchRolesMap=new HashMap<String,String>();
		activityLaunchParticipantsMap=new HashMap<String,String>();
		activityCategoriesMap=new HashMap<String,String>();
	}	
	
	public CCRBusinessActivityDefinitionImpl(String activityType,String activitySpaceName,String[] exposedSteps){
		this.activityType=activityType;
		this.activitySpaceName=activitySpaceName;
		containedDataFieldDefineMap=new HashMap<String,DataFieldDefinition>();
		conatinedStepExposedDataField=new HashMap<String,HashMap<String,DataFieldDefinition>>();
		this.exposedStepsArray=exposedSteps;
		stepDecisionPointChoiseListMap=new HashMap<String,String[]>();
		stepProcessVariableListMap=new HashMap<String,String[]>();
		stepDecisionPointAttributeNameMap=new HashMap<String,String>();
		stepUserIdentityAttributeNameMap=new HashMap<String,String>();
		containedStepRoleMap=new HashMap<String,String>();		
		launchPointDataFieldDefineMap=new HashMap<String,DataFieldDefinition>();
		activityLaunchRolesMap=new HashMap<String,String>();
		activityLaunchParticipantsMap=new HashMap<String,String>();
		activityCategoriesMap=new HashMap<String,String>();
	}	

	@Override
	public String getActivityType() {		
		return activityType;
	}

	@Override
	public DataFieldDefinition[] getActivityDataFields() {		
		containedDataFieldDefineArray=new DataFieldDefinition[containedDataFieldDefineMap.size()];		
		containedDataFieldDefineMap.values().toArray(containedDataFieldDefineArray);		
		return containedDataFieldDefineArray;		
	}

	@Override
	public boolean setActivityDataFields(DataFieldDefinition[] contentedDataFields) {		
		for(DataFieldDefinition dfd:contentedDataFields){
			String dfName=dfd.getFieldName();
			containedDataFieldDefineMap.put(dfName, dfd);			
		}		
		return true;
	}
	
	@Override
	public boolean resetActivityDataFields(DataFieldDefinition[] contentedDataFields) {	
		containedDataFieldDefineMap.clear();
		for(DataFieldDefinition dfd:contentedDataFields){
			String dfName=dfd.getFieldName();
			containedDataFieldDefineMap.put(dfName, dfd);			
		}		
		return true;
	}

	@Override
	public boolean addActivityDataField(DataFieldDefinition dataField) {		
		String dataName=dataField.getFieldName();
		if(containedDataFieldDefineMap.containsKey(dataName)){
			return false;
		}else{
			containedDataFieldDefineMap.put(dataName, dataField);
			return true;
		}
	}
	
	@Override
	public boolean updateActivityDataField(DataFieldDefinition dataField) {		
		String dataName=dataField.getFieldName();
		if(containedDataFieldDefineMap.containsKey(dataName)){
			containedDataFieldDefineMap.remove(dataName);
			containedDataFieldDefineMap.put(dataName, dataField);
			return true;
		}else{
			return false;
		}
	}

	@Override
	public boolean removeActivityDataField(String dataFieldName) {
		if(!containedDataFieldDefineMap.containsKey(dataFieldName)){
			return false;
		}else{
			containedDataFieldDefineMap.remove(dataFieldName);
			return true;
		}		
	}

	@Override
	public boolean containsActivityDataField(String dataFieldName) {		
		return containedDataFieldDefineMap.containsKey(dataFieldName);
	}

	@Override
	public boolean setActivityStepExposedDataFields(String stepName,DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException {
		if(conatinedStepExposedDataField.containsKey(stepName)){
			HashMap<String,DataFieldDefinition> edf=conatinedStepExposedDataField.get(stepName);
			for(DataFieldDefinition cedf:exposedDataFields){					
				if(containsActivityDataField(cedf.getFieldName())){
					edf.put(cedf.getFieldName(), cedf);		
				}else{
					throw new ActivityEngineDataException();
				}				
			}			
		}else{
			HashMap<String,DataFieldDefinition> edf=new HashMap<String,DataFieldDefinition>();			
			for(DataFieldDefinition cedf:exposedDataFields){				
				edf.put(cedf.getFieldName(), cedf);		
			}
			conatinedStepExposedDataField.put(stepName, edf);			
		}
		return true;
	}

	@Override
	public boolean addActivityStepExposedDataField(String stepName,DataFieldDefinition exposedDataField) throws ActivityEngineDataException {
		if(conatinedStepExposedDataField.containsKey(stepName)){
			HashMap<String,DataFieldDefinition> edf=conatinedStepExposedDataField.get(stepName);
			if(containsActivityDataField(exposedDataField.getFieldName())){				
				edf.put(exposedDataField.getFieldName(), exposedDataField);
			}else{
				throw new ActivityEngineDataException();
			}				
		}else{
			HashMap<String,DataFieldDefinition> edf=new HashMap<String,DataFieldDefinition>();
			if(containsActivityDataField(exposedDataField.getFieldName())){				
				edf.put(exposedDataField.getFieldName(), exposedDataField);
				conatinedStepExposedDataField.put(stepName, edf);
			}else{
				throw new ActivityEngineDataException();
			}			
		}
		return true;
	}

	@Override
	public boolean removeActivityStepExposedDataField(String stepName,String dataFieldName) throws ActivityEngineDataException, ActivityEngineProcessException {
		if(conatinedStepExposedDataField.containsKey(stepName)){
			HashMap<String,DataFieldDefinition> edf=conatinedStepExposedDataField.get(stepName);
			if(edf.containsKey(dataFieldName)){
				edf.remove(dataFieldName);
				return true;
			}else{
				throw new ActivityEngineDataException();
			}			
		}else{
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public boolean setDefinitionResource(Object definitionResource) {
		this.definitionResource=definitionResource;
		return true;
	}

	@Override
	public Object getDefinitionResource() {		
		return this.definitionResource;
	}

	@Override
	public Map<String, DataFieldDefinition[]> getActivityStepsExposedDataField() {		
		Map<String, DataFieldDefinition[]> targetMap=new HashMap<String, DataFieldDefinition[]>();  		
		Set<String> stepKey= conatinedStepExposedDataField.keySet();		
		Iterator<String> i=stepKey.iterator();
		while(i.hasNext()){
			String currentStep=i.next().toString();			
			HashMap<String,DataFieldDefinition> dataFieleMap=conatinedStepExposedDataField.get(currentStep);
			DataFieldDefinition[] dfdArray=new DataFieldDefinition[dataFieleMap.size()];
			targetMap.put(currentStep, dataFieleMap.values().toArray(dfdArray));
		}		
		return targetMap;
	}

	@Override
	public boolean containsExposedActivityStep(String stepName) {
		if(this.exposedStepsArray==null){
			return false;
		}else{
			for(String steps:this.exposedStepsArray){
				if(steps.equals(stepName)){
					return true;
				}				
			}
			return false;			
		}		
	}

	@Override
	public void setExposedSteps(String[] steps) {
		this.exposedStepsArray=steps;
	}

	@Override
	public String[] getExposedSteps() {		
		return this.exposedStepsArray;
	}

	@Override
	public boolean isEnabled() {		
		return this.isEnabled;
	}
	
	public void setIsEnabled(boolean status){
		this.isEnabled=status;
	}

	@Override
	public String getActivitySpaceName() {		
		return this.activitySpaceName;
	}

	@Override
	public String getRosterName() {		
		return this.rosterName;
	}
	
	public void setRosterName(String rosterName) {		
		this.rosterName=rosterName;
	}

	@Override
	public Role getActivityStepRelatedRole(String stepDefineKey) throws ActivityEngineProcessException, ActivityEngineRuntimeException {
		if(!containsExposedActivityStep(stepDefineKey)){
			//throw new ActivityEngineProcessException();
			return null;
		}else{
			String roleName=containedStepRoleMap.get(stepDefineKey);
			if(roleName==null){
				return null;
			}
			ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(this.activitySpaceName);
			Role steprelatedRole=activitySpace.getRole(roleName);
			return steprelatedRole;
		}		
	}

	@Override
	public boolean setActivityStepRelatedRole(String stepDefineKey, String roleName) throws ActivityEngineProcessException {
		if(!containsExposedActivityStep(stepDefineKey)){
			throw new ActivityEngineProcessException();
		}else{
			if(roleName!=null){
				containedStepRoleMap.put(stepDefineKey, roleName);
			}else{
				containedStepRoleMap.remove(stepDefineKey);
			}			
			return true;
		}		
	}

	@Override
	public Map<String, String> getActivityStepRoleMap() {		
		return containedStepRoleMap;
	}

	@Override
	public String[] getLaunchDecisionPointChoiseList() {		
		return this.launchDecisionPointChoiseList;
	}

	@Override
	public String[] getStepDecisionPointChoiseList(String stepName) {		
		return stepDecisionPointChoiseListMap.get(stepName);
	}

	@Override
	public void setLaunchDecisionPointChoiseList(String[] optionNameList) {
		this.launchDecisionPointChoiseList=optionNameList;		
	}

	@Override
	public boolean setStepDecisionPointChoiseList(String stepName,String[] optionNameList) throws ActivityEngineDataException {
		if(containsExposedActivityStep(stepName)){
			stepDecisionPointChoiseListMap.put(stepName, optionNameList);
			return true;			
		}else{
			throw new ActivityEngineDataException();
		}
	}

	@Override
	public String[] getLaunchProcessVariableList() {		
		return this.launchProcessVariableList;
	}

	@Override
	public String[] getStepProcessVariableList(String stepName) {		
		return stepProcessVariableListMap.get(stepName);
	}

	@Override
	public void setLaunchProcessVariableList(String[] variableNameList) {
		this.launchProcessVariableList=variableNameList;		
	}

	@Override
	public boolean setStepProcessVariableList(String stepName,String[] variableNameList) throws ActivityEngineDataException {
		if(containsExposedActivityStep(stepName)){
			if(variableNameList==null){
				stepProcessVariableListMap.remove(stepName);
			}else{
				stepProcessVariableListMap.put(stepName, variableNameList);
			}			
			return true;			
		}else{
			throw new ActivityEngineDataException();
		}
	}

	@Override
	public String getLaunchDecisionPointAttributeName() {		
		return launchDecisionPointAttributeName;
	}

	@Override
	public void setLaunchDecisionPointAttributeName(String attributeName) {
		this.launchDecisionPointAttributeName=attributeName;		
	}

	@Override
	public String getStepDecisionPointAttributeName(String stepName) {		
		return stepDecisionPointAttributeNameMap.get(stepName);
	}

	@Override
	public boolean setStepDecisionPointAttributeName(String stepName,String attributeName) throws ActivityEngineDataException {
		if(containsExposedActivityStep(stepName)){
			if(attributeName==null){
				stepDecisionPointAttributeNameMap.remove(stepName);
			}else{
				stepDecisionPointAttributeNameMap.put(stepName, attributeName);
			}			
			return true;			
		}else{
			throw new ActivityEngineDataException();
		}		
	}

	@Override
	public String getLaunchUserIdentityAttributeName() {		
		return launchUserIdentityAttributeName;
	}

	@Override
	public void setLaunchUserIdentityAttributeName(String attributeName) {
		this.launchUserIdentityAttributeName=attributeName;		
	}

	@Override
	public String getStepUserIdentityAttributeName(String stepName) {
		return stepUserIdentityAttributeNameMap.get(stepName);
	}

	@Override
	public boolean setStepUserIdentityAttributeName(String stepName,String attributeName) throws ActivityEngineDataException {
		if(containsExposedActivityStep(stepName)){
			if(attributeName==null){
				stepUserIdentityAttributeNameMap.remove(stepName);
			}else{
				stepUserIdentityAttributeNameMap.put(stepName, attributeName);
			}			
			return true;			
		}else{
			throw new ActivityEngineDataException();
		}	
	}
	
	@Override
	public boolean isDefinitionResourceUpdated() {		
		return this.definitionResourceUpdated;
	}

	@Override
	public void setDefinitionResourceUpdated(boolean updateFlag) {
		this.definitionResourceUpdated=updateFlag;		
	}	

	@Override
	public boolean setLaunchPointExposedDataFields(DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException {			
		for(DataFieldDefinition cedf:exposedDataFields){					
			if(containsActivityDataField(cedf.getFieldName())){						
				if(launchPointDataFieldDefineMap.containsKey(cedf.getFieldName())){
					launchPointDataFieldDefineMap.remove(cedf.getFieldName());						
				}					
				launchPointDataFieldDefineMap.put(cedf.getFieldName(), cedf);					
			}else{
				throw new ActivityEngineDataException();
			}				
		}			
		return true;
	}	

	@Override
	public boolean addLaunchPointExposedDataField(DataFieldDefinition exposedDataField)throws ActivityEngineDataException {
		if(containsActivityDataField(exposedDataField.getFieldName())){						
			if(launchPointDataFieldDefineMap.containsKey(exposedDataField.getFieldName())){
				launchPointDataFieldDefineMap.remove(exposedDataField.getFieldName());						
			}					
			launchPointDataFieldDefineMap.put(exposedDataField.getFieldName(), exposedDataField);					
		}else{
			throw new ActivityEngineDataException();
		}		
		return true;
	}	

	@Override
	public boolean removeLaunchPointExposedDataField(String dataFieldName) throws ActivityEngineDataException {	
		if(containsActivityDataField(dataFieldName)){	
			if(!launchPointDataFieldDefineMap.containsKey(dataFieldName)){
				return false;
			}else{
				launchPointDataFieldDefineMap.remove(dataFieldName);
				return true;
			}		
		}else{
			throw new ActivityEngineDataException();
		}				
	}
	
	@Override
	public DataFieldDefinition[] getLaunchPointExposedDataFields() throws ActivityEngineDataException {
		DataFieldDefinition[] launchPointDataFieldDefineArray=new DataFieldDefinition[launchPointDataFieldDefineMap.size()];		
		launchPointDataFieldDefineMap.values().toArray(launchPointDataFieldDefineArray);		
		return launchPointDataFieldDefineArray;		
	}	

	@Override
	public boolean setActivityLaunchRoles(String[] roleNames){		
		for(String roleName:roleNames){
			if(activityLaunchRolesMap.containsKey(roleName)){
				activityLaunchRolesMap.remove(roleName);				
			}
			activityLaunchRolesMap.put(roleName, roleName);			
		}
		return true;		
	}

	@Override
	public boolean addActivityLaunchRole(String roleName){
		if(activityLaunchRolesMap.containsKey(roleName)){
			activityLaunchRolesMap.remove(roleName);				
		}
		activityLaunchRolesMap.put(roleName, roleName);
		return true;
	}

	@Override
	public boolean removeActivityLaunchRole(String roleName) {
		if(activityLaunchRolesMap.containsKey(roleName)){
			activityLaunchRolesMap.remove(roleName);	
			return true;
		}else{
			return false;
		}		
	}

	@Override
	public String[] getActivityLaunchRoles() {		
		String[] activityLaunchRolesArray=new String[activityLaunchRolesMap.size()];		
		activityLaunchRolesMap.values().toArray(activityLaunchRolesArray);		
		return activityLaunchRolesArray;	
	}

	@Override
	public boolean setActivityLaunchParticipants(String[] participantNames) {
		for(String participantName:participantNames){
			if(activityLaunchParticipantsMap.containsKey(participantName)){
				activityLaunchParticipantsMap.remove(participantName);				
			}
			activityLaunchParticipantsMap.put(participantName, participantName);			
		}
		return true;		
	}

	@Override
	public boolean addActivityLaunchParticipant(String participantName) {
		if(activityLaunchParticipantsMap.containsKey(participantName)){
			activityLaunchParticipantsMap.remove(participantName);				
		}
		activityLaunchParticipantsMap.put(participantName, participantName);
		return true;
	}

	@Override
	public boolean removeActivityLaunchParticipant(String participantName) {
		if(activityLaunchParticipantsMap.containsKey(participantName)){
			activityLaunchParticipantsMap.remove(participantName);	
			return true;
		}else{
			return false;
		}	
	}

	@Override
	public String[] getActivityLaunchParticipants() {
		String[] activityLaunchParticipantsArray=new String[activityLaunchParticipantsMap.size()];		
		activityLaunchParticipantsMap.values().toArray(activityLaunchParticipantsArray);		
		return activityLaunchParticipantsArray;
	}
	
	@Override
	public String getActivityDescription() {
		return activityDescription;
	}
	
	@Override
	public boolean setActivityDescription(String activityDescription) {
		this.activityDescription = activityDescription;
		return true;
	}

	@Override
	public String[] getActivityCategories() {
		String[] activityCategoryArray=new String[activityCategoriesMap.size()];		
		activityCategoriesMap.values().toArray(activityCategoryArray);		
		return activityCategoryArray;
	}

	@Override
	public boolean setActivityCategories(String[] activityCategories) {		
		for(String activityCategory:activityCategories){
			if(activityCategoriesMap.containsKey(activityCategory)){
				activityCategoriesMap.remove(activityCategory);				
			}
			activityCategoriesMap.put(activityCategory, activityCategory);			
		}
		return true;
	}

	@Override
	public boolean addActivityCategory(String activityCategory) {
		if(activityCategoriesMap.containsKey(activityCategory)){
			activityCategoriesMap.remove(activityCategory);				
		}
		activityCategoriesMap.put(activityCategory, activityCategory);
		return true;
	}

	@Override
	public boolean removeActivityCategory(String activityCategory) {
		if(activityCategoriesMap.containsKey(activityCategory)){
			activityCategoriesMap.remove(activityCategory);	
			return true;
		}else{
			return false;
		}	
	}

	@Override
	public ActivityStepDefinition[] getDefinedSteps() throws ActivityEngineActivityException, ActivityEngineRuntimeException {
		if(this.activitySpaceName==null||this.activityType==null){
			throw new ActivityEngineRuntimeException();
		}
		try {
		 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
		 	List<ProcessStepDefinition> processStepDefinList=targetProcessSpace.getProcessStepsInfoByDefinitionName(this.activityType);
		 	if(processStepDefinList!=null){
		 		ActivityStepDefinition[] stepDefinitionArray=new ActivityStepDefinition[processStepDefinList.size()];
		 			for(int i=0;i<processStepDefinList.size();i++){
		 				ActivityStepDefinition currentStepDefinition=new ActivityStepDefinition();
	            		currentStepDefinition.setStepId(processStepDefinList.get(i).getStepId());
	            		currentStepDefinition.setStepName(processStepDefinList.get(i).getStepName());
	            		currentStepDefinition.setStepDescription(processStepDefinList.get(i).getStepDescription());
	            		stepDefinitionArray[i]=currentStepDefinition;
	            	}
	            	return stepDefinitionArray;
	            }
	        } catch (ProcessRepositoryRuntimeException e) {
	            e.printStackTrace();
	            throw new ActivityEngineActivityException();
	        }
		return null;
	}
	
	@Override
	public ActivityStepDefinition[] getDefinedSteps(int activityTypeDefinitionVersion) throws ActivityEngineActivityException, ActivityEngineRuntimeException {
		if(this.activitySpaceName==null||this.activityType==null){
			throw new ActivityEngineRuntimeException();
		}
		try {
		 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
		 	List<ProcessStepDefinition> processStepDefinList=targetProcessSpace.getProcessStepsInfoByDefinitionName(this.activityType,activityTypeDefinitionVersion);
		 	if(processStepDefinList!=null){
		 		ActivityStepDefinition[] stepDefinitionArray=new ActivityStepDefinition[processStepDefinList.size()];
		 			for(int i=0;i<processStepDefinList.size();i++){
		 				ActivityStepDefinition currentStepDefinition=new ActivityStepDefinition();
	            		currentStepDefinition.setStepId(processStepDefinList.get(i).getStepId());
	            		currentStepDefinition.setStepName(processStepDefinList.get(i).getStepName());
	            		currentStepDefinition.setStepDescription(processStepDefinList.get(i).getStepDescription());
	            		stepDefinitionArray[i]=currentStepDefinition;
	            	}
	            	return stepDefinitionArray;
	            }
	        } catch (ProcessRepositoryRuntimeException e) {
	            e.printStackTrace();
	            throw new ActivityEngineActivityException();
	        }
		return null;
	}

	@Override
	public InputStream getDefinitionFlowDiagram() throws ActivityEngineRuntimeException {
		if(this.activitySpaceName==null||this.activityType==null){
			throw new ActivityEngineRuntimeException();
		}
		try {
		 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
		 	return targetProcessSpace.getProcessDefinitionFlowDiagram(this.activityType);
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
	    }
	}
	
	@Override
	public InputStream getDefinitionFlowDiagram(int activityTypeDefinitionVersion) throws ActivityEngineRuntimeException {
		if(this.activitySpaceName==null||this.activityType==null){
			throw new ActivityEngineRuntimeException();
		}
		try {
		 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
		 	return targetProcessSpace.getProcessDefinitionFlowDiagram(this.activityType,activityTypeDefinitionVersion);
		} catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
	    }
	}
	
	@Override
	public InputStream getDefinitionFlowXML() throws ActivityEngineRuntimeException {		
		if(this.activitySpaceName!=null&&this.activityType!=null){
			try {
			 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
			 	return targetProcessSpace.getProcessDefinitionFile(this.activityType);
			} catch (ProcessRepositoryRuntimeException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
		    }
		}
		return null;
	}
	
	@Override
	public InputStream getDefinitionFlowXML(int activityTypeDefinitionVersion) throws ActivityEngineRuntimeException {		
		if(this.activitySpaceName!=null&&this.activityType!=null){
			try {
			 	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
			 	return targetProcessSpace.getProcessDefinitionFile(this.activityType,activityTypeDefinitionVersion);
			} catch (ProcessRepositoryRuntimeException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
		    }
		}
		return null;
	}

	@Override
	public long getMetaConfigurationVersion() {
		return this.metaConfigurationVersion;
	}	
	
	public void setMetaConfigurationVersion(long configVersion) {
		this.metaConfigurationVersion=configVersion;
	}	
}