package com.dforensic.plugin.manal.model;

import java.util.ArrayList;
import java.util.List;

public class ApiDescription {
	private String mReturnType;
	private String mReturnValue;
	private List<String> mParamTypes;
	private List<String> mParamValues;
	private String mMethodName;

	public String getMethodName() {
		return mMethodName;
	}

	public void setMethodName(String methodName) {
		mMethodName = methodName;
	}
	
	public String getReturnType() {
		return mReturnType;
	}

	public void setReturnType(String returnType) {
		mReturnType = returnType;
	}

	public String getReturnValue() {
		return mReturnValue;
	}

	public void setReturnValue(String returnValue) {
		mReturnValue = returnValue;
	}
	
	public List<String> getParamTypes() {
		return mParamTypes;
	}

	public void setParamTypes(List<String> paramTypes) {
		mParamTypes = paramTypes;
	}
	
	public List<String> getParamValues() {
		return mParamValues;
	}

	public void setParamValues(List<String> paramValues) {
		mParamValues = paramValues;
	}

}
