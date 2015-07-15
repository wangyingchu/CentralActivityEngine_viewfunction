package com.viewfunction.activityEngine.activityView.commonImpl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;

import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.common.CustomAttribute;
import com.viewfunction.activityEngine.activityView.common.CustomStructure;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
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

public class CCRCustomStructureImpl implements CustomStructure{	
	
	private String structureName;	
	private String activitySpaceName;
	private String structureParentPath;	
	private BaseContentObject storageContentObject;
	private String structureId;
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}		
	}
	
	public CCRCustomStructureImpl(String customStructureName,String customStructureParentPath,String activitySpaceName){
		this.structureName=customStructureName;		
		this.setStructureParentPath(customStructureParentPath);	
		this.activitySpaceName=activitySpaceName;
	}

	@Override
	public String getStructureName() {		
		return this.structureName;
	}

	@Override
	public void setStructureName(String structureName) {
		this.structureName=structureName;		
	}

	@Override
	public List<CustomStructure> getSubCustomStructures() throws ActivityEngineRuntimeException, ActivityEngineDataException{
		List<CustomStructure> customeStructuresList=new ArrayList<CustomStructure>();
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customStructureContainerObj;
			try {
				customStructureContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);
				if(customStructureContainerObj==null){				
					customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
				}					
				List<BaseContentObject> subCustomStructuresList=customStructureContainerObj.getSubContentObjects(null);
				for(BaseContentObject currentStructureBaseContentObject:subCustomStructuresList){
					if(!currentStructureBaseContentObject.getContentObjectName().equals(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){						
						JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)this.getStorageContentObject();
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
			}						
		}else{	
		*/	
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customStructureContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);			
					if(customStructureContainerObj==null){				
						customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
					}
					List<BaseContentObject> subCustomStructuresList=customStructureContainerObj.getSubContentObjects(null);
					for(BaseContentObject currentStructureBaseContentObject:subCustomStructuresList){
						if(!currentStructureBaseContentObject.getContentObjectName().equals(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){						
							JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)this.getStorageContentObject();
							String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();						
							CCRCustomStructureImpl currentCustomStructure=new CCRCustomStructureImpl(currentStructureBaseContentObject.getContentObjectName(),customStructureParentPath,this.activitySpaceName);						
							currentCustomStructure.setStorageContentObject(currentStructureBaseContentObject);						
							customeStructuresList.add(currentCustomStructure);						
						}					
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
			
			
			
		//}		
		return customeStructuresList;
	}

	@Override
	public CustomStructure getSubCustomStructure(String structureName) throws ActivityEngineRuntimeException, ActivityEngineDataException{	
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return null;
		}
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customStructureContainerObj;
			try {
				customStructureContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);
				if(customStructureContainerObj==null){				
					customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
				}					
				BaseContentObject targetContentObject=customStructureContainerObj.getSubContentObject(structureName);
				if(targetContentObject==null){
					return null;
				}else{
					JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)this.getStorageContentObject();
					String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();						
					CCRCustomStructureImpl targetCustomStructure=new CCRCustomStructureImpl(structureName,customStructureParentPath,this.activitySpaceName);						
					targetCustomStructure.setStorageContentObject(targetContentObject);
					return targetCustomStructure;
				}				
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			} catch (RepositoryException e) {				
				e.printStackTrace();
				throw new ActivityEngineDataException();
			}						
		}else{
			
		*/	
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}				
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customStructureContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);			
					if(customStructureContainerObj==null){				
						customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
					}
					BaseContentObject targetContentObject=customStructureContainerObj.getSubContentObject(structureName);
					if(targetContentObject==null){
						return null;
					}else{
						JCRContentObjectImpl jcrContentObjectImpl=(JCRContentObjectImpl)this.getStorageContentObject();
						String customStructureParentPath=jcrContentObjectImpl.getJcrNode().getPath();						
						CCRCustomStructureImpl targetCustomStructure=new CCRCustomStructureImpl(structureName,customStructureParentPath,this.activitySpaceName);						
						targetCustomStructure.setStorageContentObject(targetContentObject);
						return targetCustomStructure;
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
		
		
		//}		
		return null;
	}

	@Override
	public boolean addSubCustomStructure(String structureName) throws ActivityEngineRuntimeException{
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return false;
		}
		
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customStructureContainerObj;
			try {
				customStructureContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);
				if(customStructureContainerObj==null){				
					customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
				}					
				BaseContentObject targetContentObject=customStructureContainerObj.getSubContentObject(structureName);
				if(targetContentObject!=null){
					return false;
				}else{
					targetContentObject=customStructureContainerObj.addSubContentObject(structureName, null, false);
					targetContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);
					return true;
				}				
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			} 					
		}else{	
		*/	
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}	
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customStructureContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);			
					if(customStructureContainerObj==null){				
						customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
					}
					BaseContentObject targetContentObject=customStructureContainerObj.getSubContentObject(structureName);
					if(targetContentObject!=null){
						return false;
					}else{
						targetContentObject=customStructureContainerObj.addSubContentObject(structureName, null, false);
						targetContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);
						return true;
					}		
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			} finally{
				metaDataContentSpace.closeContentSpace();			
			}	
		
		
		
		//}		
		return false;
	}

	@Override
	public boolean deleteSubCustomStructure(String structureName) throws ActivityEngineRuntimeException{
		if(structureName.endsWith(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore)){
			return false;
		}
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customStructureContainerObj;
			try {
				customStructureContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);
				if(customStructureContainerObj==null){				
					customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);
					return false;
				}					
				boolean deleteResult=customStructureContainerObj.removeSubContentObject(structureName, false);
				return deleteResult;				
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			}					
		}else{
		*/	
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}						
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customStructureContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore);			
					if(customStructureContainerObj==null){				
						customStructureContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore, null, false);				
						return false;
					}					
					boolean deleteResult=customStructureContainerObj.removeSubContentObject(structureName, false);
					return deleteResult;	
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}	
		//}		
		return false;
	}

	@Override
	public boolean addCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException {
		
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customAttributesContainerObj;
			try {
				customAttributesContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);
				if(customAttributesContainerObj==null){				
					customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
				}	
				ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(customAttribute.getAttributeName());
				if(targetAttributeProperty!=null){
					throw new ActivityEngineDataException();					
				}else{
					ContentObjectProperty resultCustomAttribute=customAttributesContainerObj.addProperty(customAttribute.getAttributeName(), customAttribute.getAttributeValue(), false);	
					if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
						return true;
					}else{
						return false;
					}				
				}	
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			}						
		}else{
		*/	
			
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}					
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customAttributesContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
					if(customAttributesContainerObj==null){				
						customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
					}	
					ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(customAttribute.getAttributeName());
					if(targetAttributeProperty!=null){
						throw new ActivityEngineDataException();					
					}else{
						ContentObjectProperty resultCustomAttribute=customAttributesContainerObj.addProperty(customAttribute.getAttributeName(), customAttribute.getAttributeValue(), false);	
						if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
							return true;
						}else{
							return false;
						}				
					}						
					
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}	
			
			
		//}		
		return false;		
	}

	@Override
	public boolean updateCustomAttribute(CustomAttribute customAttribute) throws ActivityEngineRuntimeException, ActivityEngineDataException {
		
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customAttributesContainerObj;
			try {
				customAttributesContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);
				if(customAttributesContainerObj==null){				
					customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
				}						
				ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(customAttribute.getAttributeName());				
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
					ContentObjectProperty resultCustomAttribute=customAttributesContainerObj.updateProperty(newValueProperty, false);					
					if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
						return true;
					}else{
						return false;
					}								
				}		
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			}						
		}else{
		*/	
			
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}								
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customAttributesContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
					if(customAttributesContainerObj==null){				
						customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
					}						
					ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(customAttribute.getAttributeName());				
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
						ContentObjectProperty resultCustomAttribute=customAttributesContainerObj.updateProperty(newValueProperty, false);					
						if(resultCustomAttribute.getPropertyName().equals(customAttribute.getAttributeName())){
							return true;
						}else{
							return false;
						}						
					}
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}				
		//}		
		return false;
	}

	@Override
	public boolean deleteCustomAttribute(String attributeName) throws ActivityEngineRuntimeException, ActivityEngineDataException {
		
		
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customAttributesContainerObj;
			try {
				customAttributesContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);
				if(customAttributesContainerObj==null){				
					customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
				}					
				ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(attributeName);				
				if(targetAttributeProperty==null){
					throw new ActivityEngineDataException();					
				}else{
					boolean deleteResult=customAttributesContainerObj.removeProperty(attributeName, false);				
					return deleteResult;				
				}				
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			}						
		}else{
		*/	
			
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}								
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customAttributesContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);			
					if(customAttributesContainerObj==null){				
						customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);				
					}						
					ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(attributeName);				
					if(targetAttributeProperty==null){
						throw new ActivityEngineDataException();					
					}else{
						boolean deleteResult=customAttributesContainerObj.removeProperty(attributeName, false);				
						return deleteResult;				
					}				
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}				
		//}		
		return false;
	}

	@Override
	public CustomAttribute getCustomAttribute(String attributeName) throws ActivityEngineRuntimeException {
		/*
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customAttributesContainerObj;
			try {
				customAttributesContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);								
				if(customAttributesContainerObj==null){	
					customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);
					return null;
				}			
				ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(attributeName);				
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
			}						
		}else{
		*/	
			
			
			
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}								
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customAttributesContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);
					if(customAttributesContainerObj==null){		
						customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);
						return null;
					}							
					ContentObjectProperty targetAttributeProperty=customAttributesContainerObj.getProperty(attributeName);				
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
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}				
		//}
		return null;
	}

	@Override
	public List<CustomAttribute> getCustomAttributes() throws ActivityEngineRuntimeException {
		List<CustomAttribute> customAttribute=new ArrayList<CustomAttribute>();
		
		
		/*
		
		if(this.getStorageContentObject()!=null){
			BaseContentObject storageContentObject=this.getStorageContentObject();
			BaseContentObject customAttributesContainerObj;
			try {
				customAttributesContainerObj = storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);								
				if(customAttributesContainerObj==null){	
					customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);
					return null;
				}			
				List<ContentObjectProperty> customAttributesPropList=customAttributesContainerObj.getProperties();
				for(ContentObjectProperty currentContentObjectProperty:customAttributesPropList){
					CustomAttribute targetCustomAttribute=ActivityComponentFactory.createCustomAttribute();				
					targetCustomAttribute.setArrayAttribute(currentContentObjectProperty.isMultiple());
					targetCustomAttribute.setAttributeName(currentContentObjectProperty.getPropertyName());
					targetCustomAttribute.setAttributeType(currentContentObjectProperty.getPropertyType());
					targetCustomAttribute.setAttributeValue(currentContentObjectProperty.getPropertyValue());				
					customAttribute.add(targetCustomAttribute);				
				}			
			} catch (ContentReposityException e) {
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();				
			}						
		}else{
		*/	
			
			
			
			
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
				String storageObjectAbsPath=null;
				if(this.getStructureId()!=null){
					storageObjectAbsPath=this.getStructureId();
				}else{
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();					
				}								
				BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);				
				if(storageContentObject!=null){
					this.setStorageContentObject(storageContentObject);						
					BaseContentObject customAttributesContainerObj=storageContentObject.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore);
					if(customAttributesContainerObj==null){		
						customAttributesContainerObj=storageContentObject.addSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_CustomAttributeStore, null, false);
						return null;
					}							
					List<ContentObjectProperty> customAttributesPropList=customAttributesContainerObj.getProperties();
					for(ContentObjectProperty currentContentObjectProperty:customAttributesPropList){
						CustomAttribute targetCustomAttribute=ActivityComponentFactory.createCustomAttribute();				
						targetCustomAttribute.setArrayAttribute(currentContentObjectProperty.isMultiple());
						targetCustomAttribute.setAttributeName(currentContentObjectProperty.getPropertyName());
						targetCustomAttribute.setAttributeType(currentContentObjectProperty.getPropertyType());
						targetCustomAttribute.setAttributeValue(currentContentObjectProperty.getPropertyValue());				
						customAttribute.add(targetCustomAttribute);				
					}	
				}			
			} catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				metaDataContentSpace.closeContentSpace();			
			}				
			
			
		//}
		return customAttribute;
	}

	public BaseContentObject getStorageContentObject() {
		return storageContentObject;
	}

	public void setStorageContentObject(BaseContentObject storageContentObject) {
		this.storageContentObject = storageContentObject;
	}

	public String getStructureParentPath() {
		return structureParentPath;
	}

	public void setStructureParentPath(String structureParentPath) {
		this.structureParentPath = structureParentPath;
	}

	@Override
	public String getStructureId() throws ActivityEngineRuntimeException {
		if(this.structureId!=null){
			return this.structureId;
		}else{		
			
			/*
			if(this.getStorageContentObject()!=null){
				try {				
					BaseContentObject storageContentObject=this.getStorageContentObject();				
					JCRContentObjectImpl _JCRStorageContentObject=(JCRContentObjectImpl)storageContentObject;				
					String structureId=_JCRStorageContentObject.getJcrNode().getPath();
					return structureId;					
				} catch (RepositoryException e) {					
					e.printStackTrace();
					throw new ActivityEngineRuntimeException();
				}				
			}else{	
			*/	
				
				
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
					String storageObjectParentPath=this.getStructureParentPath();				
					if(!storageObjectParentPath.endsWith("/")){
						storageObjectParentPath=storageObjectParentPath+"/";
					}
					String storageObjectAbsPath=storageObjectParentPath+CCRActivityEngineConstant.ACTIVITYSPACE_CustomStructureStore+"/"+this.getStructureName();		
					BaseContentObject storageContentObject=metaDataContentSpace.getContentObjectByAbsPath(storageObjectAbsPath);
					JCRContentObjectImpl _JCRStorageContentObject=(JCRContentObjectImpl)storageContentObject;				
					String structureId=_JCRStorageContentObject.getJcrNode().getPath();
					return structureId;						
				} catch (ContentReposityException e) {			
					e.printStackTrace();
					throw new ActivityEngineRuntimeException();
				} catch (RepositoryException e) {					
					e.printStackTrace();
					throw new ActivityEngineRuntimeException();
				}finally{
					metaDataContentSpace.closeContentSpace();			
				}	
			//}				
		}		
	}

	@Override
	public void setStructureId(String structureId) {
		this.structureId=structureId;		
	}
}