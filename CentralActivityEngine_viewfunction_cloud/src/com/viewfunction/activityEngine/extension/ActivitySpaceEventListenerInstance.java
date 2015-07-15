package com.viewfunction.activityEngine.extension;

public class ActivitySpaceEventListenerInstance {
	private String listenerKey;
	private ActivityEventType eventType;
	private ActivitySpaceEventListener activitySpaceEventListener;
	public String getListenerKey() {
		return listenerKey;
	}
	public void setListenerKey(String listenerKey) {
		this.listenerKey = listenerKey;
	}
	public ActivityEventType getEventType() {
		return eventType;
	}
	public void setEventType(ActivityEventType eventType) {
		this.eventType = eventType;
	}
	public ActivitySpaceEventListener getActivitySpaceEventListener() {
		return activitySpaceEventListener;
	}
	public void setActivitySpaceEventListener(ActivitySpaceEventListener activitySpaceEventListener) {
		this.activitySpaceEventListener = activitySpaceEventListener;
	}

}
