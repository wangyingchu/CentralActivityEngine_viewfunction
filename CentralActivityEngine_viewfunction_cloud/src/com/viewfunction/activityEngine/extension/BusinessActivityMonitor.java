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
		try{
			executeBusinessActivityGeneralMonitorLogic(this.activityStepContext);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,BusinessActivityMonitorEventType.General,exception);
		}
	}
	
	public void executeProcessStartMonitorLogic(StepContext stepContext){
		try{
			executeBusinessActivityStartMonitorLogic(this.activityStepContext);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,BusinessActivityMonitorEventType.Start,exception);
		}
	}
	
	public void executeProcessEndMonitorLogic(StepContext stepContext){
		try{
			executeBusinessActivityEndMonitorLogic(this.activityStepContext);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,BusinessActivityMonitorEventType.End,exception);
		}
	}
	
	public void executeProcessTransitionMonitorLogic(StepContext stepContext){
		try{
			executeBusinessActivityTransitionMonitorLogic(this.activityStepContext);
		}catch(Exception exception){
			handelMonitorLogicError(this.activityStepContext,BusinessActivityMonitorEventType.Transition,exception);
		}
	}
	
	public abstract void executeBusinessActivityGeneralMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityStartMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityEndMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void executeBusinessActivityTransitionMonitorLogic(ActivityStepContext activityStepContext);
	
	public abstract void handelMonitorLogicError(ActivityStepContext activityStepContext,BusinessActivityMonitorEventType eventType,Exception exception);
}
