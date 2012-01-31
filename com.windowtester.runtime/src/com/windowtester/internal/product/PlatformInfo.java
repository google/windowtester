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
package com.windowtester.internal.product;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;
import org.eclipse.core.runtime.Status;

import com.windowtester.internal.debug.Logger;
import com.windowtester.internal.runtime.RuntimePlugin;
import com.windowtester.runtime.util.PluginUtilities;

/**
 * Central place for Platform information
 * <p>
 * 
 * @author Dan Rubel
 */
public class PlatformInfo
{
	/**
	 * The name displayed for an unknown IDE
	 */
	public static final String UNKNOWN_IDE_NAME = "Unknown IDE";

	/**
	 * Constant representing unknown version of Eclipse or IDE
	 */
	public static final PluginVersionIdentifier UNKNOWN_VERSION = new PluginVersionIdentifier(100, 1, 4);

	/**
	 * Constant representing unknown version string of Eclipse or IDE
	 */
	public static final String UNKNOWN_VERSION_STRING = UNKNOWN_VERSION.toString();

	/**
	 * The version displayed for an unknown IDE version
	 */
	public static final String UNKNOWN_BUILD_ID = "?BUILD-ID?";
	
	/**
	 * The NL value displayed for an unknown NL
	 */
	public static final String UNKNOWN_NL = "?NL?";
	
	/**
	 * The full name for the IDE
	 */
	private static String ideName;
	
	/**
	 * The development environment version string or <code>null</code> if not
	 * initialized yet by {@link #getIDEVersionString()}
	 */
	private static String ideVersionString;
	
	/**
	 * The development environment's NL (not null, not empty, no leading or trailing
	 * spaces)
	 */
	private static String ideNL;

	/**
	 * The version of Eclipse upon which the development environment is based or
	 * <code>null</code> if not initialized yet by {@link #getEclipseVersion()}
	 */
	private static PluginVersionIdentifier eclipseVersion;

	/**
	 * The build identifier for Eclipse (e.g. "v20061503") or <code>null</code> if not
	 * initialized yet by {@link #getEclipseBuildId()}. For Eclipse 3.2, this is the same
	 * as the Eclipse version qualifier segment. For older versions of Eclipse, this is
	 * read from the JDT plugin about.properties file.
	 */
	private static String eclipseBuildId;

	/**
	 * The name for this particular Eclipse build (e.g. "M6", "M7", "RC1", "", etc) or
	 * <code>null</code> if not initialized yet by {@link #getEclipseBuildName()}. This
	 * may contain the same value as {@link #getEclipseBuildId()} if the build is not a
	 * milestone, release candidate, or GA.
	 */
	private static String eclipseBuildName;

	/**
	 * The build date for this particular instance of Eclipse as derived from
	 * {@link #getEclipseBuildId()} or <code>null</code> if not initialized yet by
	 * {@link #getEclipseBuildDate()}.
	 */
	private static GregorianCalendar eclipseBuildDate;

	private static String ideCompatibilityWarningMessage;

