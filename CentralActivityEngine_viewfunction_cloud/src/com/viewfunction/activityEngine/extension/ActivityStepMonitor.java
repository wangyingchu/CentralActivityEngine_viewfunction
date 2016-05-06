package com.viewfunction.activityEngine.extension;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.extension.StepRouteSettingHandler;
import com.viewfunction.processRepository.extensionImpl.ActivitiProcessStepMonitorImpl;

public abstract class ActivityStepMonitor extends ActivitiProcessStepMonitorImpl{

	private static final long serialVersionUID = -6530529949513243057L;
	
	private ActivityStepContext activityStepContext;
	private ActivityStepRouteSettingHandler activityStepRouteSettingHandler;
	
	@Override
	public void notify(DelegateTask delegateTask) {
		DelegateExecution delegateExecution=delegateTask.getExecution();		
		this.activityStepContext=new ActivityStepContext(delegateExecution);
		this.activityStepRouteSettingHandler=new ActivityStepRouteSettingHandler(delegateTask);	
		this.activityStepContext.setActivityStepRouteSettingHandler(this.activityStepRouteSettingHandler);
		super.notify(delegateTask);
	}
	
	public void executeGeneralMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		try{
			executeActivityStepGeneralMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,this.activityStepRouteSettingHandler,ActivityStepMonitorEventType.General,exception);
		}
	}
	
	public void executeStepAssignMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		try{
			executeActivityStepAssignMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,this.activityStepRouteSettingHandler,ActivityStepMonitorEventType.Assign,exception);
		}
	}
	
	public void executeStepCompleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		try{
			executeActivityStepCompleteMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,this.activityStepRouteSettingHandler,ActivityStepMonitorEventType.Complete,exception);
		}
	}
	
	public void executeStepCreateMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		try{
			executeActivityStepCreateMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,this.activityStepRouteSettingHandler,ActivityStepMonitorEventType.Create,exception);
		}
	}
	
	public void executeStepDeleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		try{
			executeActivityStepDeleteMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,this.activityStepRouteSettingHandler,ActivityStepMonitorEventType.Delete,exception);
		}
	}
	
	public abstract void executeActivityStepGeneralMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepAssignMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepCompleteMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepCreateMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepDeleteMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void handelMonitorLogicError(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler,ActivityStepMonitorEventType eventType,Exception exception);

}
