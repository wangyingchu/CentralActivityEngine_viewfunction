package com.viewfunction.activityEngine.activityView.commonImpl;

import java.io.Serializable;
import java.util.Date;

import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.ParticipantTask;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public class ParticipantTaskImpl implements ParticipantTask,Serializable{
	private static final long serialVersionUID = -4852366369959556639L;
	private ActivityStep activityStep;
	public ParticipantTaskImpl(ActivityStep activityStep){
		this.activityStep=activityStep;		
	}

	@Override
	public String getActivityType() {		
		return this.activityStep.getActivityType();
	}

	@Override
	public String getRoleName() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {
		if(this.activityStep.getRelatedRole()!=null){
			return  this.activityStep.getRelatedRole().getRoleName();
		}else{
			return null;
		}		
	}

	@Override
	public String getActivityStepName() {		
		return this.activityStep.getActivityStepName();
	}

	@Override
	public String getStepDescription() {		
		return this.activityStep.getStepDescription();
	}

	@Override
	public Date getCreateTime() {		
		return this.activityStep.getCreateTime();
	}

	@Override
	public Date getDueDate() {		
		return this.activityStep.getDueDate();
	}

	@Override
	public String getStepAssignee() {		
		return this.activityStep.getStepAssignee();
	}

	@Override
	public String getStepOwner() {		
		return this.activityStep.getStepOwner();
	}

	@Override
	public ActivityStep getActivityStep() {		
		return this.activityStep;
	}

}
