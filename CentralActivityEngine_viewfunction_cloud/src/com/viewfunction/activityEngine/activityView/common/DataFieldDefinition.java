package com.viewfunction.activityEngine.activityView.common;

public interface DataFieldDefinition {
	String 	getFieldName();
	int getFieldType();
	String 	getDisplayName();
	void 	setDisplayName(String displayName);
	String 	getDescription();
	void 	setDescription(String description);	
	boolean isArrayField();
	boolean isSystemField();
	void setSystemField(boolean isSystemField);
	boolean isMandatoryField();
	void setMandatoryField(boolean isMandatoryField);	
	boolean isReadableField();
	void setReadableField(boolean isReadableField);
	boolean isWriteableField();
	void setWriteableField(boolean isWriteableField);
}