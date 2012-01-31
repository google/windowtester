package com.windowtester.test.eclipse;

import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.Constants;

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
public class EclipseUtil
{
	/** The current eclipse version */
	private static final int ECLIPSE_MAJOR;
	private static final int ECLIPSE_MINOR;
	
	static {
		/*
		 * Get the version of the current running Eclipse instance.
		 * (Note that micro versions may not be accurate.)
		 */
		String versionStr = (String)ResourcesPlugin.getPlugin().getBundle().getHeaders().get(Constants.BUNDLE_VERSION);

		/* $codepro.preprocessor.if version >= 3.1 $ */
		org.osgi.framework.Version ECLIPSE_VERSION = org.osgi.framework.Version.parseVersion(versionStr);
		ECLIPSE_MAJOR = ECLIPSE_VERSION.getMajor();
		ECLIPSE_MINOR = ECLIPSE_VERSION.getMinor();
		
		/* $codepro.preprocessor.elseif version < 3.1 $
		org.eclipse.core.runtime.PluginVersionIdentifier version = new org.eclipse.core.runtime.PluginVersionIdentifier(versionStr);
		ECLIPSE_MAJOR = version.getMajorComponent();
		ECLIPSE_MINOR = version.getMinorComponent();
		
		$codepro.preprocessor.endif $ */
	}

	public static int getMajor() {
		return ECLIPSE_MAJOR;
	}
	
	public static int getMinor() {
		return ECLIPSE_MINOR;
	}

	public static boolean isVersion_31() {
		return getMajor() == 3 && getMinor() == 1;
	}
	
	public static boolean isVersion_32() {
		return getMajor() == 3 && getMinor() == 2;
	}

	public static boolean isVersion_33() {
		return getMajor() == 3 && getMinor() == 3;
	}

	public static boolean isVersion_34() {
		return getMajor() == 3 && getMinor() == 4;
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
	
	public static boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().contains("linux");
	}

	public static boolean isAtLeastVersion_34() {
		return getMajor() >= 3 && getMinor() >= 4;
	}

	public static boolean isAtLeastVersion_35() {
		return getMajor() >= 3 && getMinor() >= 5;
	}
}
