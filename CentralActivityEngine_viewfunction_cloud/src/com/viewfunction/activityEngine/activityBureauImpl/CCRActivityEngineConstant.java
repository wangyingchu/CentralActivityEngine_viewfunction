package com.viewfunction.activityEngine.activityBureauImpl;

public interface CCRActivityEngineConstant {
	String ACTIVITYENGINE_METADATA_CONTENTSPACE="AE_METADATA_CONTENTSPACE";
	String ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT="ACTIVITYSPACE_DEFINATION_ROOT";
	//ActivitySpace structure defination
	String ACTIVITYSPACE_Role="ACTIVITYSPACE_Role";
	String ACTIVITYSPACE_Participant="ACTIVITYSPACE_Participant";
	String ACTIVITYSPACE_Roster="ACTIVITYSPACE_Roster";
	String ACTIVITYSPACE_RoleQueue="ACTIVITYSPACE_RoleQueue";
	String ACTIVITYSPACE_ProcessQueue="ACTIVITYSPACE_ProcessQueue";
	String ACTIVITYSPACE_ActivityDefinition="ACTIVITYSPACE_ActivityDefinition";	
	//ActivityDefinition property Define
	String ACTIVITYSPACE_ActivityDefinition_dataFields="ActivityDefinition_dataFields";
	String ACTIVITYSPACE_ActivityDefinition_isEnabled="ActivityDefinition_isEnabled";	
	String ACTIVITYSPACE_ActivityDefinition_description="ActivityDefinition_description";
	String ACTIVITYSPACE_ActivityDefinition_categories="ActivityDefinition_categories";	
	String ACTIVITYSPACE_ActivityDefinition_belongsToRoster="ActivityDefinition_belongsToRoster";
	String ACTIVITYSPACE_ActivityDefinition_definitionResource="ActivityDefinition_definitionResource";
	String ACTIVITYSPACE_ActivityDefinition_steps="ActivityDefinition_steps";	
	String ACTIVITYSPACE_ActivityDefinition_exposedSteps="ActivityDefinition_exposedSteps";
	String ACTIVITYSPACE_ActivityDefinition_stepRole="ActivityDefinition_stepRole";
	String ACTIVITYSPACE_ActivityDefinition_launchPoint="ActivityDefinition_launchPoint";	
	String ACTIVITYSPACE_ActivityDefinition_launchPointExposedDataFields="ActivityDefinition_launchPointExposedDataFields";	
	String ACTIVITYSPACE_ActivityDefinition_launchRoles="ActivityDefinition_launchRoles";
	String ACTIVITYSPACE_ActivityDefinition_launchParticipants="ActivityDefinition_launchParticipants";
	String ACTIVITYSPACE_ActivityDefinition_launchDecisionPointAttributeName="ActivityDefinition_LaunchDecisionPointAttributeName";
	String ACTIVITYSPACE_ActivityDefinition_launchDecisionPointChoiseList="ActivityDefinition_LaunchDecisionPointChoiseList";	
	String ACTIVITYSPACE_ActivityDefinition_launchProcessVariableList="ActivityDefinition_LaunchProcessVariableList";
	String ACTIVITYSPACE_ActivityDefinition_launchUserIdentityAttributeName="ActivityDefinition_LaunchUserIdentityAttributeName"; 
	
	String ACTIVITYSPACE_ActivityDefinition_stepDecisionPointAttributeName="ActivityDefinition_StepDecisionPointAttributeName";
	String ACTIVITYSPACE_ActivityDefinition_stepDecisionPointChoiseList="ActivityDefinition_StepDecisionPointChoiseList";		
	String ACTIVITYSPACE_ActivityDefinition_stepProcessVariableList="ActivityDefinition_StepProcessVariableList";
	String ACTIVITYSPACE_ActivityDefinition_stepUserIdentityAttributeName="ActivityDefinition_StepUserIdentityAttributeName";
	
	//Activity Content store definition  
	String ACTIVITYSPACE_ContentStore="ActivitySpace_ContentStore";	
	String ACTIVITYSPACE_ParticipantContentStore="Participant_ContentStore";
	String ACTIVITYSPACE_RoleContentStore="Role_ContentStore";
	String ACTIVITYSPACE_SpaceContentStore="Space_ContentStore";
	
