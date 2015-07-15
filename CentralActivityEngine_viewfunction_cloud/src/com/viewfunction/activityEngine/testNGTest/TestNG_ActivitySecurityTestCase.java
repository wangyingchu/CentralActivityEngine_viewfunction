package com.viewfunction.activityEngine.testNGTest;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Participant;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;

public class TestNG_ActivitySecurityTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 	

	@Test
	public void testSecurityOperation() throws ActivityEngineRuntimeException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		Role testRole1=ActivityComponentFactory.createRole(testActivitySpace, "testRole1");
		testRole1.setDisplayName("Role1DisplayName");
		testRole1.setDescription("Role1Desc");		
		boolean addres1=activitySpace.addRole(testRole1);
		Assert.assertTrue(addres1);
		
		Role resultRole=activitySpace.getRole("testRole1");
		Assert.assertNotNull(resultRole);		
		
		Assert.assertEquals(resultRole.getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(resultRole.getRoleName(),"testRole1");
		Assert.assertEquals(resultRole.getDisplayName(),"Role1DisplayName");
		Assert.assertEquals(resultRole.getDescription(),"Role1Desc");
				
		Participant[] participants=resultRole.getParticipants();
		Assert.assertNull(participants);
		
		Participant participant1=ActivityComponentFactory.createParticipant("user1", Participant.PARTICIPANT_TYPE_USER,testActivitySpace);
		participant1.setDisplayName("user1displayname");
		boolean addres2=activitySpace.addParticipant(participant1);
		Assert.assertTrue(addres2);
		
		Participant participant2=ActivityComponentFactory.createParticipant("group1", Participant.PARTICIPANT_TYPE_GROUP,testActivitySpace);
		participant2.setDisplayName("group1displayname");
		boolean addres3=activitySpace.addParticipant(participant2);
		Assert.assertTrue(addres3);
		
		Participant participant3=ActivityComponentFactory.createParticipant("user2", Participant.PARTICIPANT_TYPE_USER,testActivitySpace);
		participant3.setDisplayName("user2displayname");
		boolean addres4=activitySpace.addParticipant(participant3);
		Assert.assertTrue(addres4);
		
		Participant participant4=ActivityComponentFactory.createParticipant("user3", Participant.PARTICIPANT_TYPE_USER,testActivitySpace);
		participant1.setDisplayName("user3displayname");
		boolean addres5=activitySpace.addParticipant(participant4);
		Assert.assertTrue(addres5);
		
		Role[] roles=participant1.getRoles();
		Assert.assertNull(roles);	
		Assert.assertFalse(participant1.isInRole("testRole1"));
		Assert.assertFalse(participant3.isInRole("testRole1"));		
		
		Participant[] participantsInSpace=activitySpace.getParticipants();
		Assert.assertEquals(participantsInSpace.length,4);
		
		Assert.assertFalse(resultRole.containParticipant("user1"));		
		boolean addPRel1=resultRole.addParticipant("user1");
		Assert.assertTrue(addPRel1);
		boolean addPRel2=resultRole.addParticipant("user1");
		Assert.assertFalse(addPRel2);
		Assert.assertTrue(resultRole.containParticipant("user1"));	
		
		roles=participant1.getRoles();		
		Assert.assertEquals(roles.length,1);
		Assert.assertTrue(participant1.isInRole("testRole1"));
		Assert.assertFalse(participant3.isInRole("testRole1"));	
		
		participants=resultRole.getParticipants();
		Assert.assertEquals(participants.length,1);
		boolean addPRel3=resultRole.addParticipant("group1");
		Assert.assertTrue(addPRel3);
		participants=resultRole.getParticipants();
		Assert.assertEquals(participants.length,2);
		
		Assert.assertFalse(participants[0].isGroup());
		Assert.assertEquals(participants[1].getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(participants[1].getParticipantName(),"group1");
		Assert.assertEquals(participants[1].getDisplayName(),"group1displayname");
		Assert.assertTrue(participants[1].isGroup());
		
		Role testRole2=ActivityComponentFactory.createRole(testActivitySpace, "testRole2");
		testRole2.setDisplayName("Role2DisplayName");
		testRole2.setDescription("Role2Desc");		
		boolean addres6=activitySpace.addRole(testRole2);
		Assert.assertTrue(addres6);
		Role resultRole2=activitySpace.getRole("testRole2");
		Assert.assertNotNull(resultRole2);
		Assert.assertEquals(resultRole2.getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(resultRole2.getRoleName(),"testRole2");
		Assert.assertEquals(resultRole2.getDisplayName(),"Role2DisplayName");
		Assert.assertEquals(resultRole2.getDescription(),"Role2Desc");
		Assert.assertTrue(resultRole2.addParticipant("user1"));
		
		roles=participant1.getRoles();		
		Assert.assertEquals(roles.length,2);
		
		Assert.assertEquals(roles[1].getActivitySpaceName(),testActivitySpace);
		Assert.assertEquals(roles[1].getRoleName(),"testRole2");
		Assert.assertEquals(roles[1].getDisplayName(),"Role2DisplayName");
		Assert.assertEquals(roles[1].getDescription(),"Role2Desc");			
		
		boolean delPRel1=resultRole.removeParticipant("user1");
		Assert.assertTrue(delPRel1);
		boolean delPRel2=resultRole.removeParticipant("user1");
		Assert.assertFalse(delPRel2);
		participants=resultRole.getParticipants();
		Assert.assertEquals(participants.length,1);
		boolean delPRel3=resultRole.removeParticipant("group1");
		Assert.assertTrue(delPRel3);
		participants=resultRole.getParticipants();
		Assert.assertNull(participants);
		
		roles=participant1.getRoles();		
		Assert.assertEquals(roles.length,1);
		Assert.assertTrue(resultRole2.removeParticipant("user1"));
		
		roles=participant1.getRoles();		
		Assert.assertNull(roles);		
		Assert.assertFalse(participant1.isInRole("testRole1"));	
		Assert.assertFalse(participant1.isInRole("testRole2"));	
		participantsInSpace=activitySpace.getParticipants();
		Assert.assertEquals(participantsInSpace.length,4);			
		
		Assert.assertTrue(resultRole.addParticipant("user2"));
		Assert.assertTrue(resultRole.addParticipant("user3"));
		participants=resultRole.getParticipants();
		Assert.assertEquals(participants.length,2);
		Assert.assertTrue(activitySpace.removeParticipant("user2"));
		participants=resultRole.getParticipants();
		Assert.assertEquals(participants.length,1);
		
		participantsInSpace=activitySpace.getParticipants();
		Assert.assertEquals(participantsInSpace.length,3);				
		
		Assert.assertFalse(resultRole.containParticipant("user2"));
		Assert.assertTrue(resultRole.containParticipant("user3"));
		
		Assert.assertTrue(resultRole2.addParticipant("user3"));		
		Assert.assertTrue(participant4.isInRole("testRole1"));
		Assert.assertTrue(participant4.isInRole("testRole2"));
		roles=participant4.getRoles();	
		Assert.assertEquals(roles.length,2);
		
		roles=activitySpace.getRoles();
		Assert.assertEquals(roles.length,2);
		
		Assert.assertTrue(activitySpace.removeRole("testRole1"));
		roles=participant4.getRoles();	
		Assert.assertEquals(roles.length,1);
		Assert.assertTrue(participant4.isInRole("testRole2"));
		Assert.assertFalse(participant4.isInRole("testRole1"));
		
		roles=activitySpace.getRoles();
		Assert.assertEquals(roles.length,1);
		
	}	
}