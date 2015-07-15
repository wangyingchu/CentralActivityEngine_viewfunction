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
		executeActivityStepGeneralMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
	};
	
	public void executeStepAssignMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		executeActivityStepAssignMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
	};
	
	public void executeStepCompleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		executeActivityStepCompleteMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
	};
	
	public void executeStepCreateMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		executeActivityStepCreateMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
	};
	
	public void executeStepDeleteMonitorLogic(StepContext stepContext,StepRouteSettingHandler stepRouteSettingHandler){
		executeActivityStepDeleteMonitorLogic(this.activityStepContext,this.activityStepRouteSettingHandler);
	};
	
	public abstract void executeActivityStepGeneralMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepAssignMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepCompleteMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepCreateMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);
	
	public abstract void executeActivityStepDeleteMonitorLogic(ActivityStepContext activityStepContext,ActivityStepRouteSettingHandler activityStepRouteSettingHandler);

}
