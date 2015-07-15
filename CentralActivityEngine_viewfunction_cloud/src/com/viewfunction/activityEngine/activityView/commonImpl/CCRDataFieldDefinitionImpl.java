package com.viewfunction.activityEngine.activityView.commonImpl;

import java.io.Serializable;

import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;
import javax.jcr.PropertyType;

public class CCRDataFieldDefinitionImpl implements DataFieldDefinition,Serializable{	
	private static final long serialVersionUID = 7924776578731626689L;
	private String fieldName;
	private int fieldType=PropertyType.STRING;
	private String displayName;
	private String description;
	private boolean isSystemField=false;
	private boolean isMandatoryField=false;
	private boolean isArrayField;	
	private boolean isReadableField=true;
	private boolean isWriteableField=true;
	
	public CCRDataFieldDefinitionImpl(String fieldName,int fieldType,boolean isArrayField){
		this.fieldName=fieldName;
		this.fieldType=fieldType;
		this.isArrayField=isArrayField;
	}	

	@Override
	public String getFieldName() {		
		return this.fieldName;
	}

	@Override
	public int getFieldType() {		
		return this.fieldType;
	}

	@Override
	public String getDisplayName() {
		if(displayName!=null){
			return displayName;
		}else{
			return fieldName;
		}		
	}

	@Override
	public void setDisplayName(String displayName) {
		this.displayName=displayName;		
	}

	@Override
	public String getDescription() {
		if(description!=null){
			return description;
		}else{
			return fieldName;
		}		
	}

	@Override
	public void setDescription(String description) {
		this.description=description;		
	}	

	@Override
	public boolean isSystemField() {		
		return this.isSystemField;
	}

	@Override
	public boolean isMandatoryField() {		
		return isMandatoryField;
	}

	@Override
	public boolean isArrayField() {		
		return isArrayField;
	}

	@Override
	public void setSystemField(boolean isSystemField) {
		this.isSystemField=isSystemField;		
	}

	@Override
	public void setMandatoryField(boolean isMandatoryField) {
		this.isMandatoryField=isMandatoryField;		
	}
	
	@Override
	public boolean isReadableField() {
		return isReadableField;
	}
	
	@Override
	public void setReadableField(boolean isReadableField) {
		this.isReadableField = isReadableField;
	}
	
	@Override
	public boolean isWriteableField() {
		return isWriteableField;
	}
	
	@Override
	public void setWriteableField(boolean isWriteableField) {
		this.isWriteableField = isWriteableField;
	}
}
