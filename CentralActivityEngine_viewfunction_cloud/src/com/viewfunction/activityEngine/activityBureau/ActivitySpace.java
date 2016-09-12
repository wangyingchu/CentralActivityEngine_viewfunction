package com.viewfunction.activityEngine.activityBureau;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.viewfunction.activityEngine.activityView.ProcessQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.ActivityStepDefinition;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.extension.ActivityEventType;
import com.viewfunction.activityEngine.extension.ActivitySpaceEventListener;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.processRepository.exception.ProcessRepositoryRuntimeException;
import com.viewfunction.processRepository.processBureau.ProcessSpace;

public interface ActivitySpace {
	
	public int ACTIVITY_STATUS_FINISHED=ProcessSpace.PROCESS_STATUS_FINISHED;
	public int ACTIVITY_STATUS_UNFINISHED=ProcessSpace.PROCESS_STATUS_UNFINISHED;
	public int ACTIVITY_STATUS_ALL=ProcessSpace.PROCESS_STATUS_ALL;	
	
	public String getActivitySpaceName();
	/*roster management*/
	public Roster[] getRosters() throws ActivityEngineRuntimeException;
	public Roster getRoster(String rosterName) throws ActivityEngineRuntimeException;
	public boolean addRoster(Roster roster) throws ActivityEngineRuntimeException;
	public boolean removeRoster(String rosterName) throws ActivityEngineRuntimeException;
	public Roster updateRoster(String rosterName,String rosterDisplayName,String rosterDescription) throws ActivityEngineRuntimeException;
	
	/*queue management*/
	public RoleQueue[] getRoleQueues(Participant participant);
	public RoleQueue[] getRoleQueues() throws ActivityEngineRuntimeException;	
	public RoleQueue getRoleQueue(String roleQueueName) throws ActivityEngineRuntimeException;	
	public ProcessQueue[] getProcessQueues(String activityType,String stepName);
	
	public boolean addRoleQueue(RoleQueue userQueue) throws ActivityEngineRuntimeException;
	public boolean removeRoleQueue(String queuename) throws ActivityEngineRuntimeException;
	public RoleQueue updateRoleQueue(String roelQueueName,String roleQueueDisplayName,String roleQueueDescription) throws ActivityEngineRuntimeException;
	
	/*role management*/
	public Role[] getRoles() throws ActivityEngineRuntimeException;
	public Role getRole(String roleName) throws ActivityEngineRuntimeException;
	public boolean addRole(Role role) throws ActivityEngineRuntimeException;
	public boolean removeRole(String roleName) throws ActivityEngineRuntimeException;
	public Role updateRole(String roleName,String roleDisplayName,String roleDescription) throws ActivityEngineRuntimeException;
	
	/*participant management*/
	public Participant[] getParticipants() throws ActivityEngineRuntimeException;
	public Participant getParticipant(String participantName) throws ActivityEngineRuntimeException;
	public boolean addParticipant(Participant participant) throws ActivityEngineRuntimeException;
	public boolean removeParticipant(String participantName) throws ActivityEngineRuntimeException;
	public Participant updateParticipant(String participantName,String participantDisplayName,String participantType,String[] roles) throws ActivityEngineRuntimeException;
	
	/* business activity management */
	public boolean addBusinessActivityDefinition(BusinessActivityDefinition bd) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;
	public boolean updateBusinessActivityDefinition(BusinessActivityDefinition bd) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException;
	public boolean refreshBusinessActivityDefinitionWorkflow(String activityType,File workflowDefinitionFile)throws ActivityEngineRuntimeException,ActivityEngineActivityException, ActivityEngineProcessException,ActivityEngineDataException;
	
