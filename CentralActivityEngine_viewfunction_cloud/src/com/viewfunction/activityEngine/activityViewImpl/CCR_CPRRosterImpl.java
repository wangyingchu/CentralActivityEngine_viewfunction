package com.viewfunction.activityEngine.activityViewImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PropertyType;

import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityBureauImpl.CCR_CPRBusinessActivityImpl;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
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
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCR_CPRRosterImpl implements Roster,Serializable{
	private static final long serialVersionUID = -8414417141766620027L;
	private String activitySpaceName;
	private String rosterName;
	private String description;
	private String displayName;
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	public CCR_CPRRosterImpl(String activitySpaceName,String rosterName){
		this.activitySpaceName=activitySpaceName;
		this.rosterName=rosterName;
	} 	

	@Override
	public String getActivitySpaceName() {		
		return activitySpaceName;
	}

	@Override
	public String getRosterName() {		
		return rosterName;
	}

	@Override
	public String getDescription() {		
		return description;
	}

	@Override
	public void setDescription(String desc) {
		this.description=desc;		
	}

	@Override
	public String getDisplayName() {		
		return displayName;
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName=displayName;		
	}

	@Override
	public String[] getContainedActivityTypes() throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(this.rosterName);									
			if(targetRosterObj==null){				
				throw new ActivityEngineActivityException();				
			}else{								
				BaseContentObject targetRosterObjActivityContainerObj=targetRosterObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_containedActivityType);
				if(targetRosterObjActivityContainerObj==null||targetRosterObjActivityContainerObj.getSubLinkContentObjectsCount()==0){
					return null;
				}else{
					String[] activitysArr=new String[(int)targetRosterObjActivityContainerObj.getSubLinkContentObjectsCount()];					
					List<BaseContentObject> activityList=targetRosterObjActivityContainerObj.getSubLinkContentObjects(null);
					for(int i=0;i<activityList.size();i++){						
						activitysArr[i]=activityList.get(i).getContentObjectName();						
					}					
					return activitysArr;
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
	public boolean addActivityType(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);			
			BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(this.rosterName);
			BaseContentObject addedActivityTypeDefinObj=activityDefineObj.getSubContentObject(activityType);						
			if(targetRosterObj==null||addedActivityTypeDefinObj==null){				
				throw new ActivityEngineActivityException();				
			}else{
				//one ActivityType only belongs to one Roster
				if(addedActivityTypeDefinObj.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_belongsToRoster)!=null){
					throw new ActivityEngineActivityException();					
				}				
				addedActivityTypeDefinObj.addProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_belongsToRoster, this.rosterName, false);				
				BaseContentObject targetRosterObjActivityContainerObj=targetRosterObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_containedActivityType);
				if(targetRosterObjActivityContainerObj==null){
					targetRosterObjActivityContainerObj=targetRosterObj.
							addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_containedActivityType, null, false);
				}				
				if(targetRosterObjActivityContainerObj.getSubLinkContentObject(addedActivityTypeDefinObj.getContentObjectName())!=null){
					return true;
				}				
				return targetRosterObjActivityContainerObj.addSubLinkContentObject(addedActivityTypeDefinObj.getContentObjectName(), addedActivityTypeDefinObj, true);
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}		
	}

	@Override
	public boolean removeActivityType(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException {
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
			BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);			
			BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(this.rosterName);
			BaseContentObject addedActivityTypeDefinObj=activityDefineObj.getSubContentObject(activityType);						
			if(targetRosterObj==null||addedActivityTypeDefinObj==null){				
				throw new ActivityEngineActivityException();				
			}else{				
				addedActivityTypeDefinObj.removeProperty(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition_belongsToRoster, false);				
				BaseContentObject targetRosterObjActivityContainerObj=targetRosterObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_containedActivityType);
				if(targetRosterObjActivityContainerObj==null){					
					return false;
				}				
				return targetRosterObjActivityContainerObj.removeSubLinkContentObject(activityType, false);						
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}				
	}

	@Override
	public boolean setExposedDataFields(DataFieldDefinition[] exposedDataFields) throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException {
		if(exposedDataFields==null){
			throw new ActivityEngineDataException();
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
			BaseContentObject rosterDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_Roster);				
			BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(this.rosterName);
			if(targetRosterObj==null){				
				throw new ActivityEngineActivityException();				
			}else{
				targetRosterObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_dataFields, false);					
				BaseContentObject rosterExposedDataFieldsContainerObj=targetRosterObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_dataFields, null, false);
				for(DataFieldDefinition df:exposedDataFields){
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
					rosterExposedDataFieldsContainerObj.addSubContentObject(df.getFieldName(), paramLst, false);
				}				
			}			
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			metaDataContentSpace.closeContentSpace();			
		}		
		return true;
	}

	@Override
	public DataFieldDefinition[] getExposedDataFields() throws ActivityEngineRuntimeException, ActivityEngineActivityException {		
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
			BaseContentObject targetRosterObj=rosterDefineObj.getSubContentObject(this.rosterName);
			if(targetRosterObj==null){				
				throw new ActivityEngineActivityException();				
			}else{									
				BaseContentObject rosterExposedDataFieldsContainerObj=targetRosterObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RosterDefinition_dataFields);
				if(rosterExposedDataFieldsContainerObj==null){
					return null;
				}else{				
					long dataFieldDefineNum=rosterExposedDataFieldsContainerObj.getSubContentObjectsCount();	
					if(dataFieldDefineNum==0){
						return null;
					}					
					List<BaseContentObject> dataFieldDefineObjList=rosterExposedDataFieldsContainerObj.getSubContentObjects(null);				
					DataFieldDefinition[] dfdArray=new DataFieldDefinition[(int)dataFieldDefineNum];
					for(int i=0;i<dataFieldDefineObjList.size();i++){					
						BaseContentObject dataFieldDefin=dataFieldDefineObjList.get(i);					
						int activityDefineType=((Long)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_fieldType).getPropertyValue())).intValue();
						String activityDefineDisplayName=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_displayName).getPropertyValue().toString();
						String activityDefineDesc=dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_description).getPropertyValue().toString();					
						boolean activityDefineIsArray=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isArrayField).getPropertyValue());
						boolean activityDefineIsSystemField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isSystemField).getPropertyValue());
						boolean activityDefineIsMandatoryField=(Boolean)(dataFieldDefin.getProperty(CCRActivityEngineConstant.ACTIVITYSPACE_DataFieldDefinition_isMandatoryField).getPropertyValue());
						DataFieldDefinition dfd=ActivityComponentFactory.cteateDataFieldDefinition(dataFieldDefin.getContentObjectName(), activityDefineType, activityDefineIsArray);					
						dfd.setDescription(activityDefineDesc);
						dfd.setDisplayName(activityDefineDisplayName);
						dfd.setMandatoryField(activityDefineIsMandatoryField);
						dfd.setSystemField(activityDefineIsSystemField);
						dfdArray[i]=dfd;		
					}						
					return dfdArray;					
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
	public List<BusinessActivity> fetchBusinessActivitys() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineProcessException {		
		try {
			ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);		
			String[] activityTypes=getContainedActivityTypes();			
			List<BusinessActivity> businessActivityList=new ArrayList<BusinessActivity>();
			if(activityTypes!=null){
				for(String activityType:activityTypes){				
					List<ProcessObject>	proObjList=targetProcessSpace.getProcessObjectsByProcessType(activityType,ProcessSpace.PROCESS_STATUS_UNFINISHED);
					for(ProcessObject processObject:proObjList){					
						BusinessActivity currentBusinessActivity=ActivityComponentFactory.createBusinessActivity(processObject.getProcessObjectId(), this.activitySpaceName, activityType);	
						((CCR_CPRBusinessActivityImpl)currentBusinessActivity).setProcessObject(processObject);
						businessActivityList.add(currentBusinessActivity);
					}
				}			
			}			
			return 	businessActivityList;
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}		
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