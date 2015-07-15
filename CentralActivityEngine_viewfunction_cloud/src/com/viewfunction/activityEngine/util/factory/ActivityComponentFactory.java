package com.viewfunction.activityEngine.util.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivitySpaceImpl;
import com.viewfunction.activityEngine.activityBureauImpl.CCRBusinessActivityDefinitionImpl;
import com.viewfunction.activityEngine.activityBureauImpl.CCR_CPRBusinessActivityImpl;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.activityView.common.ParticipantTask;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRActivityDataImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRCustomAttributeImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRCustomStructureImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRDataFieldDefinitionImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.activityView.commonImpl.ParticipantTaskImpl;
import com.viewfunction.activityEngine.activityViewImpl.CCR_CPRRoleQueueImpl;
import com.viewfunction.activityEngine.activityViewImpl.CCR_CPRRosterImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.extension.ActivityEventType;
import com.viewfunction.activityEngine.extension.ActivitySpaceEventListenerInstance;
import com.viewfunction.activityEngine.extension.SpaceEventListenersLoader;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.securityImpl.CCR_CPRParticipantImpl;
import com.viewfunction.activityEngine.securityImpl.CCRRoleImpl;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.contentBureauImpl.JCRContentObjectImpl;
import com.viewfunction.contentRepository.util.PerportyHandler;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;

public class ActivityComponentFactory {
	
	private static Map<String,Map<String,List<String>>> spaceActivityEventListenerRegister=new HashMap<String,Map<String,List<String>>>();
	
	private static boolean ableToRegisterSpaceActivityEventListener=true;

	public static Participant createParticipant(String participantName,String participantType,String activitySpaceName){		
		CCR_CPRParticipantImpl participant=new CCR_CPRParticipantImpl(participantName,participantType,activitySpaceName);
		return participant;
	}
	
	public static Role createRole(String activitySpaceName,String roleName){
		CCRRoleImpl _CCRRoleImpl=new CCRRoleImpl(activitySpaceName,roleName);
		return _CCRRoleImpl;
	}
	
	public static Roster createRoster(String activitySpaceName,String rosterName){
		CCR_CPRRosterImpl _CCRRosterImpl=new CCR_CPRRosterImpl(activitySpaceName,rosterName);
		return _CCRRosterImpl;
	}
	
	public static RoleQueue createRoleQueue(String queueName,String activitySpaceName,String displayName,String descTexe){		
		CCR_CPRRoleQueueImpl _CCR_CPRRoleQueueImpl=new CCR_CPRRoleQueueImpl(queueName,activitySpaceName);
		_CCR_CPRRoleQueueImpl.setDisplayName(displayName);
		_CCR_CPRRoleQueueImpl.setDescription(descTexe);		
		return _CCR_CPRRoleQueueImpl;
	}
	
	public static ActivityStep createActivityStep(String activityType){
		CCR_CPRActivityStepImpl _CCR_CPRActivityStepImpl=new CCR_CPRActivityStepImpl(activityType);
		return _CCR_CPRActivityStepImpl;
	}
	
	public static DataFieldDefinition cteateDataFieldDefinition(String fieldName,int fieldType,boolean isArrayField){
		CCRDataFieldDefinitionImpl _CCRDataFieldDefinitionImpl=new CCRDataFieldDefinitionImpl(fieldName,fieldType,isArrayField);
		return _CCRDataFieldDefinitionImpl;		
	}
	
	public static ActivityData createActivityData(DataFieldDefinition dataFieldDefinition,Object dataValue){
		CCRActivityDataImpl _CCRActivityDataImpl=new CCRActivityDataImpl(dataFieldDefinition);
		_CCRActivityDataImpl.setDatFieldValue(dataValue);
		 return _CCRActivityDataImpl;
	}
	
	public static BusinessActivity createBusinessActivity(String activityId,String activitySpaceName,String activityType){
		return new CCR_CPRBusinessActivityImpl(activityId,activitySpaceName,activityType);
	}
	
	public static ParticipantTask createParticipantTask(ActivityStep activityStep){
		return new ParticipantTaskImpl(activityStep);
	}
	
	public static BusinessActivityDefinition createBusinessActivityDefinition(String activityType,String activitySpaceName,String[] exposedSteps){
		CCRBusinessActivityDefinitionImpl _CCRBusinessActivityDefinitionImpl;
		if(exposedSteps==null){
			_CCRBusinessActivityDefinitionImpl=new CCRBusinessActivityDefinitionImpl(activityType,activitySpaceName);
		}else{
			_CCRBusinessActivityDefinitionImpl=new CCRBusinessActivityDefinitionImpl(activityType,activitySpaceName,exposedSteps);
		}		
		return _CCRBusinessActivityDefinitionImpl;
	}
	
