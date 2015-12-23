package com.viewfunction.activityEngine.activityViewImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.Queue;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.activityBureauImpl.CCR_CPRBusinessActivityImpl;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentObjectProperty;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.util.PerportyHandler;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCR_CPRRoleQueueImpl extends CCRQueueImpl implements RoleQueue,Serializable{	
	private static final long serialVersionUID = -6873362714554088358L;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	public CCR_CPRRoleQueueImpl(String queueName, String activitySpaceName) {
		super(queueName, activitySpaceName);		
	}

	@Override
	public Role[] getRelatedRoles() throws ActivityEngineRuntimeException {
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
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(super.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue).getSubContentObject(super.queueName);
			BaseContentObject roleConainerObj=roleQueueDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles);	
			if(roleConainerObj==null||roleConainerObj.getSubLinkContentObjectsCount()==0){
				return null;
			}else{
				Role[] relatedRoleArr=new Role[(int)roleConainerObj.getSubLinkContentObjectsCount()];
				List<BaseContentObject> roleList=roleConainerObj.getSubLinkContentObjects(null);
				for(int i=0;i<roleList.size();i++){
					BaseContentObject currentObj=roleList.get(i);
					Role currentRole=ActivityComponentFactory.createRole(super.activitySpaceName, currentObj.getContentObjectName());					
					ContentObjectProperty descPro=currentObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc);
					if(descPro!=null){
						currentRole.setDescription(descPro.getPropertyValue().toString());
					}
					ContentObjectProperty displayName=currentObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName);
					if(displayName!=null){
						currentRole.setDisplayName(displayName.getPropertyValue().toString());
					}
					relatedRoleArr[i]=currentRole;					
				}
				return relatedRoleArr;				
			}
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}		
	}

	@Override
	public boolean addRole(String roleName) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(super.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject roleDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject roleObject=roleDefineObj.getSubContentObject(roleName);
			if(roleObject==null){
				throw new ActivityEngineActivityException();				
			}			
			BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue).getSubContentObject(super.queueName);			
			BaseContentObject roleConainerObj=roleQueueDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles);			
			if(roleConainerObj!=null&&roleConainerObj.getSubLinkContentObject(roleObject.getContentObjectName())!=null){				
				return false;
			}else{
				if(roleConainerObj==null){
					roleConainerObj=roleQueueDefineObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles, null, false);
				}			
				roleConainerObj.addSubLinkContentObject(roleName, roleObject, false);				
				BaseContentObject roleQuereRefObj=roleObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue);
				if(roleQuereRefObj==null){
					roleQuereRefObj=roleObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue, null, false);					
				}
				roleQuereRefObj.addSubContentObject(super.queueName, null, false);				
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
	public boolean removeRole(String roleName) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(super.activitySpaceName);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject roleDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject roleObject=roleDefineObj.getSubContentObject(roleName);
			if(roleObject==null){
				throw new ActivityEngineActivityException();				
			}
			BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue).getSubContentObject(super.queueName);
			BaseContentObject roleConainerObj=roleQueueDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles);
			if(roleConainerObj==null||roleConainerObj.getSubLinkContentObject(roleName)==null){
				return false;
			}else{
				roleConainerObj.removeSubLinkContentObject(roleName, false);
				roleObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue).removeSubContentObject(super.queueName, false);
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
	public List<ActivityStep> fetchActivitySteps() throws ActivityEngineProcessException, ActivityEngineRuntimeException {		
		try {
			ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(super.activitySpaceName);			
			Role[] relatedRoles= getRelatedRoles();			
			List<ActivityStep> activityStepList=new ArrayList<ActivityStep>();
			if(relatedRoles==null){
				return activityStepList;
			}
			for(Role currentRole:relatedRoles){				
				List<ProcessStep> processStepList=targetProcessSpace.getProcessStepsByRole(currentRole.getRoleName());
				for(ProcessStep curentProcessStep:processStepList){											
					ProcessObject processObject=targetProcessSpace.getProcessObjectById(curentProcessStep.getProcessObjectId());					
					String activityType=targetProcessSpace.getProcessNameByDefinitionId(curentProcessStep.getProcessDefinitionId());					
					BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), super.activitySpaceName, activityType);	
					((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);					
					CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);
					currentActivityStep.setBusinessActivity(currentBusinessActivity);
					currentActivityStep.setProcessStep(curentProcessStep);
					currentActivityStep.setRelatedRole(currentRole);
					activityStepList.add(currentActivityStep);					
				}
			}			
			return activityStepList;
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}	
	}	

	@Override
	public String getQueueType() {		
		return Queue.QUEUE_TYPE_USERQUEUE;
	}
	
	private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}		
	}
}