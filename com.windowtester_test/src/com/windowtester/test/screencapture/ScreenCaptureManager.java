package com.windowtester.test.screencapture;

import java.io.File;

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
public class ScreenCaptureManager {
	
	private static final String SSHOT_DIR = "wintest";

	
	/**
	 * Answer the number of screen shots in the normal location
	 * 
	 * @return the number of screen shots
	 */
	public static int getScreenShotCount() {
		return getScreenShotCount(SSHOT_DIR);
	}

	/**
	 * Answer the number of screen shots in the specified location
	 * 
	 * @return the number of screen shots
	 */
	public static int getScreenShotCount(String path) {
		int count = 0;
		File[] list = new File(path).listFiles();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (isScreenShot(list[i]))
					count++;
			}
		}
		return count;
	}

	/**
	 * Determine if the specified file is a screen shot file
	 * 
	 * @param file the file
	 * @return <code>true</code> if the file is a screen shot file, else <code>false</code>
	 */
	public static boolean isScreenShot(File file) {
		return file.getName().endsWith(".png");
	}

	public static void clearExistingScreenShotsForTest(String testName) {
		File[] list = new File(SSHOT_DIR).listFiles();
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				if (isScreenShot(list[i]) && list[i].getName().startsWith(testName))
					list[i].delete();
			}
		}
	}
}
