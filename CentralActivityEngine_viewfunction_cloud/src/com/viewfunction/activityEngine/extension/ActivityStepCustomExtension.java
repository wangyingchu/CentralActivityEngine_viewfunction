package com.viewfunction.activityEngine.extension;

import org.activiti.engine.delegate.DelegateExecution;

import com.viewfunction.processRepository.extension.StepContext;
import com.viewfunction.processRepository.extensionImpl.ActivitiExtensionStepImpl;

public abstract class ActivityStepCustomExtension extends ActivitiExtensionStepImpl{
	
	private ActivityStepContext activityStepContext;
	
	@Override
	public void execute(DelegateExecution delegateExecution) throws Exception {
		this.activityStepContext=new ActivityStepContext(delegateExecution);		
		super.execute(delegateExecution);
	}
	
	@Override
	public void executeExtensionLogic(StepContext stepContext){		
		try{
			executeActivityStepExtensionLogic(this.activityStepContext);
		}catch(Exception exception){
			handelExtensionLogicError(this.activityStepContext,exception);
		}
	}
	
	protected abstract void executeActivityStepExtensionLogic(ActivityStepContext activityStepContext);
	
	protected abstract void handelExtensionLogicError(ActivityStepContext activityStepContext,Exception exception);
}
