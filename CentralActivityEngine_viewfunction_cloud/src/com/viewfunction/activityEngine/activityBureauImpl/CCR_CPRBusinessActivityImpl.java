package com.viewfunction.activityEngine.activityBureauImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.common.ActivityComment;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentObjectProperty;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessComment;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessSpace;
import com.viewfunction.processRepository.processBureau.ProcessStep;
import com.viewfunction.processRepository.util.factory.ProcessComponentFactory;

public class CCR_CPRBusinessActivityImpl implements BusinessActivity,Serializable{	
	private static final long serialVersionUID = -504080198003492690L;
	private String activityId;
	private String activityType;
	private String activitySpaceName;
	private ProcessObject processObject;
	
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT_PWD;
	
	public CCR_CPRBusinessActivityImpl(String activityId,String activitySpaceName,String activityType){
		this.activityId=activityId;
		this.activitySpaceName=activitySpaceName;
		this.activityType=activityType;
	}

	@Override
	public ProcessObject getActivityProcessObject() throws ActivityEngineProcessException {	
		if(this.processObject!=null){
			return this.processObject;
		}
		try {
			ProcessSpace targetProcessSpace=ProcessComponentFactory.connectProcessSpace(this.activitySpaceName);
			ProcessObject processObject=targetProcessSpace.getProcessObjectById(this.activityId);
			this.processObject=processObject;
			return processObject;			
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}	

	@Override
	public ActivityData[] getActivityData() throws ActivityEngineRuntimeException, ActivityEngineDataException, ActivityEngineActivityException {
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}
		ContentSpace activityContentSpace = null;
		try {
			activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, this.activitySpaceName);
			RootContentObject activityTypeRootObject=activityContentSpace.getRootContentObject(this.activityType);			
			if(activityTypeRootObject==null){
				throw new  ActivityEngineDataException();								
			}
			BaseContentObject activityInstanceObj=activityTypeRootObject.getSubContentObject(this.activityId);
			if(activityInstanceObj==null){
				throw new  ActivityEngineDataException();
			}
			BaseContentObject activityInstanceDataObject=activityInstanceObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields);			
			List<ContentObjectProperty> propertyList=activityInstanceDataObject.getProperties();
			ActivityData[] activityDataArray=new ActivityData[propertyList.size()];
			DataFieldDefinition[] dataFieldDefinitionArry=getActivityDefinition().getActivityDataFields();
			for(int i=0;i< propertyList.size();i++){		
				ContentObjectProperty contentObjectProperty=propertyList.get(i);
				DataFieldDefinition currentDataFieldDefinition=getDataFieldDefinition(dataFieldDefinitionArry,contentObjectProperty.getPropertyName());				
				ActivityData currentActivityData=ActivityComponentFactory.createActivityData(currentDataFieldDefinition, contentObjectProperty.getPropertyValue());
				activityDataArray[i]=currentActivityData;
			}
			return activityDataArray;					
		}catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			activityContentSpace.closeContentSpace();			
		}
	}
	
	@Override
	public boolean setActivityData(ActivityData[] activityDataArray) throws ActivityEngineRuntimeException, ActivityEngineDataException{
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}
		ContentSpace activityContentSpace = null;
		try {
			activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, this.activitySpaceName);
			RootContentObject activityTypeRootObject=activityContentSpace.getRootContentObject(this.activityType);			
			if(activityTypeRootObject==null){
				throw new  ActivityEngineDataException();								
			}
			BaseContentObject activityInstanceObj=activityTypeRootObject.getSubContentObject(this.activityId);
			if(activityInstanceObj==null){
				throw new  ActivityEngineDataException();
			}
			BaseContentObject activityInstanceDataObject=activityInstanceObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields);			
			for(int i=0;i<activityDataArray.length;i++){
				ActivityData currentActivityData=activityDataArray[i];				
				String fieldName=currentActivityData.getDataFieldDefinition().getFieldName();					
				ContentObjectProperty objectProperty=activityInstanceDataObject.getProperty(fieldName);					
				if(objectProperty==null){
					activityInstanceDataObject.addProperty(fieldName, currentActivityData.getDatFieldValue(), true);					
				}else{					
					objectProperty.setPropertyValue(currentActivityData.getDatFieldValue());
					activityInstanceDataObject.updateProperty(objectProperty, true);					
				}				
			}			
			return true;					
		}catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			activityContentSpace.closeContentSpace();			
		}
	}
	
	private DataFieldDefinition getDataFieldDefinition(DataFieldDefinition[] dataFieldDefinitionArry,String dataFieldName) throws ActivityEngineRuntimeException{
		for(DataFieldDefinition _DataFieldDefinition:dataFieldDefinitionArry){
			if(_DataFieldDefinition.getFieldName().equals(dataFieldName)){
				return _DataFieldDefinition;
			}			
		}
		throw new ActivityEngineRuntimeException();		
	}

	@Override
	public ActivityData[] getStepActivityData(String stepName) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {	
		BusinessActivityDefinition currentBusinessActivityDefinition=getActivityDefinition();		
		if(!currentBusinessActivityDefinition.containsExposedActivityStep(stepName)){
			throw new ActivityEngineProcessException();			
		}	
		DataFieldDefinition[] dataFieldDefinitionArray=currentBusinessActivityDefinition.getActivityStepsExposedDataField().get(stepName);	
		if(dataFieldDefinitionArray==null){
			return null;		
		}
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}		
		ContentSpace activityContentSpace = null;
		try {
			activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, this.activitySpaceName);
			RootContentObject activityTypeRootObject=activityContentSpace.getRootContentObject(this.activityType);			
			if(activityTypeRootObject==null){
				throw new  ActivityEngineDataException();								
			}
			BaseContentObject activityInstanceObj=activityTypeRootObject.getSubContentObject(this.activityId);
			if(activityInstanceObj==null){
				throw new  ActivityEngineDataException();
			}
			BaseContentObject activityInstanceDataObject=activityInstanceObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields);
			if(activityInstanceDataObject!=null){
				ActivityData[] activityDataArray=new ActivityData[dataFieldDefinitionArray.length];	
				for(int i=0;i<dataFieldDefinitionArray.length;i++){				
					ContentObjectProperty contentObjectProperty=activityInstanceDataObject.getProperty(dataFieldDefinitionArray[i].getFieldName());					
					ActivityData currentActivityData=ActivityComponentFactory.createActivityData(dataFieldDefinitionArray[i],
							contentObjectProperty!=null?contentObjectProperty.getPropertyValue():null);
					activityDataArray[i]=currentActivityData;				
				}		
				return activityDataArray;	
			}else{
				return null;
			}							
		}catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			activityContentSpace.closeContentSpace();			
		}
	}
	
	@Override
	public ActivityData[] getActivityData(DataFieldDefinition[] dataFieldDefinitionArray) throws ActivityEngineRuntimeException, ActivityEngineDataException {	
		if(dataFieldDefinitionArray==null){
			return null;		
		}
		try {
			initContentRepositoryParameter();
		} catch (ContentReposityRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();	
		}		
		ContentSpace activityContentSpace = null;
		try {
			activityContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN_ADMINISTRATOR_ACCOUNT_PWD, this.activitySpaceName);
			RootContentObject activityTypeRootObject=activityContentSpace.getRootContentObject(this.activityType);			
			if(activityTypeRootObject==null){
				throw new  ActivityEngineDataException();								
			}
			BaseContentObject activityInstanceObj=activityTypeRootObject.getSubContentObject(this.activityId);
			if(activityInstanceObj==null){
				throw new  ActivityEngineDataException();
			}
			BaseContentObject activityInstanceDataObject=activityInstanceObj.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_dataFields);
			if(activityInstanceDataObject!=null){
				ActivityData[] activityDataArray=new ActivityData[dataFieldDefinitionArray.length];	
				for(int i=0;i<dataFieldDefinitionArray.length;i++){				
					ContentObjectProperty contentObjectProperty=activityInstanceDataObject.getProperty(dataFieldDefinitionArray[i].getFieldName());					
					ActivityData currentActivityData=ActivityComponentFactory.createActivityData(dataFieldDefinitionArray[i],
							contentObjectProperty!=null?contentObjectProperty.getPropertyValue():null);
					activityDataArray[i]=currentActivityData;				
				}		
				return activityDataArray;	
			}else{
				return null;
			}							
		}catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			activityContentSpace.closeContentSpace();			
		}
	}

	@Override
	public BusinessActivityDefinition getActivityDefinition() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {		
		ActivitySpace activitySpace =ActivityComponentFactory.getActivitySpace(this.activitySpaceName);
		BusinessActivityDefinition businessActivityDefinition=activitySpace.getBusinessActivityDefinition(this.activityType);		
		return businessActivityDefinition;
	}

	@Override
	public String getActivityId() {		
		return this.activityId;
	}

	@Override
	public String getRosterName() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {		
		return getActivityDefinition().getRosterName();
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

	public void setProcessObject(ProcessObject processObject) {
		this.processObject = processObject;
	}

	@Override
	public void addComment(ActivityComment activityComment) throws ActivityEngineProcessException {
		ProcessComment processComment=new ProcessComment();
		String roleName;
		if(activityComment.getRole()!=null){
			roleName=activityComment.getRole().getRoleName();
		}else{
			roleName="N/A";
		}
		String processMessage=activityComment.getCommentContent()+"{"+activityComment.getParticipant().getParticipantName()+"|"+roleName+"}";		
		processComment.setCommentMessage(processMessage);		
		getActivityProcessObject().addComment(processComment);		
	}

	@Override
	public List<ActivityComment> getComments() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {
		List<ProcessComment> stepCommentList=getActivityProcessObject().getComments();
		if(stepCommentList==null||stepCommentList.size()==0){
			return null;
		}else{
			List<ActivityComment> activityCommentsList=new ArrayList<ActivityComment>();
			ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(this.getActivityDefinition().getActivitySpaceName());
			for(ProcessComment processComment:stepCommentList){				
				ActivityComment currentActivityComment=new ActivityComment();				
				String commentFullMessage=processComment.getCommentMessage();					
				int userInfoIdx=commentFullMessage.lastIndexOf("{");				
				String commentMessage=commentFullMessage.substring(0,userInfoIdx);					
				String userInfStr=commentFullMessage.substring(userInfoIdx+1,commentFullMessage.length()-1);
				int participantDivIdx=userInfStr.lastIndexOf("|");
				String participantName=userInfStr.substring(0,participantDivIdx);					
				String roleName=userInfStr.substring(participantDivIdx+1,userInfStr.length());
				currentActivityComment.setAddDate(processComment.getTime());
				currentActivityComment.setParticipant(activitySpace.getParticipant(participantName));
				if(!roleName.equals("N/A")){
					currentActivityComment.setRole(activitySpace.getRole(roleName));
				}				
				currentActivityComment.setCommentContent(commentMessage);
				activityCommentsList.add(currentActivityComment);						
			}			
			return activityCommentsList;
		}	
	}

	@Override
	public boolean isFinished() throws ActivityEngineProcessException {
		return getActivityProcessObject().isFinished();		
	}

	@Override
	public ActivityStep getCurrentActivityStepByStepName(String activityStepName)throws ActivityEngineProcessException {
		CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(this.activityType);				
		currentActivityStep.setBusinessActivity(this);		
		List<ProcessStep> processStepList=this.getActivityProcessObject().getCurrentProcessSteps();			
		for(ProcessStep curentProcessStep:processStepList){
			if(curentProcessStep.getStepName().equals(activityStepName)){
				currentActivityStep.setProcessStep(curentProcessStep);
			}				
		}		
		return currentActivityStep;
	}

	@Override
	public List<ActivityStep> getCurrentActivitySteps()	throws ActivityEngineProcessException {
		List<ActivityStep> activityStepsList=new ArrayList<ActivityStep>();		
		List<ProcessStep> processStepList=this.getActivityProcessObject().getCurrentProcessSteps();			
		for(ProcessStep curentProcessStep:processStepList){
			CCR_CPRActivityStepImpl currentActivityStep=(CCR_CPRActivityStepImpl)ActivityComponentFactory.createActivityStep(this.activityType);				
			currentActivityStep.setBusinessActivity(this);
			currentActivityStep.setProcessStep(curentProcessStep);			
			activityStepsList.add(currentActivityStep);							
		}				
		return activityStepsList;
	}

	@Override
	public String getDocumentsFolderPath() {
		String activityTypeFolderRootAbsPath="/"+this.activityType+"/";
		String currentBusinessActivityFolderFullPath=activityTypeFolderRootAbsPath+this.activityId+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_attachment+"/";	
		return currentBusinessActivityFolderFullPath;
	}	
}