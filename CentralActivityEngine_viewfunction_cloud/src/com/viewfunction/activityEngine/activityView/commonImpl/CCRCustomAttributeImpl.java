package com.viewfunction.activityEngine.activityView.commonImpl;

import com.viewfunction.activityEngine.activityView.common.CustomAttribute;

public class CCRCustomAttributeImpl implements CustomAttribute{
	private String attributeName;
	private int attributeType;
	private boolean arrayAttribute;
	private Object attributeValue;
	
	@Override
	public String getAttributeName() {
		return attributeName;
	}
	
	@Override
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	
	@Override
	public int getAttributeType() {
		return attributeType;
	}
	
	@Override
	public void setAttributeType(int attributeType) {
		this.attributeType = attributeType;
	}
	
	@Override
	public boolean isArrayAttribute() {
		return arrayAttribute;
	}
	
	@Override
	public void setArrayAttribute(boolean arrayAttribute) {
		this.arrayAttribute = arrayAttribute;
	}
	
	@Override
	public Object getAttributeValue() {
		return attributeValue;
	}
	
	@Override
	public void setAttributeValue(Object attributeValue) {
		this.attributeValue = attributeValue;
	}	
}