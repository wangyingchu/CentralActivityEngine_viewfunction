package com.viewfunction.activityEngine.securityImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.activityView.common.ParticipantTask;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRCustomStructureImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.activityBureauImpl.CCR_CPRBusinessActivityImpl;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentObjectProperty;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.contentBureauImpl.JCRContentObjectImpl;
import com.viewfunction.contentRepository.util.PerportyHandler;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCR_CPRParticipantImpl implements Serializable,Participant{	
	private static final long serialVersionUID = 5012867048124782225L;
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	private String displayName;
	private String activitySpaceName;
	private String participantName;
	private String participantType;

	public CCR_CPRParticipantImpl(String participantName,String participantType,String activitySpaceName){
		this.participantName=participantName;		
		this.participantType=participantType;
		this.activitySpaceName=activitySpaceName;
		//added in Content Repository of LDAP server		
		/*
		if(0==1){
			//login ContentRepository to get user or group information,if not found throw ActivityEngineLoginException
			throw new ActivityEngineLoginException();
		}*/	
	}
	
	public CCR_CPRParticipantImpl(String participantName,String participantType){
		this.participantName=participantName;		
		this.participantType=participantType;		
	}
	
	private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}		
	}
	
	public String getActivitySpaceName(){		
		return this.activitySpaceName;
	}
	
	public void setDisplayName(String theDisplayName){		
		this.displayName=theDisplayName;
	}
	
	public String getDisplayName(){		
		if(this.displayName!=null){
			return this.displayName;
		}else{
			return this.participantName;
		}		
	}
	
	public String getParticipantName(){				
		return this.participantName;
	}
	
	public boolean isGroup(){
		if(this.participantType.equals(Participant.PARTICIPANT_TYPE_GROUP)){
			return true;
		}else{
			return false;
		}
	}

	@Override
	public Role[] getRoles() throws ActivityEngineRuntimeException {
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject belongedRoleContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant)
					.getSubContentObject(this.participantName).getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer);						
			if(belongedRoleContainerObj.getSubContentObjectsCount()==0){
				return null;
			}else{
				Role[] roleArray=new Role[new Long(belongedRoleContainerObj.getSubContentObjectsCount()).intValue()];				
				List<BaseContentObject> roleList=belongedRoleContainerObj.getSubContentObjects(null);
				BaseContentObject currentRoleNameObj=null;	
				BaseContentObject currentRoleObject=null;
				for(int i=0;i<roleList.size();i++){
					currentRoleNameObj=roleList.get(i);											
					Role currentRole=ActivityComponentFactory.createRole(activitySpaceName, currentRoleNameObj.getContentObjectName());					
					currentRoleObject=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(currentRoleNameObj.getContentObjectName());					
					if(currentRoleObject.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc)!=null){
						currentRole.setDescription(currentRoleObject.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc).getPropertyValue().toString());						
					}
					if(currentRoleObject.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName)!=null){
						currentRole.setDisplayName(currentRoleObject.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName).getPropertyValue().toString());						
					}
					roleArray[i]=currentRole;
				}				
				return roleArray;				
			}		
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}
	
	@Override
	public void setActivitySpaceName(String activitySpaceName) {
		this.activitySpaceName = activitySpaceName;
	}	

	@Override
	public boolean isInRole(String roleName) throws ActivityEngineRuntimeException {
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject belongedRoleContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant)
					.getSubContentObject(this.participantName).getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer);			
			if(belongedRoleContainerObj.getSubContentObject(roleName)==null){
				return false;
			}else{
				return true;
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}		
	}

	@Override
	public List<ParticipantTask> fetchParticipantTasks() throws ActivityEngineProcessException {
		try {
			ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);	
			List<ParticipantTask> participantTaskList=new ArrayList<ParticipantTask>();							
			List<ProcessStep> processStepList=targetProcessSpace.getProcessStepsByParticipant(this.participantName);				
			for(ProcessStep curentProcessStep:processStepList){		
				ProcessObject processObject=targetProcessSpace.getProcessObjectById(curentProcessStep.getProcessObjectId());					
				String activityType=targetProcessSpace.getProcessNameByDefinitionId(curentProcessStep.getProcessDefinitionId());					
				BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);	
				((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);					
				CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);				
				currentActivityStep.setBusinessActivity(currentBusinessActivity);
				currentActivityStep.setProcessStep(curentProcessStep);	
				ParticipantTask currentParticipantTask=ActivityComponentFactory.createParticipantTask(currentActivityStep);
				participantTaskList.add(currentParticipantTask);
			}						
			return participantTaskList;
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public boolean addCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}		
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomAttributesContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(participantCustomAttributesContainerObj==null){				
				participantCustomAttributesContainerObj=participantInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=participantCustomAttributesContainerObj.getProperty(customAttribute.getAttributeName());
			if(targetAttributeProperty!=null){
				throw new ActivityEngineDataException();					
			}else{
				ContentObjectProperty resultCustomAttribute=participantCustomAttributesContainerObj.addProperty(customAttribute.getAttributeName(), customAttribute.getAttributeValue(), false);	
				if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
					return true;
				}else{
					return false;
				}				
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public boolean updateCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}		
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomAttributesContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(participantCustomAttributesContainerObj==null){				
				participantCustomAttributesContainerObj=participantInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=participantCustomAttributesContainerObj.getProperty(customAttribute.getAttributeName());				
			if(targetAttributeProperty==null){
				throw new ActivityEngineDataException();					
			}else{
				if(targetAttributeProperty.getPropertyType()!=customAttribute.getAttributeType()){
					throw new ActivityEngineDataException();					
				}
				if(targetAttributeProperty.isMultiple()!=customAttribute.isArrayAttribute()){
					throw new ActivityEngineDataException();
				}				
				ContentObjectProperty newValueProperty=ContentComponentFactory.createContentObjectProperty();
				newValueProperty.setMultiple(customAttribute.isArrayAttribute());
				newValueProperty.setPropertyName(customAttribute.getAttributeName());
				newValueProperty.setPropertyType(customAttribute.getAttributeType());
				newValueProperty.setPropertyValue(customAttribute.getAttributeValue());				
				ContentObjectProperty resultCustomAttribute=participantCustomAttributesContainerObj.updateProperty(newValueProperty, false);					
				if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
					return true;
				}else{
					return false;
				}				
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public boolean deleteCustomAttribute(String attributeName) throws ActivityEngineRuntimeException, ActivityEngineDataException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomAttributesContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(participantCustomAttributesContainerObj==null){				
				participantCustomAttributesContainerObj=participantInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=participantCustomAttributesContainerObj.getProperty(attributeName);				
			if(targetAttributeProperty==null){
				throw new ActivityEngineDataException();					
			}else{
				boolean deleteResult=participantCustomAttributesContainerObj.removeProperty(attributeName, false);				
				return deleteResult;				
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public CustomAttribute getCustomAttribute(String attributeName) throws ActivityEngineRuntimeException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomAttributesContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(participantCustomAttributesContainerObj==null){							
				return null;
			}			
			ContentObjectProperty targetAttributeProperty=participantCustomAttributesContainerObj.getProperty(attributeName);				
			if(targetAttributeProperty==null){
				return null;					
			}else{				
				CustomAttribute targetCustomAttribute=ActivityComponentFactory.createCustomAttribute();				
				targetCustomAttribute.setArrayAttribute(targetAttributeProperty.isMultiple());
				targetCustomAttribute.setAttributeName(targetAttributeProperty.getPropertyName());
				targetCustomAttribute.setAttributeType(targetAttributeProperty.getPropertyType());
				targetCustomAttribute.setAttributeValue(targetAttributeProperty.getPropertyValue());				
				return targetCustomAttribute;				
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public List<CustomAttribute> getCustomAttributes() throws ActivityEngineRuntimeException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		List<CustomAttribute> customAttribute=new ArrayList<CustomAttribute>();
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomAttributesContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(participantCustomAttributesContainerObj==null){							
				return customAttribute;
			}			
			List<ContentObjectProperty> customAttributesPropList=participantCustomAttributesContainerObj.getProperties();
			for(ContentObjectProperty currentContentObjectProperty:customAttributesPropList){
				CustomAttribute targetCustomAttribute=ActivityComponentFactory.createCustomAttribute();				
				targetCustomAttribute.setArrayAttribute(currentContentObjectProperty.isMultiple());
				targetCustomAttribute.setAttributeName(currentContentObjectProperty.getPropertyName());
				targetCustomAttribute.setAttributeType(currentContentObjectProperty.getPropertyType());
				targetCustomAttribute.setAttributeValue(currentContentObjectProperty.getPropertyValue());				
				customAttribute.add(targetCustomAttribute);				
			}			
			return customAttribute;
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public List<CustomStructure> getSubCustomStructures() throws ActivityEngineRuntimeException, ActivityEngineDataException {
		List<CustomStructure> customeStructuresList=new ArrayList<CustomStructure>();
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		ContentSpace metaDataContentSpace = null;
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}		
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomStructuresContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(participantCustomStructuresContainerObj==null){							
				return customeStructuresList;
			}
			List<BaseContentObject> subCustomStructuresList=participantCustomStructuresContainerObj.getSubContentObjects(null);
			for(BaseContentObject currentStructureBaseContentObject:subCustomStructuresList){
				if(!currentStructureBaseContentObject.getContentObjectName().equals(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){							
					JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)participantInfoRootContainerObj;					
					String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();						
					CCRCustomStructureImpl currentCustomStructure=new CCRCustomStructureImpl(currentStructureBaseContentObject.getContentObjectName(),customStructureParentPath,this.activitySpaceName);						
					currentCustomStructure.setStorageContentObject(currentStructureBaseContentObject);						
					customeStructuresList.add(currentCustomStructure);						
				}					
			}
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		} catch (RepositoryException e) {				
			e.printStackTrace();
			throw new ActivityEngineDataException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}			
		return customeStructuresList;
	}

	@Override
	public CustomStructure getSubCustomStructure(String structureName) throws ActivityEngineRuntimeException, ActivityEngineDataException {
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return null;
		}
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}			
		ContentSpace metaDataContentSpace = null;
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}				
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomStructuresContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(participantCustomStructuresContainerObj==null){							
				return null;
			}						
			BaseContentObject currentStructureBaseContentObject=participantCustomStructuresContainerObj.getSubContentObject(structureName);			
			if(currentStructureBaseContentObject==null){
				return null;
			}else{
				JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)participantInfoRootContainerObj;					
				String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();						
				CCRCustomStructureImpl currentCustomStructure=new CCRCustomStructureImpl(currentStructureBaseContentObject.getContentObjectName(),customStructureParentPath,this.activitySpaceName);						
				currentCustomStructure.setStorageContentObject(currentStructureBaseContentObject);
				return currentCustomStructure;
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		} catch (RepositoryException e) {				
			e.printStackTrace();
			throw new ActivityEngineDataException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public boolean addSubCustomStructure(String structureName) throws ActivityEngineRuntimeException {
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return false;
		}
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		ContentSpace metaDataContentSpace = null;
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}				
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomStructuresContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(participantCustomStructuresContainerObj==null){							
				participantCustomStructuresContainerObj=participantInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);	
			}					
			BaseContentObject targetContentObject=participantCustomStructuresContainerObj.getSubContentObject(structureName);
			if(targetContentObject!=null){
				return false;
			}else{
				targetContentObject=participantCustomStructuresContainerObj.addSubContentObject(structureName, null, false);
				targetContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);
				return true;
			}					
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		} finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public boolean deleteSubCustomStructure(String structureName) throws ActivityEngineRuntimeException {
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return false;
		}
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}	
		ContentSpace metaDataContentSpace = null;
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}				
			BaseContentObject participantInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant).getSubContentObject(this.participantName);			
			BaseContentObject participantCustomStructuresContainerObj=participantInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(participantCustomStructuresContainerObj==null){							
				participantCustomStructuresContainerObj=participantInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);	
				return false;
			}					
			BaseContentObject targetContentObject=participantCustomStructuresContainerObj.getSubContentObject(structureName);
			if(targetContentObject==null){
				return false;
			}else{
				boolean deleteResult=participantCustomStructuresContainerObj.removeSubContentObject(structureName, false);
				return deleteResult;
			}					
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		} finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public String getDocumentsFolderPath() {
		String participantFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_ParticipantContentStore+"/"+this.participantName+"/";
		return participantFolderFullPath;
	}
}