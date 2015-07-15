package com.viewfunction.activityEngine.testNGTest;

import java.util.List;

import javax.jcr.PropertyType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.Roster;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;

public class TestNG_RosterTestCase {
public static String testActivitySpace="testActivitySpace_01"; 	
	
	@Test
	public void testRosterOperation() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException{
		ActivitySpace activitySpace=ActivityComponentFactory.getActivitySpace(testActivitySpace);
		Assert.assertNotNull(activitySpace);
		Assert.assertEquals(activitySpace.getActivitySpaceName(),testActivitySpace);		
		Roster[] rosters=activitySpace.getRosters();		
		Roster roster1=activitySpace.getRoster("TestingRoster1");		
		if(roster1==null){
			Assert.assertNull(rosters);
			roster1=ActivityComponentFactory.createRoster(testActivitySpace, "TestingRoster1");
			roster1.setDescription("roster1Desc");
			roster1.setDisplayName("roster1Dispalyname");			
			boolean addres1=activitySpace.addRoster(roster1);
			Assert.assertTrue(addres1);			
		}
		
		roster1=activitySpace.getRoster("TestingRoster1");		
		roster1.removeActivityType("businessActivityType1");		
		Assert.assertNull(roster1.getContainedActivityTypes());
		
		BusinessActivityDefinition businessActivityDefinition=activitySpace.getBusinessActivityDefinition("businessActivityType1");
		Assert.assertNull(businessActivityDefinition.getRosterName());		
		
		boolean addATResult=roster1.addActivityType("businessActivityType1");
		Assert.assertTrue(addATResult);
		Assert.assertEquals(roster1.getContainedActivityTypes().length,1);
		Assert.assertEquals(roster1.getContainedActivityTypes()[0],"businessActivityType1");
		
		businessActivityDefinition=activitySpace.getBusinessActivityDefinition("businessActivityType1");		
		Assert.assertEquals(businessActivityDefinition.getRosterName(), "TestingRoster1");	
		
		DataFieldDefinition df1=ActivityComponentFactory.cteateDataFieldDefinition("TestingRoster1_df1", PropertyType.STRING, true);
		df1.setDescription("TestingRoster1_df1 desc");
		df1.setDisplayName("TestingRoster1_df1 displayname");
		df1.setMandatoryField(false);
		df1.setSystemField(true);		
		
		DataFieldDefinition df2=ActivityComponentFactory.cteateDataFieldDefinition("TestingRoster1_df2", PropertyType.STRING, false);
		df2.setDescription("TestingRoster1_df2 desc");
		df2.setDisplayName("TestingRoster1_df2 displayname");
		df2.setMandatoryField(true);
		df2.setSystemField(false);		
		
		DataFieldDefinition[] rosterDfArray=roster1.getExposedDataFields();
		//Assert.assertNull(rosterDfArray);
		
		DataFieldDefinition[] dfdArray=new DataFieldDefinition[2];
		dfdArray[0]=df1;
		dfdArray[1]=df2;		
		boolean setDfResult=roster1.setExposedDataFields(dfdArray);
		Assert.assertTrue(setDfResult);
		
		rosterDfArray=roster1.getExposedDataFields();
		Assert.assertEquals(rosterDfArray.length,2);
		Assert.assertEquals(rosterDfArray[0].getFieldName(),"TestingRoster1_df1");
		Assert.assertEquals(rosterDfArray[0].getDisplayName(),"TestingRoster1_df1 displayname");
		Assert.assertEquals(rosterDfArray[0].getDescription(),"TestingRoster1_df1 desc");
		Assert.assertEquals(rosterDfArray[0].getFieldType(),PropertyType.STRING);
		Assert.assertEquals(rosterDfArray[0].isArrayField(),true);
		Assert.assertEquals(rosterDfArray[0].isMandatoryField(),false);
		Assert.assertEquals(rosterDfArray[0].isSystemField(),true);
		
		Assert.assertEquals(rosterDfArray[1].getFieldName(),"TestingRoster1_df2");
		Assert.assertEquals(rosterDfArray[1].getDisplayName(),"TestingRoster1_df2 displayname");
		Assert.assertEquals(rosterDfArray[1].getDescription(),"TestingRoster1_df2 desc");
		Assert.assertEquals(rosterDfArray[1].getFieldType(),PropertyType.STRING);
		Assert.assertEquals(rosterDfArray[1].isArrayField(),false);
		Assert.assertEquals(rosterDfArray[1].isMandatoryField(),true);
		Assert.assertEquals(rosterDfArray[1].isSystemField(),false);
		
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
		
		List<BusinessActivity> baList=roster1.fetchBusinessActivitys();
		
		for(BusinessActivity businessActivity:baList){			
			Assert.assertEquals(businessActivity.getActivityDefinition().getActivityType(),"businessActivityType1");
			Assert.assertEquals(businessActivity.getActivityDefinition().getRosterName(),"TestingRoster1");
			//System.out.println(businessActivity.getActivityId());
			//System.out.println(businessActivity.getActivityAttachmentFolder());
			//System.out.println(businessActivity.getActivityProcessObject().getCurrentProcessSteps());
			//System.out.println(businessActivity.getActivityData().length);			
		}		
	}
}