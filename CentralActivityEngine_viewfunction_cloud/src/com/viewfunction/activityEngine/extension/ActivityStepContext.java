package com.viewfunction.activityEngine.extension;

import org.activiti.engine.delegate.DelegateExecution;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.extensionImpl.ActivitiStepContextImpl;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public class ActivityStepContext extends ActivitiStepContextImpl{	
	
	private ActivityStepRouteSettingHandler activityStepRouteSettingHandler;
	
	public ActivityStepContext(DelegateExecution delegateExecution) {
		super(delegateExecution);			
	}
	
	public String getActivitySpaceName(){
		String activitySpaceName=this.getProcessSpaceName();
		return activitySpaceName;
	}
	
	public ActivitySpace getActivitySpace(){
		String activitySpaceName=this.getProcessSpaceName();
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(activitySpaceName);
		return activitySpace;		
	}
	
	public BusinessActivity getCurrentBusinessActivity() throws ActivityEngineProcessException{
		if(this.getProcessObjectId()==null){
			return null;
		}		
		try {
			ProcessObject currentProcessObject=this.getProcessObject();
			if(currentProcessObject!=null){
				BusinessActivity currentBusinessActivity=getActivitySpace().getBusinessActivityByActivityInfo(this.getProcessType(), this.getProcessObjectId());
				return currentBusinessActivity;
			}else{
				return null;
			}			
		} catch (ProcessRepositoryRuntimeException e1) {			
			e1.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}
	
	public ActivityStep getCurrentActivityStep() throws ActivityEngineProcessException{
		ActivityStep currentActivityStep=null;
		String activityStepName=null;
		if(this.getProcessStepName()!=null){
			activityStepName=this.getProcessStepName();
			currentActivityStep=getActivitySpace().getCurrentActivityStepByStepInfo(this.getProcessType(), this.getProcessObjectId(), activityStepName);
			return currentActivityStep;
		}else{			
			if(getActivityStepRouteSettingHandler()!=null){
				activityStepName=getActivityStepRouteSettingHandler().getStepName();
				currentActivityStep=getActivitySpace().getCurrentActivityStepByStepInfo(this.getProcessType(), this.getProcessObjectId(), activityStepName);
				return currentActivityStep;
			}else{
				return null;
			}
		}		
	}	
	
	public ActivityStep getActivityStepByName(String stepName) throws ActivityEngineProcessException{		
		ActivityStep currentActivityStep=getActivitySpace().getCurrentActivityStepByStepInfo(this.getProcessType(), this.getProcessObjectId(), stepName);
		return currentActivityStep;
	}	
	
	public BusinessActivityDefinition getCurrentBusinessActivityDefinition() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
		BusinessActivityDefinition currentBusinessActivityDefinition=getActivitySpace().getActivityInstanceActivityDefinitionSnapshoot(this.getProcessType(), this.getProcessObjectId());
		return currentBusinessActivityDefinition;
	}
	
	public String getParticipantDocumentFolderPath(String participantName){
		String participantFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_ParticipantContentStore+"/"+participantName+"/";
		return participantFolderFullPath;
	}
	
	public String getRoleDocumentFolderPath(String roleName){
		String roleFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_RoleContentStore+"/"+roleName+"/";
		return roleFolderFullPath;
	}
	
	public String getApplicationSpaceDocumentFolderPath(){
		String applicationFolderFullPath="/"+CCRActivityEngineConstant.ACTIVITYSPACE_ContentStore+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_SpaceContentStore+"/";
		return applicationFolderFullPath;
	}
	
	public String getCurrentBusinessActivityDocumentFolder(){
		String activityTypeFolderRootAbsPath="/"+this.getProcessType()+"/";
		String currentBusinessActivityFolderFullPath=activityTypeFolderRootAbsPath+this.getProcessObjectId()+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_attachment+"/";	
		return currentBusinessActivityFolderFullPath;
	}

	public ActivityStepRouteSettingHandler getActivityStepRouteSettingHandler() {
		return activityStepRouteSettingHandler;
	}

	public void setActivityStepRouteSettingHandler(ActivityStepRouteSettingHandler activityStepRouteSettingHandler) {
		this.activityStepRouteSettingHandler = activityStepRouteSettingHandler;
	}
}
