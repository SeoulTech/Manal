package com.dforensic.plugin.manal.model;

public class ProjectProperties {

	private static String apkName;
	private static String prjName;
	
	public ProjectProperties()
	{
		
	}

	public static String getProjName() {
		return prjName;
	}

	public static void setProjName(String prjName) {
		ProjectProperties.prjName = prjName;
	}

	public static String getApkName() {
		return apkName;
	}

	public static void setApkName(String apkName) {
		ProjectProperties.apkName = apkName;
	}
}
