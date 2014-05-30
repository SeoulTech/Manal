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
package com.dforensic.plugin.manal.utils;

public class ResUtils {
	private final static String PROJECT_RES_PATH_URL = "platform:/plugin/com.dforensic.plugin.manal/";
	private final static String OUTPUT_PATH_URL = "platform:/plugin/com.dforensic.plugin.manal/output/";
	
	// TODO when store output
	// check if a folder exist (e.g. output),
	// create if not
	/*
	public static BufferedReader openProjectFileReader(final String name) {
		try {
			URL url = new URL(PROJECT_RES_PATH + name);
			InputStream inputStream = url.openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));

			return in;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedReader openProjectFile(final String name) {
		try {
			URL url = new URL(PROJECT_RES_PATH + name);
			File file = File..openConnection().getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					inputStream));

			return in;

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	*/
	
}
