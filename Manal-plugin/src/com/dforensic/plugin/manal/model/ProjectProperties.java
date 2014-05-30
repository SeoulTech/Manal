package com.dforensic.plugin.manal.model;

public class ProjectProperties {
	
	public static final String QUALIFIER = "Manal";

	private static final String apkNameKey = "apkNameKey";
	private static String apkNameVal;
	
//	private static final String prjNameKey = "prjNameKey";
//	private static String prjNameVal;
	
	private static final String androidPathKey = "androidPathKey";
	private static String androidPathVal;
	
	public ProjectProperties()
	{
		
	}

//	public static String getPrjNameVal() {
//		return prjNameVal;
//	}
	
//	public static String getPrjNameKey() {
//		return prjNameKey;
//	}

//	public static void setPrjNameVal(String prjName) {
//		prjNameVal = prjName;
//	}

	public static String getApkNameVal() {
		return apkNameVal;
	}
	
	public static String getApkNameKey() {
		return apkNameKey;
	}

	public static void setApkNameVal(String apkName) {
		apkNameVal = apkName;
	}
	
	public static String getAndroidPathVal() {
		return androidPathVal;
	}
	
	public static String getAndroidPathKey() {
		return androidPathKey;
	}

	public static void setAndroidPathVal(String androidPath) {
		androidPathVal = androidPath;
	}
	
}
