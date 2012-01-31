/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.util;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;


public class WTSettingsFileAccessor {

	
	public boolean getBooleanSetting(String settingPropertyName) throws IOException {
		String setting = getSetting(settingPropertyName);
		return setting != null && "true".equals(setting);
	}
	
	public String getSetting(String settingPropertyName) throws IOException {
		File file = getWTSettingsFile();
		String setting = new PropertiesFile(file).getProperty(settingPropertyName);
		return setting;
	}
	
	private File getWTSettingsFile() {
		IPath path = getWTDir();
		path = path.append("settings.properties");
		//System.out.println(path);
		return path.toFile();
	}

	private IPath getWTDir() {
		String userDir = System.getProperty("user.home");
		//System.out.println(userDir);
		IPath path = new Path(userDir);
		path = path.append("WindowTester");
		return path;
	}

	public void setSetting(String settingName, String settingValue) throws IOException {
		new PropertiesFile(getWTSettingsFile()).setProperty(settingName, settingValue);
	}
	
	
	
}
