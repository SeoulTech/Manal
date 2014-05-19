package com.dforensic.plugin.manal.model;

public class dataApkandProject {

	private static String apkName;
	private static String projName;
	
	public dataApkandProject()
	{
		
	}

	public static String getProjName() {
		return projName;
	}

	public static void setProjName(String projName) {
		dataApkandProject.projName = projName;
	}

	public static String getApkName() {
		return apkName;
	}

	public static void setApkName(String apkName) {
		dataApkandProject.apkName = apkName;
	}
}
