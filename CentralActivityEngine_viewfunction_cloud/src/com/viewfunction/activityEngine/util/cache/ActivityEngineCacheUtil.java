package com.viewfunction.activityEngine.util.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class ActivityEngineCacheUtil {	
	
	private static final String BUSINESSDEFINITION_LATEST_CACHE_NAME="BUSINESSDEFINITION_LATEST_CACHE_NAME";	
	private static final String BUSINESSDEFINITION_SNAPSHOOT_CACHE_NAME="BUSINESSDEFINITION_SNAPSHOOT_CACHE_NAME";
	private static final String ACTIVITYID_METACONFIGVERSION_MAPPING_CACHE_NAME="ACTIVITYID_METACONFIGVERSION_MAPPING_CACHE_NAME";
	
	public ActivityEngineCache initActivityEngineCache(){		
		//init encache		
		CacheManager _CacheManager=CacheManager.getInstance();		
		ActivityEngineCache activityEngineCache=new ActivityEngineCache(_CacheManager);		
		
		Cache businessDefinitionLatestInfoCache = new Cache(
				new CacheConfiguration(BUSINESSDEFINITION_LATEST_CACHE_NAME, 10000)
				.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
			    .eternal(false)
			    .timeToLiveSeconds(86400)
			    .timeToIdleSeconds(60000)
			    .diskExpiryThreadIntervalSeconds(0)		
			    .diskPersistent(false));			    
		_CacheManager.addCache(businessDefinitionLatestInfoCache);
		businessDefinitionLatestInfoCache=_CacheManager.getCache(BUSINESSDEFINITION_LATEST_CACHE_NAME);

		activityEngineCache.setBusinessDefinitionLatestInfoCache(businessDefinitionLatestInfoCache);
		
		Cache businessDefinitionSnapshootInfoCache = new Cache(
				new CacheConfiguration(BUSINESSDEFINITION_SNAPSHOOT_CACHE_NAME, 10000)
				.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
			    .eternal(false)
			    .timeToLiveSeconds(86400)
			    .timeToIdleSeconds(60000)
			    .diskExpiryThreadIntervalSeconds(0)
			    .diskPersistent(false));				
		_CacheManager.addCache(businessDefinitionSnapshootInfoCache);
		businessDefinitionSnapshootInfoCache=_CacheManager.getCache(BUSINESSDEFINITION_SNAPSHOOT_CACHE_NAME);
		activityEngineCache.setBusinessDefinitionSnapshootInfoCache(businessDefinitionSnapshootInfoCache);
		
		Cache activityId_MetaConfigVersionMappingCache = new Cache(
				new CacheConfiguration(ACTIVITYID_METACONFIGVERSION_MAPPING_CACHE_NAME, 10000)
				.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
			    .eternal(false)
			    .timeToLiveSeconds(86400)
			    .timeToIdleSeconds(60000)
			    .diskExpiryThreadIntervalSeconds(0)
			    .diskPersistent(false));			   	
		_CacheManager.addCache(activityId_MetaConfigVersionMappingCache);
		businessDefinitionSnapshootInfoCache=_CacheManager.getCache(ACTIVITYID_METACONFIGVERSION_MAPPING_CACHE_NAME);
		activityEngineCache.setActivityId_MetaConfigVersionMappingCache(activityId_MetaConfigVersionMappingCache);		
		
		return activityEngineCache;		
	}
}
