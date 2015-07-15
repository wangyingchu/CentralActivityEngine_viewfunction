package com.viewfunction.activityEngine.activityView.common;

public interface CustomAttribute {	
	public String getAttributeName();	
	public void setAttributeName(String attributeName);
	public int getAttributeType();	
	public void setAttributeType(int attributeType);	
	public boolean isArrayAttribute();	
	public void setArrayAttribute(boolean arrayAttribute);	
	public Object getAttributeValue();	
	public void setAttributeValue(Object objectValue);    
}