package com.viewfunction.activityEngine.activityView.commonImpl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityView.common.ActivityComment;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessComment;
import com.viewfunction.processRepository.processBureau.ProcessObject;
import com.viewfunction.processRepository.processBureau.ProcessStep;

public class CCR_CPRActivityStepImpl implements ActivityStep,Serializable{	
	private static final long serialVersionUID = 1800158965130734844L;
	private BusinessActivity businessActivity;
	private ProcessStep processStep;
	private Role relatedRole;	
	private String activityType;	
	
	private String stepName;
	private Date stepCreateTime;
	private Date stepFinishTime;
	private String stepAssignee;
	private String stepDescription;
	private String stepOwner;
	private Date dueDate;
	private String stepDefinitionKey;
	
	public CCR_CPRActivityStepImpl(String activityType){		
		this.activityType=activityType;
	}
	
	@Override
	public ProcessStep getActivityProcessStep() {		
		return this.processStep;
	}
	
	@Override
	public String getActivityType() {		
		return this.activityType;
	}

	@Override
	public String getActivityStepName() {	
		if(this.stepName!=null){
			return this.stepName;
		}else{
			if(this.processStep!=null){
				return this.processStep.getStepName();
			}else{
				return null;
			}			
		}		
	}
	
	@Override
	public Role getRelatedRole() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {		
		if(this.relatedRole==null){
			this.relatedRole=getBusinessActivity().getActivityDefinition().getActivityStepRelatedRole(getActivityStepDefinitionKey());
		}
		return this.relatedRole;
	}

	@Override
	public String getActivityId() {		
		return getBusinessActivity().getActivityId();
	}	

	@Override
	public ProcessObject getActivityProcessObject() throws ActivityEngineProcessException {		
		return getBusinessActivity().getActivityProcessObject();
	}	

	@Override
	public ActivityData[] getActivityStepData() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException {			
		return getBusinessActivity().getStepActivityData(this.getActivityStepDefinitionKey());
	}	

