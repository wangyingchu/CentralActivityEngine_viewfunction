package com.viewfunction.activityEngine.util.cache;

import com.viewfunction.activityEngine.activityBureau.BusinessActivityDefinition;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class ActivityEngineCache {
	
	private CacheManager cacheManager;	
	private Cache businessDefinitionLatestInfoCache;
	private Cache businessDefinitionSnapshootInfoCache;
	private Cache activityId_MetaConfigVersionMappingCache;
	
	public ActivityEngineCache(CacheManager cacheManager){
		this.cacheManager=cacheManager;
	}
	
	public void clearCache(){		
		if(getCacheManager()!=null){
			getCacheManager().shutdown();
		}		
	}

	private CacheManager getCacheManager() {
		return cacheManager;
	}

	private Cache getBusinessDefinitionLatestInfoCache() {
		return businessDefinitionLatestInfoCache;
	}

	public void setBusinessDefinitionLatestInfoCache(Cache businessDefinitionLatestInfoCache) {
		this.businessDefinitionLatestInfoCache = businessDefinitionLatestInfoCache;
	}

	private Cache getBusinessDefinitionSnapshootInfoCache() {
		return businessDefinitionSnapshootInfoCache;
	}

	public void setBusinessDefinitionSnapshootInfoCache(Cache businessDefinitionSnapshootInfoCache) {
		this.businessDefinitionSnapshootInfoCache = businessDefinitionSnapshootInfoCache;
	}	
	
	private Cache getActivityId_MetaConfigVersionMappingCache() {
		return activityId_MetaConfigVersionMappingCache;
	}

	public void setActivityId_MetaConfigVersionMappingCache(Cache activityId_MetaConfigVersionMappingCache) {
		this.activityId_MetaConfigVersionMappingCache = activityId_MetaConfigVersionMappingCache;
	}
	
	public void addBusinessDefinitionLatestInfo(BusinessActivityDefinition bd){
		String spaceName=bd.getActivitySpaceName();
		String activityType=bd.getActivityType();
		String cacheKeyString=spaceName+"_"+activityType;
		getBusinessDefinitionLatestInfoCache().put(new Element(cacheKeyString, bd));		
	}
	
	public void updateBusinessDefinitionLatestInfo(BusinessActivityDefinition bd){
		String spaceName=bd.getActivitySpaceName();
		String activityType=bd.getActivityType();
		String cacheKeyString=spaceName+"_"+activityType;		
		Element cachedBusinessDefinitionLatestInfo = getBusinessDefinitionLatestInfoCache().get(cacheKeyString);
		if(cachedBusinessDefinitionLatestInfo!=null){
			getBusinessDefinitionLatestInfoCache().remove(cacheKeyString);
		}			
		getBusinessDefinitionLatestInfoCache().put(new Element(cacheKeyString, bd));		
	}
	
	public BusinessActivityDefinition getBusinessDefinitionLatestInfo(String spaceName,String activityType){		
		String cacheKeyString=spaceName+"_"+activityType;
		Element cachedBusinessDefinitionLatestInfo = getBusinessDefinitionLatestInfoCache().get(cacheKeyString);
		if(cachedBusinessDefinitionLatestInfo!=null){
			BusinessActivityDefinition businessActivityDefinition=(BusinessActivityDefinition)cachedBusinessDefinitionLatestInfo.getObjectValue();				
			return businessActivityDefinition;
		}else{
			return null;
		}
	}
	
	public boolean removeBusinessDefinitionLatestInfo(String spaceName,String activityType){		
		String cacheKeyString=spaceName+"_"+activityType;
		return getBusinessDefinitionLatestInfoCache().remove(cacheKeyString);		
	}
	
	public void addBusinessDefinitionSnapshootInfo(BusinessActivityDefinition bd){		
		String spaceName=bd.getActivitySpaceName();
		String activityType=bd.getActivityType();
		long metaConfigVersion=bd.getMetaConfigurationVersion();		
		String cacheKeyString=spaceName+"_"+activityType+"_"+metaConfigVersion;
		getBusinessDefinitionSnapshootInfoCache().put(new Element(cacheKeyString, bd));		
	}
	
	public BusinessActivityDefinition getBusinessDefinitionSnapshootInfo(String spaceName,String activityType,long metaConfigVersion){		
		String cacheKeyString=spaceName+"_"+activityType+"_"+metaConfigVersion;
		Element cachedBusinessDefinitionSnapshootInfo = getBusinessDefinitionSnapshootInfoCache().get(cacheKeyString);
		if(cachedBusinessDefinitionSnapshootInfo!=null){
			BusinessActivityDefinition businessActivityDefinition=(BusinessActivityDefinition)cachedBusinessDefinitionSnapshootInfo.getObjectValue();				
			return businessActivityDefinition;
		}else{
			return null;
		}		
	}
	
	public void addActivityIdAndMetaConfigVersionMapping(String spaceName,String activityId,long metaConfigVersion){		
		String cacheKeyString=spaceName+"_"+activityId;
		Long versionValue=new Long(metaConfigVersion);
		getActivityId_MetaConfigVersionMappingCache().put(new Element(cacheKeyString, versionValue));		
	}
	
	public long getMetaConfigVersionInCacheByActivityId(String spaceName,String activityId){		
		String cacheKeyString=spaceName+"_"+activityId;		
		Element cachedActivityIdAndMetaConfigVersionMapping = getActivityId_MetaConfigVersionMappingCache().get(cacheKeyString);
		if(cachedActivityIdAndMetaConfigVersionMapping!=null){			
			return ((Long)cachedActivityIdAndMetaConfigVersionMapping.getObjectValue()).longValue();			
		}else{
			return 0;
		}
	}
	
	public boolean removeActivityIdAndMetaConfigVersionMapping(String spaceName,String activityId){		
		String cacheKeyString=spaceName+"_"+activityId;
		return getActivityId_MetaConfigVersionMappingCache().remove(cacheKeyString);		
	}	
}
