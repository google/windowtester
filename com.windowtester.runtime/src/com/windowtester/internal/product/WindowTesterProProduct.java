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
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PluginVersionIdentifier;

import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.util.PluginUtilities;

/**
 * Shared behavior for all products.
 * <p>
 * 
 * @author Dan Rubel
 */
public class WindowTesterProProduct
	implements IProduct
{
	private static final String ECLIPSE_VERSION_KEY = "eclipse.version";
	private static final String TARGET_PROPERTIES = "target.properties";
	private static final String PLUGIN_PROPERTIES = "plugin.properties";
	private static final String BUILD_KEY = "build";
	
	private static final String PRODUCT_ID = "WindowTesterPro";
	private static final String WTPRO_ID = PRODUCT_ID;
	private static final String[] PRO_IDS = new String[] {WTPRO_ID};
	
	private static final String NAME = "WindowTester Pro";
	private static final String DESCRIPTION = NAME + " creates and executes system level unit tests for Swing, SWT and Eclipse RCP based user interfaces";
	private static final String PLUGIN_ID =  "com.windowtester.runtime";
	private static final String[] PLUGIN_IDS = new String[] { PLUGIN_ID };
	private static final String DEVELOPMENT_BUILD_NUM = "${build_num}";
	

	
	/**
	 * The product version or <code>null</code> if it has not yet been initialized by
	 * {@link #getVersion()}. Typically the product version is derived from the version
	 * of the primary plugin.
	 */
	private PluginVersionIdentifier version;

	/**
	 * The product build or <code>null</code> if it has not yet been initialized by
	 * {@link #getBuild()}. Typically the product build is read from the
	 * plugin.properties file of the primary plugin.
	 */
	private String build;

	/**
	 * The product build date or <code>null</code> if it has not yet been initialized by
	 * {@link #getBuildDate()}. Typically the product build is read from the
	 * plugin.properties file of the primary plugin and then translated into a date.
	 * If the build cannot be translated into a date, then this field will have an
	 */
	private GregorianCalendar buildDate;
	
	/**
	 * The expected version of Eclipse for which this product was built or
	 * <code>null</code> if it has not yet been initialized by
	 * {@link #getExpectedEclipseVersion()}. Typically the expected Eclipse version is
	 * read from the "eclipse.version" key in the target.properties file of the primary
	 * plugin and then translated into a version. If the version cannot be determined for
	 * some reason such as the file does not exist or the string cannot be converted to a
	 * version, then this field will have the value {@link IProduct#UNKNOWN_VERSION}.
	 */
	private PluginVersionIdentifier expectedEclipseVersion;
	
		
	
	private static WindowTesterProProduct instance;

	public static WindowTesterProProduct getInstance() {
		if (instance == null)
			instance = new WindowTesterProProduct();
		return instance;
	}

	private WindowTesterProProduct() {
	}
	
	
	/**
	 * Answer the all known products.
	 * <p>
	 * WARNING! The array used by this method ({@linkplain #_products}) is constructed
	 * as a result of each {@linkplain BaseProduct} subclass being instantiated. DO NOT
	 * CALL THIS METHOD DIRECTLY, but instead call {@linkplain Products#getAllProducts()}
	 * which causes the ({@linkplain #_products}) field to be properly initialized.
	 * 
	 * @return an array of all products (not <code>null</code>, contains no
	 *         <code>null</code>s)
	 */
	public static IProduct[] getAllProducts() {
		return new IProduct[] {getInstance()};
	}

	/**
	 * Answer the internal base identifier for this product.
	 * 
	 * @return the identifier (not <code>null</code>, not empty)
	 */
	public String getProductId() {
		return PRODUCT_ID;
	}

	public String[] getPluginIds() {
		return PLUGIN_IDS;
	}

	public String getName() {
		return NAME;
	}

	public String getDescription() {
		return DESCRIPTION;
	}
	/**
	 * Determine if the specified plugin is installed in the currently executing development
	 * environment without actually loading or starting the plugin
	 * 
	 * @param pluginId the plugin identifier
	 * @return <code>true</code> if installed, else <code>false</code>
	 */
	public static boolean isInstalled(String pluginId) {
		return PluginUtilities.getInstallUrl(pluginId) != null;
	}

	public PluginVersionIdentifier getVersion() {
		if (version != null)
			return version;

		// Determine if the platform is running
		
		boolean isEclipseRunning;
		/* $codepro.preprocessor.if version >= 3.0 $ */
		try {
			isEclipseRunning = Platform.isRunning();
		}
		catch (NoClassDefFoundError e) {
			System.out.println(e);
			version = IProduct.UNKNOWN_VERSION;
			return version;
		}
		/* $codepro.preprocessor.elseif version < 3.0 $
		isEclipseRunning = true;
		$codepro.preprocessor.endif $ */
		
		// If Eclipse is running, get the version from the plugin
		
		if (isEclipseRunning) {
			version = getPluginVersion(getPluginId());
			return version;
		}
		
		// If Eclipse is NOT running, get the version from the ProductInfo

		if (!isInstalled()) {
			version = IProduct.UNKNOWN_VERSION;
			return version;
		}
		String infoClassName = getProductInfoClassName();
		try {
			Class infoClass = getClass().getClassLoader().loadClass(infoClassName);
			Field versionField = infoClass.getField("version");
			String versionString = (String) versionField.get(null);
			version = new PluginVersionIdentifier(versionString);
		}
		catch (Exception e) {
			Logger.log(getName(), e); // Failed to get version of 
			version = IProduct.UNKNOWN_VERSION;
		}
		return version;
	}

	/**
	 * Answer the version for the specified plugin
	 * 
	 * @param id the unique plugin identifier (not <code>null</code>)
	 * @return the version for the plugin or 0.0.0 if it could not be determined
	 */
	public static PluginVersionIdentifier getPluginVersion(String pluginId) {
		PluginVersionIdentifier version = null;
		Throwable exception = null;
		try {
			version = PluginUtilities.getVersion(pluginId);
		}
		catch (Exception e) {
			exception = e;
		}
		if (version == null) {
			// DDC 9/22/2010 Disabled this output.
		    //Logger.log(LicenseUtil
			//	.decode("XUPSVRWPMMHJDDWEPC777866K422D06Y0W3USSMM5NKKXHWFHDNBJ944F543YYAZ4XBVVTVRYPHH")
			//	+ pluginId, exception); // Failed to determine version of plugin
			version = IProduct.UNKNOWN_VERSION;
		}
		return version;
	}

	public String getBuild() {
		if (build != null)
			return build;

		// Determine if the platform is running
		
		boolean isEclipseRunning;
		/* $codepro.preprocessor.if version >= 3.0 $ */
		try {
			isEclipseRunning = Platform.isRunning();
		}
		catch (NoClassDefFoundError e) {
			System.out.println(e);
			build = IProduct.UNKNOWN_BUILD;
			return build;
		}
		/* $codepro.preprocessor.elseif version < 3.0 $
		isEclipseRunning = true;
		$codepro.preprocessor.endif $ */
		
		// If Eclipse is running, get the build from the plugin
		
		if (isEclipseRunning) {
			build = getPluginBuild(getPluginId());
			return build;
		}
		
		// If Eclipse is NOT running, get the build from the ProductInfo

		if (!isInstalled()) {
			build = IProduct.UNKNOWN_BUILD;
			return build;
		}
		String infoClassName = getProductInfoClassName();
		try {
			Class infoClass = getClass().getClassLoader().loadClass(infoClassName);
			Field buildField = infoClass.getField(BUILD_KEY);
			build = (String) buildField.get(null);
			if (build == null)
				build = IProduct.UNKNOWN_BUILD;
		}
		catch (Exception e) {
			Logger.log(getName(), e); // Failed to determine build for 
			build = IProduct.UNKNOWN_BUILD;
		}
		return build;
	}

	/**
	 * If this product is to be used outside Eclipse,
	 * override this method to return the fully qualified name for a class
	 * that has a public static final field named "build" containing the build number (e.g. "200905111215")
	 * and a public static final field named "version" containing the version (e.g. "3.9.0")
	 * The default implementation returns <code>null</code> indicating
	 * that the product is not licensed for use outside Eclipse.
	 */
	protected String getProductInfoClassName() {
		return null;
	}

	/**
	 * Concrete implementation of {@link IProduct#getPluginId()} that iterates
	 * through all identifiers returned by {@link #getPluginIds()}
	 * and returns the first plugin with that identifier that is installed.
	 * If no plugin is found, then the first identifier is returned.
	 * 
	 */
	public final String getPluginId() {
		String[] allIds = getPluginIds();
		for (int i = 0; i < allIds.length; i++) {
			if (isInstalled(allIds[i]))
				return allIds[i];
		}
		return allIds[0];
	}
	
	/**
	 * Concrete implementation of {@link IProduct#isInstalled()} that iterates
	 * through all identifiers returned by {@link #getPluginIds()} and returns
	 * <code>true</code> if it finds a plugin with that identifier that is
	 * installed. If no plugin is found, then returns <code>false</code>.
	 * 
	 * If executing outside Eclipse, then the existence of the ProductInfo class
	 * as returned by {@link #getProductInfoClassName()} is used to determine
	 * if the product is installed
	 * 
	 * 
	 */
	public final boolean isInstalled() {

		// Determine if the platform is running
		
		boolean isEclipseRunning;
		/* $codepro.preprocessor.if version >= 3.0 $ */
		try {
			isEclipseRunning = Platform.isRunning();
		}
		catch (NoClassDefFoundError e) {
			System.out.println(e);
			return false;
		}
		/* $codepro.preprocessor.elseif version < 3.0 $
		isEclipseRunning = true;
		$codepro.preprocessor.endif $ */
		
		// If Eclipse is running, get the build from the plugin
		
		if (isEclipseRunning) {
			String[] allIds = getPluginIds();
			for (int i = 0; i < allIds.length; i++) {
				if (isInstalled(allIds[i]))
					return true;
			}
			return false;
		}
		
		// If Eclipse is NOT running, get the build from the ProductInfo

		String infoClassName = getProductInfoClassName();
//		System.out.println("[" + getName() + " getProductInfoClassName() returned " + infoClassName + "]");
		if (infoClassName == null)
			return false;
		try {
			Class infoClass = getClass().getClassLoader().loadClass(infoClassName);
			return infoClass != null;
		}
		catch (Exception e) {
			Logger.log(infoClassName +  getName(), e); // Failed to find class  //  for 
			return false;
		}
	}

	
	/**
	 * Answer the build for the specified plugin.
	 * This method ASSUMES that we are executing inside an Eclipse based application.
	 * 
	 * @param id the unique plugin identifier (not <code>null</code>)
	 * @return the build for the plugin or UNKNOWN if it could not be determined
	 */
	public static String getPluginBuild(String pluginId) {
		
		URL url = PluginUtilities.getUrl(pluginId, PLUGIN_PROPERTIES);
		if (url != null) {
			Properties properties = new Properties();
			InputStream stream = null;
			try {
				stream = url.openStream();
				properties.load(stream);
			}
			catch (IOException e) {
				Logger.log(e);
			}
			finally {
				try {
					if (stream != null)
						stream.close();
				}
				catch (Exception e) {
					Logger.log(e);
				}
			}
			String build = properties.getProperty(BUILD_KEY);
			
			// If this is a code under development in a runtime workbench, then return today's date
			if (build == null || build.equals(DEVELOPMENT_BUILD_NUM))
				return Products.getStartDateTimeString();
			
			if (build.length() > 0)
				return build;
		}
		
		return IProduct.UNKNOWN_BUILD;
	}
	
	/**
	 * Convert the build ({@link #getBuild()}) into a date and answer the result.
	 * 
	 * @return the build date or <code>null</code> if it cannot be translated
	 */
	public GregorianCalendar getBuildDate() {
		if (buildDate == null)
			buildDate = getBuildDate(getBuild());
		return buildDate;
	}
	
	/**
	 * Convert the build ({@link #getBuild()}) into a date and answer the result.
	 *
	 * @param build the build to be converted into a date
	 * @return the build date or <code>null</code> if it cannot be translated
	 */
	public static GregorianCalendar getBuildDate(String build) {
		
		// If this is a code under development in a runtime workbench, then return today's date
		if (build == null || build.equals(DEVELOPMENT_BUILD_NUM))
			return new GregorianCalendar();
		
		int len = build.length();
		if (len < 8)
			return null;
		for (int i = 0; i < 8; i++)
			if (!Character.isDigit(build.charAt(i)))
				return null;
		try {
			int year = Integer.parseInt(build.substring(0, 4));
			int month = Integer.parseInt(build.substring(4, 6));
			int day = Integer.parseInt(build.substring(6, 8));
			return new GregorianCalendar(year, month - 1, day);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}



	
	////////////////////////////////////////////////////////////////////////////
	//
	// Product Compatibility Checking
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Check compatibility between this product and the version of the Eclipse IDE in
	 * which it is executing. Subclasses may override to provide additional behavior such
	 * as checking the Eclipse build date.
	 * 
	 * @return <code>true</code> if this product is compatible, else <code>false</code>
	 */
	public boolean isCompatibleWithIDE() {
		PluginVersionIdentifier actual = PlatformInfo.getEclipseVersion();
		PluginVersionIdentifier expected = getExpectedEclipseVersion();
		
		/* $codepro.preprocessor.if version > 4.2 $
			This preprocessor code exists to remind us to adjust the following things
			once we start compiling against Eclipse 4.0.
			
			* adjust this preprocessor code to be version > 4.0
			* adjust the expression below to allow 3.6 compiled code to execute on 4.0 and above
			* delete all warn_if_E35_running_on_E36 methods assuming all products compile against 3.3
			* create a new warn_if_E35_running_on_E40 method
			* adjust the installer to recognize Eclipse 40 as a valid target for installation
			* anything else ? ... please list it here ...
			
			Please talk with Dan when making these adjustments
		$codepro.preprocessor.endif $ */
		
		/*
		 * If this product is NOT compiled against Eclipse 3.7, then do not warn the user
		 * when running code compiled for Eclipse 3.5 on Eclipse 3.6. Individual products
		 * should override #warn_if_E34_running_on_E35 as appropriate.
		 */
		if (!warn_if_E37_running_on_E38_or_42()
			&& ((actual.getMajorComponent() == 4 && actual.getMinorComponent() == 2)
				|| (actual.getMajorComponent() == 3 && actual.getMinorComponent() == 8)) 
			&& expected.getMajorComponent() == 3 && expected.getMinorComponent() == 7)
			return true;
		
		// First check major and minor version numbers
		
		if (actual.getMajorComponent() != expected.getMajorComponent()
			|| actual.getMinorComponent() != expected.getMinorComponent())
			return false;
		
		return true;
	}

	/**
	 * Should the user be warned if the Eclipse 3.4 version of this product is running on
	 * Eclipse 3.6 or 4.0? Subclasses should override this method and return <code>true</code>
	 * if a version of this product exists that is compiled against Eclipse 3.4.
	 * 
	 * @return <code>true</code> the user should be warned, else false.
	 */
	protected boolean warn_if_E37_running_on_E38_or_42() {
		// Our goal is to have all products including D2 building on Hudson and for E-3.8 and E-4.2 by April 1st
		return new GregorianCalendar().after(new GregorianCalendar(2012, 4, 1));
	}

	/**
	 * Print a message detailing the incompatibility between the currently installed
	 * product and the IDE in which it is currently executing. Typically,
	 * {@link #isCompatibleWithIDE()} is called to determine if this method should be
	 * called. Subclasses may override to change the generated message.
	 * 
	 * @param writer the writer to which the incompatibility message is appended
	 */
	public void printIDECompatibilityWarningMessage(PrintWriter writer) {
		PluginVersionIdentifier actual = PlatformInfo.getEclipseVersion();
		writer.print("This version of ");
		writer.print(getName());
		writer.print(" was compiled for ");
		writer.print(getExpectedEclipseText());
		writer.print(" but is running on Eclipse ");
		writer.print(actual.getMajorComponent());
		writer.print(".");
		writer.print(actual.getMinorComponent());
		String buildName = PlatformInfo.getEclipseBuildName();
		if (buildName != null && buildName.length() > 0) {
			writer.print(" ");
			writer.print(buildName);
		}
		writer.println(".");
	}

	/**
	 * Answer the version of Eclipse for which this product was built
	 * 
	 * @return the expected Eclipse version or {@link #UNKNOWN_VERSION} if it cannot be
	 *         determined (not <code>null</code>)
	 */
	protected PluginVersionIdentifier getExpectedEclipseVersion() {
		if (expectedEclipseVersion == null)
			expectedEclipseVersion = getExpectedEclipseVersion(getPluginId());
		return expectedEclipseVersion;
	}

	/**
	 * Answer the version of Eclipse for which the specfied plug-in was built
	 * 
	 * @param pluginId the unique identifier for the plug-in
	 * @return the expected Eclipse version or {@link #UNKNOWN_VERSION} if it cannot be
	 *         determined (not <code>null</code>)
	 */
	protected static PluginVersionIdentifier getExpectedEclipseVersion(String pluginId) {
		URL url = PluginUtilities.getUrl(pluginId, TARGET_PROPERTIES);
		
		// If this is the runtime workbench then the target.properties file does not exist
		// so simply return the current Eclipse version
		if (url == null)
			return PlatformInfo.getEclipseVersion();
		
		Properties props = new Properties();
		InputStream stream;
		try {
			stream = url.openStream();
		}
		catch (IOException e) {
			Logger.log("Failed to open stream " + url, e);
			return UNKNOWN_VERSION;
		}
		try {
			props.load(stream);
		}
		catch (IOException e) {
			Logger.log("Failed to read stream " + url, e);
			return UNKNOWN_VERSION;
		}
		finally {
			try {
				stream.close();
			}
			catch (IOException e) {
				Logger.log("Failed to close stream " + url, e);
			}
		}
		String version = (String) props.get(ECLIPSE_VERSION_KEY);
		if (version == null || version.length() == 0) {
			Logger.log("Failed to find " + ECLIPSE_VERSION_KEY + " in " + url);
			return UNKNOWN_VERSION;
		}
		try {
			return new PluginVersionIdentifier(version);
		}
		catch (Exception e) {
			Logger.log("Failed to parse " + ECLIPSE_VERSION_KEY + "=" + version + " in " + url, e);
			return UNKNOWN_VERSION;
		}		
	}

	/**
	 * Answer the version of Eclipse for which this product is compiled.
	 * 
	 * @return the version (not <code>null</code>)
	 */
	public String getExpectedEclipseText() {
		StringBuffer buf = new StringBuffer(10);
		buf.append("Eclipse ");

		PluginVersionIdentifier expected = getExpectedEclipseVersion();
		buf.append(expected.getMajorComponent());
		buf.append(".");
		buf.append(expected.getMinorComponent());
//		buf.append(".");
//		buf.append(expected.getServiceComponent());

		// Adjust this for each milestone and release candidate
//		if (expected.getMajorComponent() == 3 && expected.getMinorComponent() == 3)
//			buf.append(" M1");
		
		return buf.toString();
	}

	
		
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	////////////////////////////////////////////////////////////////////////////
	
	public String toString() {
		return getName();
	}
}