	/**
	 * No instances
	 */
	private PlatformInfo() {
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	// //////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the development environment full name
	 * 
	 * @return the full name (not null, not empty, no leading or trailing spaces)
	 */
	public static String getIDEName() {
		if (ideName == null) {
			try {
				/* $codepro.preprocessor.if version >= 3.0 $ */
				if (Platform.getProduct() != null)
					ideName = Platform.getProduct().getName();
				
				/* $codepro.preprocessor.elseif version < 3.0 $
				org.eclipse.core.runtime.IPluginDescriptor descriptor = Platform.getPluginRegistry()
					.getPluginDescriptor(
						org.eclipse.core.boot.BootLoader.getCurrentPlatformConfiguration()
							.getPrimaryFeatureIdentifier());
				if (descriptor != null)
					ideName = descriptor.getLabel();
				
				$codepro.preprocessor.endif $ */
				if (ideName == null)
					ideName = UNKNOWN_IDE_NAME;
				else {
					ideName = ideName.trim();
					if (ideName.length() == 0)
						ideName = UNKNOWN_IDE_NAME;
					else if (ideName.startsWith("Eclipse Pl"))
						ideName = "Eclipse";
					else if (ideName.startsWith("Common OS-independent base of the Eclipse platform"))
						ideName = "Eclipse";
				}
			}
			catch (Exception ex) {
				// Logger may not be open, so cannot log exception
				ideName = UNKNOWN_IDE_NAME;
			}
		}
		return ideName;
	}
	
	/**
	 * Answer the development environment version string
	 * 
	 * @return the version string (not null, not empty, no leading or trailing spaces)
	 */
	public static String getIDEVersionString() {
		if (ideVersionString == null) {
			try {
				String pluginId = null;
				
				/* $codepro.preprocessor.if version >= 3.0 $ */
				if (Platform.getProduct() != null)
					pluginId = Platform.getProduct().getDefiningBundle().getSymbolicName();
				
				/* $codepro.preprocessor.elseif version < 3.0 $
				pluginId = org.eclipse.core.boot.BootLoader.getCurrentPlatformConfiguration().getPrimaryFeatureIdentifier();
					
				$codepro.preprocessor.endif $ */
				ideVersionString = PluginUtilities.getVersionString(pluginId);
			}
			catch (Exception ex) {
				// Logger may not be open, so cannot log exception
			}
			if (ideVersionString == null || ideVersionString.length() == 0)
				ideVersionString = UNKNOWN_VERSION_STRING;
		}
		return ideVersionString;
	}

	/**
	 * Returns the string name of the current locale for use in finding files whose path
	 * starts with <code>$nl$</code>.
	 * 
	 * @return the national language being used (not null, not empty, no leading or
	 *         trailing spaces)
	 */
	public static String getIDENL() {
		if (ideNL == null) {
			/* $codepro.preprocessor.if version >= 3.0 $ */
			try {
				ideNL = Platform.getNL();
			}
			catch (Exception e) {
				// Logger may not be open, so cannot log exception
			}
			/* $codepro.preprocessor.endif $ */
			if (ideNL == null)
				ideNL = UNKNOWN_NL;
		}
		return ideNL;
	}
	
	/**
	 * Answer the version of Eclipse upon which the development environment is based.
	 * 
	 * @return the version (not <code>null</code>)
	 */
	public static PluginVersionIdentifier getEclipseVersion() {
		if (eclipseVersion == null) {
			try {
				eclipseVersion = PluginUtilities.getVersion("org.eclipse.core.runtime");
				
				// Use org.eclipse.jdt.ui to tell the difference between E-3.5 and E-3.6 M#
				if (eclipseVersion.getMajorComponent() == 3 && eclipseVersion.getMinorComponent() == 5) {
					PluginVersionIdentifier version = PluginUtilities.getVersion("org.eclipse.jdt.ui");
					if (version.getMajorComponent() == 3 && version.getMinorComponent() == 6)
						eclipseVersion = version;
				}
				
				// For older versions of Eclipse, read the build from the JDT
				if (eclipseVersion.getQualifierComponent().length() == 0)
					eclipseVersion = new PluginVersionIdentifier(
						eclipseVersion.getMajorComponent(), 
						eclipseVersion.getMinorComponent(), 
						eclipseVersion.getServiceComponent(), 
						"v" + readBuildId("org.eclipse.jdt", "about.mappings"));
			}
			catch (Exception e) {
				// Logger may not be open, so cannot log exception
			}			
			if (eclipseVersion == null)
				eclipseVersion = new PluginVersionIdentifier(0, 0, 0);
		}		
		return eclipseVersion;
	}
	
	/**
	 * Answer the build identifier for Eclipse (e.g. "200408122000"). This is read from
	 * the JDT plugin about.properties file.
	 * 
	 * @return the build id or {@link #UNKNOWN_BUILD_ID} if it cannot be deteremined
	 */
	public static String getEclipseBuildId() {
		if (eclipseBuildId == null) {
			try {
				eclipseBuildId = readBuildId("org.eclipse.jdt", "about.mappings");
			}
			catch (Exception e) {
				// Logger may not be open, so cannot log exception
			}
			if (eclipseBuildId == null)
				eclipseBuildId = UNKNOWN_BUILD_ID;
		}
		return eclipseBuildId;
	}
	
	/**
	 * Answer the name for this particular Eclipse build (e.g. "M6", "M7", "RC1", "", etc).
	 * This may return the same value as {@link #getEclipseBuildId()} if the build is not a milestone, release candidate, or GA.
	 * 
	 * @return the build name (not <code>null</code>, but may be empty if it is a GA)
	 */
	public static String getEclipseBuildName() {
		if (eclipseBuildName == null) {
//			HashMap buildIdMap = new HashMap();
//			
//			// Eclipse 3.0 Milestones
//			buildIdMap.put("200312182000", "M6");
//			buildIdMap.put("200402122000", "M7");
//			buildIdMap.put("200403261517", "M8");
//			buildIdMap.put("200405211200", "M9");
//			buildIdMap.put("200405290105", "RC1");
//			buildIdMap.put("200406111814", "RC2");
//			buildIdMap.put("200406192000", "RC3");
//			
//			// Eclipse 3.0 GA
//			buildIdMap.put("200406251208", "");
//			
//			// Eclipse 3.0.1 GA
//			buildIdMap.put("200409161125", "");
//
//			// Eclipse 3.0.2 GA
//			buildIdMap.put("200503110845", "");
//			
//			// Eclipse 3.1 Milestones
//			buildIdMap.put("200408122000", "M1"); // M1
//			buildIdMap.put("200409240800", "M2"); // M2
//			buildIdMap.put("200411050810", "M3"); // M3
//			buildIdMap.put("200412162000", "M4"); // M4
//			buildIdMap.put("I20050218-1600", "M5"); // M5
//			buildIdMap.put("200502181600", "M5"); // M5
//			buildIdMap.put("I20050219-1500", "M5a"); // M5a
//			buildIdMap.put("200502191500", "M5a"); // M5a
//			buildIdMap.put("I20050401-1645", "M6"); // M6
//			buildIdMap.put("200504011645", "M6"); // M6
//			buildIdMap.put("I20050513-1415", "M7"); // M7
//			buildIdMap.put("200505131415", "M7"); // M7
//			buildIdMap.put("I20050527-1300", "RC1"); // RC1
//			buildIdMap.put("200505271300", "RC1"); // RC1
//			buildIdMap.put("I20050610-1757", "RC2"); // RC2
//			buildIdMap.put("200506101757", "RC2"); // RC2
//			buildIdMap.put("I20050617-1618", "RC3"); // RC3
//			buildIdMap.put("200506171618", "RC3"); // RC3
//			buildIdMap.put("I20050624-1300", "RC4"); // RC4
//			buildIdMap.put("200506241300", "RC4"); // RC4
//			
//			// Eclipse 3.1 GA
//			buildIdMap.put("I20050627-1435", ""); // 3.1
//			buildIdMap.put("200506271435", ""); // 3.1
//
//			// Eclipse 3.1.1 GA
//			buildIdMap.put("M20050929-0840", ""); // 3.1.1
//			buildIdMap.put("200509290840", ""); // 3.1.1
//
//			// Eclipse 3.1.2 GA
//			buildIdMap.put("M20060118-1600", ""); // 3.1.2
//			buildIdMap.put("200601181600", ""); // 3.1.2
//						
//			// Eclipse 3.2 Milestones
//			buildIdMap.put("I20050811-1530", "M1"); // M1
//			buildIdMap.put("200508111530", "M1"); // M1
//			buildIdMap.put("I20050923-1000", "M2"); // M2
//			buildIdMap.put("200509231000", "M2"); // M2
//			buildIdMap.put("I20051102-1600", "M3"); // M3
//			buildIdMap.put("200511021600", "M3"); // M3
//			buildIdMap.put("I20051215-1506", "M4"); // M4
//			buildIdMap.put("200512151506", "M4"); // M4
//			buildIdMap.put("I20060217-1115", "M5"); // M5
//			buildIdMap.put("200602171115", "M5"); // M5
//			buildIdMap.put("I20060223-1656", "M5a"); // M5a
//			buildIdMap.put("200602231656", "M5a"); // M5a
//			buildIdMap.put("I20060331-2000", "M6"); // M6
//			buildIdMap.put("200603312000", "M6"); // M6
//			buildIdMap.put("I20060413-1718", ""); // RC1
//			buildIdMap.put("200604131718", ""); // RC1
//			buildIdMap.put("I20060419-1640", "RC1a"); // RC1a
//			buildIdMap.put("200604191640", "RC1a"); // RC1a
//			buildIdMap.put("I20060428-1315", "RC2"); // RC2
//			buildIdMap.put("200604281315", "RC2"); // RC2
//			buildIdMap.put("I20060505-1306", "RC3"); // RC3
//			buildIdMap.put("200605051306", "RC3"); // RC3
//			buildIdMap.put("I20060512-1600", "RC4"); // RC4
//			buildIdMap.put("200605121600", "RC4"); // RC4
//			buildIdMap.put("I20060519-1206", "RC5"); // RC5
//			buildIdMap.put("200605191206", "RC5"); // RC5
//			buildIdMap.put("I20060526-0010", "RC6"); // RC6
//			buildIdMap.put("200605260010", "RC6"); // RC6
//			buildIdMap.put("I20060602-1317", ""); // RC7
//			buildIdMap.put("200606021317", ""); // RC7
//
//			// Eclipse 3.2 GA
//			buildIdMap.put("M20060629-1905", ""); // 3.2
//			buildIdMap.put("200606291905", ""); // 3.2
//
//			eclipseBuildName = (String) buildIdMap.get(getEclipseBuildId());
			
			// [author=Dan] Don't bother with the build name and simply focus on
			// whether or not this was compiled for a major/minor version of Eclipse
			eclipseBuildName = "";

			if (eclipseBuildName == null)
				eclipseBuildName = getEclipseBuildId();
		}		
		return eclipseBuildName;
	}
	
	/**
	 * Answer the build date for this particular instance of Eclipse as derived from
	 * {@link #getEclipseBuildId()} or Jan 1, 2003 if it cannot be determined.
	 * 
	 * @return the build date (not <code>null</code>)
	 */
	public static Calendar getEclipseBuildDate() {
		if (eclipseBuildDate == null) {
			try {
				String ymd = getEclipseBuildId();
				if (Character.isLetter(ymd.charAt(0)))
					ymd = ymd.substring(1);
				int y = Integer.parseInt(ymd.substring(0, 4));
				int m = Integer.parseInt(ymd.substring(4, 6)) - 1;
				int d = Integer.parseInt(ymd.substring(6, 8));
				eclipseBuildDate = new GregorianCalendar(y, m, d);
			}
			catch (Exception e) {
				// Logger may not be open, so cannot log exception
			}			
			if (eclipseBuildDate == null)
				eclipseBuildDate = new GregorianCalendar(2003, 0, 1);
		}		
		return eclipseBuildDate;
	}

	/**
	 * Read the build identifier from the specified plugin file.
	 * 
	 * @param pluginId the plugin's unique identifier
	 * @param fileName the name of the file containing the build id
	 * @return the build identifier or <code>null</code> if it cannot be determined
	 */
	private static String readBuildId(String pluginId, String fileName) {
		InputStream stream = null;
		try {
			URL url = PluginUtilities.getUrl(pluginId, fileName);
			stream = url.openStream();
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(stream));
			while (true) {
				String line = reader.readLine();
				if (line == null)
					return null;
				if (line.startsWith("0="))
					return line.substring(2).trim();
			}
		}
		catch (Exception e) {
			// Logger may not be open, so cannot log exception
		}
		finally {
			try {
				if (stream != null)
					stream.close();
			}
			catch (IOException e) {
				// Logger may not be open, so cannot log exception
			}
		}
		return null;
	}

