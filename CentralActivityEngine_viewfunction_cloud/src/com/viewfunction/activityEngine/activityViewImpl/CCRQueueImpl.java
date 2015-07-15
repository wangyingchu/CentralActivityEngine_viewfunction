package com.viewfunction.activityEngine.activityViewImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.PropertyType;

import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.Queue;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
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

public abstract class CCRQueueImpl implements Queue,Serializable{	
	private static final long serialVersionUID = -5175679239390982098L;
	protected String queueName;
	protected String activitySpaceName;
	protected String displayName;
	protected String description;
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	public CCRQueueImpl(String queueName,String activitySpaceName){
		this.queueName=queueName;
		this.activitySpaceName=activitySpaceName;
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
			BaseContentObject queueDefineObj=null;
			if(this.getQueueType().equals(Queue.QUEUE_TYPE_USERQUEUE)){
				queueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);				
			}else{
				queueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ProcessQueue);	
			}		
			BaseContentObject targetQueueObj=queueDefineObj.getSubContentObject(this.queueName);			
			if(targetQueueObj==null){				
				throw new ActivityEngineActivityException();				
			}else{
				targetQueueObj.removeSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_QueueDefinition_dataFields, false);
				
				BaseContentObject queueExposedDataFieldsContainerObj=targetQueueObj.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_QueueDefinition_dataFields, null, false);
				
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
					queueExposedDataFieldsContainerObj.addSubContentObject(df.getFieldName(), paramLst, false);
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
			BaseContentObject queueDefineObj=null;
			if(this.getQueueType().equals(Queue.QUEUE_TYPE_USERQUEUE)){
				queueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_RoleQueue);				
			}else{
				queueDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ProcessQueue);	
			}		
			BaseContentObject targetQueueObj=queueDefineObj.getSubContentObject(this.queueName);			
			if(targetQueueObj==null){				
				throw new ActivityEngineActivityException();				
			}else{									
				BaseContentObject queueExposedDataFieldsContainerObj=targetQueueObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_QueueDefinition_dataFields);
				if(queueExposedDataFieldsContainerObj==null){
					return null;
				}else{				
					long dataFieldDefineNum=queueExposedDataFieldsContainerObj.getSubContentObjectsCount();	
					if(dataFieldDefineNum==0){
						return null;
					}					
					List<BaseContentObject> dataFieldDefineObjList=queueExposedDataFieldsContainerObj.getSubContentObjects(null);				
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
	public String getActivitySpaceName() {		
		return this.activitySpaceName;
	}

	@Override
	public String getQueueName() {		
		return this.queueName;
	}

	@Override
	public String getDescription() {		
		return this.description;
	}

	@Override
	public String getDisplayName() {		
		return this.displayName;
	}
	
	public void setDescription(String desc) {
		this.description=desc;		
	}
	
	public void setDisplayName(String displayName) {
		this.displayName=displayName;	
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