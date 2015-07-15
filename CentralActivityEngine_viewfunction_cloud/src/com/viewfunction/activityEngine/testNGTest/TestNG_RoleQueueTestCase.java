package com.viewfunction.activityEngine.testNGTest;

import javax.jcr.PropertyType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityView.RoleQueue;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;

public class TestNG_RoleQueueTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 
	@Test
	public void testRoleOperation() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);
		
		activitySpace.removeRoleQueue("roleQueue_1");
		activitySpace.removeRoleQueue("roleQueue_2");
		activitySpace.removeRole("role_1");
		activitySpace.removeRole("role_2");
		activitySpace.removeRole("role_3");
		
		Role _role_1=ActivityComponentFactory.createRole(testActivitySpace, "role_1");
		Role _role_2=ActivityComponentFactory.createRole(testActivitySpace, "role_2");
		Role _role_3=ActivityComponentFactory.createRole(testActivitySpace, "role_3");
		activitySpace.addRole(_role_1);
		activitySpace.addRole(_role_2);
		activitySpace.addRole(_role_3);
		_role_1=activitySpace.getRole("role_1");
		_role_2=activitySpace.getRole("role_2");
		_role_3=activitySpace.getRole("role_3");		
		
		RoleQueue _RoleQueue1=ActivityComponentFactory.createRoleQueue("roleQueue_1", testActivitySpace, "displayName1", "descTexe1");		
		boolean addRoleQResult=activitySpace.addRoleQueue(_RoleQueue1);
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=activitySpace.addRoleQueue(_RoleQueue1);;
		Assert.assertFalse(addRoleQResult);	
		
		RoleQueue _RoleQueue2=ActivityComponentFactory.createRoleQueue("roleQueue_2", testActivitySpace, "displayName2", "descTexe2");
		addRoleQResult=activitySpace.addRoleQueue(_RoleQueue2);
		Assert.assertTrue(addRoleQResult);
		
		Assert.assertTrue(_RoleQueue1.addRole("role_1"));
		Assert.assertTrue(_RoleQueue1.addRole("role_2"));
		Assert.assertTrue(_RoleQueue1.addRole("role_3"));
		Assert.assertFalse(_RoleQueue1.addRole("role_1"));
		
		Assert.assertEquals(_RoleQueue1.getRelatedRoles().length, 3);		
		Assert.assertEquals(_RoleQueue1.getRelatedRoles()[0].getRoleName(),"role_1");
		
		Assert.assertEquals(_role_1.getRelatedRoleQueues().length,1);
		Assert.assertEquals(_role_1.getRelatedRoleQueues()[0].getQueueName(),"roleQueue_1");
		
		addRoleQResult=_RoleQueue1.removeRole("role_1");
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=_RoleQueue1.removeRole("role_1");
		Assert.assertFalse(addRoleQResult);
		Assert.assertNull(_role_1.getRelatedRoleQueues());
		
		Assert.assertEquals(_RoleQueue1.getRelatedRoles().length, 2);
		Assert.assertEquals(_RoleQueue1.getRelatedRoles()[0].getRoleName(),"role_2");
		
		addRoleQResult=_role_2.removeFromRoleQueue("roleQueue_1");
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=_role_2.removeFromRoleQueue("roleQueue_1");
		Assert.assertFalse(addRoleQResult);
		
		Assert.assertEquals(_RoleQueue1.getRelatedRoles().length, 1);
		Assert.assertEquals(_RoleQueue1.getRelatedRoles()[0].getRoleName(),"role_3");		
		
		addRoleQResult=_role_2.addInRoleQueue("roleQueue_1");
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=_role_2.addInRoleQueue("roleQueue_1");
		Assert.assertFalse(addRoleQResult);
		Assert.assertEquals(_RoleQueue1.getRelatedRoles().length, 2);
		Assert.assertEquals(_RoleQueue1.getRelatedRoles()[1].getRoleName(),"role_2");
		
		addRoleQResult=activitySpace.removeRoleQueue("roleQueue_2");
		Assert.assertTrue(addRoleQResult);
		addRoleQResult=activitySpace.removeRoleQueue("roleQueue_2");
		Assert.assertFalse(addRoleQResult);	
		
		DataFieldDefinition df1=ActivityComponentFactory.cteateDataFieldDefinition("TestingRoleQueue1_df1", PropertyType.STRING, true);
		df1.setDescription("TestingRoleQueue1_df1 desc");
		df1.setDisplayName("TestingRoleQueue1_df1 displayname");
		df1.setMandatoryField(false);
		df1.setSystemField(true);		
		
		DataFieldDefinition df2=ActivityComponentFactory.cteateDataFieldDefinition("TestingRoleQueue1_df2", PropertyType.STRING, false);
		df2.setDescription("TestingRoleQueue1_df2 desc");
		df2.setDisplayName("TestingRoleQueue1_df2 displayname");
		df2.setMandatoryField(true);
		df2.setSystemField(false);		
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[2];
		dfdArray[0]=df1;
		dfdArray[1]=df2;
		
		addRoleQResult=_RoleQueue1.setExposedDataFields(dfdArray);
		Assert.assertTrue(addRoleQResult);
		
		DataFieldDefinition[] dfdResArray=_RoleQueue1.getExposedDataFields();
		Assert.assertEquals(dfdResArray.length, 2);
		
		Assert.assertEquals(dfdResArray[0].getFieldName(),"TestingRoleQueue1_df1");
		Assert.assertEquals(dfdResArray[0].getDescription(),"TestingRoleQueue1_df1 desc");
		Assert.assertEquals(dfdResArray[0].getDisplayName(),"TestingRoleQueue1_df1 displayname");
		Assert.assertEquals(dfdResArray[0].getFieldType(),PropertyType.STRING);
		Assert.assertEquals(dfdResArray[0].isArrayField(),true);
		Assert.assertEquals(dfdResArray[0].isMandatoryField(),false);
		Assert.assertEquals(dfdResArray[0].isSystemField(),true);		
	}
}