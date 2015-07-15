package com.viewfunction.activityEngine.activityView.commonImpl;

import java.io.Serializable;

import com.viewfunction.activityEngine.activityView.common.ActivityData;
import com.viewfunction.activityEngine.activityView.common.DataFieldDefinition;

public class CCRActivityDataImpl implements ActivityData,Serializable{
	private static final long serialVersionUID = -4299311156968333654L;
	private DataFieldDefinition dataFieldDefinition;	
	private Object dataFieldValue;
	
	public CCRActivityDataImpl(DataFieldDefinition dataFieldDefinition){
		this.dataFieldDefinition=dataFieldDefinition;
	}

	@Override
	public DataFieldDefinition getDataFieldDefinition() {		
		return this.dataFieldDefinition;
	}

	@Override
	public Object getDatFieldValue() {		
		return this.dataFieldValue;
	}

	@Override
	public void setDatFieldValue(Object objectValue) {
		this.dataFieldValue=objectValue;		
	}
}