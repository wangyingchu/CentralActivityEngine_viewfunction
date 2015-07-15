package com.viewfunction.activityEngine.testNGTest;

import java.io.File;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.Queue;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineException;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import javax.jcr.PropertyType;

public class TestNG_ActivityEngineFactoryTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 
	
	@Test
	public void testCreateActivitySpace() throws ActivityEngineException{
		ActivitySpace activitySpace=ActivityComponentFactory.createActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		ActivitySpace activitySpace2=ActivityComponentFactory.createActivitySpace(testActivitySpace);
		Assert.assertNull(activitySpace2);
		Assert.assertTrue(activitySpace instanceof ActivitySpace,"Class Type should by ActivitySpace");
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		ActivitySpace[] activitySpaceArray=ActivityComponentFactory.getActivitySpaces();
		Assert.assertEquals(activitySpaceArray.length,1);		
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateParticipant(){		
		Participant participant1=ActivityComponentFactory.createParticipant("user1", Participant.PARTICIPANT_TYPE_USER,testActivitySpace);
		Assert.assertTrue(participant1 instanceof Participant,"Class Type should by Participant");
		Assert.assertEquals(participant1.getParticipantName(),"user1");
		Assert.assertFalse(participant1.isGroup());
		Participant participant2=ActivityComponentFactory.createParticipant("group1", Participant.PARTICIPANT_TYPE_GROUP,testActivitySpace);
		Assert.assertTrue(participant2 instanceof Participant,"Class Type should by Participant");
		Assert.assertEquals(participant2.getParticipantName(),"group1");
		Assert.assertTrue(participant2.isGroup());
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateRole(){
		Role role=ActivityComponentFactory.createRole("activitySpace1", "role1");
		Assert.assertTrue(role instanceof Role,"Class Type should by Role");
		Assert.assertEquals(role.getActivitySpaceName(),"activitySpace1");
		Assert.assertEquals(role.getRoleName(),"role1");		
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateRoster(){
		Roster roster=ActivityComponentFactory.createRoster(testActivitySpace,"roster1");
		Assert.assertTrue(roster instanceof Roster,"Class Type should by Roster");
		Assert.assertEquals(roster.getRosterName(),"roster1");		
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCteateDataFieldDefinition(){
		DataFieldDefinition dataFieldDefinition=ActivityComponentFactory.cteateDataFieldDefinition("dtaField1", PropertyType.LONG, false);
		Assert.assertTrue(dataFieldDefinition instanceof DataFieldDefinition,"Class Type should by DataFieldDefinition");
		Assert.assertEquals(dataFieldDefinition.getFieldType(), PropertyType.LONG);		
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateBusinessActivityDefinition(){
		BusinessActivityDefinition businessActivityDefinition=ActivityComponentFactory.createBusinessActivityDefinition("BusinessActiyityDefine1", testActivitySpace,null);
		Assert.assertTrue(businessActivityDefinition instanceof BusinessActivityDefinition,"Class Type should by BusinessActivityDefinition");
		Assert.assertEquals(businessActivityDefinition.getActivityType(),"BusinessActiyityDefine1");		
	}
	
	@Test(dependsOnMethods = { "testCteateDataFieldDefinition" })
	public void testCreateActivityData(){
		DataFieldDefinition dataFieldDefinition=ActivityComponentFactory.cteateDataFieldDefinition("dtaField1", PropertyType.LONG, false);		
		ActivityData activityData=ActivityComponentFactory.createActivityData(dataFieldDefinition,new Long(12345));
		activityData.setDatFieldValue(new Long(1234567));		
		Assert.assertTrue(activityData instanceof ActivityData,"Class Type should by ActivityData");	
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testGetActivitySpace(){
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertTrue(activitySpace instanceof ActivitySpace,"Class Type should by ActivitySpace");
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);		
	}	
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateBusinessActivity(){
		BusinessActivity businessActivity=ActivityComponentFactory.createBusinessActivity("activityID","activitySpaceName","activityType");
		Assert.assertTrue(businessActivity instanceof BusinessActivity,"Class Type should by BusinessActivity");
		Assert.assertNotNull(businessActivity.getActivityId());		
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateActivityStep(){
		ActivityStep activityStep=ActivityComponentFactory.createActivityStep("activityType");
		Assert.assertTrue(activityStep instanceof ActivityStep,"Class Type should by ActivityStep");		
		Assert.assertEquals(activityStep.getActivityType(),"activityType");	
	}
	
	@Test(dependsOnMethods = { "testCreateActivitySpace" })
	public void testCreateRoleQueue(){
		RoleQueue _RoleQueue=ActivityComponentFactory.createRoleQueue("queueName", "activitySpaceName", "displayName", "descTexe");		
		Assert.assertTrue(_RoleQueue instanceof RoleQueue,"Class Type should by RoleQueue");
		Assert.assertEquals(_RoleQueue.getQueueName(), "queueName");
		Assert.assertEquals(_RoleQueue.getActivitySpaceName(), "activitySpaceName");
		Assert.assertEquals(_RoleQueue.getDisplayName(),"displayName");
		Assert.assertEquals(_RoleQueue.getDescription(),"descTexe");
		Assert.assertEquals(_RoleQueue.getQueueType(),Queue.QUEUE_TYPE_USERQUEUE);			
	}	
	
	@BeforeClass
	public void tearDown() throws Exception {		
		System.out.println("remove TestNG Used ContentSpace");				
		boolean delResult0=false;
		//File workspacesDir=new File("contentRepository/workspaces");
		//delResult0=deleteDirectory(workspacesDir);
		//if(delResult0){
		//	System.out.println("workspaces removed!!");
		//}
		boolean delResult=false;
		File versionDir=new File("contentRepository/version");
		delResult=deleteDirectory(versionDir);
		if(delResult){
			System.out.println("version removed!!");
		}			
		boolean delResult2=false;
		File repositoryDir=new File("contentRepository/repository");
		delResult2=deleteDirectory(repositoryDir);
		if(delResult2){
			System.out.println("repository removed!!");
		}			
	}	
	private static boolean deleteDirectory(File path) {
		    if( path.exists() ) {
		      File[] files = path.listFiles();
		      for(int i=0; i<files.length; i++) {
		         if(files[i].isDirectory()) {
		           deleteDirectory(files[i]);
		         }
		         else {
		           files[i].delete();
		         }
		      }
		    }
		    return( path.delete() );
	}		
}