	/**
	 * Check the compatibility of the currently installed products with the currently
	 * executing version of Eclipse.
	 */
	public static void checkCompatibility() {
		String text = getIDECompatibilityWarningMessage();
		if (text != null && text.length() > 0)
			Logger.log(new Status(IStatus.WARNING, RuntimePlugin.PLUGIN_ID, 0, text, null));
	}

	/**
	 * Determine if any of the currently installed products is incompatible with the IDE
	 * in which they are currently executing. Typically this is accessed on a background
	 * thread and the result pumped to the log or to a dialog presenting the information
	 * to the user.
	 * 
	 * @return a message used to warn the user about potential incompatibilities or an
	 *         empty string if no incompatibilities are detected (not <code>null</code>)
	 */
	public static String getIDECompatibilityWarningMessage() {
		if (ideCompatibilityWarningMessage == null) {
			StringWriter stringWriter = new StringWriter();
			boolean suppressWarnings = "true".equalsIgnoreCase(System
				.getProperty("suppressCompatibilityWarningMessage"));
			if (!suppressWarnings) {
				PrintWriter writer = new PrintWriter(stringWriter);
				IProduct[] allProducts = Products.getAllProducts();
				for (int i = 0; i < allProducts.length; i++) {
					IProduct product = allProducts[i];
				//	if (product.isInstalled() && !product.isCompatibleWithIDE())
					if (!product.isCompatibleWithIDE())
					product.printIDECompatibilityWarningMessage(writer);
				}
			}
			ideCompatibilityWarningMessage = stringWriter.toString();
		}
		return ideCompatibilityWarningMessage;
	}
}
