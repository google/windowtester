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
package com.windowtester.runtime.internal;

import org.eclipse.swt.SWT;

public final class OS {

	private static boolean isLinux;
	private static boolean isMac = System.getProperty("mrj.version") != null;
	private static String OS_NAME = System.getProperty("os.name");
	private static boolean isOSX = isMac && OS_NAME.indexOf("OS X") != -1;

	private OS() {

	}

	static {
		String property = System.getProperty("os.name");
		// actually it can not be null, but sometimes miracles happen
		if (property != null) {
			isLinux = property.equalsIgnoreCase("Linux");
		}
	}

	public static boolean isLinux() {
		return isLinux;
	}

	/**
	 * @return
	 * @since 3.8.1
	 */
	public static boolean isOSX() {
		return isOSX;
	}

	// moved from abbot.Platform
	/**
	 * @since 3.9.1
	 */
	public static boolean isMacCocoa() {
		return isMac && SWT.getPlatform().equals("cocoa");
	}

	/**
	 * @since 3.9.1
	 */
	public static boolean is64BitCocoa() {
		String arch = System.getProperty("osgi.arch");
		if (arch == null)
			return false;
		return arch.equals("x86_64");
	}

}