	@Override
	public boolean handleActivityStep(String activeParticipantName) throws ActivityEngineProcessException {	
		try {
			return this.processStep.handleCurrentStep(activeParticipantName);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean saveActivityStep(String activeParticipantName) throws ActivityEngineProcessException {
		try {
			//need save content data
			return this.processStep.saveCurrentStep(activeParticipantName);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean completeActivityStep(String activeParticipantName) throws ActivityEngineProcessException {		
		try {
			saveActivityStep(activeParticipantName);			
			return this.processStep.completeCurrentStep(activeParticipantName);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}		
	}	

	@Override
	public boolean completeActivityStep(String activeParticipantName,Map<String, Object> processVariables)throws ActivityEngineProcessException {
		try {
			saveActivityStep(activeParticipantName);			
			return this.processStep.completeCurrentStep(activeParticipantName,processVariables);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public Date getCreateTime() {
		if(this.stepCreateTime!=null){
			return this.stepCreateTime;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getCreateTime();
		}		
	}

	@Override
	public String getStepAssignee() {
		if(this.stepAssignee!=null){
			return this.stepAssignee;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getStepAssignee();
		}		
	}

	@Override
	public String getStepDescription() {
		if(this.stepDescription!=null){
			return this.stepDescription;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getStepDescription();
		}		
	}

	@Override
	public String getStepOwner() {
		if(this.stepOwner!=null){
			return this.stepOwner;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getStepOwner();	
		}		
	}

	@Override
	public Date getDueDate() {
		if(this.dueDate!=null){
			return this.dueDate;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getDueDate();	
		}		
	}
	
	@Override
	public String getActivityStepDefinitionKey() {
		if(this.stepDefinitionKey!=null){
			return this.stepDefinitionKey;
		}else{
			if(this.processStep==null){
				return null;
			}
			return this.processStep.getStepDefinitionKey();
		}		
	}
	
	@Override
	public BusinessActivity getBusinessActivity() {
		return businessActivity;
	}	
	
	public void setRelatedRole(Role relatedRole) {		
		this.relatedRole=relatedRole;
	}

	public void setBusinessActivity(BusinessActivity businessActivity) {
		this.businessActivity = businessActivity;
	}
	
	public void setProcessStep(ProcessStep processStep) {
		this.processStep = processStep;
	}

	@Override
	public void addComment(ActivityComment activityComment) {
		ProcessComment processComment=new ProcessComment();	
		String roleName;
		if(activityComment.getRole()!=null){
			roleName=activityComment.getRole().getRoleName();
		}else{
			roleName="N/A";
		}
		String processMessage=activityComment.getCommentContent()+"{"+activityComment.getParticipant().getParticipantName()+"|"+roleName+"}";		
		processComment.setCommentMessage(processMessage);		
		this.processStep.addComment(processComment);
	}

	@Override
	public List<ActivityComment> getComments() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException {
		List<ProcessComment> stepCommentList=this.processStep.getComments();
		if(stepCommentList==null||stepCommentList.size()==0){
			return null;
		}else{
			List<ActivityComment> activityCommentsList=new ArrayList<ActivityComment>();		
			ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(this.businessActivity.getActivityDefinition().getActivitySpaceName());			
			for(ProcessComment processComment:stepCommentList){
				ActivityComment currentActivityComment=new ActivityComment();				
				String commentFullMessage=processComment.getCommentMessage();					
				int userInfoIdx=commentFullMessage.lastIndexOf("{");				
				String commentMessage=commentFullMessage.substring(0,userInfoIdx);					
				String userInfStr=commentFullMessage.substring(userInfoIdx+1,commentFullMessage.length()-1);
				int participantDivIdx=userInfStr.lastIndexOf("|");
				String participantName=userInfStr.substring(0,participantDivIdx);					
				String roleName=userInfStr.substring(participantDivIdx+1,userInfStr.length());
				currentActivityComment.setAddDate(processComment.getTime());
				currentActivityComment.setParticipant(activitySpace.getParticipant(participantName));
				if(!roleName.equals("N/A")){
					currentActivityComment.setRole(activitySpace.getRole(roleName));
				}					
				currentActivityComment.setCommentContent(commentMessage);
				activityCommentsList.add(currentActivityComment);					
			}			
			return activityCommentsList;
		}	
	}	

	public void setActivityStepName(String stepName) {
		this.stepName = stepName;
	}	

	public void setStepCreateTime(Date stepCreateTime) {
		this.stepCreateTime = stepCreateTime;
	}

	public void setStepAssignee(String stepAssignee) {
		this.stepAssignee = stepAssignee;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public void setStepOwner(String stepOwner) {
		this.stepOwner = stepOwner;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getStepDefinitionKey() {
		return stepDefinitionKey;
	}

	public void setStepDefinitionKey(String stepDefinitionKey) {
		this.stepDefinitionKey = stepDefinitionKey;
	}

	@Override
	public boolean returnActivityStep() throws ActivityEngineProcessException {		
		try {
			return this.processStep.returnCurrentStep();
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean reassignActivityStep(String newActiveParticipantName) throws ActivityEngineProcessException {		
		try {
			return this.processStep.reassignCurrentStep(newActiveParticipantName);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public Date getFinishTime() {
		if(this.stepFinishTime!=null){
			return this.stepFinishTime;
		}else{
			return null;
		}		
	}
	
	public void setStepFinishTime(Date finishTime) {
		this.stepFinishTime=finishTime;		
	}

	@Override
	public String getActivityStepId() {
		if(this.processStep!=null){
			return this.processStep.getStepId();
		}else{
			return null;
		}
	}
	
	@Override
	public String getParentActivityStepId() {
		if(this.processStep!=null){
			return this.processStep.getParentStepId();
		}else{
			return null;
		}
	}

	@Override
	public boolean hasParentActivityStep() throws ActivityEngineProcessException{
		if(this.processStep!=null){
			return this.processStep.hasParentStep();
		}else{
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean hasChildActivityStep() throws ActivityEngineProcessException{
		if(this.processStep!=null){
			return this.processStep.hasChildStep();
		}else{
			throw new ActivityEngineProcessException();
		}		
	}		

	@Override
	public boolean deleteChildActivityStepByStepId(String stepId) throws ActivityEngineProcessException{		
		if(this.processStep!=null){
			return this.processStep.deleteChildProcessStepByStepId(stepId);
		}else{
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public boolean deleteChildActivitySteps() throws ActivityEngineProcessException{
		if(this.processStep!=null){
			return this.processStep.deleteChildProcessSteps();
		}else{
			throw new ActivityEngineProcessException();
		}		
	}	

	@Override
	public boolean isAllChildActivityStepsFinished() throws ActivityEngineProcessException{
		if(this.processStep!=null){
			return this.processStep.isAllChildProcessStepsFinished();
		}else{
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public ActivityStep createChildActivityStep(String stepAssignee,String childStepName, String childStepDescription,Date childStepDueDate) throws ActivityEngineProcessException{
		if(this.processStep!=null){			
			ProcessStep childProcessStep=this.processStep.createChildProcessStep(stepAssignee, childStepName, childStepDescription, childStepDueDate);
			CCR_CPRActivityStepImpl childActivityStep=new  CCR_CPRActivityStepImpl(this.getActivityType());			
			childActivityStep.setBusinessActivity(this.getBusinessActivity());			
			childActivityStep.setProcessStep(childProcessStep);			
			return childActivityStep;
		}else{
			throw new ActivityEngineProcessException();
		}		
	}
	
	@Override
	public List<ActivityStep> getChildActivitySteps() throws ActivityEngineProcessException{
		if(this.processStep!=null){
			List<ActivityStep> childActivityStepList=new ArrayList<ActivityStep>();			
			List<ProcessStep> childProcessStepList=this.processStep.getChildProcessSteps();
			for(ProcessStep childProcessStep:childProcessStepList){
				CCR_CPRActivityStepImpl childActivityStep=new  CCR_CPRActivityStepImpl(this.getActivityType());			
				childActivityStep.setBusinessActivity(this.getBusinessActivity());			
				childActivityStep.setProcessStep(childProcessStep);		
				if(childProcessStep.getEndTime()!=null){
					childActivityStep.setStepFinishTime(childProcessStep.getEndTime());
				}				
				childActivityStepList.add(childActivityStep);
			}
			return childActivityStepList;			
		}else{
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public ActivityStep getParentActivityStep() throws ActivityEngineProcessException{
		if(this.processStep!=null){					
			if(this.processStep.hasParentStep()){
				ProcessStep parentProcessStep;
				try {
					parentProcessStep = this.processStep.getParentProcessStep();
				} catch (ProcessRepositoryRuntimeException e) {				
					throw new ActivityEngineProcessException();
				}
				if(parentProcessStep!=null){
					CCR_CPRActivityStepImpl parentActivityStep=new  CCR_CPRActivityStepImpl(this.getActivityType());	
					parentActivityStep.setBusinessActivity(this.getBusinessActivity());
					parentActivityStep.setProcessStep(parentProcessStep);		
					if(parentProcessStep.getEndTime()!=null){
						parentActivityStep.setStepFinishTime(parentProcessStep.getEndTime());
					}		
					return parentActivityStep;					
				}else{
					throw new ActivityEngineProcessException();					
				}				
			}else{
				throw new ActivityEngineProcessException();				
			}			
		}else{
			throw new ActivityEngineProcessException();
		}		
	}

	@Override
	public boolean setActivityStepPriority(int priority) throws ActivityEngineProcessException {
		try {
			return this.processStep.setStepPriority(priority);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public int getActivityStepPriority() throws ActivityEngineProcessException {
		try {
			return this.processStep.getStepPriority();
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean isSuspendedActivityStep() throws ActivityEngineProcessException {
		try {
			return this.processStep.isSuspendedStep();
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean delegateActivityStep(String delegateToParticipantName) throws ActivityEngineProcessException {
		try {
			return this.processStep.delegateCurrentStep(delegateToParticipantName);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean isDelegatedActivityStep() throws ActivityEngineProcessException {
		try {
			return this.processStep.isDelegatedStep();
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean finishDelegatedActivityStepWork() throws ActivityEngineProcessException {
		try {
			return this.processStep.resolveDelegateJob();
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public boolean finishDelegatedActivityStepWork(Map<String, Object> processVariables) throws ActivityEngineProcessException {
		try {
			return this.processStep.resolveDelegateJob(processVariables);
		} catch (ProcessRepositoryRuntimeException e) {			
			e.printStackTrace();
			throw new ActivityEngineProcessException();
		}
	}

	@Override
	public int getActivityProcessVersion() throws ActivityEngineProcessException {
		return this.processStep.getProcessDefinitionVersion();
	}	
}