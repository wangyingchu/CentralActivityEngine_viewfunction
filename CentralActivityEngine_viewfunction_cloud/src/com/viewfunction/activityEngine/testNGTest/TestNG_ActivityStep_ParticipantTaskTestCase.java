package com.viewfunction.activityEngine.testNGTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import javax.jcr.PropertyType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.activityView.common.ParticipantTask;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;

public class TestNG_ActivityStep_ParticipantTaskTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 
	@Test
	public void testRoleQueue_ActivityStep_ParticipantTaskOperation() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineProcessException, ActivityEngineDataException, FileNotFoundException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		activitySpace.removeRoleQueue("accountancyRoleQueue");
		activitySpace.removeRoleQueue("managemenRoleQueue");
		activitySpace.removeRole("accountancy");
		activitySpace.removeRole("management");		
		
		Role accountancy_role=ActivityComponentFactory.createRole(testActivitySpace, "accountancy");
		Role management_role=ActivityComponentFactory.createRole(testActivitySpace, "management");		
		activitySpace.addRole(accountancy_role);
		activitySpace.addRole(management_role);
		accountancy_role=activitySpace.getRole("accountancy");
		management_role=activitySpace.getRole("management");
		
		RoleQueue accountancy_RoleQueue=ActivityComponentFactory.createRoleQueue("accountancyRoleQueue", testActivitySpace, "displayName1", "descTexe1");		
		boolean addRoleQResult=activitySpace.addRoleQueue(accountancy_RoleQueue);
		Assert.assertTrue(addRoleQResult);
		accountancy_RoleQueue.addRole("accountancy");
		
		RoleQueue management_RoleQueue=ActivityComponentFactory.createRoleQueue("managemenRoleQueue", testActivitySpace, "displayName2", "descTexe2");
		addRoleQResult=activitySpace.addRoleQueue(management_RoleQueue);
		Assert.assertTrue(addRoleQResult);
		management_RoleQueue.addRole("management");		
		
		DataFieldDefinition dfd=ActivityComponentFactory.cteateDataFieldDefinition("df3", PropertyType.STRING, true);
		dfd.setMandatoryField(true);
		dfd.setSystemField(false);		
		
		DataFieldDefinition dfd2=ActivityComponentFactory.cteateDataFieldDefinition("df4", PropertyType.STRING, false);
		dfd.setMandatoryField(true);
		dfd.setSystemField(false);			
		
		ActivityData ad=ActivityComponentFactory.createActivityData(dfd,new String[]{"testStringValue_df3","testStringValue_df32"});	
		ActivityData ad2=ActivityComponentFactory.createActivityData(dfd2,"testStringValue_df4");	
		ActivityData[] launchDataArray=new ActivityData[2];
		launchDataArray[0]=ad;
		launchDataArray[1]=ad2;
		
		activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray,null);
		activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray,null);		
		
		List<ActivityStep> accountancyActivityStepList=accountancy_RoleQueue.fetchActivitySteps();			
		int initActivityStepNuminAcQueue=accountancyActivityStepList.size();
		
		List<ActivityStep> managementActivityStepList=management_RoleQueue.fetchActivitySteps();		
		int initActivityStepNuminMaQueue=managementActivityStepList.size();
		
		ActivityStep activityStep0=accountancyActivityStepList.get(0);
		Assert.assertEquals(activityStep0.getActivityStepDefinitionKey(),"writeReportTask");			
		Assert.assertEquals(activityStep0.getActivityStepName(),"Write monthly financial report");	
		Assert.assertEquals(activityStep0.getActivityType(),"businessActivityType1");
		Assert.assertEquals(activityStep0.getRelatedRole().getRoleName(),"accountancy");		
		Assert.assertEquals(activityStep0.getStepDescription(),"Write monthly financial report for publication to shareholders.");		
		activityStep0.handleActivityStep("FairyAngela");
		accountancyActivityStepList=accountancy_RoleQueue.fetchActivitySteps();
		Assert.assertEquals(accountancyActivityStepList.size(),initActivityStepNuminAcQueue-1);
			
		managementActivityStepList=management_RoleQueue.fetchActivitySteps();	
		Assert.assertEquals(managementActivityStepList.size(),initActivityStepNuminMaQueue);
		
		activityStep0.completeActivityStep("FairyAngela", null);
		managementActivityStepList=management_RoleQueue.fetchActivitySteps();	
		Assert.assertEquals(managementActivityStepList.size(),initActivityStepNuminMaQueue+1);
		initActivityStepNuminMaQueue=managementActivityStepList.size();
		ActivityStep activityStep1=managementActivityStepList.get(0);
		Assert.assertEquals(activityStep1.getActivityStepName(),"Verify monthly financial report");			
		activityStep1.handleActivityStep("KK the Great");
		managementActivityStepList=management_RoleQueue.fetchActivitySteps();		
		Assert.assertEquals(managementActivityStepList.size(),initActivityStepNuminMaQueue-1);
		
		//real usecase test
		activitySpace.removeRoleQueue("accountancyTeamRoleQueue");
		activitySpace.removeRoleQueue("managemenTeamRoleQueue");
		activitySpace.removeRole("accountancyTeam");
		activitySpace.removeRole("managementTeam");	
		activitySpace.removeParticipant("FairyAngela");
		
		Participant _P_FairyAngela=ActivityComponentFactory.createParticipant("FairyAngela", Participant.PARTICIPANT_TYPE_USER, testActivitySpace);
		activitySpace.addParticipant(_P_FairyAngela);
		_P_FairyAngela=activitySpace.getParticipant("FairyAngela");
		
		Role accountancyTeam_role=ActivityComponentFactory.createRole(testActivitySpace, "accountancyTeam");
		Role managementTeam_role=ActivityComponentFactory.createRole(testActivitySpace, "managementTeam");		
		activitySpace.addRole(accountancyTeam_role);
		activitySpace.addRole(managementTeam_role);
		accountancyTeam_role=activitySpace.getRole("accountancyTeam");
		managementTeam_role=activitySpace.getRole("managementTeam");
		accountancyTeam_role.addParticipant("FairyAngela");		
		
		RoleQueue accountancyTeam_RoleQueue=ActivityComponentFactory.createRoleQueue("accountancyTeamRoleQueue", testActivitySpace, "displayName1", "descTexe1");		
		activitySpace.addRoleQueue(accountancyTeam_RoleQueue);		
		accountancyTeam_RoleQueue.addRole("accountancyTeam");
		
		RoleQueue managementTeam_RoleQueue=ActivityComponentFactory.createRoleQueue("managemenTeamRoleQueue", testActivitySpace, "displayName2", "descTexe2");
		activitySpace.addRoleQueue(managementTeam_RoleQueue);		
		managementTeam_RoleQueue.addRole("managementTeam");		
		
		
		DataFieldDefinition dfoperOnwer=ActivityComponentFactory.cteateDataFieldDefinition("operationOwner", PropertyType.STRING, false);
		dfoperOnwer.setDescription("who was in charge for this operation");
		dfoperOnwer.setDisplayName("业务经办人");
		dfoperOnwer.setMandatoryField(true);
		dfoperOnwer.setSystemField(false);
		
		DataFieldDefinition dfoperCost=ActivityComponentFactory.cteateDataFieldDefinition("operationCost", PropertyType.DOUBLE, false);
		dfoperCost.setDescription("The final cost of this operation");
		dfoperCost.setDisplayName("业务最终花费");
		dfoperCost.setMandatoryField(true);
		dfoperCost.setSystemField(false);
		
		DataFieldDefinition dfoperVerifyResult=ActivityComponentFactory.cteateDataFieldDefinition("operationVerfifyResult", PropertyType.BOOLEAN, false);
		dfoperVerifyResult.setDescription("the result of operation verify");
		dfoperVerifyResult.setDisplayName("业务花费评审结果");
		dfoperVerifyResult.setMandatoryField(true);
		dfoperVerifyResult.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[3];
		dfdArray[0]=dfoperOnwer;
		dfdArray[1]=dfoperCost;
		dfdArray[2]=dfoperVerifyResult;
		
		String[] exposedSteps=new String[]{"writeReportTask","verifyReportTask"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("operationFinancialReport", testActivitySpace,exposedSteps);
		bsd.setActivityDataFields(dfdArray);		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/operationFinancialReport.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[2];
		step1edfdArray[0]=dfoperOnwer;
		step1edfdArray[1]=dfoperCost;
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[3];
		step2edfdArray[0]=dfoperOnwer;
		step2edfdArray[1]=dfoperCost;
		step2edfdArray[2]=dfoperVerifyResult;
		
		bsd.setActivityStepExposedDataFields("writeReportTask", step1edfdArray);
		bsd.setActivityStepRelatedRole("writeReportTask", "accountancyTeam");
		
		bsd.setActivityStepExposedDataFields("verifyReportTask", step2edfdArray);
		bsd.setActivityStepRelatedRole("verifyReportTask", "managementTeam");
		
		boolean addBADResult=activitySpace.addBusinessActivityDefinition(bsd);
		//boolean addBADResult=activitySpace.updateBusinessActivityDefinition(bsd);
		Assert.assertTrue(addBADResult);		
		
		ActivityData _OFR_ad_1=ActivityComponentFactory.createActivityData(dfoperOnwer,"KK The Great");	
		ActivityData _OFR_ad_2=ActivityComponentFactory.createActivityData(dfoperCost,new Double(1250.5));	
		ActivityData _OFR_ad_3=ActivityComponentFactory.createActivityData(dfoperVerifyResult,new Boolean(true));
		
		ActivityData[] _OFR_launchDataArray=new ActivityData[3];
		_OFR_launchDataArray[0]=_OFR_ad_1;
		_OFR_launchDataArray[1]=_OFR_ad_2;
		_OFR_launchDataArray[2]=_OFR_ad_3;

		activitySpace.launchBusinessActivity("operationFinancialReport", _OFR_launchDataArray,null);
		activitySpace.launchBusinessActivity("operationFinancialReport", _OFR_launchDataArray,null);
		activitySpace.launchBusinessActivity("operationFinancialReport", _OFR_launchDataArray,null);
		activitySpace.launchBusinessActivity("operationFinancialReport", _OFR_launchDataArray,null);
		activitySpace.launchBusinessActivity("operationFinancialReport", _OFR_launchDataArray,null);
		
		List<ActivityStep> accTAcivityStepList=accountancyTeam_RoleQueue.fetchActivitySteps();
		int aacActivitySttpNumber=accTAcivityStepList.size();
		Assert.assertTrue(aacActivitySttpNumber>0);
		
		List<ActivityStep> manTAcivityStepList=managementTeam_RoleQueue.fetchActivitySteps();
		int manActivitySttpNumber=manTAcivityStepList.size();		
		
		ActivityStep firstActivityStep=accTAcivityStepList.get(0);		
		ActivityData[] ad_1Arr=firstActivityStep.getActivityStepData();
		Assert.assertTrue(ad_1Arr.length==2);		
		Assert.assertEquals(firstActivityStep.getActivityType(),"operationFinancialReport");
		Assert.assertEquals(firstActivityStep.getActivityStepDefinitionKey(),"writeReportTask");
		
		Assert.assertEquals(ad_1Arr[0].getDataFieldDefinition().getFieldName(), "operationCost");
		Assert.assertEquals(((Double)ad_1Arr[0].getDatFieldValue()).doubleValue(),1250.5);
		Assert.assertEquals(ad_1Arr[0].getDataFieldDefinition().getDisplayName(), "业务最终花费");
		
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().getFieldName(), "operationOwner");
		Assert.assertEquals(ad_1Arr[1].getDatFieldValue().toString(),"KK The Great");
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().getDisplayName(), "业务经办人");
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().getDescription(),"who was in charge for this operation");
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().isArrayField(),false);
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().isMandatoryField(),true);
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().isSystemField(),false);
		Assert.assertEquals(ad_1Arr[1].getDataFieldDefinition().getFieldType(), PropertyType.STRING);
		
		firstActivityStep.handleActivityStep("FairyAngela");
		accTAcivityStepList=accountancyTeam_RoleQueue.fetchActivitySteps();
		Assert.assertEquals(accTAcivityStepList.size(),aacActivitySttpNumber-1);		
		firstActivityStep.completeActivityStep("FairyAngela");
		
		ActivityStep anotherFirstActivityStep=accTAcivityStepList.get(3);
		anotherFirstActivityStep.handleActivityStep("FairyAngela");		
		
		manTAcivityStepList=managementTeam_RoleQueue.fetchActivitySteps();
		Assert.assertEquals(manTAcivityStepList.size(),manActivitySttpNumber+1);
		manActivitySttpNumber=manTAcivityStepList.size();		
		
		ActivityStep secondActivityStep=manTAcivityStepList.get(0);		
		ActivityData[] ad_2Arr=secondActivityStep.getActivityStepData();
		Assert.assertTrue(ad_2Arr.length==3);
		Assert.assertEquals(secondActivityStep.getActivityType(),"operationFinancialReport");
		Assert.assertEquals(secondActivityStep.getActivityStepDefinitionKey(),"verifyReportTask");		
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().getFieldName(), "operationVerfifyResult");
		Assert.assertEquals(((Boolean)ad_2Arr[0].getDatFieldValue()).booleanValue(),true);
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().getDisplayName(), "业务花费评审结果");
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().getDescription(),"the result of operation verify");
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().isArrayField(),false);
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().isMandatoryField(),true);
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().isSystemField(),false);
		Assert.assertEquals(ad_2Arr[0].getDataFieldDefinition().getFieldType(), PropertyType.BOOLEAN);
		secondActivityStep.handleActivityStep("Someone in managementTeam");
		manTAcivityStepList=managementTeam_RoleQueue.fetchActivitySteps();
		Assert.assertEquals(manTAcivityStepList.size(),manActivitySttpNumber-1);
		
		secondActivityStep.completeActivityStep("Someone in managementTeam");
		
		List<ParticipantTask> ptl=_P_FairyAngela.fetchParticipantTasks();		
		Assert.assertTrue(ptl.size()>0);		
		
		Assert.assertEquals(ptl.get(0).getActivityType(),"operationFinancialReport");
		Assert.assertEquals(ptl.get(0).getActivityStepName(),"Write monthly financial report");
		Assert.assertEquals(ptl.get(0).getRoleName(),"accountancyTeam");
		Assert.assertEquals(ptl.get(0).getStepAssignee(),"FairyAngela");
	}
}