	//ActivityMessage & notice store definition
	String ACTIVITYSPACE_MessageStore="ActivitySpace_MessageStore";	
	String ACTIVITYSPACE_ParticipantMessageStore="Participant_MessageStore";
	//String ACTIVITYSPACE_RoleMessageStore="Role_MessageStore";
	//String ACTIVITYSPACE_SpaceMessageStore="Space_MessageStore";
	
	//role property Define
	String ACTIVITYSPACE_Role_desc="Role_desc";
	String ACTIVITYSPACE_Role_displayName="Role_displayName";
	String ACTIVITYSPACE_Role_participantContainer="Role_participantContainer";
	String ACTIVITYSPACE_Role_belongsToRoleQueue="Role_belongsToRoleQueue";
	//Participant property Define
	String ACTIVITYSPACE_Participant_participantType="Participant_participantType";	
	String ACTIVITYSPACE_Participant_displayName="Participant_displayName";
	String ACTIVITYSPACE_Participant_participantRoles="Participant_participantRoles";
	String ACTIVITYSPACE_Participant_belongedRoleContainer="Participant_belongedRoleContainer";
	//roster property Define
	String ACTIVITYSPACE_Roster_desc="Roster_desc";
	String ACTIVITYSPACE_Roster_displayName="Roster_displayName";
	String ACTIVITYSPACE_RosterDefinition_containedActivityType="RosterDefinition_containedActivityType";
	String ACTIVITYSPACE_RosterDefinition_dataFields="RosterDefinition_dataFields";
	//Queue property Define
	String ACTIVITYSPACE_QueueDefinition_dataFields="QueueDefinition_dataFields";
	//roleQueue property Define
	String ACTIVITYSPACE_RoleQueue_desc="RoleQueue_desc";
	String ACTIVITYSPACE_RoleQueue_displayName="RoleQueue_displayName";
	String ACTIVITYSPACE_RoleQueue_containedRoles="RoleQueueDefinition_containedRoles";
	//dataFieldDefinition define
	String ACTIVITYSPACE_DataFieldDefinition_fieldType="DataFieldDefinition_fieldType";
	String ACTIVITYSPACE_DataFieldDefinition_displayName="DataFieldDefinition_displayName";
	String ACTIVITYSPACE_DataFieldDefinition_description="DataFieldDefinition_description";
	String ACTIVITYSPACE_DataFieldDefinition_isArrayField="DataFieldDefinition_isArrayField";
	String ACTIVITYSPACE_DataFieldDefinition_isSystemField="DataFieldDefinition_isSystemField";
	String ACTIVITYSPACE_DataFieldDefinition_isMandatoryField="DataFieldDefinition_isMandatoryField";	
	String ACTIVITYSPACE_DataFieldDefinition_isReadableField="DataFieldDefinition_isReadableField";
	String ACTIVITYSPACE_DataFieldDefinition_isWriteableField="DataFieldDefinition_isWriteableField";	
	//activity instance define
	String ACTIVITYSPACE_ActivityInstanceDefinition_processDefinitionId="ActivityInstanceDefinition_processDefinitionId";
	String ACTIVITYSPACE_ActivityInstanceDefinition_dataFields="ActivityInstance_dataFields";
	String ACTIVITYSPACE_ActivityInstanceDefinition_attachment="ActivityInstance_attachment";	
	
	//custom properties define
	String ACTIVITYSPACE_CustomAttributeStore="CustomAttributeStore";
	String ACTIVITYSPACE_CustomStructureStore="CustomStructureStore";
	
	//activity space activity event listeners define
	String ACTIVITYSPACE_ActivityEventListeners="ActivitySpace_ActivityEventListeners";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_ASSIGNED="ACTIVITYSTEP_ASSIGNED";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_CREATED="ACTIVITYSTEP_CREATED";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEP_COMPLETED="ACTIVITYSTEP_COMPLETED";
	String ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_COMPLETED="BUSINESSACTIVITY_COMPLETED";
	String ACTIVITYSPACE_ActivityEvent_BUSINESSACTIVITY_CANCELLED="BUSINESSACTIVITY_CANCELLED";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_SUCCESS="ACTIVITYSTEPMONITOR_SUCCESS";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_FAILURE="ACTIVITYSTEPMONITOR_FAILURE";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_RETRY_DECREMENTED="ACTIVITYSTEPMONITOR_RETRY_DECREMENTED";
	String ACTIVITYSPACE_ActivityEvent_ACTIVITYSTEPMONITOR_CANCELED="ACTIVITYSTEPMONITOR_CANCELED";
}