package com.dforensic.plugin.manal.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ResUtils {
	private final static String PROJECT_RES_PATH = "platform:/plugin/com.dforensic.plugin.manal/";
	
	public static BufferedReader openProjectFile(final String name) {
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
	
}
