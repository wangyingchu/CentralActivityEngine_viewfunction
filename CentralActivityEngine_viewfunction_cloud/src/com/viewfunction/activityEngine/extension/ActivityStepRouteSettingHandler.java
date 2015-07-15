package com.viewfunction.activityEngine.extension;

import org.activiti.engine.delegate.DelegateTask;

import com.viewfunction.processRepository.extensionImpl.ActivitiStepRouteSettingHandlerImpl;

public class ActivityStepRouteSettingHandler extends ActivitiStepRouteSettingHandlerImpl{

	public ActivityStepRouteSettingHandler(DelegateTask delegateTask) {
		super(delegateTask);	
	}	
}
