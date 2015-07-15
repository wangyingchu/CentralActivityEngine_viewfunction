package com.viewfunction.activityEngine.extension;

import com.viewfunction.processRepository.extension.ProcessSpaceEventContext;
import com.viewfunction.processRepository.extensionImpl.ActivitiProcessSpaceEventListenerImpl;

public abstract class ActivitySpaceEventListener extends ActivitiProcessSpaceEventListenerImpl{
	
	@Override
	public void executeEventHandleLogic(ProcessSpaceEventContext processSpaceEventContext){
		ActivitySpaceEventContext activitySpaceEventContext=new ActivitySpaceEventContext(processSpaceEventContext);
		executeActivitySpaceEventHandleLogic(activitySpaceEventContext);
	}
	
	public abstract void executeActivitySpaceEventHandleLogic(ActivitySpaceEventContext activitySpaceEventContext);
}