	public static ActivitySpace createActivitySpace(String activitySpaceName) throws ActivityEngineException{
		String BUILDIN_ADMINISTRATOR_ACCOUNT;
		String BUILDIN__ADMINISTRATOR_ACCOUNT_PWD;
		ContentSpace metaDataContentSpace = null;
		ContentSpace activityCS=null;
		try {					
			List<String> currentContentSpaceList= ContentComponentFactory.getRegisteredContentSpace();
			boolean alreadyInited=false;
			for(String spaceName:currentContentSpaceList){
				if(spaceName.equals(CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE)){
					alreadyInited=true;
				}
			}
			if(!alreadyInited){
				ContentComponentFactory.createContentSpace(CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			}			
			activityCS=ContentComponentFactory.createContentSpace(activitySpaceName);
			if(activityCS==null){
				return null;
			}				
			//Need sleep more then 1 second to wait session sync data status.this is new change for Jackrabbit V3 OAK session management feature
			try {
				Thread.sleep(1500);
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
			BUILDIN__ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);
			
			activityCS=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, activitySpaceName);
			RootContentObject activityContentStoreRoot=ContentComponentFactory.createRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore);
			activityCS.addRootContentObject(activityContentStoreRoot);
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_SpaceContentStore, null, false);			
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleContentStore, null, false);
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ParticipantContentStore, null, false);			
			
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);				
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				activitySpaceDefineObject=ContentComponentFactory.createRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);						
				metaDataContentSpace.addRootContentObject(activitySpaceDefineObject);
				activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			}			
			BaseContentObject newActivitySpaceDefineObject=activitySpaceDefineObject.addSubContentObject(activitySpaceName, null, false);			
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role, null, false);			
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue, null, false);
			ActivitySpace activitySpace=getActivitySpace(activitySpaceName);			
			return activitySpace;
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineException();
		}finally{
			if(metaDataContentSpace!=null){
				metaDataContentSpace.closeContentSpace();
			}			
			if(activityCS!=null){
				activityCS.closeContentSpace();
			}
		}		
	}
	
	public static ActivitySpace createInitActivitySpace(String activitySpaceName) throws ActivityEngineException{
		String BUILDIN_ADMINISTRATOR_ACCOUNT;
		String BUILDIN__ADMINISTRATOR_ACCOUNT_PWD;
		ContentSpace metaDataContentSpace = null;
		ContentSpace activityCS=null;
		try {			
			ContentComponentFactory.createContentSpace(CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);			
			activityCS=ContentComponentFactory.createContentSpace(activitySpaceName);
			if(activityCS==null){
				return null;
			}				
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
			BUILDIN__ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);
			
			activityCS=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, activitySpaceName);
			RootContentObject activityContentStoreRoot=ContentComponentFactory.createRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore);
			activityCS.addRootContentObject(activityContentStoreRoot);
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_SpaceContentStore, null, false);			
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleContentStore, null, false);
			activityContentStoreRoot.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ParticipantContentStore, null, false);			
			
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);				
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				activitySpaceDefineObject=ContentComponentFactory.createRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);						
				metaDataContentSpace.addRootContentObject(activitySpaceDefineObject);
				activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			}			
			BaseContentObject newActivitySpaceDefineObject=activitySpaceDefineObject.addSubContentObject(activitySpaceName, null, false);			
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role, null, false);			
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition, null, false);
			newActivitySpaceDefineObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue, null, false);
			ActivitySpace activitySpace=getActivitySpace(activitySpaceName);			
			return activitySpace;
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineException();
		}finally{
			if(metaDataContentSpace!=null){
				metaDataContentSpace.closeContentSpace();
			}			
			if(activityCS!=null){
				activityCS.closeContentSpace();
			}
		}		
	}
	
	public static ActivitySpace getActivitySpace(String activitySpaceName){
		CCRActivitySpaceImpl _CCRActivitySpaceImpl=new CCRActivitySpaceImpl(activitySpaceName);
		registerSpaceActivityEventListeners(activitySpaceName);
		return _CCRActivitySpaceImpl;
	}
	
	public static ActivitySpace[] getActivitySpaces() throws ActivityEngineException{
		String BUILDIN_ADMINISTRATOR_ACCOUNT;
		String BUILDIN__ADMINISTRATOR_ACCOUNT_PWD;
		ContentSpace metaDataContentSpace = null;
		try {					
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
			BUILDIN__ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);			
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				return null;
			}		
			
			List<BaseContentObject> activitySpaceObjList=activitySpaceDefineObject.getSubContentObjects(null);			
			ActivitySpace[] activitySpaceArray=new ActivitySpace[activitySpaceObjList.size()];			
			for(int i=0;i<activitySpaceObjList.size();i++){
				String activityName=activitySpaceObjList.get(i).getContentObjectName();
				activitySpaceArray[i]=getActivitySpace(activityName);			
			}
			return activitySpaceArray;			
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineException();
		}finally{
			if(metaDataContentSpace!=null){
				metaDataContentSpace.closeContentSpace();
			}
		}		
	}
	
	public static CustomAttribute createCustomAttribute(){
		return new CCRCustomAttributeImpl();
	}
	
	public static CustomStructure getCustomStructure(String activitySpaceName,String structureId) throws ActivityEngineException{
		String BUILDIN_ADMINISTRATOR_ACCOUNT;
		String BUILDIN__ADMINISTRATOR_ACCOUNT_PWD;
		ContentSpace targetContentSpace = null;
		try {					
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
			BUILDIN__ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);
			targetContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD,CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);			
			BaseContentObject storageContentObject=targetContentSpace.getContentObjectByAbsPath(structureId);
			
			BaseContentObject parentContentObject=storageContentObject.getParentContentObject();			
			if(parentContentObject!=null&&parentContentObject.getContentObjectName().equals(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
				parentContentObject=parentContentObject.getParentContentObject();				
			}
			JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)parentContentObject;
			String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();
			
			CCRCustomStructureImpl targetCustomStructure=new CCRCustomStructureImpl(storageContentObject.getContentObjectName(),customStructureParentPath,activitySpaceName);						
			targetCustomStructure.setStorageContentObject(storageContentObject);
			targetCustomStructure.setStructureId(structureId);				
			return targetCustomStructure;
						
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineException();
		} catch (RepositoryException e) {
			throw new ActivityEngineException();
		}finally{
			if(targetContentSpace!=null){
				targetContentSpace.closeContentSpace();
			}
		}		
	}
	
	public static void registerSpaceActivityEventListeners(String activitySpaceName){
		Map<String,List<String>> spaceEventListenerRegisterMap=spaceActivityEventListenerRegister.get(activitySpaceName);
		if(spaceEventListenerRegisterMap!=null){
			return;
		}
		if(ableToRegisterSpaceActivityEventListener){
			String activityEventListenersLoaderClassName = 
					com.viewfunction.activityEngine.util.PerportyHandler.getPerportyValue(com.viewfunction.activityEngine.util.PerportyHandler.SPACEEVENT_LISTENERSLOADER_IMPL_CLASSNAME);
			if(activityEventListenersLoaderClassName==null){
				ableToRegisterSpaceActivityEventListener=false;
			}else{
				try {
					SpaceEventListenersLoader spaceEventListenersLoader=(SpaceEventListenersLoader)Class.forName(activityEventListenersLoaderClassName).newInstance();
					spaceEventListenerRegisterMap=new HashMap<String,List<String>>();
					spaceActivityEventListenerRegister.put(activitySpaceName, spaceEventListenerRegisterMap);
					ActivitySpace targetActivitySpace=getActivitySpace(activitySpaceName);
					List<ActivitySpaceEventListenerInstance> listenerList=spaceEventListenersLoader.loadSpaceEventListeners();
					if(listenerList!=null){
						for(ActivitySpaceEventListenerInstance activitySpaceEventListenerInstance:listenerList){
							if(validateEventListenerExistsion(activitySpaceEventListenerInstance.getListenerKey(),activitySpaceEventListenerInstance.getEventType(),spaceEventListenerRegisterMap)){
								targetActivitySpace.registerActivityEventListener(activitySpaceEventListenerInstance.getEventType(), activitySpaceEventListenerInstance.getActivitySpaceEventListener());
							}
						}	
					}					
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ActivityEngineProcessException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static boolean validateEventListenerExistsion(String listenerKey,ActivityEventType eventType,Map<String,List<String>> spaceEventListenerRegisterMap){
		switch(eventType){
			case ACTIVITYSTEP_ASSIGNED:
				List<String> _ACTIVITYSTEP_ASSIGNED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_ASSIGNED);
				if(_ACTIVITYSTEP_ASSIGNED_ListenerList==null){
					_ACTIVITYSTEP_ASSIGNED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_ASSIGNED, _ACTIVITYSTEP_ASSIGNED_ListenerList);
				}
				if(_ACTIVITYSTEP_ASSIGNED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEP_ASSIGNED_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEP_CREATED:
				List<String> _ACTIVITYSTEP_CREATED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_CREATED);
				if(_ACTIVITYSTEP_CREATED_ListenerList==null){
					_ACTIVITYSTEP_CREATED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_CREATED, _ACTIVITYSTEP_CREATED_ListenerList);
				}
				if(_ACTIVITYSTEP_CREATED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEP_CREATED_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEP_COMPLETED:
				List<String> _ACTIVITYSTEP_COMPLETED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_COMPLETED);
				if(_ACTIVITYSTEP_COMPLETED_ListenerList==null){
					_ACTIVITYSTEP_COMPLETED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_COMPLETED, _ACTIVITYSTEP_COMPLETED_ListenerList);
				}
				if(_ACTIVITYSTEP_COMPLETED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEP_COMPLETED_ListenerList.add(listenerKey);
					return true;
				}
			case BUSINESSACTIVITY_COMPLETED:
				List<String> _BUSINESSACTIVITY_COMPLETED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_COMPLETED);
				if(_BUSINESSACTIVITY_COMPLETED_ListenerList==null){
					_BUSINESSACTIVITY_COMPLETED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_COMPLETED, _BUSINESSACTIVITY_COMPLETED_ListenerList);
				}
				if(_BUSINESSACTIVITY_COMPLETED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_BUSINESSACTIVITY_COMPLETED_ListenerList.add(listenerKey);
					return true;
				}
			case BUSINESSACTIVITY_CANCELLED:
				List<String> _BUSINESSACTIVITY_CANCELLED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_CANCELLED);
				if(_BUSINESSACTIVITY_CANCELLED_ListenerList==null){
					_BUSINESSACTIVITY_CANCELLED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_CANCELLED, _BUSINESSACTIVITY_CANCELLED_ListenerList);
				}
				if(_BUSINESSACTIVITY_CANCELLED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_BUSINESSACTIVITY_CANCELLED_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEPMONITOR_SUCCESS:
				List<String> _ACTIVITYSTEPMONITOR_SUCCESS_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_SUCCESS);
				if(_ACTIVITYSTEPMONITOR_SUCCESS_ListenerList==null){
					_ACTIVITYSTEPMONITOR_SUCCESS_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_SUCCESS, _ACTIVITYSTEPMONITOR_SUCCESS_ListenerList);
				}
				if(_ACTIVITYSTEPMONITOR_SUCCESS_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEPMONITOR_SUCCESS_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEPMONITOR_FAILURE:
				List<String> _ACTIVITYSTEPMONITOR_FAILURE_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_FAILURE);
				if(_ACTIVITYSTEPMONITOR_FAILURE_ListenerList==null){
					_ACTIVITYSTEPMONITOR_FAILURE_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_FAILURE, _ACTIVITYSTEPMONITOR_FAILURE_ListenerList);
				}
				if(_ACTIVITYSTEPMONITOR_FAILURE_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEPMONITOR_FAILURE_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEPMONITOR_RETRY_DECREMENTED:
				List<String> _ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED);
				if(_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList==null){
					_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED, _ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList);
				}
				if(_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED_ListenerList.add(listenerKey);
					return true;
				}
			case ACTIVITYSTEPMONITOR_CANCELED:
				List<String> _ACTIVITYSTEPMONITOR_CANCELED_ListenerList=spaceEventListenerRegisterMap.get(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_CANCELED);
				if(_ACTIVITYSTEPMONITOR_CANCELED_ListenerList==null){
					_ACTIVITYSTEPMONITOR_CANCELED_ListenerList=new ArrayList<String>();
					spaceEventListenerRegisterMap.put(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_CANCELED, _ACTIVITYSTEPMONITOR_CANCELED_ListenerList);
				}
				if(_ACTIVITYSTEPMONITOR_CANCELED_ListenerList.contains(listenerKey)){
					return false;
				}else{
					_ACTIVITYSTEPMONITOR_CANCELED_ListenerList.add(listenerKey);
					return true;
				}
		}
		return false;
	}
}