	public boolean disableBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean enableBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public BusinessActivityDefinition[] getBusinessActivityDefinitions() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;
	public BusinessActivityDefinition getBusinessActivityDefinition(String activityType) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;	
	public String[] getBusinessActivityTypes() throws ActivityEngineRuntimeException;
	public boolean updateBusinessActivityDefinitionProperties(BusinessActivityDefinition bd)throws ActivityEngineRuntimeException, ActivityEngineActivityException,ActivityEngineDataException;
	public boolean addBusinessActivityDefinitionDataFieldDefinition(String activityType,DataFieldDefinition dataField) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;
	public boolean updateBusinessActivityDefinitionDataFieldDefinition(String activityType,DataFieldDefinition dataField) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;;
	public boolean removeBusinessActivityDefinitionDataFieldDefinition(String activityType,String dataFieldName) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;
	public boolean addBusinessActivityDefinitionExposedStep(String activityType,String stepName,String relatedRoleName) throws ActivityEngineRuntimeException,ActivityEngineActivityException;
	public boolean removeBusinessActivityDefinitionExposedStep(String activityType,String stepName) throws ActivityEngineRuntimeException,ActivityEngineActivityException;
	public boolean setBusinessActivityDefinitionExposedStepDataFieldDefinitions(String activityType,String stepName,DataFieldDefinition[] dataFieldDefinitions) throws ActivityEngineRuntimeException,ActivityEngineActivityException;
	public boolean setBusinessActivityDefinitionExposedStepProcessProperties(String activityType,ActivityStepDefinition activityStepDefinition)throws ActivityEngineRuntimeException,ActivityEngineActivityException;
	public boolean setBusinessActivityDefinitionExposedStepDecisionPointProperties(String activityType,ActivityStepDefinition activityStepDefinition)throws ActivityEngineRuntimeException,ActivityEngineActivityException;
	
	public BusinessActivity launchBusinessActivity(String activityType,ActivityData[] initActivityData,String startUserId) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;		
	public BusinessActivity launchBusinessActivity(String activityType,ActivityData[] initActivityData,Map<String,Object> processVariables,String startUserId) throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException;	
	public List<BusinessActivity> getBusinessActivitiesByStartUserId(String startUserId,int activityStatus) throws ProcessRepositoryRuntimeException;	
	public List<ActivityStep> getActivityStepsByInvolvedUserId(String involvedUserId,int activityStatus) throws ProcessRepositoryRuntimeException;	
	public ActivityStep getCurrentActivityStepByStepInfo(String activityType,String activityId,String activityStepName) throws ActivityEngineProcessException;
	public ActivityStep getCurrentActivityStepByStepInfo(String activityId,String activityStepName) throws ActivityEngineProcessException;
	public BusinessActivity getBusinessActivityByActivityInfo(String activityType,String activityId) throws ActivityEngineProcessException;
	public BusinessActivity getBusinessActivityByActivityId(String activityId) throws ActivityEngineProcessException;
	public ActivityStep getFinishedActivityStepByStepInfo(String activityType,String activityId, String activityStepName) throws ActivityEngineProcessException;
	
	public boolean deleteBusinessActivityByActivityId(String activityId,String deleteReason) throws ActivityEngineProcessException;
	public boolean suspendBusinessActivityByActivityId(String activityId) throws ActivityEngineProcessException;
	public boolean activateBusinessActivityByActivityId(String activityId) throws ActivityEngineProcessException;
	
	/*content management*/
	public String getDocumentsFolderPath();
	
	/* Activity Event listener management*/
	public void registerActivityEventListener(ActivityEventType eventType,ActivitySpaceEventListener listener)throws ActivityEngineProcessException;
	
	/*Additional information management*/
	public String[] getActivityTypeCategories()throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean setActivityTypeCategories(String[] categories)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean addActivityTypeCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean removeActivityTypeCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	
	public String[] getActivitySpaceExtendFeatureCategories()throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean setActivitySpaceExtendFeatureCategories(String[] categories)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean addActivitySpaceExtendFeatureCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean removeActivitySpaceExtendFeatureCategory(String categoryName)throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	
	
}