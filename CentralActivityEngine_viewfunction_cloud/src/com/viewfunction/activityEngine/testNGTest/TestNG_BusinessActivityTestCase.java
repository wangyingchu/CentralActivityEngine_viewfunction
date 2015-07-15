package com.viewfunction.activityEngine.testNGTest;

import javax.jcr.PropertyType;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.viewfunction.activityEngine.activityBureau.ActivitySpace;
import com.viewfunction.activityEngine.activityBureau.BusinessActivity;
import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;

import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;

import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

import com.viewfunction.activityEngine.util.factory.ActivityComponentFactory;
import com.viewfunction.contentRepository.util.exception.ContentReposityException;


public class TestNG_BusinessActivityTestCase {
	public static String testActivitySpace="testActivitySpace_01"; 	
	
	@Test
	public void testBusinessActivityOperation() throws ActivityEngineRuntimeException, ActivityEngineActivityException, ActivityEngineDataException, ActivityEngineProcessException, ContentReposityException{		
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
		Assert.assertEquals(resultBusinessActivity.getActivityProcessObject().getCurrentProcessSteps().get(0).getStepName(), "Write monthly financial report");
		
		BusinessActivityDefinition BusinessActivityDefinition=resultBusinessActivity.getActivityDefinition();		
		Assert.assertEquals(BusinessActivityDefinition.getActivityType(),"businessActivityType1");		
		
		ActivityData[] activityDataArray=resultBusinessActivity.getActivityData();
		Assert.assertEquals(activityDataArray[0].getDataFieldDefinition().getFieldName(),"df3");
		Assert.assertEquals(activityDataArray[1].getDataFieldDefinition().getFieldName(),"df4");
		Assert.assertEquals(activityDataArray[1].getDatFieldValue(),"testStringValue_df4");
		
		ActivityData[] stepDataArray=resultBusinessActivity.getStepActivityData("stepA");		
		Assert.assertEquals(stepDataArray[0].getDataFieldDefinition().getFieldName(),"df3");
		
		ActivityData[] stepDataArray_2=resultBusinessActivity.getStepActivityData("stepB");		
		Assert.assertEquals(stepDataArray_2[0].getDataFieldDefinition().getFieldName(),"df4");		
	}
}