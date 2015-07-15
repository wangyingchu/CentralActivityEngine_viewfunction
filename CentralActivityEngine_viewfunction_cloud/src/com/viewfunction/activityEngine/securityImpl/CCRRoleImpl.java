package com.viewfunction.activityEngine.securityImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.activityView.commonImpl.CCRCustomStructureImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
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

public class CCRRoleImpl implements Serializable,Role{	
	private static final long serialVersionUID = -53034139069088533L;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	private String activitySpaceName;
	private String roleName;
	private String description;
	private String displayName;
	
	private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}		
	}
	
	public CCRRoleImpl(String activitySpaceName,String roleName){
		this.activitySpaceName=activitySpaceName;
		this.roleName=roleName;
	}

	@Override
	public String getActivitySpaceName() {		
		return activitySpaceName;
	}

	@Override
	public String getRoleName() {		
		return roleName;
	}

	@Override
	public String getDescription() {		
		return description;
	}

	@Override
	public String getDisplayName() {		
		return displayName;
	}	

	@Override
	public void setDescription(String desc) {
		this.description=desc;		
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName=displayName;		
	}

	@Override
	public boolean addParticipant(String participantName) throws ActivityEngineRuntimeException {
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			if(currentRoleObj==null){
				throw new ActivityEngineRuntimeException();
			}				
			BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
			BaseContentObject targetParticipantObj=participantDefineNode.getSubContentObject(participantName);
			if(targetParticipantObj==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject role_PtContainer=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer);			
			if(role_PtContainer.getSubLinkContentObject(participantName)!=null){
				return false;
			}		
			role_PtContainer.addSubLinkContentObject(participantName, targetParticipantObj, true);
			targetParticipantObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer)
				.addSubContentObject(this.roleName, null, true);			
			return true;
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}		
	}

	@Override
	public boolean removeParticipant(String participantName) throws ActivityEngineRuntimeException {
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			if(currentRoleObj==null){
				throw new ActivityEngineRuntimeException();
			}				
			BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
			BaseContentObject targetParticipantObj=participantDefineNode.getSubContentObject(participantName);
			if(targetParticipantObj==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject role_PtContainer=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer);
			if(role_PtContainer.getSubLinkContentObject(participantName)==null){
				return false;
			}else{
				targetParticipantObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer)
					.removeSubContentObject(this.roleName, true);
				role_PtContainer.removeSubLinkContentObject(participantName, true);
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
	public boolean containParticipant(String participantName) throws ActivityEngineRuntimeException {
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			if(currentRoleObj==null){
				throw new ActivityEngineRuntimeException();
			}		
			BaseContentObject role_PtContainer=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer);
			if(role_PtContainer.getSubLinkContentObject(participantName)!=null){
				return true;
			}else{
				return false;
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}			
	}
	
	@Override
	public Participant[] getParticipants() throws ActivityEngineRuntimeException{
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			if(currentRoleObj==null){
				throw new ActivityEngineRuntimeException();
			}		
			BaseContentObject role_PtContainer=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer);
			if(role_PtContainer.getSubLinkContentObjectsCount()==0){
				return null;
			}else{				
				Participant[] participantArray=new Participant[new Long(role_PtContainer.getSubLinkContentObjectsCount()).intValue()];
				List<BaseContentObject> participantList=role_PtContainer.getSubLinkContentObjects(null);
				BaseContentObject currentparticipantObj=null;
				for(int i=0;i<participantList.size();i++){					
					currentparticipantObj=participantList.get(i);					
					String participantType=currentparticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_participantType).getPropertyValue().toString();						
					Participant currentParticipant=ActivityComponentFactory.createParticipant(currentparticipantObj.getContentObjectName(),participantType,activitySpaceName);					
					if(currentparticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName)!=null){
						currentParticipant.setDisplayName(currentparticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName).getPropertyValue().toString());					
					}
					participantArray[i]=currentParticipant;
				}				
				return participantArray;
			}
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public RoleQueue[] getRelatedRoleQueues() throws ActivityEngineRuntimeException {
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			if(currentRoleObj==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject roleQuereRefObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue);
			if(roleQuereRefObj==null||roleQuereRefObj.getSubContentObjectsCount()==0){
				return null;
			}else{
				List<BaseContentObject> roleQueueListObj=roleQuereRefObj.getSubContentObjects(null);
				RoleQueue[] roleQueueArr=new RoleQueue[roleQueueListObj.size()];				
				BaseContentObject roleQueueRelatedRoleContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);				
				for(int i=0;i<roleQueueListObj.size();i++){
					BaseContentObject currentBaseContentObject=roleQueueListObj.get(i);
					String roleQueueName=currentBaseContentObject.getContentObjectName();
					BaseContentObject roleQueueObj=roleQueueRelatedRoleContainerObj.getSubContentObject(roleQueueName);
					ContentObjectProperty roleQueueDisplayNamePro=roleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);					
					String roelQueueDisplayName=roleQueueDisplayNamePro!=null?roleQueueDisplayNamePro.getPropertyValue().toString():null;
					ContentObjectProperty roleQueueDescPro=roleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
					String roelQueueDesc=roleQueueDescPro!=null?roleQueueDescPro.getPropertyValue().toString():null;
					RoleQueue _RoleQueue1=ActivityComponentFactory.createRoleQueue(roleQueueName, this.activitySpaceName, roelQueueDisplayName, roelQueueDesc);					
					roleQueueArr[i]=_RoleQueue1;
				}
				return roleQueueArr;
			}		
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}	
	}

	@Override
	public boolean addInRoleQueue(String queueName) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue).getSubContentObject(queueName);
			if(roleQueueDefineObj==null){
				throw new ActivityEngineActivityException();				
			}			
			BaseContentObject roleConainerObj=roleQueueDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles);				
			if(roleConainerObj!=null&&roleConainerObj.getSubLinkContentObject(this.roleName)!=null){
				return false;
			}else{
				if(roleConainerObj==null){
					roleConainerObj=roleQueueDefineObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles,null,false);	
				}
				BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
				BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);				
				roleConainerObj.addSubLinkContentObject(this.roleName, currentRoleObj, false);	
				BaseContentObject roleQueueRefObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue);
				if(roleQueueRefObj==null){
					roleQueueRefObj=currentRoleObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue, null, false);					
				}
				roleQueueRefObj.addSubContentObject(queueName, null, false);
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
	public boolean removeFromRoleQueue(String queueName) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue).getSubContentObject(queueName);
			if(roleQueueDefineObj==null){
				throw new ActivityEngineActivityException();				
			}
			BaseContentObject roleConainerObj=roleQueueDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles);				
			if(roleConainerObj==null||roleConainerObj.getSubLinkContentObject(this.roleName)==null){
				return false;
			}else{
				roleConainerObj.removeSubLinkContentObject(this.roleName, false);
				BaseContentObject currentRoleObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(this.roleName);			
				currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue).removeSubContentObject(queueName, false);		
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);				
			BaseContentObject roleCustomAttributesContainerObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(roleCustomAttributesContainerObj==null){				
				roleCustomAttributesContainerObj=currentRoleObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=roleCustomAttributesContainerObj.getProperty(customAttribute.getAttributeName());
			if(targetAttributeProperty!=null){
				throw new ActivityEngineDataException();					
			}else{
				ContentObjectProperty resultCustomAttribute=roleCustomAttributesContainerObj.addProperty(customAttribute.getAttributeName(), customAttribute.getAttributeValue(), false);	
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);
			BaseContentObject roleCustomAttributesContainerObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(roleCustomAttributesContainerObj==null){				
				roleCustomAttributesContainerObj=currentRoleObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=roleCustomAttributesContainerObj.getProperty(customAttribute.getAttributeName());				
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
				ContentObjectProperty resultCustomAttribute=roleCustomAttributesContainerObj.updateProperty(newValueProperty, false);					
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);			
			BaseContentObject roleCustomAttributesContainerObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(roleCustomAttributesContainerObj==null){				
				roleCustomAttributesContainerObj=currentRoleObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
			}			
			ContentObjectProperty targetAttributeProperty=roleCustomAttributesContainerObj.getProperty(attributeName);				
			if(targetAttributeProperty==null){
				throw new ActivityEngineDataException();					
			}else{
				boolean deleteResult=roleCustomAttributesContainerObj.removeProperty(attributeName, false);				
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);						
			BaseContentObject roleCustomAttributesContainerObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(roleCustomAttributesContainerObj==null){							
				return null;
			}			
			ContentObjectProperty targetAttributeProperty=roleCustomAttributesContainerObj.getProperty(attributeName);				
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
			BaseContentObject roleDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);			
			BaseContentObject currentRoleObj=roleDefineNode.getSubContentObject(this.roleName);							
			BaseContentObject roleCustomAttributesContainerObj=currentRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
			if(roleCustomAttributesContainerObj==null){							
				return customAttribute;
			}			
			List<ContentObjectProperty> customAttributesPropList=roleCustomAttributesContainerObj.getProperties();
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
			BaseContentObject roleInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(this.roleName);			
			BaseContentObject roleCustomStructuresContainerObj=roleInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(roleCustomStructuresContainerObj==null){							
				return customeStructuresList;
			}			
			List<BaseContentObject> subCustomStructuresList=roleCustomStructuresContainerObj.getSubContentObjects(null);
			for(BaseContentObject currentStructureBaseContentObject:subCustomStructuresList){
				if(!currentStructureBaseContentObject.getContentObjectName().equals(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){							
					JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)roleInfoRootContainerObj;					
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
			BaseContentObject roleInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(this.roleName);			
			BaseContentObject roleCustomStructuresContainerObj=roleInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(roleCustomStructuresContainerObj==null){							
				return null;
			}						
			BaseContentObject currentStructureBaseContentObject=roleCustomStructuresContainerObj.getSubContentObject(structureName);			
			if(currentStructureBaseContentObject==null){
				return null;
			}else{
				JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)roleInfoRootContainerObj;					
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
			BaseContentObject roleInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(this.roleName);			
			BaseContentObject roleCustomStructuresContainerObj=roleInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(roleCustomStructuresContainerObj==null){							
				roleCustomStructuresContainerObj=roleInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);	
			}					
			BaseContentObject targetContentObject=roleCustomStructuresContainerObj.getSubContentObject(structureName);
			if(targetContentObject!=null){
				return false;
			}else{
				targetContentObject=roleCustomStructuresContainerObj.addSubContentObject(structureName, null, false);
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
			BaseContentObject roletInfoRootContainerObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).getSubContentObject(this.roleName);			
			BaseContentObject roleCustomStructuresContainerObj=roletInfoRootContainerObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);				
			if(roleCustomStructuresContainerObj==null){							
				roleCustomStructuresContainerObj=roletInfoRootContainerObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);	
				return false;
			}					
			BaseContentObject targetContentObject=roleCustomStructuresContainerObj.getSubContentObject(structureName);
			if(targetContentObject==null){
				return false;
			}else{
				boolean deleteResult=roleCustomStructuresContainerObj.removeSubContentObject(structureName, false);
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
		String roleFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_RoleContentStore+"/"+this.roleName+"/";
		return roleFolderFullPath;
	}
}