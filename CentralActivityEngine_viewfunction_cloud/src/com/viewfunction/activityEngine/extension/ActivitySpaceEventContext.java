package com.viewfunction.activityEngine.extension;

import java.util.ArrayList;
import java.util.List;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.commonImpl.CCR_CPRActivityStepImpl;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.processRepository.extension.ExtensionStepRuntimeInfo;
import com.viewfunction.processRepository.extension.ProcessSpaceEventContext;
import com.viewfunction.processRepository.processBureau.HistoricProcessStep;
import com.viewfunction.processRepository.processBureau.ProcessObject;

public class ActivitySpaceEventContext{
	
	private ProcessSpaceEventContext processSpaceEventContext;

	public ActivitySpaceEventContext(ProcessSpaceEventContext processSpaceEventContext) {
		this.processSpaceEventContext=processSpaceEventContext;
	}
	
	public String getActivitySpaceName(){
		String activitySpaceName=this.processSpaceEventContext.getProcessSpaceName();
		return activitySpaceName;
	}
	
	public ActivitySpace getActivitySpace(){
		String activitySpaceName=this.processSpaceEventContext.getProcessSpaceName();
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(activitySpaceName);
		return activitySpace;		
	}

	public BusinessActivity getAttachedBusinessActivity() throws ActivityEngineProcessException{
		ProcessObject currentProcessObject=this.processSpaceEventContext.getEventAttachedProcessObject();
		if(currentProcessObject==null){
			return null;
		}else{
			if(currentProcessObject.getProcessObjectId()==null){
				return null;
			}else{
				BusinessActivity currentBusinessActivity;
				try {
					currentBusinessActivity = getActivitySpace().getBusinessActivityByActivityInfo(this.processSpaceEventContext.getProcessType(), currentProcessObject.getProcessObjectId());
					return currentBusinessActivity;
				} catch (ActivityEngineProcessException e) {
					e.printStackTrace();
					throw new ActivityEngineProcessException();
				}
			}	
		}
	}
	
	public BusinessActivityDefinition getAttachedBusinessActivityDefinition() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
		BusinessActivityDefinition currentBusinessActivityDefinition=getActivitySpace().getBusinessActivityDefinition(this.processSpaceEventContext.getProcessType());
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
	
	public String getAttachedBusinessActivityDocumentFolder(){
		ProcessObject currentProcessObject=this.processSpaceEventContext.getEventAttachedProcessObject();
		if(currentProcessObject==null){
			return null;
		}else{
			String activityTypeFolderRootAbsPath="/"+this.processSpaceEventContext.getProcessType()+"/";
			String currentBusinessActivityFolderFullPath=activityTypeFolderRootAbsPath+currentProcessObject.getProcessObjectId()+"/"+CCRActivityEngineConstant.ACTIVITYSPACE_ActivityInstanceDefinition_attachment+"/";	
			return currentBusinessActivityFolderFullPath;
		}
	}
	
	public ActivityEventType getAttachedActivityEventType(){
		switch(this.processSpaceEventContext.getProcessSpaceEventType()){
			case PROCESSSTEP_ASSIGNED: return ActivityEventType.ACTIVITYSTEP_ASSIGNED;
			case PROCESSSTEP_CREATED: return ActivityEventType.ACTIVITYSTEP_CREATED;
			case PROCESSSTEP_COMPLETED: return ActivityEventType.ACTIVITYSTEP_COMPLETED;
			case PROCESSINSTANCE_COMPLETED: return ActivityEventType.BUSINESSACTIVITY_COMPLETED;
			case PROCESSINSTANCE_CANCELLED: return ActivityEventType.BUSINESSACTIVITY_CANCELLED;
			case PROCESSEXTENSIONSTEP_SUCCESS: return ActivityEventType.ACTIVITYSTEPMONITOR_SUCCESS;
			case PROCESSEXTENSIONSTEP_FAILURE: return ActivityEventType.ACTIVITYSTEPMONITOR_FAILURE;
			case PROCESSEXTENSIONSTEP_RETRY_DECREMENTED: return ActivityEventType.ACTIVITYSTEPMONITOR_RETRY_DECREMENTED;
			case PROCESSEXTENSIONSTEP_CANCELED: return ActivityEventType.ACTIVITYSTEPMONITOR_CANCELED;
		}
		return ActivityEventType.ACTIVITYEVENT_UNKNOWN;
	}
	
