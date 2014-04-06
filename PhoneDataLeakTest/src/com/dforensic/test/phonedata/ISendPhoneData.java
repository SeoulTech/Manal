package com.dforensic.test.phonedata;

import java.io.File;

public interface ISendPhoneData {
	
	public abstract void sendFile(File file);
	
	public abstract String getInterfaceName();
}
