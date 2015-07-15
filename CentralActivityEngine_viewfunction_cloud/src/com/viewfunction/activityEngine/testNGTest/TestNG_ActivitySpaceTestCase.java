package com.viewfunction.activityEngine.testNGTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityBureauImpl.CCRActivityEngineConstant;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;

import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.contentBureau.BaseContentObject;
import com.viewfunction.contentRepository.contentBureau.ContentSpace;
import com.viewfunction.contentRepository.contentBureau.RootContentObject;
import com.viewfunction.contentRepository.util.PerportyHandler;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;
import com.viewfunction.contentRepository.util.exception.ContentReposityRuntimeException;
import com.viewfunction.contentRepository.util.factory.ContentComponentFactory;

import javax.jcr.PropertyType;

public class TestNG_ActivitySpaceTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 
	private static String BUILDIN_ADMINISTRATOR_ACCOUNT;
	private static String BUILDIN__ADMINISTRATOR_ACCOUNT_PWD;

	@Test
	public void testRoleOperation() throws ActivityEngineRuntimeException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);		
		Role[] roles=activitySpace.getRoles();
		Assert.assertNull(roles);
		
		Role testRole1=ActivityComponentFactory.createRole(testActivitySpace, "testRole1");
		testRole1.setDisplayName("Role1DisplayName");
		testRole1.setDescription("Role1Desc");
		
		boolean addres1=activitySpace.addRole(testRole1);
		Assert.assertTrue(addres1);
		boolean addres2=activitySpace.addRole(testRole1);
		Assert.assertFalse(addres2);
		
		roles=activitySpace.getRoles();		
		Assert.assertEquals(roles.length,1);
		
		Role resultRole=activitySpace.getRole("testRole1");
		Assert.assertNotNull(resultRole);		
		
		Assert.assertEquals(resultRole.getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(resultRole.getRoleName(),"testRole1");
		Assert.assertEquals(resultRole.getDisplayName(),"Role1DisplayName");
		Assert.assertEquals(resultRole.getDescription(),"Role1Desc");
		
		Role testRole2=ActivityComponentFactory.createRole(testActivitySpace, "testRole2");
		testRole2.setDisplayName("Role2DisplayName");
		testRole2.setDescription("Role2Desc");
		boolean addres3=activitySpace.addRole(testRole2);
		Assert.assertTrue(addres3);
		roles=activitySpace.getRoles();		
		Assert.assertEquals(roles.length,2);
		
		Assert.assertEquals(roles[1].getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(roles[1].getRoleName(),"testRole2");
		Assert.assertEquals(roles[1].getDisplayName(),"Role2DisplayName");
		Assert.assertEquals(roles[1].getDescription(),"Role2Desc");		
		
		boolean delRes1=activitySpace.removeRole("testRole1");
		Assert.assertTrue(delRes1);
		boolean delRes2=activitySpace.removeRole("testRole1");
		Assert.assertFalse(delRes2);
		roles=activitySpace.getRoles();		
		Assert.assertEquals(roles.length,1);
		
		boolean delRes3=activitySpace.removeRole("testRole2");
		Assert.assertTrue(delRes3);
		roles=activitySpace.getRoles();	
		Assert.assertNull(roles);		
	}
	
	@Test
	public void testParticipantOperation() throws ActivityEngineRuntimeException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);		
		Participant[] participants=activitySpace.getParticipants();
		Assert.assertNull(participants);
		
		Participant participant1=ActivityComponentFactory.createParticipant("user1", Participant.PARTICIPANT_TYPE_USER,testActivitySpace);
		participant1.setDisplayName("user1displayname");
		boolean addres1=activitySpace.addParticipant(participant1);
		Assert.assertTrue(addres1);
		boolean addres2=activitySpace.addParticipant(participant1);
		Assert.assertFalse(addres2);
		
		participants=activitySpace.getParticipants();
		Assert.assertEquals(participants.length,1);
		
		Participant resultParticipant=activitySpace.getParticipant("user1");
		Assert.assertNotNull(resultParticipant);
		
		Assert.assertEquals(resultParticipant.getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(resultParticipant.getParticipantName(),"user1");
		Assert.assertEquals(resultParticipant.getDisplayName(),"user1displayname");
		Assert.assertFalse(resultParticipant.isGroup());		
		
		Participant participant2=ActivityComponentFactory.createParticipant("group1", Participant.PARTICIPANT_TYPE_GROUP,testActivitySpace);
		participant2.setDisplayName("group1displayname");
		boolean addres3=activitySpace.addParticipant(participant2);
		Assert.assertTrue(addres3);
		
		participants=activitySpace.getParticipants();
		Assert.assertEquals(participants.length,2);
		
		Assert.assertEquals(participants[1].getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(participants[1].getParticipantName(),"group1");
		Assert.assertEquals(participants[1].getDisplayName(),"group1displayname");
		Assert.assertTrue(participants[1].isGroup());
		
		boolean delRes1=activitySpace.removeParticipant("user1");
		Assert.assertTrue(delRes1);
		boolean delRes2=activitySpace.removeParticipant("user1");
		Assert.assertFalse(delRes2);
		
		participants=activitySpace.getParticipants();
		Assert.assertEquals(participants.length,1);
		
		boolean delRes3=activitySpace.removeParticipant("group1");
		Assert.assertTrue(delRes3);
		participants=activitySpace.getParticipants();
		Assert.assertNull(participants);		
	}
	
	@Test
	public void testRosterOperation() throws ActivityEngineRuntimeException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);		
		Roster[] rosters=activitySpace.getRosters();
		Assert.assertNull(rosters);
		
		Roster roster1=ActivityComponentFactory.createRoster(testActivitySpace, "roster1");
		roster1.setDescription("roster1Desc");
		roster1.setDisplayName("roster1Dispalyname");		
		
		boolean addres1=activitySpace.addRoster(roster1);
		Assert.assertTrue(addres1);
		boolean addres2=activitySpace.addRoster(roster1);
		Assert.assertFalse(addres2);
		
		rosters=activitySpace.getRosters();
		Assert.assertEquals(rosters.length,1);
		
		Roster resultRoster=activitySpace.getRoster("roster1");
		Assert.assertNotNull(resultRoster);
		
		Assert.assertEquals(resultRoster.getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(resultRoster.getRosterName(),"roster1");
		Assert.assertEquals(resultRoster.getDisplayName(),"roster1Dispalyname");
		Assert.assertEquals(resultRoster.getDescription(),"roster1Desc");
		
		Roster roster2=ActivityComponentFactory.createRoster(testActivitySpace, "roster2");
		roster2.setDescription("roster2Desc");
		roster2.setDisplayName("roster2Dispalyname");		
		boolean addres3=activitySpace.addRoster(roster2);
		Assert.assertTrue(addres3);
		
		rosters=activitySpace.getRosters();
		Assert.assertEquals(rosters.length,2);
		
		Assert.assertEquals(rosters[1].getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(rosters[1].getRosterName(),"roster2");
		Assert.assertEquals(rosters[1].getDisplayName(),"roster2Dispalyname");
		Assert.assertEquals(rosters[1].getDescription(),"roster2Desc");
		
		boolean delRes1=activitySpace.removeRoster("roster1");
		Assert.assertTrue(delRes1);
		boolean delRes2=activitySpace.removeRoster("roster1");
		Assert.assertFalse(delRes2);
		
		rosters=activitySpace.getRosters();
		Assert.assertEquals(rosters.length,1);
		
		boolean delRes3=activitySpace.removeRoster("roster2");
		Assert.assertTrue(delRes3);
		rosters=activitySpace.getRosters();
		Assert.assertNull(rosters);			
	}
	
	@Test
	public void testRoleQueueOperation() throws ActivityEngineRuntimeException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		RoleQueue _RoleQueue1=ActivityComponentFactory.createRoleQueue("roleQueue_1", testActivitySpace, "displayName1", "descTexe1");		
		boolean addRoleQResult=activitySpace.addRoleQueue(_RoleQueue1);
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=activitySpace.addRoleQueue(_RoleQueue1);;
		Assert.assertFalse(addRoleQResult);
		
		RoleQueue _RoleQueue2=ActivityComponentFactory.createRoleQueue("roleQueue_2", testActivitySpace, "displayName2", "descTexe2");
		addRoleQResult=activitySpace.addRoleQueue(_RoleQueue2);
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=activitySpace.removeRoleQueue("roleQueue_2");
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=activitySpace.removeRoleQueue("roleQueue_2");
		Assert.assertFalse(addRoleQResult);		
	}

	@Test
	public void testBusinessActivityDefinitionOperation() throws ActivityEngineDataException, ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineProcessException, ContentReposityRuntimeException, FileNotFoundException{	
		if(BUILDIN_ADMINISTRATOR_ACCOUNT==null){
			BUILDIN_ADMINISTRATOR_ACCOUNT = PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT);
		}
		if(BUILDIN__ADMINISTRATOR_ACCOUNT_PWD==null){
			BUILDIN__ADMINISTRATOR_ACCOUNT_PWD=PerportyHandler.getPerportyValue(PerportyHandler.BUILDIN_ADMINISTRATOR_ACCOUNT_PWD);			
		}	
		ContentSpace metaDataContentSpace = null;		
		try {
			metaDataContentSpace=ContentComponentFactory.connectContentSpace(BUILDIN_ADMINISTRATOR_ACCOUNT, BUILDIN__ADMINISTRATOR_ACCOUNT_PWD, 
					CCRActivityEngineConstant.ACTIVITYENGINE_METADATA_CONTENTSPACE);
			RootContentObject activitySpaceDefineObject=metaDataContentSpace.getRootContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_DEFINATION_ROOTCONTENTOBJECT);
			if(activitySpaceDefineObject==null){
				throw new ActivityEngineRuntimeException();
			}
			BaseContentObject activitySpaceBco=activitySpaceDefineObject.getSubContentObject(testActivitySpace);
			if(activitySpaceBco==null){
				throw new ActivityEngineRuntimeException();
			}			
			BaseContentObject activityDefineObj=activitySpaceBco.getSubContentObject(CCRActivityEngineConstant.ACTIVITYSPACE_ActivityDefinition);	
			activityDefineObj.removeSubContentObject("businessActivityType1", false);
		} catch (ContentReposityException e) {			
			e.printStackTrace();
			throw new ActivityEngineRuntimeException();
		}finally{
			if(metaDataContentSpace!=null){
				metaDataContentSpace.closeContentSpace();	
			}
		}
		
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		DataFieldDefinition dfd1=ActivityComponentFactory.cteateDataFieldDefinition("df1", PropertyType.STRING, true);
		dfd1.setDescription("dfd1 desc");
		dfd1.setDisplayName("dfd1 displayname");
		dfd1.setMandatoryField(false);
		dfd1.setSystemField(true);		
		DataFieldDefinition dfd2=ActivityComponentFactory.cteateDataFieldDefinition("df2", PropertyType.STRING, false);
		dfd2.setDescription("dfd2 desc");
		dfd2.setDisplayName("dfd2 displayname");
		dfd2.setMandatoryField(true);
		dfd2.setSystemField(false);
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[2];
		dfdArray[0]=dfd1;
		dfdArray[1]=dfd2;
		
		String[] exposedSteps=new String[]{"step1","step2","step3","step4","step5"};
		
		BusinessActivityDefinition bsd=ActivityComponentFactory.createBusinessActivityDefinition("businessActivityType1", testActivitySpace,exposedSteps);
		bsd.setActivityDataFields(dfdArray);
		
		FileInputStream fileInputStream = new FileInputStream(new File("processDefine/businessActivityType1.bpmn20.xml"));
		bsd.setDefinitionResource(fileInputStream);
		
		
		DataFieldDefinition[] step1edfdArray=new DataFieldDefinition[1];
		step1edfdArray[0]=dfd1;
		bsd.setActivityStepExposedDataFields("step1", step1edfdArray);
		bsd.setActivityStepExposedDataFields("step3", step1edfdArray);
		bsd.setActivityStepExposedDataFields("step5", step1edfdArray);
		
		DataFieldDefinition[] step2edfdArray=new DataFieldDefinition[1];
		step2edfdArray[0]=dfd2;
		bsd.setActivityStepExposedDataFields("step2", step2edfdArray);		
		bsd.setActivityStepExposedDataFields("step4", step2edfdArray);	
		
		boolean addBADResult=activitySpace.addBusinessActivityDefinition(bsd);
		Assert.assertTrue(addBADResult);		
		
		BusinessActivityDefinition resultBAD=activitySpace.getBusinessActivityDefinition("businessActivityType1");
		Assert.assertNotNull(resultBAD);
		Assert.assertEquals(resultBAD.getActivityType(),"businessActivityType1");		
		Assert.assertTrue(resultBAD.isEnabled());
		
		DataFieldDefinition[] dataFieldDefinArra=resultBAD.getActivityDataFields();		
		Assert.assertEquals(dataFieldDefinArra.length,2);
		
		DataFieldDefinition DataFieldDefinition_0=dataFieldDefinArra[0];		
		Assert.assertEquals(DataFieldDefinition_0.getFieldName(),"df2");
		Assert.assertEquals(DataFieldDefinition_0.getDescription(),"dfd2 desc");
		Assert.assertEquals(DataFieldDefinition_0.getDisplayName(),"dfd2 displayname");
		Assert.assertEquals(DataFieldDefinition_0.getFieldType(),PropertyType.STRING);
		Assert.assertEquals(DataFieldDefinition_0.isArrayField(),false);
		Assert.assertEquals(DataFieldDefinition_0.isMandatoryField(),true);
		Assert.assertEquals(DataFieldDefinition_0.isSystemField(),false);		
		
		DataFieldDefinition DataFieldDefinition_1=dataFieldDefinArra[1];
		Assert.assertEquals(DataFieldDefinition_1.getFieldName(),"df1");
		Assert.assertEquals(DataFieldDefinition_1.getDescription(),"dfd1 desc");
		Assert.assertEquals(DataFieldDefinition_1.getDisplayName(),"dfd1 displayname");
		Assert.assertEquals(DataFieldDefinition_1.getFieldType(),PropertyType.STRING);
		Assert.assertEquals(DataFieldDefinition_1.isArrayField(),true);
		Assert.assertEquals(DataFieldDefinition_1.isMandatoryField(),false);
		Assert.assertEquals(DataFieldDefinition_1.isSystemField(),true);
		
		String[] exStep=resultBAD.getExposedSteps();
		Assert.assertEquals(exStep.length,5);
		Assert.assertEquals(exStep[0],"step1");
		Assert.assertEquals(exStep[1],"step2");
		
		Map<String,DataFieldDefinition[]> stepDad=resultBAD.getActivityStepsExposedDataField();
		
		DataFieldDefinition[] step1Df=stepDad.get("step1");
		Assert.assertNotNull(step1Df);		
		Assert.assertEquals(step1Df[0].getFieldName(),"df1");
		Assert.assertEquals(step1Df[0].getDescription(),"dfd1 desc");
		Assert.assertEquals(step1Df[0].getDisplayName(),"dfd1 displayname");
		Assert.assertEquals(step1Df[0].getFieldType(),PropertyType.STRING);
		Assert.assertEquals(step1Df[0].isArrayField(),true);
		Assert.assertEquals(step1Df[0].isMandatoryField(),false);
		Assert.assertEquals(step1Df[0].isSystemField(),true);		
		
		DataFieldDefinition[] step2Df=stepDad.get("step2");
		Assert.assertNotNull(step2Df);
		Assert.assertEquals(step2Df[0].getFieldName(),"df2");
		Assert.assertEquals(step2Df[0].getDescription(),"dfd2 desc");
		Assert.assertEquals(step2Df[0].getDisplayName(),"dfd2 displayname");
		Assert.assertEquals(step2Df[0].getFieldType(),PropertyType.STRING);
		Assert.assertEquals(step2Df[0].isArrayField(),false);
		Assert.assertEquals(step2Df[0].isMandatoryField(),true);
		Assert.assertEquals(step2Df[0].isSystemField(),false);		
		
		activitySpace.disableBusinessActivityDefinition("businessActivityType1");
		resultBAD=activitySpace.getBusinessActivityDefinition("businessActivityType1");
		Assert.assertFalse(resultBAD.isEnabled());
		 
		activitySpace.enableBusinessActivityDefinition("businessActivityType1");
		resultBAD=activitySpace.getBusinessActivityDefinition("businessActivityType1");
		Assert.assertTrue(resultBAD.isEnabled());	
		
		String[] activityTypeArray=activitySpace.getBusinessActivityTypes();
		Assert.assertEquals(activityTypeArray.length,1);
		Assert.assertEquals(activityTypeArray[0],"businessActivityType1");		
		
		BusinessActivityDefinition[] bada=activitySpace.getBusinessActivityDefinitions();
		Assert.assertEquals(bada.length,1);
		Assert.assertEquals(bada[0].getActivityType(),"businessActivityType1");			
		
		DataFieldDefinition dfd3=ActivityComponentFactory.cteateDataFieldDefinition("df3", PropertyType.STRING, true);
		dfd3.setDescription("dfd3 desc");
		dfd3.setDisplayName("dfd3 displayname");
		dfd3.setMandatoryField(false);
		dfd3.setSystemField(true);		
		
		DataFieldDefinition dfd4=ActivityComponentFactory.cteateDataFieldDefinition("df4", PropertyType.STRING, false);
		dfd4.setDescription("dfd4 desc");
		dfd4.setDisplayName("dfd4 displayname");
		dfd4.setMandatoryField(true);
		dfd4.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray2=new DataFieldDefinition[2];
		dfdArray2[0]=dfd3;
		dfdArray2[1]=dfd4;		
		String[] exposedSteps2=new String[]{"stepA","stepB","stepC","writeReportTask","verifyReportTask"};	
		
		BusinessActivityDefinition bsd2=ActivityComponentFactory.createBusinessActivityDefinition("businessActivityType1", testActivitySpace,exposedSteps2);
		
		bsd2.setActivityDataFields(dfdArray2);		
		DataFieldDefinition[] step1edfdArray1=new DataFieldDefinition[1];
		step1edfdArray1[0]=dfd3;
		bsd2.setActivityStepExposedDataFields("stepA", step1edfdArray1);		
		DataFieldDefinition[] step2edfdArray1=new DataFieldDefinition[1];
		step2edfdArray1[0]=dfd4;
		bsd2.setActivityStepExposedDataFields("stepB", step2edfdArray1);
		bsd2.setActivityStepExposedDataFields("stepC", step1edfdArray1);
		
		fileInputStream = new FileInputStream(new File("processDefine/businessActivityType1.bpmn20.xml"));		
		bsd2.setDefinitionResource(fileInputStream);		
		// can not execute together with addBusinessActivityDefinition when use in-memory mode
		boolean updateRest=activitySpace.updateBusinessActivityDefinition(bsd2);
		Assert.assertTrue(updateRest);	
		resultBAD=activitySpace.getBusinessActivityDefinition("businessActivityType1");
		Assert.assertTrue(resultBAD.isEnabled());
		Assert.assertEquals(resultBAD.getExposedSteps().length,5);		
		
		BusinessActivityDefinition bsd3=activitySpace.getBusinessActivityDefinition("businessActivityType1");			
		DataFieldDefinition[] dfdArray3=bsd3.getActivityDataFields();		
		DataFieldDefinition[] newDfdArray=new DataFieldDefinition[dfdArray3.length+1];		
		for(int i=0;i<dfdArray3.length;i++){
			newDfdArray[i]=dfdArray3[i];			
		}
		DataFieldDefinition dfd_4=ActivityComponentFactory.cteateDataFieldDefinition("df_4", PropertyType.STRING, true);
		dfd_4.setDescription("dfd_4 desc");
		dfd_4.setDisplayName("dfd_4 displayname");
		dfd_4.setMandatoryField(false);
		dfd_4.setSystemField(true);
		newDfdArray[dfdArray3.length]=dfd_4;
		bsd3.setActivityDataFields(newDfdArray);		
		
		
		activitySpace.updateBusinessActivityDefinition(bsd3);		
	}
	
	@Test(dependsOnMethods = { "testBusinessActivityDefinitionOperation" })
	public void testLaunchBusinessActivity() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException{		
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
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
		
		BusinessActivity resultBusinessActivity=activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray,null);	
		Assert.assertNotNull(resultBusinessActivity.getActivityId());			
		//activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray);	
		//activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray);	
		//activitySpace.launchBusinessActivity("businessActivityType1", launchDataArray);	
		//System.out.println(resultBusinessActivity.getActivityProcessObject().getCurrentProcessSteps().get(0).getStepName());		
	}
}