	public List<ActivityStep> getAttachedActivitySteps() throws ActivityEngineProcessException{
		switch(getAttachedActivityEventType()){
			case BUSINESSACTIVITY_COMPLETED: return null;
			case BUSINESSACTIVITY_CANCELLED: return null;
			case ACTIVITYSTEPMONITOR_SUCCESS: return null;
			case ACTIVITYSTEPMONITOR_FAILURE: return null;
			case ACTIVITYSTEPMONITOR_RETRY_DECREMENTED: return null;
			case ACTIVITYSTEPMONITOR_CANCELED: return null;
			case ACTIVITYEVENT_UNKNOWN: return null;
		}
		ProcessObject currentProcessObject=this.processSpaceEventContext.getEventAttachedProcessObject();
		if(currentProcessObject==null){
			return null;
		}else{
			List<HistoricProcessStep> historicProcessSteps=this.processSpaceEventContext.getEventAttachedProcessSteps();
			if(historicProcessSteps!= null){
				List<ActivityStep> activityStepList=new ArrayList<ActivityStep>();
				for(HistoricProcessStep historicProcessStep:historicProcessSteps){
					ActivityStep currentActivityStep=null;
					String activityStepName=historicProcessStep.getStepName();
					switch(getAttachedActivityEventType()){
						case ACTIVITYSTEP_ASSIGNED:
							currentActivityStep=getActivitySpace().getCurrentActivityStepByStepInfo(this.processSpaceEventContext.getProcessType(), currentProcessObject.getProcessObjectId(), activityStepName);
							break;
						case ACTIVITYSTEP_CREATED:
							currentActivityStep=getActivitySpace().getCurrentActivityStepByStepInfo(this.processSpaceEventContext.getProcessType(), currentProcessObject.getProcessObjectId(), activityStepName);
							break;
						case ACTIVITYSTEP_COMPLETED:;
							currentActivityStep=getActivitySpace().getFinishedActivityStepByStepInfo(this.processSpaceEventContext.getProcessType(), currentProcessObject.getProcessObjectId(), activityStepName);
							break;
					}
					CCR_CPRActivityStepImpl activityStepImpl=(CCR_CPRActivityStepImpl)currentActivityStep;
					activityStepImpl.setStepAssignee(historicProcessStep.getStepAssignee());
					activityStepImpl.setStepDefinitionKey(historicProcessStep.getStepDefinitionKey());
					activityStepImpl.setActivityStepName(activityStepName);
					activityStepImpl.setStepCreateTime(historicProcessStep.getStartTime());
					activityStepImpl.setBusinessActivity(getAttachedBusinessActivity());
					activityStepList.add(currentActivityStep);
				}
				return activityStepList;
			}else{
				return null;
			}
		}
	}
	
	public List<ActivityStepMonitorRuntimeInfo> getAttachedActivityStepMonitorsInfo(){
		switch(getAttachedActivityEventType()){
			case BUSINESSACTIVITY_COMPLETED: return null;
			case BUSINESSACTIVITY_CANCELLED: return null;
			case ACTIVITYEVENT_UNKNOWN: return null;
			case ACTIVITYSTEP_ASSIGNED:return null;
			case ACTIVITYSTEP_CREATED:return null;
			case ACTIVITYSTEP_COMPLETED:return null;
		}
		List<ExtensionStepRuntimeInfo> extensionStepRuntimeInfoKist=this.processSpaceEventContext.getEventAttachedExtensionSteps();
		List<ActivityStepMonitorRuntimeInfo> activityStepMonitorRuntimeInfoList=new ArrayList<ActivityStepMonitorRuntimeInfo>();
		for(ExtensionStepRuntimeInfo extensionStepRuntimeInfo:extensionStepRuntimeInfoKist){
			ActivityStepMonitorRuntimeInfo currentActivityStepMonitorRuntimeInfo=new ActivityStepMonitorRuntimeInfo();
			currentActivityStepMonitorRuntimeInfo.setActivityStepMonitorClassName(extensionStepRuntimeInfo.getExtensionStepClassName());
			currentActivityStepMonitorRuntimeInfo.setMonitorDefinitionKey(extensionStepRuntimeInfo.getStepDefinitionKey());
			currentActivityStepMonitorRuntimeInfo.setMonitorRetryTimes(extensionStepRuntimeInfo.getRetryTimes());
			currentActivityStepMonitorRuntimeInfo.setMonitorStepId(extensionStepRuntimeInfo.getStepId());
			currentActivityStepMonitorRuntimeInfo.setRelatedProcessDefinitionId(extensionStepRuntimeInfo.getProcessDefinitionId());
			currentActivityStepMonitorRuntimeInfo.setRelatedProcessObjectId(extensionStepRuntimeInfo.getProcessObjectId());
			activityStepMonitorRuntimeInfoList.add(currentActivityStepMonitorRuntimeInfo);
		}
		return activityStepMonitorRuntimeInfoList;
	}
}
