package com.viewfunction.activityEngine.extension;

import org.activiti.engine.delegate.DelegateExecution;

import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.extensionImpl.ActivitiProcessMonitorImpl;

public abstract class BusinessActivityMonitor extends ActivitiProcessMonitorImpl {

	private static final long serialVersionUID = -3886386705862531018L;
	
	private ActivityStepContext activityStepContext;

	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		this.activityStepContext=new ActivityStepContext(delegateExecution);
		super.notify(delegateExecution);
	}
	
	public void executeGeneralMonitorLogic(StepContext stepContext){
		executeBusinessActivityGeneralMonitorLogic(this.activityStepContext);
	};
	
	public void executeProcessStartMonitorLogic(StepContext stepContext){
		executeBusinessActivityStartMonitorLogic(this.activityStepContext);
	};
	
	public void executeProcessEndMonitorLogic(StepContext stepContext){
		executeBusinessActivityEndMonitorLogic(this.activityStepContext);
	};
	
	public void executeProcessTransitionMonitorLogic(StepContext stepContext){
		executeBusinessActivityTransitionMonitorLogic(this.activityStepContext);
	};
	
	public abstract void executeBusinessActivityGeneralMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityStartMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityEndMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityTransitionMonitorLogic(ActivityStepContext activityStepContext);
}
