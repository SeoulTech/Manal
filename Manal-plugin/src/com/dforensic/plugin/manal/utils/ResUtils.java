package com.dforensic.plugin.manal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.eclipse.core.internal.resources.File;

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
