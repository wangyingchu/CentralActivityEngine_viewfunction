package com.viewfunction.activityEngine.helperImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.helper.BatchOperationHelper;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentObjectProperty;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;

public class CCR_CPRBatchOperationHelperImpl implements BatchOperationHelper{
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	public List<ActivityData[]> batchQueryActivityStepsData(String activitySpaceName,List<ActivityStep> activityStepsList,Map<String, BusinessActivityDefinition> businessActivityDefinitionMap) throws ActivityEngineRuntimeException, ActivityEngineDataException, ActivityEngineActivityException{
		List<ActivityData[]> resultActivityDataArraysList=new ArrayList<ActivityData[]>();
		if(activityStepsList!=null&&activityStepsList.size()>0){
			try {
				initContentRepositoryParameter();
			} catch (ContentReposityRuntimeException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();	
			}		
			ContentSpace activityContentSpace = null;
			try {
				activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD,activitySpaceName);
				for(ActivityStep currentActivityStep:activityStepsList){
					BusinessActivityDefinition targetActivityDefinition=businessActivityDefinitionMap.get(currentActivityStep.getActivityType());
					if(targetActivityDefinition!=null){
						String activityType=currentActivityStep.getActivityType();
						String activityId=currentActivityStep.getActivityId();
						String activityStepName=currentActivityStep.getActivityStepDefinitionKey();	
						
						RootContentObject activityTypeRootObject=activityContentSpace.getRootContentObject(activityType);			
						if(activityTypeRootObject==null){
							throw new  ActivityEngineDataException();								
						}
						BaseContentObject activityInstanceObj=activityTypeRootObject.getSubContentObject(activityId);
						if(activityInstanceObj==null){
							throw new  ActivityEngineDataException();
						}
						BaseContentObject activityInstanceDataObject=activityInstanceObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields);
						
						DataFieldDefinition[] dataFieldDefinitionArray=targetActivityDefinition.getActivityStepsExposedDataField().get(activityStepName);
						if(activityInstanceDataObject!=null){
							ActivityData[] activityDataArray=new ActivityData[dataFieldDefinitionArray.length];	
							for(int i=0;i<dataFieldDefinitionArray.length;i++){				
								ContentObjectProperty contentObjectProperty=activityInstanceDataObject.getProperty(dataFieldDefinitionArray[i].getFieldName());					
								ActivityData currentActivityData=ActivityComponentFactory.createActivityData(dataFieldDefinitionArray[i],
										contentObjectProperty!=null?contentObjectProperty.getPropertyValue():null);
								activityDataArray[i]=currentActivityData;				
							}		
							resultActivityDataArraysList.add(activityDataArray);
						}else{
							resultActivityDataArraysList.add(new ActivityData[0]);
						}
					}
				}
			}catch (ContentReposityException e) {			
				e.printStackTrace();
				throw new ActivityEngineRuntimeException();
			}finally{
				activityContentSpace.closeContentSpace();			
			}
		}
		return resultActivityDataArraysList;
	}
	
	private void initContentRepositoryParameter() throws ContentReposityRuntimeException{
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = com.viewfunction.contentRepository.util.PerportyHandler.getPerportyValue(
					com.viewfunction.contentRepository.util.PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN_ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT_PWD=com.viewfunction.contentRepository.util.PerportyHandler.getPerportyValue(
					com.viewfunction.contentRepository.util.PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}		
	}	
}
