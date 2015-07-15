package com.viewfunction.activityEngine.activityView;

import java.util.List;

import com.viewfunction.activityEngine.activityView.common.ActivityStep;
import com.viewfunction.activityEngine.exception.ActivityEngineActivityException;
import com.viewfunction.activityEngine.exception.ActivityEngineProcessException;
import com.viewfunction.activityEngine.exception.ActivityEngineRuntimeException;
import com.viewfunction.activityEngine.security.Role;

public interface RoleQueue extends Queue{	
	public Role[] getRelatedRoles() throws ActivityEngineRuntimeException;	
	public boolean addRole(String roleName) throws ActivityEngineRuntimeException, ActivityEngineActivityException;
	public boolean removeRole(String roleName) throws ActivityEngineRuntimeException, ActivityEngineActivityException;		
	public List<ActivityStep> fetchActivitySteps() throws ActivityEngineProcessException, ActivityEngineRuntimeException;
}