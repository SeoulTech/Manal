/*
 *  <Manal project is an eclipse plugin for the automation of malware analysis.>
 *  Copyright (C) <2014>  <Nikolay Akatyev, Hojun Son>
 *  This file is part of Manal project.
 *
 *  Manal project is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3 of the License.
 *
 *  Manal project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Manal project. If not, see <http://www.gnu.org/licenses/>.
 *  
 *  Contact information of contributors:
 *  - Nikolay Akatyev: nikolay.akatyev@gmail.com
 *  - Hojun Son: smuoon4680@gmail.com
 */
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
