package com.viewfunction.activityEngine.helper;

import java.util.List;
import java.util.Map;

import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;
import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineDataException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;

public interface BatchOperationHelper {
	public List<ActivityData[]> batchQueryActivityStepsData(String activitySpaceName,List<ActivityStep> activityStepsList,Map<String, BusinessActivityDefinition> businessActivityDefinitionMap) throws ActivityEngineRuntimeException, ActivityEngineDataException, ActivityEngineActivityException;
}
