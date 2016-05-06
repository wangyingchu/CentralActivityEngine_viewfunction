package com.viewfunction.activityEngine.extension;

import com.viewfunction.processRepository.extension.ProcessSpaceEventContext;
import com.viewfunction.processRepository.extensionImpl.ActivitiProcessSpaceEventListenerImpl;

public abstract class ActivitySpaceEventListener extends ActivitiProcessSpaceEventListenerImpl{
	
	@Override
	public void executeEventHandleLogic(ProcessSpaceEventContext processSpaceEventContext){
		ActivitySpaceEventContext activitySpaceEventContext=new ActivitySpaceEventContext(processSpaceEventContext);
		try{
			executeActivitySpaceEventHandleLogic(activitySpaceEventContext);
		}catch(Exception exception){
			handelListenerLogicError(activitySpaceEventContext,exception);
		}
	}
	
	public abstract void executeActivitySpaceEventHandleLogic(ActivitySpaceEventContext activitySpaceEventContext);
	
	public abstract void handelListenerLogicError(ActivitySpaceEventContext activitySpaceEventContext,Exception exception);
}
