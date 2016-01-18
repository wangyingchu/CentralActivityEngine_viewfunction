package com.viewfunction.activityEngine.activityBureauImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.PropertyType;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.ProcessQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.ActivityStepDefinition;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.extension.ActivityEventType;
import com.viewfunction.activityEngine.extension.ActivitySpaceEventListener;
import com.viewfunction.activityEngine.security.Participant;
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
import com.viewfunction.contentRepository.util.helper.ContentOperationHelper;
import com.viewfunction.contentRepository.util.helper.TextContent;
import com.viewfunction.processRepository.exception.ProcessRepositoryDeploymentException;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.extension.ProcessSpaceEventType;

import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.processBureauImpl.ActivitiHistoricProcessStepImpl;
import com.viewfunction.processRepository.processBureauImpl.ActivitiProcessStepImpl;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCRActivitySpaceImpl implements ActivitySpace,Serializable{
    private static final long serialVersionUID = 8884036178781460104L;
    private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
    private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;

    private String activitySpaceName;

    public CCRActivitySpaceImpl(String activitySpaceName){
        this.activitySpaceName=activitySpaceName;
    }

    private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
        if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
            BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
        }
        if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
            BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);
        }
    }

    @Override
    public Roster[] getRosters() throws ActivityEngineRuntimeException{
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
            BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);
            if(rosterDefineObj.getSubContentObjectsCount()==0){
                return null;
            }else{
                Roster[] rosterArray=new Roster[new Long(rosterDefineObj.getSubContentObjectsCount()).intValue()];
                List<BaseContentObject> rosterList=rosterDefineObj.getSubContentObjects(null);
                BaseContentObject currentRosterObj=null;
                for(int i=0;i<rosterList.size();i++){
                    currentRosterObj=rosterList.get(i);

                    Roster currentRoster=ActivityComponentFactory.createRoster(activitySpaceName, currentRosterObj.getContentObjectName());
                    if(currentRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc)!=null){
                        currentRoster.setDescription(currentRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc).getPropertyValue().toString());
                    }
                    if(currentRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName)!=null){
                        currentRoster.setDisplayName(currentRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName).getPropertyValue().toString());
                    }
                    rosterArray[i]=currentRoster;
                }
                return rosterArray;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public Roster getRoster(String rosterName) throws ActivityEngineRuntimeException{
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
            BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);
            BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(rosterName);
            if(targetRosterObj==null){
                return null;
            }else{
                Roster targtRoster=ActivityComponentFactory.createRoster(activitySpaceName, targetRosterObj.getContentObjectName());
                if(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc)!=null){
                    targtRoster.setDescription(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc).getPropertyValue().toString());
                }
                if(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName)!=null){
                    targtRoster.setDisplayName(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName).getPropertyValue().toString());
                }
                return targtRoster;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public Roster updateRoster(String rosterName, String rosterDisplayName,String rosterDescription) throws ActivityEngineRuntimeException {
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
            BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);
            BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(rosterName);
            if(targetRosterObj==null){
                return null;
            }else{
                String orgRosterDisplayName=targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName).getPropertyValue().toString();
                if(!orgRosterDisplayName.equals(rosterDisplayName)){
                    ContentObjectProperty displayNameProperty=ContentComponentFactory.createContentObjectProperty();
                    displayNameProperty.setMultiple(false);
                    displayNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName);
                    displayNameProperty.setPropertyType(PropertyType.STRING);
                    displayNameProperty.setPropertyValue(rosterDisplayName);
                    targetRosterObj.updateProperty(displayNameProperty, false);
                }
                String orgRosterDesc=targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc).getPropertyValue().toString();
                if(!orgRosterDesc.equals(rosterDescription)){
                    ContentObjectProperty roleDescProperty=ContentComponentFactory.createContentObjectProperty();
                    roleDescProperty.setMultiple(false);
                    roleDescProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc);
                    roleDescProperty.setPropertyType(PropertyType.STRING);
                    roleDescProperty.setPropertyValue(rosterDescription);
                    targetRosterObj.updateProperty(roleDescProperty, false);
                }
                Roster targtRoster=ActivityComponentFactory.createRoster(activitySpaceName, targetRosterObj.getContentObjectName());
                if(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc)!=null){
                    targtRoster.setDescription(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc).getPropertyValue().toString());
                }
                if(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName)!=null){
                    targtRoster.setDisplayName(targetRosterObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName).getPropertyValue().toString());
                }
                return targtRoster;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean addRoster(Roster roster) throws ActivityEngineRuntimeException {
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
            BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);
            if(rosterDefineObj.getSubContentObject(roster.getRosterName())!=null){
                return false;
            }else{
                List<ContentObjectProperty> paramLst=null;
                if(roster.getDescription()!=null||roster.getDisplayName()!=null){
                    paramLst=new ArrayList<ContentObjectProperty>();
                    if(roster.getDescription()!=null){
                        ContentObjectProperty descProperty=ContentComponentFactory.createContentObjectProperty();
                        descProperty.setMultiple(false);
                        descProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_desc);
                        descProperty.setPropertyType(PropertyType.STRING);
                        descProperty.setPropertyValue(roster.getDescription());
                        paramLst.add(descProperty);
                    }
                    if(roster.getDisplayName()!=null){
                        ContentObjectProperty dsnProperty=ContentComponentFactory.createContentObjectProperty();
                        dsnProperty.setMultiple(false);
                        dsnProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Roster_displayName);
                        dsnProperty.setPropertyType(PropertyType.STRING);
                        dsnProperty.setPropertyValue(roster.getDisplayName());
                        paramLst.add(dsnProperty);
                    }
                }
                BaseContentObject newRosterObject=rosterDefineObj.addSubContentObject(roster.getRosterName(), paramLst, true);
                if(newRosterObject!=null){
                    return true;
                }
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
        return false;
    }

    @Override
    public boolean removeRoster(String rosterName) throws ActivityEngineRuntimeException {
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
            BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);
            BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(rosterName);
            if(targetRosterObj==null){
                return false;
            }else{
                BaseContentObject targetRosterObjActivityContainerObj=targetRosterObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_containedActivityType);
                if(targetRosterObjActivityContainerObj!=null&&targetRosterObjActivityContainerObj.getSubLinkContentObjectsCount()!=0){
                    List<BaseContentObject> activityList=targetRosterObjActivityContainerObj.getSubLinkContentObjects(null);
                    for(BaseContentObject baseContentObject:activityList){
                        baseContentObject.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_belongsToRoster, false);
                    }
                }
                return rosterDefineObj.removeSubContentObject(targetRosterObj.getContentObjectName(), true);
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public Role[] getRoles()  throws ActivityEngineRuntimeException {
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
            if(roleDefineNode.getSubContentObjectsCount()==0){
                return null;
            }else{
                Role[] roleArray=new Role[new Long(roleDefineNode.getSubContentObjectsCount()).intValue()];
                List<BaseContentObject> roleList=roleDefineNode.getSubContentObjects(null);
                BaseContentObject currentRoleObj=null;
                for(int i=0;i<roleList.size();i++){
                    currentRoleObj=roleList.get(i);
                    Role currentRole=ActivityComponentFactory.createRole(activitySpaceName, currentRoleObj.getContentObjectName());
                    if(currentRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc)!=null){
                        currentRole.setDescription(currentRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc).getPropertyValue().toString());
                    }
                    if(currentRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName)!=null){
                        currentRole.setDisplayName(currentRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName).getPropertyValue().toString());
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
    public Role getRole(String roleName) throws ActivityEngineRuntimeException  {
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
            BaseContentObject targetRoleObj=roleDefineNode.getSubContentObject(roleName);
            if(targetRoleObj==null){
                return null;
            }else{
                Role currentRole=ActivityComponentFactory.createRole(activitySpaceName, targetRoleObj.getContentObjectName());
                if(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc)!=null){
                    currentRole.setDescription(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc).getPropertyValue().toString());
                }
                if(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName)!=null){
                    currentRole.setDisplayName(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName).getPropertyValue().toString());
                }
                return currentRole;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public Role updateRole(String roleName, String roleDisplayName,	String roleDescription) throws ActivityEngineRuntimeException {
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
            BaseContentObject targetRoleObj=roleDefineNode.getSubContentObject(roleName);
            if(targetRoleObj==null){
                return null;
            }else{
                String orgRoleDisplayName=targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName).getPropertyValue().toString();
                if(!orgRoleDisplayName.equals(roleDisplayName)){
                    ContentObjectProperty displayNameProperty=ContentComponentFactory.createContentObjectProperty();
                    displayNameProperty.setMultiple(false);
                    displayNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName);
                    displayNameProperty.setPropertyType(PropertyType.STRING);
                    displayNameProperty.setPropertyValue(roleDisplayName);
                    targetRoleObj.updateProperty(displayNameProperty, false);
                }
                String orgRoleDesc=targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc).getPropertyValue().toString();
                if(!orgRoleDesc.equals(roleDescription)){
                    ContentObjectProperty roleDescProperty=ContentComponentFactory.createContentObjectProperty();
                    roleDescProperty.setMultiple(false);
                    roleDescProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc);
                    roleDescProperty.setPropertyType(PropertyType.STRING);
                    roleDescProperty.setPropertyValue(roleDescription);
                    targetRoleObj.updateProperty(roleDescProperty, false);
                }
                Role currentRole=ActivityComponentFactory.createRole(activitySpaceName, targetRoleObj.getContentObjectName());
                if(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc)!=null){
                    currentRole.setDescription(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc).getPropertyValue().toString());
                }
                if(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName)!=null){
                    currentRole.setDisplayName(targetRoleObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName).getPropertyValue().toString());
                }
                return currentRole;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean addRole(Role role) throws ActivityEngineRuntimeException {
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
            BaseContentObject roleDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role);
            if(roleDefineObj.getSubContentObject(role.getRoleName())!=null){
                return false;
            }else{
                List<ContentObjectProperty> paramLst=null;
                if(role.getDescription()!=null||role.getDisplayName()!=null){
                    paramLst=new ArrayList<ContentObjectProperty>();
                    if(role.getDescription()!=null){
                        ContentObjectProperty descProperty=ContentComponentFactory.createContentObjectProperty();
                        descProperty.setMultiple(false);
                        descProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Role_desc);
                        descProperty.setPropertyType(PropertyType.STRING);
                        descProperty.setPropertyValue(role.getDescription());
                        paramLst.add(descProperty);
                    }
                    if(role.getDisplayName()!=null){
                        ContentObjectProperty dsnProperty=ContentComponentFactory.createContentObjectProperty();
                        dsnProperty.setMultiple(false);
                        dsnProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Role_displayName);
                        dsnProperty.setPropertyType(PropertyType.STRING);
                        dsnProperty.setPropertyValue(role.getDisplayName());
                        paramLst.add(dsnProperty);
                    }
                }
                BaseContentObject newRoleObject=roleDefineObj.addSubContentObject(role.getRoleName(), paramLst, true);
                newRoleObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer, null, false);
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
    public boolean removeRole(String roleName) throws ActivityEngineRuntimeException  {
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
            BaseContentObject targetRoleObj=roleDefineNode.getSubContentObject(roleName);
            if(targetRoleObj==null){
                return false;
            }else{
                BaseContentObject containedParticipantsObj=targetRoleObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer);
                if(containedParticipantsObj.getSubLinkContentObjectsCount()!=0){
                    List<BaseContentObject> roleList=containedParticipantsObj.getSubLinkContentObjects(null);
                    for(BaseContentObject currentParticipant:roleList){
                        currentParticipant.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer).
                                removeSubContentObject(roleName, true);
                    }
                }
                return roleDefineNode.removeSubContentObject(targetRoleObj.getContentObjectName(), true);
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public Participant[] getParticipants() throws ActivityEngineRuntimeException {
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
            BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
            if(participantDefineNode.getSubContentObjectsCount()==0){
                return null;
            }else{
                Participant[] participantArray=new Participant[new Long(participantDefineNode.getSubContentObjectsCount()).intValue()];
                List<BaseContentObject> participantList=participantDefineNode.getSubContentObjects(null);
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
    public Participant getParticipant(String participantName) throws ActivityEngineRuntimeException {
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
            BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
            BaseContentObject currentParticipantObj=participantDefineNode.getSubContentObject(participantName);
            if(currentParticipantObj==null){
                return null;
            }else{
                String participantType=currentParticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_participantType).getPropertyValue().toString();
                Participant currentParticipant=ActivityComponentFactory.createParticipant(currentParticipantObj.getContentObjectName(),participantType,activitySpaceName);
                if(currentParticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName)!=null){
                    currentParticipant.setDisplayName(currentParticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName).getPropertyValue().toString());
                }
                return currentParticipant;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }


    @Override
    public Participant updateParticipant(String participantName,String participantNewDisplayName, String participantNewType,String[] roles) throws ActivityEngineRuntimeException {
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
            BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
            BaseContentObject currentParticipantObj=participantDefineNode.getSubContentObject(participantName);
            if(currentParticipantObj==null){
                return null;
            }else{
                String participantType=currentParticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_participantType).getPropertyValue().toString();
                if(!participantType.equals(participantNewType)){
                    ContentObjectProperty groupProperty=ContentComponentFactory.createContentObjectProperty();
                    groupProperty.setMultiple(false);
                    groupProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_participantType);
                    groupProperty.setPropertyType(PropertyType.STRING);
                    groupProperty.setPropertyValue(participantNewType);
                    currentParticipantObj.updateProperty(groupProperty, false);
                }
                String participantDisplayName=currentParticipantObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName).getPropertyValue().toString();
                if(participantDisplayName==null||!participantDisplayName.equals(participantNewDisplayName)){
                    ContentObjectProperty dnProperty=ContentComponentFactory.createContentObjectProperty();
                    dnProperty.setMultiple(false);
                    dnProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName);
                    dnProperty.setPropertyType(PropertyType.STRING);
                    dnProperty.setPropertyValue(participantNewDisplayName);
                    currentParticipantObj.updateProperty(dnProperty, false);
                }
                Participant newParticipant=ActivityComponentFactory.createParticipant(participantName,participantNewType,activitySpaceName);
                newParticipant.setDisplayName(participantNewDisplayName);
                Role[] orginalRoleArray=newParticipant.getRoles();
                if(orginalRoleArray!=null){
                    for(Role role:orginalRoleArray){
                        role.removeParticipant(participantName);
                    }
                }
                if(roles!=null){
                    Role newRole=null;
                    for(String roleName:roles){
                        newRole=this.getRole(roleName);
                        newRole.addParticipant(participantName);
                    }
                }
                return newParticipant;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean addParticipant(Participant participant) throws ActivityEngineRuntimeException {
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
            BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
            if(participantDefineNode.getSubContentObject(participant.getParticipantName())!=null){
                return false;
            }else{
                List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                ContentObjectProperty groupProperty=ContentComponentFactory.createContentObjectProperty();
                groupProperty.setMultiple(false);
                groupProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_participantType);
                groupProperty.setPropertyType(PropertyType.STRING);
                if(participant.isGroup()){
                    groupProperty.setPropertyValue(Participant.PARTICIPANT_TYPE_GROUP);
                }else{
                    groupProperty.setPropertyValue(Participant.PARTICIPANT_TYPE_USER);
                }
                paramLst.add(groupProperty);

                if(participant.getDisplayName()!=null){
                    ContentObjectProperty dnProperty=ContentComponentFactory.createContentObjectProperty();
                    dnProperty.setMultiple(false);
                    dnProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_displayName);
                    dnProperty.setPropertyType(PropertyType.STRING);
                    dnProperty.setPropertyValue(participant.getDisplayName());
                    paramLst.add(dnProperty);
                }
                BaseContentObject newParticipantObject=participantDefineNode.addSubContentObject(participant.getParticipantName(), paramLst, true);
                newParticipantObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer, null, false);
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
    public boolean removeParticipant(String participantName) throws ActivityEngineRuntimeException{
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
            BaseContentObject participantDefineNode=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant);
            BaseContentObject currentParticipantObj=participantDefineNode.getSubContentObject(participantName);
            if(currentParticipantObj==null){
                return false;
            }else{
                BaseContentObject belongedRoleContainerObj=currentParticipantObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Participant_belongedRoleContainer);
                if(belongedRoleContainerObj.getSubContentObjectsCount()!=0){
                    List<BaseContentObject> roleList=belongedRoleContainerObj.getSubContentObjects(null);
                    BaseContentObject currentRoleNameObj=null;
                    for(int i=0;i<roleList.size();i++){
                        currentRoleNameObj=roleList.get(i);
                        activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role).
                                getSubContentObject(currentRoleNameObj.getContentObjectName()).
                                getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_participantContainer).removeSubLinkContentObject(participantName, true);
                    }
                }
                return participantDefineNode.removeSubContentObject(participantName, true);
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean addBusinessActivityDefinition(BusinessActivityDefinition bd) throws ActivityEngineRuntimeException,ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {
        try {
            initContentRepositoryParameter();
        } catch (ContentReposityRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }
        String activityType=bd.getActivityType();
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            if(activityDefineObj.getSubContentObject(activityType)!=null){
                throw new ActivityEngineActivityException();
            }else{
                List<ContentObjectProperty> defineParamLst=new ArrayList<ContentObjectProperty>();
                ContentObjectProperty activityDefineStatusProperty=ContentComponentFactory.createContentObjectProperty();
                activityDefineStatusProperty.setMultiple(false);
                activityDefineStatusProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled);
                activityDefineStatusProperty.setPropertyType(PropertyType.BOOLEAN);
                activityDefineStatusProperty.setPropertyValue(true);
                defineParamLst.add(activityDefineStatusProperty);

                ContentObjectProperty exposedStepsProperty=ContentComponentFactory.createContentObjectProperty();
                exposedStepsProperty.setMultiple(true);
                exposedStepsProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps);
                exposedStepsProperty.setPropertyType(PropertyType.STRING);
                exposedStepsProperty.setPropertyValue(bd.getExposedSteps());
                defineParamLst.add(exposedStepsProperty);

                if(bd.getLaunchDecisionPointAttributeName()!=null){
                    ContentObjectProperty launchDecisionPointAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                    launchDecisionPointAttributeNameProperty.setMultiple(false);
                    launchDecisionPointAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName);
                    launchDecisionPointAttributeNameProperty.setPropertyType(PropertyType.STRING);
                    launchDecisionPointAttributeNameProperty.setPropertyValue(bd.getLaunchDecisionPointAttributeName());
                    defineParamLst.add(launchDecisionPointAttributeNameProperty);
                }
                if(bd.getLaunchDecisionPointChoiseList()!=null){
                    ContentObjectProperty launchDecisionPointChoiseListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchDecisionPointChoiseListProperty.setMultiple(true);
                    launchDecisionPointChoiseListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList);
                    launchDecisionPointChoiseListProperty.setPropertyType(PropertyType.STRING);
                    launchDecisionPointChoiseListProperty.setPropertyValue(bd.getLaunchDecisionPointChoiseList());
                    defineParamLst.add(launchDecisionPointChoiseListProperty);
                }
                if(bd.getLaunchProcessVariableList()!=null){
                    ContentObjectProperty launchProcessVariableListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchProcessVariableListProperty.setMultiple(true);
                    launchProcessVariableListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList);
                    launchProcessVariableListProperty.setPropertyType(PropertyType.STRING);
                    launchProcessVariableListProperty.setPropertyValue(bd.getLaunchProcessVariableList());
                    defineParamLst.add(launchProcessVariableListProperty);
                }
                if(bd.getLaunchUserIdentityAttributeName()!=null){
                    ContentObjectProperty launchUserIdentityAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                    launchUserIdentityAttributeNameProperty.setMultiple(false);
                    launchUserIdentityAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName);
                    launchUserIdentityAttributeNameProperty.setPropertyType(PropertyType.STRING);
                    launchUserIdentityAttributeNameProperty.setPropertyValue(bd.getLaunchUserIdentityAttributeName());
                    defineParamLst.add(launchUserIdentityAttributeNameProperty);
                }
                if(bd.getActivityLaunchParticipants()!=null){
                    ContentObjectProperty launchParticipantListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchParticipantListProperty.setMultiple(true);
                    launchParticipantListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants);
                    launchParticipantListProperty.setPropertyType(PropertyType.STRING);
                    launchParticipantListProperty.setPropertyValue(bd.getActivityLaunchParticipants());
                    defineParamLst.add(launchParticipantListProperty);
                }
                if(bd.getActivityLaunchRoles()!=null){
                    ContentObjectProperty launchRoleListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchRoleListProperty.setMultiple(true);
                    launchRoleListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles);
                    launchRoleListProperty.setPropertyType(PropertyType.STRING);
                    launchRoleListProperty.setPropertyValue(bd.getActivityLaunchRoles());
                    defineParamLst.add(launchRoleListProperty);
                }
                if(bd.getActivityCategories()!=null){
                    ContentObjectProperty activityCategoriesProperty=ContentComponentFactory.createContentObjectProperty();
                    activityCategoriesProperty.setMultiple(true);
                    activityCategoriesProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories);
                    activityCategoriesProperty.setPropertyType(PropertyType.STRING);
                    activityCategoriesProperty.setPropertyValue(bd.getActivityCategories());
                    defineParamLst.add(activityCategoriesProperty);
                }
                ContentObjectProperty activityDescriptionProperty=ContentComponentFactory.createContentObjectProperty();
                activityDescriptionProperty.setMultiple(false);
                activityDescriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description);
                activityDescriptionProperty.setPropertyType(PropertyType.STRING);
                if(bd.getActivityDescription()!=null){
                    activityDescriptionProperty.setPropertyValue(bd.getActivityDescription());
                }else{
                    activityDescriptionProperty.setPropertyValue("");
                }
                defineParamLst.add(activityDescriptionProperty);

                BaseContentObject activityObj=activityDefineObj.addSubContentObject(activityType, defineParamLst, true);
                BaseContentObject dataFieldObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields, null, false);
                BaseContentObject defineResourceObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource, null, false);

                Object definitionResource=bd.getDefinitionResource();
                String businessActivityDefineFileName=bd.getActivityType()+".bpmn20.xml";
                InputStream businessActivityDefineFileInputStream=(InputStream)definitionResource;
                try {
                    File activityProcessDefineFile=new File(businessActivityDefineFileName);
                    OutputStream out=new FileOutputStream(activityProcessDefineFile);
                    byte buf[]=new byte[1024];
                    int len;
                    while((len=businessActivityDefineFileInputStream.read(buf))>0){
                        out.write(buf,0,len);
                    }
                    out.close();
                    businessActivityDefineFileInputStream.close();
                    //add process define xml file in content repository
                    ContentOperationHelper coh=ContentComponentFactory.getContentOperationHelper();
                    boolean addProcessDefineResult=coh.addTextContent(defineResourceObj, activityProcessDefineFile, businessActivityDefineFileName, true);
                    if(!addProcessDefineResult){
                        throw new ActivityEngineDataException();
                    }
                    //add process define in process repository
                    ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
                    TextContent defineXMLContent=coh.getTextContent(defineResourceObj, businessActivityDefineFileName);
                    boolean addProcessResult=targetProcessSpace.addProcessDefinition(businessActivityDefineFileName,defineXMLContent.getContentInputStream());
                    if(!addProcessResult){
                        throw new ActivityEngineProcessException();
                    }
                    activityProcessDefineFile.delete();
                } catch (ProcessRepositoryRuntimeException e) {
                    e.printStackTrace();
                    throw new ActivityEngineProcessException();
                } catch (ProcessRepositoryDeploymentException e) {
                    e.printStackTrace();
                    throw new ActivityEngineProcessException();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    throw new ActivityEngineDataException();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new ActivityEngineDataException();
                }

                DataFieldDefinition[] dfdArray=bd.getActivityDataFields();
                if(dfdArray==null){
                    throw new ActivityEngineDataException();
                }
                for(DataFieldDefinition df:dfdArray){
                    List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                    ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                    fieldTypeProperty.setMultiple(false);
                    fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                    fieldTypeProperty.setPropertyType(PropertyType.LONG);
                    fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                    paramLst.add(fieldTypeProperty);

                    ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                    displayProperty.setMultiple(false);
                    displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                    displayProperty.setPropertyType(PropertyType.STRING);
                    displayProperty.setPropertyValue(df.getDisplayName());
                    paramLst.add(displayProperty);

                    ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                    descriptionProperty.setMultiple(false);
                    descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                    descriptionProperty.setPropertyType(PropertyType.STRING);
                    descriptionProperty.setPropertyValue(df.getDescription());
                    paramLst.add(descriptionProperty);

                    ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isArrayFieldProperty.setMultiple(false);
                    isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                    isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isArrayFieldProperty.setPropertyValue(df.isArrayField());
                    paramLst.add(isArrayFieldProperty);

                    ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isSystemFieldProperty.setMultiple(false);
                    isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                    isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isSystemFieldProperty.setPropertyValue(df.isSystemField());
                    paramLst.add(isSystemFieldProperty);

                    ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isMandatoryFieldProperty.setMultiple(false);
                    isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                    isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                    paramLst.add(isMandatoryFieldProperty);

                    ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isReadableFieldProperty.setMultiple(false);
                    isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                    isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isReadableFieldProperty.setPropertyValue(df.isReadableField());
                    paramLst.add(isReadableFieldProperty);

                    ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isWriteableFieldProperty.setMultiple(false);
                    isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                    isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                    paramLst.add(isWriteableFieldProperty);

                    dataFieldObj.addSubContentObject(df.getFieldName(), paramLst, false);
                }

                BaseContentObject stepsObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps, null, false);
                Map<String,DataFieldDefinition[]> stepsExposedDataFields=bd.getActivityStepsExposedDataField();

                if(stepsExposedDataFields!=null){
                    Set<String> stepNames=stepsExposedDataFields.keySet();
                    Iterator<String> stepNameItor=stepNames.iterator();
                    while(stepNameItor.hasNext()){
                        String stepName=stepNameItor.next();
                        if(!bd.containsExposedActivityStep(stepName)){
                            throw new ActivityEngineProcessException();
                        }
                        DataFieldDefinition[] dataFieldDefinArray=stepsExposedDataFields.get(stepName);
                        BaseContentObject currentStepdfd=stepsObj.addSubContentObject(stepName, null, false);

                        Map<String, String> stepRoleMap=bd.getActivityStepRoleMap();
                        String roleOfCurrentStep=stepRoleMap.get(stepName);
                        if(roleOfCurrentStep!=null){
                            currentStepdfd.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole, roleOfCurrentStep, false);
                        }

                        for(DataFieldDefinition df:dataFieldDefinArray){
                            List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                            ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                            fieldTypeProperty.setMultiple(false);
                            fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                            fieldTypeProperty.setPropertyType(PropertyType.LONG);
                            fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                            paramLst.add(fieldTypeProperty);

                            ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                            displayProperty.setMultiple(false);
                            displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                            displayProperty.setPropertyType(PropertyType.STRING);
                            displayProperty.setPropertyValue(df.getDisplayName());
                            paramLst.add(displayProperty);

                            ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                            descriptionProperty.setMultiple(false);
                            descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                            descriptionProperty.setPropertyType(PropertyType.STRING);
                            descriptionProperty.setPropertyValue(df.getDescription());
                            paramLst.add(descriptionProperty);

                            ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isArrayFieldProperty.setMultiple(false);
                            isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                            isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isArrayFieldProperty.setPropertyValue(df.isArrayField());
                            paramLst.add(isArrayFieldProperty);

                            ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isSystemFieldProperty.setMultiple(false);
                            isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                            isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isSystemFieldProperty.setPropertyValue(df.isSystemField());
                            paramLst.add(isSystemFieldProperty);

                            ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isMandatoryFieldProperty.setMultiple(false);
                            isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                            isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                            paramLst.add(isMandatoryFieldProperty);

                            ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isReadableFieldProperty.setMultiple(false);
                            isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                            isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isReadableFieldProperty.setPropertyValue(df.isReadableField());
                            paramLst.add(isReadableFieldProperty);

                            ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isWriteableFieldProperty.setMultiple(false);
                            isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                            isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                            paramLst.add(isWriteableFieldProperty);

                            currentStepdfd.addSubContentObject(df.getFieldName(), paramLst, false);
                        }
                    }
                }

                BaseContentObject launchPointMetaInfoObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPoint, null, false);
                BaseContentObject launchPointExposedDataFieldsObj=launchPointMetaInfoObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields, null, false);

                DataFieldDefinition[] launchPointExposedDataFields=bd.getLaunchPointExposedDataFields();
                if(launchPointExposedDataFields!=null){
                    for(DataFieldDefinition df:launchPointExposedDataFields){
                        List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                        ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                        fieldTypeProperty.setMultiple(false);
                        fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                        fieldTypeProperty.setPropertyType(PropertyType.LONG);
                        fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                        paramLst.add(fieldTypeProperty);

                        ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                        displayProperty.setMultiple(false);
                        displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                        displayProperty.setPropertyType(PropertyType.STRING);
                        displayProperty.setPropertyValue(df.getDisplayName());
                        paramLst.add(displayProperty);

                        ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                        descriptionProperty.setMultiple(false);
                        descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                        descriptionProperty.setPropertyType(PropertyType.STRING);
                        descriptionProperty.setPropertyValue(df.getDescription());
                        paramLst.add(descriptionProperty);

                        ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isArrayFieldProperty.setMultiple(false);
                        isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                        isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isArrayFieldProperty.setPropertyValue(df.isArrayField());
                        paramLst.add(isArrayFieldProperty);

                        ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isSystemFieldProperty.setMultiple(false);
                        isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                        isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isSystemFieldProperty.setPropertyValue(df.isSystemField());
                        paramLst.add(isSystemFieldProperty);

                        ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isMandatoryFieldProperty.setMultiple(false);
                        isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                        isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                        paramLst.add(isMandatoryFieldProperty);

                        ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isReadableFieldProperty.setMultiple(false);
                        isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                        isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isReadableFieldProperty.setPropertyValue(df.isReadableField());
                        paramLst.add(isReadableFieldProperty);

                        ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isWriteableFieldProperty.setMultiple(false);
                        isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                        isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                        paramLst.add(isWriteableFieldProperty);

                        launchPointExposedDataFieldsObj.addSubContentObject(df.getFieldName(), paramLst, false);
                    }
                }
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
	public boolean refreshBusinessActivityDefinitionWorkflow(String activityType, File workflowDefinitionFile)throws ActivityEngineRuntimeException,ActivityEngineActivityException,ActivityEngineProcessException,ActivityEngineDataException {
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            if(activityDefineObj.getSubContentObject(activityType)==null){
                throw new ActivityEngineActivityException();
            }
            BaseContentObject activityObj=activityDefineObj.getSubContentObject(activityType);
            if(activityObj==null){
                throw new ActivityEngineActivityException();
            }else{
            	ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            	boolean updateProcessResult=targetProcessSpace.updateProcessDefinition(activityType, new FileInputStream(workflowDefinitionFile));
            	
            	
            	/*
            	if(updateProcessResult){
            		//update definition resource File
            		activityObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource, false);
            		BaseContentObject defineResourceObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource, null, false);
                    
            		
            		
            		
            		String businessActivityDefineFileName=activityType+".bpmn20.xml";
                    //add process define xml file in content repository
                    ContentOperationHelper coh=ContentComponentFactory.getContentOperationHelper();
                    boolean addProcessDefineResult=coh.addTextContent(defineResourceObj, workflowDefinitionFile, businessActivityDefineFileName, true);
                    if(!addProcessDefineResult){
                        throw new ActivityEngineDataException();
                    }
                    
                    
                    
                    
                    
                    
                    return true;
            	}else{
            		return false;
            	}
            	*/
            	
            	return true;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        } catch (ProcessRepositoryRuntimeException e) {
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		} catch (ProcessRepositoryDeploymentException e) {
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
            metaDataContentSpace.closeContentSpace();
        }
    }
    
    @Override
    public boolean updateBusinessActivityDefinition(BusinessActivityDefinition bd) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {
        try {
            initContentRepositoryParameter();
        } catch (ContentReposityRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }
        String activityType=bd.getActivityType();
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            if(activityDefineObj.getSubContentObject(activityType)==null){
                throw new ActivityEngineActivityException();
            }else{
                BusinessActivityDefinition currentActivityDefinition=this.getBusinessActivityDefinition(activityType);
                String currentRoster=currentActivityDefinition.getRosterName();
                if(currentRoster!=null){
                    Roster currentRelatedRoster=this.getRoster(currentRoster);
                    currentRelatedRoster.removeActivityType(activityType);
                }
                BaseContentObject activityObj=activityDefineObj.getSubContentObject(activityType);
                List<ContentObjectProperty> orginalPropertes=activityObj.getProperties();
                for(ContentObjectProperty contentObjectProperty:orginalPropertes){
                    activityObj.removeProperty(contentObjectProperty.getPropertyName(), false);
                }
                activityObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields, false);
                activityObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps, false);
                activityObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPoint, false);

                if(bd.isDefinitionResourceUpdated()){
                    activityObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource, false);
                }

                List<ContentObjectProperty> defineParamLst=new ArrayList<ContentObjectProperty>();
                ContentObjectProperty activityDefineStatusProperty=ContentComponentFactory.createContentObjectProperty();
                activityDefineStatusProperty.setMultiple(false);
                activityDefineStatusProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled);
                activityDefineStatusProperty.setPropertyType(PropertyType.BOOLEAN);
                activityDefineStatusProperty.setPropertyValue(true);
                defineParamLst.add(activityDefineStatusProperty);

                ContentObjectProperty exposedStepsProperty=ContentComponentFactory.createContentObjectProperty();
                exposedStepsProperty.setMultiple(true);
                exposedStepsProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps);
                exposedStepsProperty.setPropertyType(PropertyType.STRING);
                exposedStepsProperty.setPropertyValue(bd.getExposedSteps());
                defineParamLst.add(exposedStepsProperty);

                if(bd.getLaunchDecisionPointAttributeName()!=null){
                    ContentObjectProperty launchDecisionPointAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                    launchDecisionPointAttributeNameProperty.setMultiple(false);
                    launchDecisionPointAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName);
                    launchDecisionPointAttributeNameProperty.setPropertyType(PropertyType.STRING);
                    launchDecisionPointAttributeNameProperty.setPropertyValue(bd.getLaunchDecisionPointAttributeName());
                    defineParamLst.add(launchDecisionPointAttributeNameProperty);
                }
                if(bd.getLaunchDecisionPointChoiseList()!=null){
                    ContentObjectProperty launchDecisionPointChoiseListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchDecisionPointChoiseListProperty.setMultiple(true);
                    launchDecisionPointChoiseListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList);
                    launchDecisionPointChoiseListProperty.setPropertyType(PropertyType.STRING);
                    launchDecisionPointChoiseListProperty.setPropertyValue(bd.getLaunchDecisionPointChoiseList());
                    defineParamLst.add(launchDecisionPointChoiseListProperty);
                }
                if(bd.getLaunchProcessVariableList()!=null){
                    ContentObjectProperty launchProcessVariableListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchProcessVariableListProperty.setMultiple(true);
                    launchProcessVariableListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList);
                    launchProcessVariableListProperty.setPropertyType(PropertyType.STRING);
                    launchProcessVariableListProperty.setPropertyValue(bd.getLaunchProcessVariableList());
                    defineParamLst.add(launchProcessVariableListProperty);
                }
                if(bd.getLaunchUserIdentityAttributeName()!=null){
                    ContentObjectProperty launchUserIdentityAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                    launchUserIdentityAttributeNameProperty.setMultiple(false);
                    launchUserIdentityAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName);
                    launchUserIdentityAttributeNameProperty.setPropertyType(PropertyType.STRING);
                    launchUserIdentityAttributeNameProperty.setPropertyValue(bd.getLaunchUserIdentityAttributeName());
                    defineParamLst.add(launchUserIdentityAttributeNameProperty);
                }
                if(bd.getActivityLaunchParticipants()!=null){
                    ContentObjectProperty launchParticipantListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchParticipantListProperty.setMultiple(true);
                    launchParticipantListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants);
                    launchParticipantListProperty.setPropertyType(PropertyType.STRING);
                    launchParticipantListProperty.setPropertyValue(bd.getActivityLaunchParticipants());
                    defineParamLst.add(launchParticipantListProperty);
                }
                if(bd.getActivityLaunchRoles()!=null){
                    ContentObjectProperty launchRoleListProperty=ContentComponentFactory.createContentObjectProperty();
                    launchRoleListProperty.setMultiple(true);
                    launchRoleListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles);
                    launchRoleListProperty.setPropertyType(PropertyType.STRING);
                    launchRoleListProperty.setPropertyValue(bd.getActivityLaunchRoles());
                    defineParamLst.add(launchRoleListProperty);
                }
                if(bd.getActivityCategories()!=null){
                    ContentObjectProperty activityCategoriesProperty=ContentComponentFactory.createContentObjectProperty();
                    activityCategoriesProperty.setMultiple(true);
                    activityCategoriesProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories);
                    activityCategoriesProperty.setPropertyType(PropertyType.STRING);
                    activityCategoriesProperty.setPropertyValue(bd.getActivityCategories());
                    defineParamLst.add(activityCategoriesProperty);
                }
                ContentObjectProperty activityDescriptionProperty=ContentComponentFactory.createContentObjectProperty();
                activityDescriptionProperty.setMultiple(false);
                activityDescriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description);
                activityDescriptionProperty.setPropertyType(PropertyType.STRING);
                if(bd.getActivityDescription()!=null){
                    activityDescriptionProperty.setPropertyValue(bd.getActivityDescription());
                }else{
                    activityDescriptionProperty.setPropertyValue("");
                }
                defineParamLst.add(activityDescriptionProperty);

                activityObj.addProperty(defineParamLst, false);

                if(bd.isDefinitionResourceUpdated()){
                    BaseContentObject defineResourceObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource, null, false);
                    Object definitionResource=bd.getDefinitionResource();
                    String businessActivityDefineFileName=bd.getActivityType()+".bpmn20.xml";
                    if(definitionResource instanceof InputStream){
                        InputStream businessActivityDefineFileInputStream=(InputStream)definitionResource;
                        try {
                            File temFile=File.createTempFile("apd_fle", null);
                            String tempDir=temFile.getAbsolutePath().replace(temFile.getName(), "");
                            temFile.delete();
                            File activityProcessDefineFile=new File(tempDir,businessActivityDefineFileName);
                            OutputStream out=new FileOutputStream(activityProcessDefineFile);
                            byte buf[]=new byte[1024];
                            int len;
                            while((len=businessActivityDefineFileInputStream.read(buf))>0){
                                out.write(buf,0,len);
                            }
                            out.close();
                            businessActivityDefineFileInputStream.close();
                            //add process define xml file in content repository
                            ContentOperationHelper coh=ContentComponentFactory.getContentOperationHelper();
                            boolean addProcessDefineResult=coh.addTextContent(defineResourceObj, activityProcessDefineFile, businessActivityDefineFileName, true);
                            if(!addProcessDefineResult){
                                throw new ActivityEngineDataException();
                            }
                            //add process define in process repository
                            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
                            TextContent defineXMLContent=coh.getTextContent(defineResourceObj, businessActivityDefineFileName);
                            boolean addProcessResult=targetProcessSpace.addProcessDefinition(businessActivityDefineFileName,defineXMLContent.getContentInputStream());
                            if(!addProcessResult){
                                throw new ActivityEngineProcessException();
                            }
                            activityProcessDefineFile.delete();
                        } catch (ProcessRepositoryRuntimeException e) {
                            e.printStackTrace();
                            throw new ActivityEngineProcessException();
                        } catch (ProcessRepositoryDeploymentException e) {
                            e.printStackTrace();
                            throw new ActivityEngineProcessException();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                            throw new ActivityEngineDataException();
                        } catch (IOException e) {
                            e.printStackTrace();
                            throw new ActivityEngineDataException();
                        }
                    }else if(definitionResource instanceof File){
                        File activityProcessDefineFile=(File)definitionResource;
                        //add process define xml file in content repository
                        ContentOperationHelper coh=ContentComponentFactory.getContentOperationHelper();
                        boolean addProcessDefineResult=coh.addTextContent(defineResourceObj, activityProcessDefineFile, businessActivityDefineFileName, true);
                        if(!addProcessDefineResult){
                            throw new ActivityEngineDataException();
                        }
                        //add process define in process repository
                        ProcessSpace targetProcessSpace;
                        try {
                            targetProcessSpace = ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
                            TextContent defineXMLContent=coh.getTextContent(defineResourceObj, businessActivityDefineFileName);
                            boolean addProcessResult=targetProcessSpace.addProcessDefinition(businessActivityDefineFileName,defineXMLContent.getContentInputStream());
                            if(!addProcessResult){
                                throw new ActivityEngineProcessException();
                            }
                        } catch (ProcessRepositoryRuntimeException e) {
                            e.printStackTrace();
                        } catch (ProcessRepositoryDeploymentException e) {
                            e.printStackTrace();
                        }
                        activityProcessDefineFile.deleteOnExit();
                    }
                }
                //add datafields information in content repository
                BaseContentObject dataFieldObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields, null, false);
                DataFieldDefinition[] dfdArray=bd.getActivityDataFields();
                if(dfdArray==null){
                    throw new ActivityEngineDataException();
                }
                for(DataFieldDefinition df:dfdArray){
                    List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                    ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                    fieldTypeProperty.setMultiple(false);
                    fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                    fieldTypeProperty.setPropertyType(PropertyType.LONG);
                    fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                    paramLst.add(fieldTypeProperty);

                    ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                    displayProperty.setMultiple(false);
                    displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                    displayProperty.setPropertyType(PropertyType.STRING);
                    displayProperty.setPropertyValue(df.getDisplayName());
                    paramLst.add(displayProperty);

                    ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                    descriptionProperty.setMultiple(false);
                    descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                    descriptionProperty.setPropertyType(PropertyType.STRING);
                    descriptionProperty.setPropertyValue(df.getDescription());
                    paramLst.add(descriptionProperty);

                    ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isArrayFieldProperty.setMultiple(false);
                    isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                    isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isArrayFieldProperty.setPropertyValue(df.isArrayField());
                    paramLst.add(isArrayFieldProperty);

                    ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isSystemFieldProperty.setMultiple(false);
                    isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                    isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isSystemFieldProperty.setPropertyValue(df.isSystemField());
                    paramLst.add(isSystemFieldProperty);

                    ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isMandatoryFieldProperty.setMultiple(false);
                    isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                    isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                    paramLst.add(isMandatoryFieldProperty);

                    ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isReadableFieldProperty.setMultiple(false);
                    isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                    isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isReadableFieldProperty.setPropertyValue(df.isReadableField());
                    paramLst.add(isReadableFieldProperty);

                    ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isWriteableFieldProperty.setMultiple(false);
                    isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                    isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                    paramLst.add(isWriteableFieldProperty);

                    dataFieldObj.addSubContentObject(df.getFieldName(), paramLst, false);
                }
                //add steps information in content repository
                BaseContentObject stepsObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps, null, false);
                Map<String,DataFieldDefinition[]> stepsExposedDataFields=bd.getActivityStepsExposedDataField();
                Map<String, String> stepRoleMap=bd.getActivityStepRoleMap();
                String[] exposedStepsArray=bd.getExposedSteps();
                if(exposedStepsArray!=null&&exposedStepsArray.length>0){
                    for(String currentStep:exposedStepsArray){
                        List<ContentObjectProperty> stepParamLst=null;
                        boolean hasStepProperty=false;
                        hasStepProperty=hasStepProperty|(bd.getStepDecisionPointAttributeName(currentStep)!=null);
                        hasStepProperty=hasStepProperty|(bd.getStepDecisionPointChoiseList(currentStep)!=null);
                        hasStepProperty=hasStepProperty|(bd.getStepProcessVariableList(currentStep)!=null);
                        hasStepProperty=hasStepProperty|(bd.getStepUserIdentityAttributeName(currentStep)!=null);
                        if(hasStepProperty){
                            stepParamLst=new ArrayList<ContentObjectProperty>();
                            if(bd.getStepDecisionPointAttributeName(currentStep)!=null){
                                ContentObjectProperty stepDecisionPointAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                                stepDecisionPointAttributeNameProperty.setMultiple(false);
                                stepDecisionPointAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName);
                                stepDecisionPointAttributeNameProperty.setPropertyType(PropertyType.STRING);
                                stepDecisionPointAttributeNameProperty.setPropertyValue(bd.getStepDecisionPointAttributeName(currentStep));
                                stepParamLst.add(stepDecisionPointAttributeNameProperty);
                            }
                            if(bd.getStepDecisionPointChoiseList(currentStep)!=null){
                                ContentObjectProperty stepDecisionPointChoiseListProperty=ContentComponentFactory.createContentObjectProperty();
                                stepDecisionPointChoiseListProperty.setMultiple(true);
                                stepDecisionPointChoiseListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList);
                                stepDecisionPointChoiseListProperty.setPropertyType(PropertyType.STRING);
                                stepDecisionPointChoiseListProperty.setPropertyValue(bd.getStepDecisionPointChoiseList(currentStep));
                                stepParamLst.add(stepDecisionPointChoiseListProperty);
                            }
                            if(bd.getStepProcessVariableList(currentStep)!=null){
                                ContentObjectProperty stepProcessVariableListProperty=ContentComponentFactory.createContentObjectProperty();
                                stepProcessVariableListProperty.setMultiple(true);
                                stepProcessVariableListProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList);
                                stepProcessVariableListProperty.setPropertyType(PropertyType.STRING);
                                stepProcessVariableListProperty.setPropertyValue(bd.getStepProcessVariableList(currentStep));
                                stepParamLst.add(stepProcessVariableListProperty);
                            }
                            if(bd.getStepUserIdentityAttributeName(currentStep)!=null){
                                ContentObjectProperty stepUserIdentityAttributeNameProperty=ContentComponentFactory.createContentObjectProperty();
                                stepUserIdentityAttributeNameProperty.setMultiple(false);
                                stepUserIdentityAttributeNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName);
                                stepUserIdentityAttributeNameProperty.setPropertyType(PropertyType.STRING);
                                stepUserIdentityAttributeNameProperty.setPropertyValue(bd.getStepUserIdentityAttributeName(currentStep));
                                stepParamLst.add(stepUserIdentityAttributeNameProperty);
                            }
                        }
                        BaseContentObject currentStepdfd=stepsObj.addSubContentObject(currentStep, stepParamLst, false);
                        String roleOfCurrentStep=stepRoleMap.get(currentStep);
                        if(roleOfCurrentStep!=null){
                            currentStepdfd.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole, roleOfCurrentStep, false);
                        }
                        if(stepsExposedDataFields!=null&&bd.containsExposedActivityStep(currentStep)){
                            DataFieldDefinition[] dataFieldDefinArray=stepsExposedDataFields.get(currentStep);
                            if(dataFieldDefinArray!=null){
                                for(DataFieldDefinition df:dataFieldDefinArray){
                                    List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                                    ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                                    fieldTypeProperty.setMultiple(false);
                                    fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                                    fieldTypeProperty.setPropertyType(PropertyType.LONG);
                                    fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                                    paramLst.add(fieldTypeProperty);

                                    ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                                    displayProperty.setMultiple(false);
                                    displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                                    displayProperty.setPropertyType(PropertyType.STRING);
                                    displayProperty.setPropertyValue(df.getDisplayName());
                                    paramLst.add(displayProperty);

                                    ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                                    descriptionProperty.setMultiple(false);
                                    descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                                    descriptionProperty.setPropertyType(PropertyType.STRING);
                                    descriptionProperty.setPropertyValue(df.getDescription());
                                    paramLst.add(descriptionProperty);

                                    ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                                    isArrayFieldProperty.setMultiple(false);
                                    isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                                    isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                                    isArrayFieldProperty.setPropertyValue(df.isArrayField());
                                    paramLst.add(isArrayFieldProperty);

                                    ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                                    isSystemFieldProperty.setMultiple(false);
                                    isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                                    isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                                    isSystemFieldProperty.setPropertyValue(df.isSystemField());
                                    paramLst.add(isSystemFieldProperty);

                                    ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                                    isMandatoryFieldProperty.setMultiple(false);
                                    isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                                    isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                                    isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                                    paramLst.add(isMandatoryFieldProperty);

                                    ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                                    isReadableFieldProperty.setMultiple(false);
                                    isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                                    isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                                    isReadableFieldProperty.setPropertyValue(df.isReadableField());
                                    paramLst.add(isReadableFieldProperty);

                                    ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                                    isWriteableFieldProperty.setMultiple(false);
                                    isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                                    isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                                    isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                                    paramLst.add(isWriteableFieldProperty);

                                    currentStepdfd.addSubContentObject(df.getFieldName(), paramLst, false);
                                }
                            }
                        }

                    }
                }

                BaseContentObject launchPointMetaInfoObj=activityObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPoint, null, false);
                BaseContentObject launchPointExposedDataFieldsObj=launchPointMetaInfoObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields, null, false);
                DataFieldDefinition[] launchPointExposedDataFields=bd.getLaunchPointExposedDataFields();
                if(launchPointExposedDataFields!=null){
                    for(DataFieldDefinition df:launchPointExposedDataFields){
                        List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                        ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                        fieldTypeProperty.setMultiple(false);
                        fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                        fieldTypeProperty.setPropertyType(PropertyType.LONG);
                        fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                        paramLst.add(fieldTypeProperty);

                        ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                        displayProperty.setMultiple(false);
                        displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                        displayProperty.setPropertyType(PropertyType.STRING);
                        displayProperty.setPropertyValue(df.getDisplayName());
                        paramLst.add(displayProperty);

                        ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                        descriptionProperty.setMultiple(false);
                        descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                        descriptionProperty.setPropertyType(PropertyType.STRING);
                        descriptionProperty.setPropertyValue(df.getDescription());
                        paramLst.add(descriptionProperty);

                        ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isArrayFieldProperty.setMultiple(false);
                        isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                        isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isArrayFieldProperty.setPropertyValue(df.isArrayField());
                        paramLst.add(isArrayFieldProperty);

                        ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isSystemFieldProperty.setMultiple(false);
                        isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                        isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isSystemFieldProperty.setPropertyValue(df.isSystemField());
                        paramLst.add(isSystemFieldProperty);

                        ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isMandatoryFieldProperty.setMultiple(false);
                        isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                        isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                        paramLst.add(isMandatoryFieldProperty);

                        ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isReadableFieldProperty.setMultiple(false);
                        isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                        isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isReadableFieldProperty.setPropertyValue(df.isReadableField());
                        paramLst.add(isReadableFieldProperty);

                        ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                        isWriteableFieldProperty.setMultiple(false);
                        isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                        isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                        isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                        paramLst.add(isWriteableFieldProperty);

                        launchPointExposedDataFieldsObj.addSubContentObject(df.getFieldName(), paramLst, false);
                    }
                }
                String newRosterName=bd.getRosterName();
                if(newRosterName!=null){
                    Roster newRelatedRoster=this.getRoster(newRosterName);
                    newRelatedRoster.addActivityType(activityType);
                }
            }
            return true;
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean disableBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
            if(targetActivityDefineObj==null){
                throw new ActivityEngineActivityException();
            }
            ContentObjectProperty activityDefineStatusProperty=ContentComponentFactory.createContentObjectProperty();
            activityDefineStatusProperty.setMultiple(false);
            activityDefineStatusProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled);
            activityDefineStatusProperty.setPropertyType(PropertyType.BOOLEAN);
            activityDefineStatusProperty.setPropertyValue(false);
            targetActivityDefineObj.updateProperty(activityDefineStatusProperty, false);
            return true;
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean enableBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
            if(targetActivityDefineObj==null){
                throw new ActivityEngineActivityException();
            }
            ContentObjectProperty activityDefineStatusProperty=ContentComponentFactory.createContentObjectProperty();
            activityDefineStatusProperty.setMultiple(false);
            activityDefineStatusProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled);
            activityDefineStatusProperty.setPropertyType(PropertyType.BOOLEAN);
            activityDefineStatusProperty.setPropertyValue(true);
            targetActivityDefineObj.updateProperty(activityDefineStatusProperty, false);
            return true;
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public BusinessActivityDefinition[] getBusinessActivityDefinitions() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
        String[] activityTypes=getBusinessActivityTypes();
        BusinessActivityDefinition[] batda;
        if(activityTypes!=null){
            batda=new BusinessActivityDefinition[activityTypes.length];
            for(int i=0;i<activityTypes.length;i++){
                BusinessActivityDefinition currentBad=getBusinessActivityDefinition(activityTypes[i]);
                batda[i]=currentBad;
            }
        }else{
            batda=new BusinessActivityDefinition[0];
        }
        return batda;
    }

    @Override
    public BusinessActivityDefinition getBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
            if(targetActivityDefineObj==null){
                throw new ActivityEngineActivityException();
            }
            else{
                BaseContentObject dataFieldObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields);
                long dataFieldDefineNum=dataFieldObj.getSubContentObjectsCount();
                List<BaseContentObject> dataFieldDefineObjList=dataFieldObj.getSubContentObjects(null);
                DataFieldDefinition[] dfdArray=new DataFieldDefinition[(int)dataFieldDefineNum];
                for(int i=0;i<dataFieldDefineObjList.size();i++){
                    BaseContentObject dataFieldDefin=dataFieldDefineObjList.get(i);
                    int activityDefineType=((Long)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType).getPropertyValue())).intValue();
                    String activityDefineDisplayName=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName).getPropertyValue().toString();
                    String activityDefineDesc=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description).getPropertyValue().toString();
                    boolean activityDefineIsArray=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField).getPropertyValue());
                    boolean activityDefineIsSystemField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField).getPropertyValue());
                    boolean activityDefineIsMandatoryField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField).getPropertyValue());
                    boolean activityDefineIsReadableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField).getPropertyValue());
                    boolean activityDefineIsWriteableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField).getPropertyValue());

                    DataFieldDefinition dfd=ActivityComponentFactory.cteateDataFieldDefinition(dataFieldDefin.getContentObjectName(), activityDefineType, activityDefineIsArray);
                    dfd.setDescription(activityDefineDesc);
                    dfd.setDisplayName(activityDefineDisplayName);
                    dfd.setMandatoryField(activityDefineIsMandatoryField);
                    dfd.setSystemField(activityDefineIsSystemField);
                    dfd.setReadableField(activityDefineIsReadableField);
                    dfd.setWriteableField(activityDefineIsWriteableField);
                    dfdArray[i]=dfd;
                }
                BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition(targetActivityDefineObj.getContentObjectName(), this.activitySpaceName,null);
                bsd.setActivityDataFields(dfdArray);

                boolean isEnabled=((Boolean)(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled).getPropertyValue())).booleanValue();
                ((CCRBusinessActivityDefinitionImpl)bsd).setIsEnabled(isEnabled);
                ContentObjectProperty rosterPro=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_belongsToRoster);
                if(rosterPro!=null){
                    String rosterName= rosterPro.getPropertyValue().toString();
                    ((CCRBusinessActivityDefinitionImpl)bsd).setRosterName(rosterName);
                }

                ContentObjectProperty launchDecisionPointAttrPro=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName);
                if(launchDecisionPointAttrPro!=null){
                    String decisionPointAttrName=launchDecisionPointAttrPro.getPropertyValue().toString();
                    bsd.setLaunchDecisionPointAttributeName(decisionPointAttrName);
                }

                ContentObjectProperty launchDecisionPointChoiseListPro=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList);
                if(launchDecisionPointChoiseListPro!=null){
                    String[] decisionPointOptionList;
                    if(launchDecisionPointChoiseListPro.isMultiple()){
                        decisionPointOptionList=(String[])launchDecisionPointChoiseListPro.getPropertyValue();
                    }else{
                        decisionPointOptionList=new String[1];
                        decisionPointOptionList[0]=launchDecisionPointChoiseListPro.getPropertyValue().toString();
                    }
                    bsd.setLaunchDecisionPointChoiseList(decisionPointOptionList);
                }

                ContentObjectProperty launchUserIdentityAttrPro=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName);
                if(launchUserIdentityAttrPro!=null){
                    String launchUserIdentityAttrName=launchUserIdentityAttrPro.getPropertyValue().toString();
                    bsd.setLaunchUserIdentityAttributeName(launchUserIdentityAttrName);
                }

                ContentObjectProperty launchProcessVariableListPro=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList);
                if(launchProcessVariableListPro!=null){
                    String[] launchProcessVariableList;
                    if(launchProcessVariableListPro.isMultiple()){
                        launchProcessVariableList=(String[])launchProcessVariableListPro.getPropertyValue();
                    }else{
                        launchProcessVariableList=new String[1];
                        launchProcessVariableList[0]=launchProcessVariableListPro.getPropertyValue().toString();
                    }
                    bsd.setLaunchProcessVariableList(launchProcessVariableList);
                }

                String[] exposedSteps=(String[])(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps).getPropertyValue());
                bsd.setExposedSteps(exposedSteps);

                if(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles)!=null){
                    String[] launchRolesList=(String[])(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles).getPropertyValue());
                    bsd.setActivityLaunchRoles(launchRolesList);
                }

                if(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants)!=null){
                    String[] launchParticipantsList=(String[])(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants).getPropertyValue());
                    bsd.setActivityLaunchParticipants(launchParticipantsList);
                }

                if(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories)!=null){
                    String[] activityCategoriesList=(String[])(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories).getPropertyValue());
                    bsd.setActivityCategories(activityCategoriesList);
                }

                if(targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description)!=null){
                    String activityDescription=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description).getPropertyValue().toString();
                    bsd.setActivityDescription(activityDescription);
                }

                BaseContentObject defineResourceObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_definitionResource);
                ContentOperationHelper coh=ContentComponentFactory.getContentOperationHelper();
                TextContent defineXMLContent=coh.getTextContent(defineResourceObj, activityType+".bpmn20.xml");
                if(defineXMLContent!=null){
                    bsd.setDefinitionResource(defineXMLContent.getContentInputStream());
                }else{
                    //throw new ActivityEngineActivityException();
                    System.out.println("ERROR: activityDefinition XML is miss["+activityType+"]");
                }
                BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
                if(stepsObj!=null){
                    List<BaseContentObject> stepDataFieldDefineObjList=stepsObj.getSubContentObjects(null);
                    for(BaseContentObject stepDefinObj:stepDataFieldDefineObjList){
                        String stepName=stepDefinObj.getContentObjectName();

                        ContentObjectProperty roleProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole);
                        if(roleProperty!=null){
                            try {
                                bsd.setActivityStepRelatedRole(stepName, roleProperty.getPropertyValue().toString());
                            } catch (ActivityEngineProcessException e) {
                                e.printStackTrace();
                                throw new ActivityEngineActivityException();
                            }
                        }

                        ContentObjectProperty stepDecisionPointAttrPro=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName);
                        if(stepDecisionPointAttrPro!=null){
                            String decisionPointAttrName=stepDecisionPointAttrPro.getPropertyValue().toString();
                            bsd.setStepDecisionPointAttributeName(stepDefinObj.getContentObjectName(), decisionPointAttrName);
                        }

                        ContentObjectProperty stepDecisionPointChoiseListPro=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList);
                        if(stepDecisionPointChoiseListPro!=null){
                            String[] decisionPointOptionList;
                            if(stepDecisionPointChoiseListPro.isMultiple()){
                                decisionPointOptionList=(String[])stepDecisionPointChoiseListPro.getPropertyValue();
                            }else{
                                decisionPointOptionList=new String[1];
                                decisionPointOptionList[0]=stepDecisionPointChoiseListPro.getPropertyValue().toString();
                            }
                            bsd.setStepDecisionPointChoiseList(stepDefinObj.getContentObjectName(), decisionPointOptionList);
                        }

                        ContentObjectProperty stepProcessVariableListPro=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList);
                        if(stepProcessVariableListPro!=null){
                            String[] stepProcessVariableList;
                            if(stepProcessVariableListPro.isMultiple()){
                                stepProcessVariableList=(String[])stepProcessVariableListPro.getPropertyValue();
                            }else{
                                stepProcessVariableList=new String[1];
                                stepProcessVariableList[0]=stepProcessVariableListPro.getPropertyValue().toString();
                            }
                            bsd.setStepProcessVariableList(stepDefinObj.getContentObjectName(), stepProcessVariableList);
                        }

                        ContentObjectProperty stepUserIdentityAttrPro=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName);
                        if(stepUserIdentityAttrPro!=null){
                            String stepUserIdentityAttrName=stepUserIdentityAttrPro.getPropertyValue().toString();
                            bsd.setStepUserIdentityAttributeName(stepDefinObj.getContentObjectName(), stepUserIdentityAttrName);
                        }

                        List<BaseContentObject> stepDataFieldDefObj=stepDefinObj.getSubContentObjects(null);
                        DataFieldDefinition[] stepDfdArray=new DataFieldDefinition[stepDataFieldDefObj.size()];
                        for(int i=0;i<stepDataFieldDefObj.size();i++){
                            BaseContentObject dataFieldDefin=stepDataFieldDefObj.get(i);
                            int activityDefineType=((Long)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType).getPropertyValue())).intValue();
                            String activityDefineDisplayName=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName).getPropertyValue().toString();
                            String activityDefineDesc=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description).getPropertyValue().toString();
                            boolean activityDefineIsArray=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField).getPropertyValue());
                            boolean activityDefineIsSystemField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField).getPropertyValue());
                            boolean activityDefineIsMandatoryField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField).getPropertyValue());
                            boolean activityDefineIsReadableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField).getPropertyValue());
                            boolean activityDefineIsWriteableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField).getPropertyValue());

                            DataFieldDefinition dfd=ActivityComponentFactory.cteateDataFieldDefinition(dataFieldDefin.getContentObjectName(), activityDefineType, activityDefineIsArray);
                            dfd.setDescription(activityDefineDesc);
                            dfd.setDisplayName(activityDefineDisplayName);
                            dfd.setMandatoryField(activityDefineIsMandatoryField);
                            dfd.setSystemField(activityDefineIsSystemField);
                            dfd.setReadableField(activityDefineIsReadableField);
                            dfd.setWriteableField(activityDefineIsWriteableField);
                            stepDfdArray[i]=dfd;
                        }
                        bsd.setActivityStepExposedDataFields(stepName, stepDfdArray);
                    }
                }else{
                    //throw new ActivityEngineActivityException();
                    System.out.println("ERROR: activityDefinition Step Data is miss["+activityType+"]");
                }

                BaseContentObject activityLaunchPointMeteObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPoint);
                if(activityLaunchPointMeteObj!=null){
                    BaseContentObject activityLaunchpointDataFieldsObject=activityLaunchPointMeteObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields);
                    if(activityLaunchpointDataFieldsObject!=null){
                        List<BaseContentObject> launchPointDataFieldDefObj=activityLaunchpointDataFieldsObject.getSubContentObjects(null);
                        DataFieldDefinition[] launchPointDfdArray=new DataFieldDefinition[launchPointDataFieldDefObj.size()];
                        for(int i=0;i<launchPointDataFieldDefObj.size();i++){
                            BaseContentObject dataFieldDefin=launchPointDataFieldDefObj.get(i);
                            int activityDefineType=((Long)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType).getPropertyValue())).intValue();
                            String activityDefineDisplayName=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName).getPropertyValue().toString();
                            String activityDefineDesc=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description).getPropertyValue().toString();
                            boolean activityDefineIsArray=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField).getPropertyValue());
                            boolean activityDefineIsSystemField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField).getPropertyValue());
                            boolean activityDefineIsMandatoryField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField).getPropertyValue());
                            boolean activityDefineIsReadableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField).getPropertyValue());
                            boolean activityDefineIsWriteableField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField).getPropertyValue());

                            DataFieldDefinition dfd=ActivityComponentFactory.cteateDataFieldDefinition(dataFieldDefin.getContentObjectName(), activityDefineType, activityDefineIsArray);
                            dfd.setDescription(activityDefineDesc);
                            dfd.setDisplayName(activityDefineDisplayName);
                            dfd.setMandatoryField(activityDefineIsMandatoryField);
                            dfd.setSystemField(activityDefineIsSystemField);
                            dfd.setReadableField(activityDefineIsReadableField);
                            dfd.setWriteableField(activityDefineIsWriteableField);
                            launchPointDfdArray[i]=dfd;
                        }
                        bsd.setLaunchPointExposedDataFields(launchPointDfdArray);
                    }
                }
                return bsd;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public BusinessActivity launchBusinessActivity(String activityType,	ActivityData[] initActivityData,String startUserId) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
        BusinessActivityDefinition currentActivityDefin=getBusinessActivityDefinition(activityType);
        DataFieldDefinition[] activityDataFieldArray=currentActivityDefin.getActivityDataFields();
        if(!verifyActivityData(initActivityData,activityDataFieldArray)){
            throw new ActivityEngineDataException();
        }
        String processId=null;
        String processDefinaId=null;
        try {
            initContentRepositoryParameter();
            //connect to process engine,launch new process instance,get returned process ID
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.launchProcess(activityType,startUserId);
            processId=processObject.getProcessObjectId();
            processDefinaId=processObject.getProcessDefinitionId();
        } catch (ContentReposityRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineActivityException();
        } catch (ProcessRepositoryDeploymentException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }
        ContentSpace activityContentSpace = null;
        try {
            activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, getActivitySpaceName());
            RootContentObject activitySpaceDefineObject=activityContentSpace.getRootContentObject(activityType);
            if(activitySpaceDefineObject==null){
                RootContentObject activityRoot=ContentComponentFactory.createRootContentObject(activityType);
                activitySpaceDefineObject=activityContentSpace.addRootContentObject(activityRoot);
            }
            BaseContentObject activityInstanceObj=activitySpaceDefineObject.addSubContentObject(processId, null, false);
            activityInstanceObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_processDefinitionId, processDefinaId, false);
            List<ContentObjectProperty> activityInitDataPropList=new ArrayList<ContentObjectProperty>();
            for(ActivityData activityData:initActivityData){
                ContentObjectProperty fieldDataProperty=ContentComponentFactory.createContentObjectProperty();
                fieldDataProperty.setMultiple(activityData.getDataFieldDefinition().isArrayField());
                fieldDataProperty.setPropertyName(activityData.getDataFieldDefinition().getFieldName());
                fieldDataProperty.setPropertyType(activityData.getDataFieldDefinition().getFieldType());
                fieldDataProperty.setPropertyValue(activityData.getDatFieldValue());
                activityInitDataPropList.add(fieldDataProperty);
            }
            activityInstanceObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields, activityInitDataPropList, false);
            //Add activity's attachment container
            activityInstanceObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_attachment, null, false);
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            activityContentSpace.closeContentSpace();
        }
        BusinessActivity businessActivity=ActivityComponentFactory.createBusinessActivity(processId,this.activitySpaceName,activityType);
        return businessActivity;
    }

    @Override
    public BusinessActivity launchBusinessActivity(String activityType,	ActivityData[] initActivityData,Map<String,Object> processVariables,String startUserId) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
        BusinessActivityDefinition currentActivityDefin=getBusinessActivityDefinition(activityType);
        DataFieldDefinition[] activityDataFieldArray=currentActivityDefin.getActivityDataFields();
        if(!verifyActivityData(initActivityData,activityDataFieldArray)){
            throw new ActivityEngineDataException();
        }
        String processId=null;
        String processDefinaId=null;
        try {
            initContentRepositoryParameter();
            //connect to process engine,launch new process instance,get returned process ID
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.launchProcess(activityType,processVariables,startUserId);
            processId=processObject.getProcessObjectId();
            processDefinaId=processObject.getProcessDefinitionId();
        } catch (ContentReposityRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineActivityException();
        } catch (ProcessRepositoryDeploymentException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }
        ContentSpace activityContentSpace = null;
        try {
            activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, getActivitySpaceName());
            RootContentObject activitySpaceDefineObject=activityContentSpace.getRootContentObject(activityType);
            if(activitySpaceDefineObject==null){
                RootContentObject activityRoot=ContentComponentFactory.createRootContentObject(activityType);
                activitySpaceDefineObject=activityContentSpace.addRootContentObject(activityRoot);
            }
            BaseContentObject activityInstanceObj=activitySpaceDefineObject.addSubContentObject(processId, null, false);
            activityInstanceObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_processDefinitionId, processDefinaId, false);
            List<ContentObjectProperty> activityInitDataPropList=new ArrayList<ContentObjectProperty>();
            for(ActivityData activityData:initActivityData){
                ContentObjectProperty fieldDataProperty=ContentComponentFactory.createContentObjectProperty();
                fieldDataProperty.setMultiple(activityData.getDataFieldDefinition().isArrayField());
                fieldDataProperty.setPropertyName(activityData.getDataFieldDefinition().getFieldName());
                fieldDataProperty.setPropertyType(activityData.getDataFieldDefinition().getFieldType());
                fieldDataProperty.setPropertyValue(activityData.getDatFieldValue());
                activityInitDataPropList.add(fieldDataProperty);
            }
            activityInstanceObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields, activityInitDataPropList, false);
            //Add activity's attachment container
            activityInstanceObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_attachment, null, false);
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            activityContentSpace.closeContentSpace();
        }
        BusinessActivity businessActivity=ActivityComponentFactory.createBusinessActivity(processId,this.activitySpaceName,activityType);
        return businessActivity;
    }

    private boolean verifyActivityData(ActivityData[] inputActivityData,DataFieldDefinition[] activityDataFieldArray){
        boolean finalVerifyResult=true;
        for(ActivityData activityData:inputActivityData){
            boolean singleResult=false;
            String fieldName=activityData.getDataFieldDefinition().getFieldName();
            int fieldType=activityData.getDataFieldDefinition().getFieldType();
            boolean fieldIsArray=activityData.getDataFieldDefinition().isArrayField();
            for(DataFieldDefinition dataFieldDefinition:activityDataFieldArray){
                int defineType=dataFieldDefinition.getFieldType();
                String defineName=dataFieldDefinition.getFieldName();
                boolean defineArray=dataFieldDefinition.isArrayField();
                if(defineName.equals(fieldName)&&defineType==fieldType&&defineArray==fieldIsArray){
                    singleResult=true;
                    break;
                }
            }
            finalVerifyResult=finalVerifyResult&singleResult;
            if(!finalVerifyResult){
                return false;
            }
        }
        return finalVerifyResult;
    }

    @Override
    public String getActivitySpaceName() {
        return this.activitySpaceName;
    }

    @Override
    public String[] getBusinessActivityTypes() throws ActivityEngineRuntimeException {
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
            BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
            List<BaseContentObject> activityTypeList=activityDefineObj.getSubContentObjects(null);
            if(activityTypeList==null||activityTypeList.size()==0){
                return null;
            }else{
                String[] typeArray=new String[activityTypeList.size()];
                for(int i=0;i<activityTypeList.size();i++){
                    typeArray[i]=activityTypeList.get(i).getContentObjectName();
                }
                return typeArray;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public boolean addRoleQueue(RoleQueue userQueue) throws ActivityEngineRuntimeException {
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
            BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);
            if(roleQueueDefineObj.getSubContentObject(userQueue.getQueueName())!=null){
                return false;
            }else{
                List<ContentObjectProperty> paramLst=null;
                if(userQueue.getDescription()!=null||userQueue.getDisplayName()!=null){
                    paramLst=new ArrayList<ContentObjectProperty>();
                    if(userQueue.getDescription()!=null){
                        ContentObjectProperty descProperty=ContentComponentFactory.createContentObjectProperty();
                        descProperty.setMultiple(false);
                        descProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
                        descProperty.setPropertyType(PropertyType.STRING);
                        descProperty.setPropertyValue(userQueue.getDescription());
                        paramLst.add(descProperty);
                    }
                    if(userQueue.getDisplayName()!=null){
                        ContentObjectProperty dsnProperty=ContentComponentFactory.createContentObjectProperty();
                        dsnProperty.setMultiple(false);
                        dsnProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);
                        dsnProperty.setPropertyType(PropertyType.STRING);
                        dsnProperty.setPropertyValue(userQueue.getDisplayName());
                        paramLst.add(dsnProperty);
                    }
                }
                BaseContentObject newRoleQueueObject=roleQueueDefineObj.addSubContentObject(userQueue.getQueueName(), paramLst, true);
                if(newRoleQueueObject!=null){
                    return true;
                }
            }

        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
        return false;
    }

    @Override
    public boolean removeRoleQueue(String queuename) throws ActivityEngineRuntimeException {
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
            BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);
            BaseContentObject targetRoleQueueObj=roleQueueDefineObj.getSubContentObject(queuename);
            if(targetRoleQueueObj==null){
                return false;
            }else{
                if(targetRoleQueueObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles)!=null){
                    List<BaseContentObject> roleList=targetRoleQueueObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_containedRoles).getSubLinkContentObjects(null);
                    for(BaseContentObject baseContentObject:roleList){
                        baseContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Role_belongsToRoleQueue).removeSubContentObject(queuename, false);
                    }
                }
                return roleQueueDefineObj.removeSubContentObject(queuename, false);
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public RoleQueue getRoleQueue(String queuename)throws ActivityEngineRuntimeException {
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
            BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);
            BaseContentObject targetRoleQueueObj=roleQueueDefineObj.getSubContentObject(queuename);
            if(targetRoleQueueObj==null){
                return null;
            }else{
                String roleQueueName=targetRoleQueueObj.getContentObjectName();
                ContentObjectProperty roleQueueDisplayNamePro=targetRoleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);
                String roelQueueDisplayName=roleQueueDisplayNamePro!=null?roleQueueDisplayNamePro.getPropertyValue().toString():null;
                ContentObjectProperty roleQueueDescPro=targetRoleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
                String roelQueueDesc=roleQueueDescPro!=null?roleQueueDescPro.getPropertyValue().toString():null;
                RoleQueue _RoleQueue=ActivityComponentFactory.createRoleQueue(roleQueueName, this.activitySpaceName, roelQueueDisplayName, roelQueueDesc);
                return _RoleQueue;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public RoleQueue updateRoleQueue(String roelQueueName,String roleQueueNewDisplayName, String roleQueueNewDescription)throws ActivityEngineRuntimeException {
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
            BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);
            BaseContentObject targetRoleQueueObj=roleQueueDefineObj.getSubContentObject(roelQueueName);
            if(targetRoleQueueObj==null){
                return null;
            }else{
                String roleQueueName=targetRoleQueueObj.getContentObjectName();
                ContentObjectProperty roleQueueDisplayNamePro=targetRoleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);
                String orgRoelQueueDisplayName=roleQueueDisplayNamePro!=null?roleQueueDisplayNamePro.getPropertyValue().toString():null;
                ContentObjectProperty roleQueueDescPro=targetRoleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
                String orgRoelQueueDesc=roleQueueDescPro!=null?roleQueueDescPro.getPropertyValue().toString():null;
                if(!roleQueueNewDisplayName.equals(orgRoelQueueDisplayName)){
                    ContentObjectProperty displayNameProperty=ContentComponentFactory.createContentObjectProperty();
                    displayNameProperty.setMultiple(false);
                    displayNameProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);
                    displayNameProperty.setPropertyType(PropertyType.STRING);
                    displayNameProperty.setPropertyValue(roleQueueNewDisplayName);
                    targetRoleQueueObj.updateProperty(displayNameProperty, false);
                }
                if(!roleQueueNewDescription.equals(orgRoelQueueDesc)){
                    ContentObjectProperty roleDescProperty=ContentComponentFactory.createContentObjectProperty();
                    roleDescProperty.setMultiple(false);
                    roleDescProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
                    roleDescProperty.setPropertyType(PropertyType.STRING);
                    roleDescProperty.setPropertyValue(roleQueueNewDescription);
                    targetRoleQueueObj.updateProperty(roleDescProperty, false);
                }
                RoleQueue _RoleQueue=ActivityComponentFactory.createRoleQueue(roleQueueName, this.activitySpaceName, roleQueueNewDisplayName, roleQueueNewDescription);
                return _RoleQueue;
            }
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public RoleQueue[] getRoleQueues(Participant participant) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ProcessQueue[] getProcessQueues(String activityType,String stepName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RoleQueue[] getRoleQueues() throws ActivityEngineRuntimeException {
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
            BaseContentObject roleQueueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);
            List<BaseContentObject> roleQueueObjList=roleQueueDefineObj.getSubContentObjects(null);
            RoleQueue[] roleQueueArr=new RoleQueue[roleQueueObjList.size()];
            for(int i=0;i<roleQueueObjList.size();i++){
                BaseContentObject roleQueueObj=roleQueueObjList.get(i);
                String roleQueueName=roleQueueObj.getContentObjectName();
                ContentObjectProperty roleQueueDisplayNamePro=roleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_displayName);
                String roelQueueDisplayName=roleQueueDisplayNamePro!=null?roleQueueDisplayNamePro.getPropertyValue().toString():null;
                ContentObjectProperty roleQueueDescPro=roleQueueObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue_desc);
                String roelQueueDesc=roleQueueDescPro!=null?roleQueueDescPro.getPropertyValue().toString():null;
                RoleQueue _RoleQueue1=ActivityComponentFactory.createRoleQueue(roleQueueName, this.activitySpaceName, roelQueueDisplayName, roelQueueDesc);
                roleQueueArr[i]=_RoleQueue1;
            }
            return roleQueueArr;
        } catch (ContentReposityException e) {
            e.printStackTrace();
            throw new ActivityEngineRuntimeException();
        }finally{
            metaDataContentSpace.closeContentSpace();
        }
    }

    @Override
    public List<BusinessActivity> getBusinessActivitiesByStartUserId(String startUserId, int activityStatus) throws ProcessRepositoryRuntimeException {
        ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
        List<ProcessObject> processObjectList=targetProcessSpace.getProcessObjectsByStartUserId(startUserId, activityStatus);
        List<BusinessActivity> businessActivityList=new ArrayList<BusinessActivity>();
        for(ProcessObject processObject:processObjectList){
            String activityId=processObject.getProcessObjectId();
            String activityType=targetProcessSpace.getProcessNameByDefinitionId(processObject.getProcessDefinitionId());
            BusinessActivity businessActivity=ActivityComponentFactory.createBusinessActivity(activityId,this.activitySpaceName,activityType);
            businessActivityList.add(businessActivity);
        }
        return businessActivityList;
    }

    @Override
    public List<ActivityStep> getActivityStepsByInvolvedUserId(String involvedUserId, int activityStatus) throws ProcessRepositoryRuntimeException {
        ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
        List<HistoricProcessStep> historicProcessSteps=targetProcessSpace.getHistoricProcessStepByInvolvedUserId(involvedUserId, activityStatus);
        List<ActivityStep> activityStepList=new ArrayList<ActivityStep>();
        for(HistoricProcessStep historicProcessStep:historicProcessSteps){
            ProcessObject processObject=targetProcessSpace.getProcessObjectById(historicProcessStep.getProcessObjectId());
            String activityType=targetProcessSpace.getProcessNameByDefinitionId(historicProcessStep.getProcessDefinitionId());
            BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);
            ((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);
            CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);

            ActivitiProcessStepImpl processStep=new ActivitiProcessStepImpl(
                    historicProcessStep.getStepName(),historicProcessStep.getStepDefinitionKey(),
                    historicProcessStep.getStepId(),historicProcessStep.getProcessObjectId(),
                    historicProcessStep.getProcessDefinitionId(),historicProcessStep.getStartTime());
            processStep.setProcessEngine(((ActivitiHistoricProcessStepImpl)historicProcessStep).getProcessEngine());
            currentActivityStep.setProcessStep(processStep);

            currentActivityStep.setBusinessActivity(currentBusinessActivity);
            currentActivityStep.setActivityStepName(historicProcessStep.getStepName());
            currentActivityStep.setStepCreateTime(historicProcessStep.getStartTime());
            currentActivityStep.setStepFinishTime(historicProcessStep.getEndTime());
            currentActivityStep.setStepAssignee(historicProcessStep.getStepAssignee());
            currentActivityStep.setStepDescription(historicProcessStep.getStepDescription());
            currentActivityStep.setStepOwner(historicProcessStep.getStepOwner());
            currentActivityStep.setDueDate(historicProcessStep.getDueDate());
            currentActivityStep.setStepDefinitionKey(historicProcessStep.getStepDefinitionKey());
            activityStepList.add(currentActivityStep);
        }
        return activityStepList;
    }

    @Override
    public boolean deleteBusinessActivityByActivityId(String activityId,String deleteReason) throws ActivityEngineProcessException {
        ProcessSpace targetProcessSpace;
        try {
            targetProcessSpace = ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            return targetProcessSpace.deleteProcessByProcessObjectId(activityId, deleteReason);
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineProcessException();
        }
    }

    @Override
    public BusinessActivity getBusinessActivityByActivityId(String activityId) throws ActivityEngineProcessException {
        ProcessSpace targetProcessSpace;
        try {
            targetProcessSpace = ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.getProcessObjectById(activityId);
            if(processObject==null){
                return null;
            }
            String activityType=targetProcessSpace.getProcessNameByDefinitionId(processObject.getProcessDefinitionId());
            BusinessActivity businessActivity=ActivityComponentFactory.createBusinessActivity(activityId,this.activitySpaceName,activityType);
            return businessActivity;
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineProcessException();
        }
    }

    @Override
    public BusinessActivity getBusinessActivityByActivityInfo(String activityType, String activityId)throws ActivityEngineProcessException {
        return ActivityComponentFactory.createBusinessActivity(activityId,this.activitySpaceName,activityType);
    }

    @Override
    public ActivityStep getCurrentActivityStepByStepInfo(String activityType,String activityId, String activityStepName)throws ActivityEngineProcessException {
        try {
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.getProcessObjectById(activityId);
            if(processObject==null){
                return null;
            }
            BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);
            ((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);
            CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);
            currentActivityStep.setBusinessActivity(currentBusinessActivity);
            List<ProcessStep> processStepList=processObject.getCurrentProcessSteps();
            for(ProcessStep curentProcessStep:processStepList){
                if(curentProcessStep.getStepName().equals(activityStepName)){
                    currentActivityStep.setProcessStep(curentProcessStep);
                }
            }
            return currentActivityStep;
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ActivityStep getCurrentActivityStepByStepInfo(String activityId,String activityStepName) throws ActivityEngineProcessException {
        try {
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.getProcessObjectById(activityId);

            String activityType=targetProcessSpace.getProcessNameByDefinitionId(processObject.getProcessDefinitionId());
            BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);
            ((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);
            CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);
            currentActivityStep.setBusinessActivity(currentBusinessActivity);
            List<ProcessStep> processStepList=processObject.getCurrentProcessSteps();
            for(ProcessStep curentProcessStep:processStepList){
                if(curentProcessStep.getStepName().equals(activityStepName)){
                    currentActivityStep.setProcessStep(curentProcessStep);
                }
            }
            return currentActivityStep;
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getDocumentsFolderPath() {
        String applicationFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_SpaceContentStore+"/";
        return applicationFolderFullPath;
    }

    @Override
    public ActivityStep getFinishedActivityStepByStepInfo(String activityType,String activityId, String activityStepName)throws ActivityEngineProcessException {
        try {
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            ProcessObject processObject=targetProcessSpace.getProcessObjectById(activityId);
            if(processObject==null){
                return null;
            }
            BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);
            ((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);
            CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(activityType);
            currentActivityStep.setBusinessActivity(currentBusinessActivity);

            List<HistoricProcessStep> finishedProcessStepList=processObject.getFinishedProcessSteps();
            for(HistoricProcessStep currentHistoricProcessStep:finishedProcessStepList){
                if(currentHistoricProcessStep.getStepName().equals(activityStepName)){
                    ProcessStep curentProcessStep=ProcessComponentFactory.createProcessStep(currentHistoricProcessStep.getStepName(), currentHistoricProcessStep.getStepDefinitionKey(),
                            currentHistoricProcessStep.getStepId(), currentHistoricProcessStep.getProcessObjectId(), currentHistoricProcessStep.getProcessDefinitionId(), currentHistoricProcessStep.getStartTime());
                    curentProcessStep.setDueDate(currentHistoricProcessStep.getDueDate());
                    curentProcessStep.setParentStepId(currentHistoricProcessStep.getParentStepId());
                    curentProcessStep.setStepAssignee(currentHistoricProcessStep.getStepAssignee());
                    curentProcessStep.setStepDescription(currentHistoricProcessStep.getStepDescription());
                    curentProcessStep.setStepOwner(currentHistoricProcessStep.getStepOwner());
                    ((ActivitiProcessStepImpl)curentProcessStep).setEndTime(currentHistoricProcessStep.getEndTime());
                    currentActivityStep.setProcessStep(curentProcessStep);
                }
            }
            return currentActivityStep;
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void registerActivityEventListener(ActivityEventType eventType, ActivitySpaceEventListener listener) throws ActivityEngineProcessException{
        switch(eventType){
            case ACTIVITYEVENT_UNKNOWN:return;
        }
        try {
            ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
            switch(eventType){
                case ACTIVITYSTEP_ASSIGNED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSSTEP_ASSIGNED, listener);
                    break;
                case ACTIVITYSTEP_CREATED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSSTEP_CREATED, listener);
                    break;
                case ACTIVITYSTEP_COMPLETED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSSTEP_COMPLETED, listener);
                    break;
                case BUSINESSACTIVITY_COMPLETED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSINSTANCE_COMPLETED, listener);
                    break;
                case BUSINESSACTIVITY_CANCELLED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSINSTANCE_CANCELLED, listener);
                    break;
                case ACTIVITYSTEPMONITOR_SUCCESS:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSEXTENSIONSTEP_SUCCESS, listener);
                    break;
                case ACTIVITYSTEPMONITOR_FAILURE:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSEXTENSIONSTEP_FAILURE, listener);
                    break;
                case ACTIVITYSTEPMONITOR_RETRY_DECREMENTED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSEXTENSIONSTEP_RETRY_DECREMENTED, listener);
                    break;
                case ACTIVITYSTEPMONITOR_CANCELED:
                    targetProcessSpace.registerProcessEventListener(ProcessSpaceEventType.PROCESSEXTENSIONSTEP_CANCELED, listener);
                    break;
            }
        } catch (ProcessRepositoryRuntimeException e) {
            e.printStackTrace();
            throw new ActivityEngineProcessException();
        }
    }

	@Override
	public boolean addBusinessActivityDefinitionDataFieldDefinition(String activityType,DataFieldDefinition dataField) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
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
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }
	        else{
	            BaseContentObject dataFieldObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields);
	            if(dataFieldObj==null){
	            	dataFieldObj=targetActivityDefineObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields, null, false);
	            }
	            if(dataFieldObj.getSubContentObject(dataField.getFieldName())!=null){
	              	throw new ActivityEngineDataException();
	            }else{
	              	List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
	                ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
	                fieldTypeProperty.setMultiple(false);
	                fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
	                fieldTypeProperty.setPropertyType(PropertyType.LONG);
	                fieldTypeProperty.setPropertyValue(new Long(dataField.getFieldType()));
	                paramLst.add(fieldTypeProperty);

	                ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
	                displayProperty.setMultiple(false);
	                displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
	                displayProperty.setPropertyType(PropertyType.STRING);
	                displayProperty.setPropertyValue(dataField.getDisplayName());
	                paramLst.add(displayProperty);

	                ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
	                descriptionProperty.setMultiple(false);
	                descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
	                descriptionProperty.setPropertyType(PropertyType.STRING);
	                descriptionProperty.setPropertyValue(dataField.getDescription());
	                paramLst.add(descriptionProperty);

	                ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isArrayFieldProperty.setMultiple(false);
	                isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
	                isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isArrayFieldProperty.setPropertyValue(dataField.isArrayField());
	                paramLst.add(isArrayFieldProperty);

	                ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isSystemFieldProperty.setMultiple(false);
	                isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
	                isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isSystemFieldProperty.setPropertyValue(dataField.isSystemField());
	                paramLst.add(isSystemFieldProperty);

	                ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isMandatoryFieldProperty.setMultiple(false);
	                isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
	                isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isMandatoryFieldProperty.setPropertyValue(dataField.isMandatoryField());
	                paramLst.add(isMandatoryFieldProperty);

	                ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isReadableFieldProperty.setMultiple(false);
	                isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
	                isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isReadableFieldProperty.setPropertyValue(dataField.isReadableField());
	                paramLst.add(isReadableFieldProperty);

	                ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isWriteableFieldProperty.setMultiple(false);
	                isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
	                isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isWriteableFieldProperty.setPropertyValue(dataField.isWriteableField());
	                paramLst.add(isWriteableFieldProperty);

	                dataFieldObj.addSubContentObject(dataField.getFieldName(), paramLst, false);
	            }
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
	public boolean updateBusinessActivityDefinitionDataFieldDefinition(String activityType,DataFieldDefinition dataField) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
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
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }
	        else{
	            BaseContentObject dataFieldObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields);
	            if(dataFieldObj==null){
	            	throw new ActivityEngineActivityException();
	            }
	            if(dataFieldObj.getSubContentObject(dataField.getFieldName())==null){
	              	throw new ActivityEngineDataException();
	            }else{
	            	dataFieldObj.removeSubContentObject(dataField.getFieldName(), false);
	            	List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
	                ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
	                fieldTypeProperty.setMultiple(false);
	                fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
	                fieldTypeProperty.setPropertyType(PropertyType.LONG);
	                fieldTypeProperty.setPropertyValue(new Long(dataField.getFieldType()));
	                paramLst.add(fieldTypeProperty);

	                ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
	                displayProperty.setMultiple(false);
	                displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
	                displayProperty.setPropertyType(PropertyType.STRING);
	                displayProperty.setPropertyValue(dataField.getDisplayName());
	                paramLst.add(displayProperty);

	                ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
	                descriptionProperty.setMultiple(false);
	                descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
	                descriptionProperty.setPropertyType(PropertyType.STRING);
	                descriptionProperty.setPropertyValue(dataField.getDescription());
	                paramLst.add(descriptionProperty);

	                ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isArrayFieldProperty.setMultiple(false);
	                isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
	                isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isArrayFieldProperty.setPropertyValue(dataField.isArrayField());
	                paramLst.add(isArrayFieldProperty);

	                ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isSystemFieldProperty.setMultiple(false);
	                isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
	                isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isSystemFieldProperty.setPropertyValue(dataField.isSystemField());
	                paramLst.add(isSystemFieldProperty);

	                ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isMandatoryFieldProperty.setMultiple(false);
	                isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
	                isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isMandatoryFieldProperty.setPropertyValue(dataField.isMandatoryField());
	                paramLst.add(isMandatoryFieldProperty);

	                ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isReadableFieldProperty.setMultiple(false);
	                isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
	                isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isReadableFieldProperty.setPropertyValue(dataField.isReadableField());
	                paramLst.add(isReadableFieldProperty);

	                ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
	                isWriteableFieldProperty.setMultiple(false);
	                isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
	                isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
	                isWriteableFieldProperty.setPropertyValue(dataField.isWriteableField());
	                paramLst.add(isWriteableFieldProperty);

	                dataFieldObj.addSubContentObject(dataField.getFieldName(), paramLst, false);
	            }
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
	public boolean removeBusinessActivityDefinitionDataFieldDefinition(String activityType, String dataFieldName) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
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
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }
	        else{
	            BaseContentObject dataFieldObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_dataFields);
	            if(dataFieldObj==null){
	            	throw new ActivityEngineActivityException();
	            }
	            if(dataFieldObj.getSubContentObject(dataFieldName)==null){
	              	throw new ActivityEngineDataException();
	            }else{
	            	dataFieldObj.removeSubContentObject(dataFieldName, false);
	            }
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
	public boolean addBusinessActivityDefinitionExposedStep(String activityType, String stepName,String relatedRoleName) throws ActivityEngineRuntimeException,ActivityEngineActivityException {
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
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }else{
	        	BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
	        	if(stepsObj==null){
	        		stepsObj=targetActivityDefineObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps, null, false);
	        	}
	        	BaseContentObject activityStepContentObject=stepsObj.getSubContentObject(stepName);
	        	if(activityStepContentObject!=null){
	        		throw new ActivityEngineRuntimeException();
	        	}
	        	ContentObjectProperty exposedStepsProperty=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps);
	        	if(exposedStepsProperty==null){
	        		targetActivityDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps, new String[]{stepName}, false);
	        	}else{
	        		String[] exposedStepsArray=(String[])exposedStepsProperty.getPropertyValue();
	        		String[] newExposedStepsArray=new String[exposedStepsArray.length+1];
	        		for(int i=0;i<exposedStepsArray.length;i++){
	        			if(exposedStepsArray[i].equals(stepName)){
	        				throw new ActivityEngineRuntimeException();
	        			}else{
	        				newExposedStepsArray[i]=exposedStepsArray[i];
	        			}
	        		}
	        		newExposedStepsArray[exposedStepsArray.length]=stepName;
	        		exposedStepsProperty.setPropertyValue(newExposedStepsArray);
	        		targetActivityDefineObj.updateProperty(exposedStepsProperty, false);
	        	}
	        	BaseContentObject currentStepdfd=stepsObj.addSubContentObject(stepName, null, false);
	        	if(relatedRoleName!=null){
                     currentStepdfd.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole, relatedRoleName, false);
                 }
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
	public boolean removeBusinessActivityDefinitionExposedStep(String activityType, String stepName) throws ActivityEngineRuntimeException,ActivityEngineActivityException{
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
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
	        if(activitySpaceBco==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }else{
	        	BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
	        	if(stepsObj==null){
	        		throw new ActivityEngineActivityException();
	        	}
	        	BaseContentObject activityStepContentObject=stepsObj.getSubContentObject(stepName);
	        	if(activityStepContentObject==null){
	        		throw new ActivityEngineRuntimeException();
	        	}else{
	        		stepsObj.removeSubContentObject(stepName,false);
	        	}
	        	ContentObjectProperty exposedStepsProperty=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps);
	        	if(exposedStepsProperty==null){
	        		throw new ActivityEngineActivityException();
	        	}else{
	        		String[] exposedStepsArray=(String[])exposedStepsProperty.getPropertyValue();
	        		List<String> tempList=new ArrayList<String>();
	        		for(String currentStep:exposedStepsArray){
	        			if(!currentStep.equals(stepName)){
	        				tempList.add(currentStep);
	        			}
	        		}
	        		String[] newExposedStepsArray=new String[tempList.size()];
	        		tempList.toArray(newExposedStepsArray);
	        		exposedStepsProperty.setPropertyValue(newExposedStepsArray);
	        		targetActivityDefineObj.updateProperty(exposedStepsProperty, false);
	        	}
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
	public boolean setBusinessActivityDefinitionExposedStepDataFieldDefinitions(String activityType, String stepName,DataFieldDefinition[] dataFieldDefinitions) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
	        if(activitySpaceBco==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }else{
	        	ContentObjectProperty exposedStepsProperty=targetActivityDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_exposedSteps);
	        	if(exposedStepsProperty==null){
	        		throw new ActivityEngineActivityException();
	        	}else{
	        		String[] exposedStepsArray=(String[])exposedStepsProperty.getPropertyValue();
	        		boolean currentStepIsExposed=false;
	        		for(String currentStep:exposedStepsArray){
	        			if(currentStep.equals(stepName)){
	        				currentStepIsExposed=true;
	        				break;
	        			}
	        		}
	        		if(!currentStepIsExposed){
	        			 throw new ActivityEngineRuntimeException();
	        		}
	        		BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
		        	if(stepsObj==null){
		        		throw new ActivityEngineActivityException();
		        	}
		        	BaseContentObject activityStepContentObject=stepsObj.getSubContentObject(stepName);
		        	if(activityStepContentObject==null){
		        		throw new ActivityEngineRuntimeException();
		        	}else{
		        		List<BaseContentObject> dataFieldDefinitionsList=activityStepContentObject.getSubContentObjects(null);
		        		if(dataFieldDefinitionsList!=null){
		        			for(BaseContentObject currentFieldDefinition:dataFieldDefinitionsList){
		        				activityStepContentObject.removeSubContentObject(currentFieldDefinition.getContentObjectName(), false);
		        			}
		        		}
		        		for(DataFieldDefinition df:dataFieldDefinitions){
                            List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                            ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                            fieldTypeProperty.setMultiple(false);
                            fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                            fieldTypeProperty.setPropertyType(PropertyType.LONG);
                            fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                            paramLst.add(fieldTypeProperty);

                            ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                            displayProperty.setMultiple(false);
                            displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                            displayProperty.setPropertyType(PropertyType.STRING);
                            displayProperty.setPropertyValue(df.getDisplayName());
                            paramLst.add(displayProperty);

                            ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                            descriptionProperty.setMultiple(false);
                            descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                            descriptionProperty.setPropertyType(PropertyType.STRING);
                            descriptionProperty.setPropertyValue(df.getDescription());
                            paramLst.add(descriptionProperty);

                            ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isArrayFieldProperty.setMultiple(false);
                            isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                            isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isArrayFieldProperty.setPropertyValue(df.isArrayField());
                            paramLst.add(isArrayFieldProperty);

                            ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isSystemFieldProperty.setMultiple(false);
                            isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                            isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isSystemFieldProperty.setPropertyValue(df.isSystemField());
                            paramLst.add(isSystemFieldProperty);

                            ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isMandatoryFieldProperty.setMultiple(false);
                            isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                            isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                            paramLst.add(isMandatoryFieldProperty);

                            ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isReadableFieldProperty.setMultiple(false);
                            isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                            isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isReadableFieldProperty.setPropertyValue(df.isReadableField());
                            paramLst.add(isReadableFieldProperty);

                            ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                            isWriteableFieldProperty.setMultiple(false);
                            isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                            isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                            isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                            paramLst.add(isWriteableFieldProperty);

                            activityStepContentObject.addSubContentObject(df.getFieldName(), paramLst, false);
                        }	
		        	}
	        	}
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
	public boolean setBusinessActivityDefinitionExposedStepProcessProperties(String activityType,ActivityStepDefinition activityStepDefinition) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
		String stepName=activityStepDefinition.getStepId();
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
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
	        if(activitySpaceBco==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }else{
	        	BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
	        	if(stepsObj==null){
	        		throw new ActivityEngineActivityException();
	        	}
	        	BaseContentObject stepDefinObj=stepsObj.getSubContentObject(stepName);
	        	if(stepDefinObj==null){
	        		throw new ActivityEngineRuntimeException();
	        	}
	        	String stepRole=activityStepDefinition.getStepRole();
	        	ContentObjectProperty roleProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole);
	        	if(stepRole!=null){
	        		if(roleProperty!=null){
	        			roleProperty.setPropertyValue(stepRole);
	        			stepDefinObj.updateProperty(roleProperty, false);
	        		}else{
	        			stepDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole, stepRole, false);
	        		}
	        	}else{
	        		if(roleProperty!=null){
	        			stepDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepRole, false);
	        		}
	        	}
	        	String stepUserIdentityAttribute=activityStepDefinition.getStepUserIdentityAttribute();
	        	ContentObjectProperty stepUserIdentityAttrProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName);
	        	if(stepUserIdentityAttribute!=null){
	        		if(stepUserIdentityAttrProperty!=null){
	        			stepUserIdentityAttrProperty.setPropertyValue(stepUserIdentityAttribute);
	        			stepDefinObj.updateProperty(stepUserIdentityAttrProperty, false);
	        		}else{
	        			stepDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName, stepUserIdentityAttribute, false);
	        		}
	        	}else{
	        		if(stepUserIdentityAttrProperty!=null){
	        			stepDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName, false);
	        		}
	        	}
	        	String[] stepProcessVariables=activityStepDefinition.getStepProcessVariables();
	        	ContentObjectProperty stepProcessVariableListProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList);
	        	if(stepProcessVariables!=null){
	        		if(stepProcessVariableListProperty!=null){
	        			stepProcessVariableListProperty.setPropertyValue(stepProcessVariables);
	        			stepDefinObj.updateProperty(stepProcessVariableListProperty, false);
	        		}else{
	        			stepDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList, stepProcessVariables, false);
	        		}
	        	}else{
	        		if(stepProcessVariableListProperty!=null){
	        			stepDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList, false);
	        		}
	        	}
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
	public boolean setBusinessActivityDefinitionExposedStepDecisionPointProperties(String activityType,ActivityStepDefinition activityStepDefinition) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
		String stepName=activityStepDefinition.getStepId();
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
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
	        if(activitySpaceBco==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        BaseContentObject targetActivityDefineObj=activityDefineObj.getSubContentObject(activityType);
	        if(targetActivityDefineObj==null){
	            throw new ActivityEngineActivityException();
	        }else{
	        	BaseContentObject stepsObj=targetActivityDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_steps);
	        	if(stepsObj==null){
	        		throw new ActivityEngineActivityException();
	        	}
	        	BaseContentObject stepDefinObj=stepsObj.getSubContentObject(stepName);
	        	if(stepDefinObj==null){
	        		throw new ActivityEngineRuntimeException();
	        	}
	        	
	        	String stepDecisionPointAttribute=activityStepDefinition.getStepDecisionPointAttribute();
	        	ContentObjectProperty stepDecisionPointAttributeProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName);
	        	if(stepDecisionPointAttribute!=null){
	        		if(stepDecisionPointAttributeProperty!=null){
	        			stepDecisionPointAttributeProperty.setPropertyValue(stepDecisionPointAttribute);
	        			stepDefinObj.updateProperty(stepDecisionPointAttributeProperty, false);
	        		}else{
	        			stepDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName, stepDecisionPointAttribute, false);
	        		}
	        	}else{
	        		if(stepDecisionPointAttributeProperty!=null){
	        			stepDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName, false);
	        		}
	        	}
	        	
	        	String[] stepDecisionPointChoiseList=activityStepDefinition.getStepDecisionPointChooseOptions();
	        	ContentObjectProperty stepDecisionPointChoiseListProperty=stepDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList);
	        	if(stepDecisionPointChoiseList!=null){
	        		if(stepDecisionPointChoiseListProperty!=null){
	        			stepDecisionPointChoiseListProperty.setPropertyValue(stepDecisionPointChoiseList);
	        			stepDefinObj.updateProperty(stepDecisionPointChoiseListProperty, false);
	        		}else{
	        			stepDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList, stepDecisionPointChoiseList, false);
	        		}
	        	}else{
	        		if(stepDecisionPointChoiseListProperty!=null){
	        			stepDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList, false);
	        		}
	        	}
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
	public boolean updateBusinessActivityDefinitionProperties(BusinessActivityDefinition bd)throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
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
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(this.activitySpaceName);
	        if(activitySpaceBco==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);
	        if(activityDefineObj==null){
	        	throw new ActivityEngineActivityException();
	        }
	        BaseContentObject activityTypeDefineObj=activityDefineObj.getSubContentObject(bd.getActivityType());
	        if(activityTypeDefineObj==null){
	        	throw new ActivityEngineActivityException();
	        }
	        String rosterName=bd.getRosterName();
	        BusinessActivityDefinition currentBusinessActivity=this.getBusinessActivityDefinition(bd.getActivityType());
        	String currentRosterName=currentBusinessActivity.getRosterName();
        	if(currentRosterName!=null){
        		Roster currentRoster=this.getRoster(rosterName);
        		currentRoster.removeActivityType(bd.getActivityType());
        	}
	        if(rosterName!=null){
	        	Roster newRoster=this.getRoster(rosterName);
	        	newRoster.addActivityType(bd.getActivityType());
	        }
	        
	        boolean isActive=bd.isEnabled();
	        ContentObjectProperty activityDefineStatusProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_isEnabled);
	        activityDefineStatusProperty.setPropertyValue(isActive);
	        activityTypeDefineObj.updateProperty(activityDefineStatusProperty, false);
	        
	        String launchUserIdAttr=bd.getLaunchUserIdentityAttributeName();
	        ContentObjectProperty launchUserIdentityAttributeNameProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName);
	        if(launchUserIdAttr!=null){
	        	if(launchUserIdentityAttributeNameProperty!=null){
	        		launchUserIdentityAttributeNameProperty.setPropertyValue(launchUserIdAttr);
	        		activityTypeDefineObj.updateProperty(launchUserIdentityAttributeNameProperty,false);
	        	}else{
	        		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName, launchUserIdAttr, false);
	        	}
	        }else{
	        	if(launchUserIdentityAttributeNameProperty!=null){
	        		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName, false);
	        	}
	        }
	        	
	        String[] launchRoles=bd.getActivityLaunchRoles();
	        ContentObjectProperty launchRoleListProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles);
            if(launchRoles!=null){
            	if(launchRoleListProperty!=null){
            		launchRoleListProperty.setPropertyValue(launchRoles);
            		activityTypeDefineObj.updateProperty(launchRoleListProperty, false);
            	}else{
            		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles, launchRoles, false);
            	}
            }else{
            	if(launchRoleListProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchRoles, false);
            	}
            }
            
            String[] launchParticipants=bd.getActivityLaunchParticipants();
            ContentObjectProperty launchParticipantListProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants);
            if(launchParticipants!=null){
            	if(launchParticipantListProperty!=null){
            		launchParticipantListProperty.setPropertyValue(launchParticipants);
            		activityTypeDefineObj.updateProperty(launchParticipantListProperty, false);
            	}else{
            		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants, launchParticipants, false);
            	}
            }else{
            	if(launchParticipantListProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchParticipants, false);
            	}
            }

            String[] launchProcessVariables=bd.getLaunchProcessVariableList();
            ContentObjectProperty launchProcessVariableListProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList);
            if(launchProcessVariables!=null){
            	if(launchProcessVariableListProperty!=null){
            		launchProcessVariableListProperty.setPropertyValue(launchProcessVariables);
            		activityTypeDefineObj.updateProperty(launchProcessVariableListProperty, false);
            	}else{
            		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList, launchProcessVariables, false);
            	}
            }else{
            	if(launchProcessVariableListProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList, false);
            	}
            }

            String launchDecisionPointAttr=bd.getLaunchDecisionPointAttributeName();
            ContentObjectProperty launchDecisionPointAttributeNameProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName);
            if(launchDecisionPointAttr!=null){
        	   if(launchDecisionPointAttributeNameProperty!=null){
        		   launchDecisionPointAttributeNameProperty.setPropertyValue(launchDecisionPointAttr);
        		   activityTypeDefineObj.updateProperty(launchDecisionPointAttributeNameProperty, false);
        	   }else{
        		   activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName, launchDecisionPointAttr,false);
        	   }
            }else{
            	if(launchDecisionPointAttributeNameProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName, false);
            	}
            }

            String[] launchDecisionPointOptions=bd.getLaunchDecisionPointChoiseList();
            ContentObjectProperty launchDecisionPointChoiseListProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList);
            if(launchDecisionPointOptions!=null){
            	if(launchDecisionPointChoiseListProperty!=null){
            		launchDecisionPointChoiseListProperty.setPropertyValue(launchDecisionPointOptions);
            		activityTypeDefineObj.updateProperty(launchDecisionPointChoiseListProperty, false);
            	}else{
            		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList, launchDecisionPointOptions,false);
            	}
            }else{
            	if(launchDecisionPointChoiseListProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList, false);
            	}
            }
            
            String activityTypeDesc=bd.getActivityDescription();
            ContentObjectProperty activityDescriptionProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description);
            if(activityTypeDesc!=null){
        	   if(activityDescriptionProperty!=null){
        		   activityDescriptionProperty.setPropertyValue(activityTypeDesc);
        		   activityTypeDefineObj.updateProperty(activityDescriptionProperty, false);
        	   }else{
        		   activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description, activityTypeDesc, false);
        	   }
        	}else{
        		if(activityDescriptionProperty!=null){
        			activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_description, false);
        		}
        	}
            
            String[] activityTypeCategories=bd.getActivityCategories();
            ContentObjectProperty activityCategoriesProperty=activityTypeDefineObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories);
            if(activityTypeCategories!=null){
            	if(activityCategoriesProperty!=null){
            		activityCategoriesProperty.setPropertyValue(activityTypeCategories);
            		activityTypeDefineObj.updateProperty(activityCategoriesProperty, false);
            	}else{
            		activityTypeDefineObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories, activityTypeCategories,false);
            	}
            }else{
            	if(activityCategoriesProperty!=null){
            		activityTypeDefineObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_categories, false);
            	}
            }
            
            BaseContentObject launchPointMetaInfoObj=activityTypeDefineObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPoint);
            if(launchPointMetaInfoObj==null){
	        	throw new ActivityEngineActivityException();
	        }
            BaseContentObject launchPointExposedDataFieldsObj=launchPointMetaInfoObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields);
            if(launchPointExposedDataFieldsObj==null){
	        	throw new ActivityEngineActivityException();
	        }
            launchPointMetaInfoObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields, false);
            launchPointExposedDataFieldsObj=launchPointMetaInfoObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields, null, false);
            DataFieldDefinition[] launchPointExposedDataFields=bd.getLaunchPointExposedDataFields();
            if(launchPointExposedDataFields!=null){
                for(DataFieldDefinition df:launchPointExposedDataFields){
                    List<ContentObjectProperty> paramLst=new ArrayList<ContentObjectProperty>();
                    ContentObjectProperty fieldTypeProperty=ContentComponentFactory.createContentObjectProperty();
                    fieldTypeProperty.setMultiple(false);
                    fieldTypeProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType);
                    fieldTypeProperty.setPropertyType(PropertyType.LONG);
                    fieldTypeProperty.setPropertyValue(new Long(df.getFieldType()));
                    paramLst.add(fieldTypeProperty);

                    ContentObjectProperty displayProperty=ContentComponentFactory.createContentObjectProperty();
                    displayProperty.setMultiple(false);
                    displayProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName);
                    displayProperty.setPropertyType(PropertyType.STRING);
                    displayProperty.setPropertyValue(df.getDisplayName());
                    paramLst.add(displayProperty);

                    ContentObjectProperty descriptionProperty=ContentComponentFactory.createContentObjectProperty();
                    descriptionProperty.setMultiple(false);
                    descriptionProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description);
                    descriptionProperty.setPropertyType(PropertyType.STRING);
                    descriptionProperty.setPropertyValue(df.getDescription());
                    paramLst.add(descriptionProperty);

                    ContentObjectProperty isArrayFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isArrayFieldProperty.setMultiple(false);
                    isArrayFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField);
                    isArrayFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isArrayFieldProperty.setPropertyValue(df.isArrayField());
                    paramLst.add(isArrayFieldProperty);

                    ContentObjectProperty isSystemFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isSystemFieldProperty.setMultiple(false);
                    isSystemFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField);
                    isSystemFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isSystemFieldProperty.setPropertyValue(df.isSystemField());
                    paramLst.add(isSystemFieldProperty);

                    ContentObjectProperty isMandatoryFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isMandatoryFieldProperty.setMultiple(false);
                    isMandatoryFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField);
                    isMandatoryFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isMandatoryFieldProperty.setPropertyValue(df.isMandatoryField());
                    paramLst.add(isMandatoryFieldProperty);

                    ContentObjectProperty isReadableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isReadableFieldProperty.setMultiple(false);
                    isReadableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isReadableField);
                    isReadableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isReadableFieldProperty.setPropertyValue(df.isReadableField());
                    paramLst.add(isReadableFieldProperty);

                    ContentObjectProperty isWriteableFieldProperty=ContentComponentFactory.createContentObjectProperty();
                    isWriteableFieldProperty.setMultiple(false);
                    isWriteableFieldProperty.setPropertyName(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isWriteableField);
                    isWriteableFieldProperty.setPropertyType(PropertyType.BOOLEAN);
                    isWriteableFieldProperty.setPropertyValue(df.isWriteableField());
                    paramLst.add(isWriteableFieldProperty);

                    launchPointExposedDataFieldsObj.addSubContentObject(df.getFieldName(), paramLst, false);
                }
            }
            return true;
	    } catch (ContentReposityException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }finally{
	        metaDataContentSpace.closeContentSpace();
	    }
	}

	@Override
	public String[] getActivityTypeCategories()throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
	        ContentObjectProperty activitySpaceBusinessCategoriesProperty=activitySpaceBco.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories);
	        if(activitySpaceBusinessCategoriesProperty==null){
	        	return null;
	        }else{
	        	return (String[])activitySpaceBusinessCategoriesProperty.getPropertyValue();
	        }
	    } catch (ContentReposityException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }finally{
	        metaDataContentSpace.closeContentSpace();
	    }
	}

	@Override
	public boolean setActivityTypeCategories(String[] categories)throws ActivityEngineRuntimeException, ActivityEngineActivityException {
		try {
	        initContentRepositoryParameter();
	    } catch (ContentReposityRuntimeException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }
		if(categories==null){
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
	        ContentObjectProperty activitySpaceBusinessCategoriesProperty=activitySpaceBco.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories);
	        if(activitySpaceBusinessCategoriesProperty!=null){
	        	activitySpaceBusinessCategoriesProperty.setPropertyValue(categories);
	        	activitySpaceBco.updateProperty(activitySpaceBusinessCategoriesProperty, false);
	        }else{
	        	activitySpaceBco.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories, categories, false);
	        }
	        return true;
	    } catch (ContentReposityException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }finally{
	        metaDataContentSpace.closeContentSpace();
	    }
	}

	@Override
	public boolean addActivityTypeCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException {
		try {
	        initContentRepositoryParameter();
	    } catch (ContentReposityRuntimeException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }
		if(categoryName==null){
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
	        ContentObjectProperty activitySpaceBusinessCategoriesProperty=activitySpaceBco.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories);
	        if(activitySpaceBusinessCategoriesProperty!=null){
	        	String[] currentCategories=(String[])activitySpaceBusinessCategoriesProperty.getPropertyValue();
	        	String[] newCategories=new String[currentCategories.length+1];
	        	for(int i=0;i<currentCategories.length;i++){
	        		String currentCategory=currentCategories[i];
	        		if(currentCategory.equals(categoryName)){
	        			return false;
	        		}else{
	        			newCategories[i]=currentCategories[i];
	        		}
	        	}
	        	newCategories[newCategories.length-1]=categoryName;
	        	activitySpaceBusinessCategoriesProperty.setPropertyValue(newCategories);
	        	activitySpaceBco.updateProperty(activitySpaceBusinessCategoriesProperty, false);
	        }else{
	        	activitySpaceBco.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories, new String[]{categoryName}, false);
	        }
	        return true;
	    } catch (ContentReposityException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }finally{
	        metaDataContentSpace.closeContentSpace();
	    }
	}

	@Override
	public boolean removeActivityTypeCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException {
		try {
	        initContentRepositoryParameter();
	    } catch (ContentReposityRuntimeException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }
		if(categoryName==null){
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
	        ContentObjectProperty activitySpaceBusinessCategoriesProperty=activitySpaceBco.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories);
	        if(activitySpaceBusinessCategoriesProperty!=null){
	        	String[] currentCategories=(String[])activitySpaceBusinessCategoriesProperty.getPropertyValue();
	        	List<String> newCateoriesList=new ArrayList<String>();
	        	for(int i=0;i<currentCategories.length;i++){
	        		String currentCategory=currentCategories[i];
	        		if(!currentCategory.equals(categoryName)){
	        			newCateoriesList.add(categoryName);
	        		}
	        	}
	        	if(newCateoriesList.size()==currentCategories.length){
	        		throw new ActivityEngineRuntimeException();
	        	}
	        	String[] newCategories=new String[newCateoriesList.size()];
	        	newCateoriesList.toArray(newCategories);
	        	activitySpaceBusinessCategoriesProperty.setPropertyValue(newCategories);
	        	activitySpaceBco.updateProperty(activitySpaceBusinessCategoriesProperty, false);
	        }else{
	        	activitySpaceBco.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityCategories, new String[]{categoryName}, false);
	        }
	        return true;
	    } catch (ContentReposityException e) {
	        e.printStackTrace();
	        throw new ActivityEngineRuntimeException();
	    }finally{
	        metaDataContentSpace.closeContentSpace();
	    }
	}
}