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
package com.windowtester.runtime.swt.internal.preferences;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.IStatus;

import com.windowtester.internal.product.IProduct;
import com.windowtester.internal.product.ISupport;
import com.windowtester.internal.product.PlatformInfo;
import com.windowtester.internal.runtime.RuntimePlugin;
import com.windowtester.runtime.util.PluginUtilities;
import com.windowtester.runtime.util.URLUtilities;

/**
 * Provides product specific tech support information. Products may override this as
 * necessary.
 * <p>
 * @author Dan Rubel
 */
public class CommonSupport implements ISupport
{
	public static final String INDENT = "    ";

	private final IProduct product;

	
	/**
	 * Construct a new instance
	 * 
	 * @param product the product being supported (not <code>null</code>)
	 * @param buildNum the build number hard coded in the subclass at build time
	 *            <p>
	 *            TODO [author=Dan] Use the buildNum here to validate against the build
	 *            and build date read from the plugin.properties file by
	 *            {@link IProduct#getBuild()}
	 */
	public CommonSupport(IProduct product, String buildNum) {
		if (product == null)
			throw new IllegalArgumentException("product cannot be null");
		this.product = product;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Answer the product associated with the receiver
	 * 
	 * @return the product (not <code>null</code>)
	 */
	public IProduct getProduct() {
		return product;
	}
	
	/**
	 * Answer a single string containing a comma delimited list of one or more authors
	 * involved in the product that should be shown on the product pref page
	 * 
	 * @return a comma delimited list string or <code>null</code> if undefined
	 */
	protected String getAuthorNames() {
		return null;
	}

	/**
	 * Answer the last product exception that occurred.
	 * 
	 * @return the exception or <code>null</code> if none
	 */
	public IStatus getLastStatus() {
		// TODO [author=Dan] someday, move this to Logger
		return null;
	}

	/**
	 * Answer the product information to be displayed on the product's
	 * preference page
	 */
	public String getPrefPageInfo() {
		StringWriter stringWriter = new StringWriter(250);
		PrintWriter writer = new PrintWriter(stringWriter);
		printPrefPageInfo(writer);
		return stringWriter.toString().trim();
	}
	
	/**
	 * Return the installation location.
	 * 
	 * @return a string representing the installation location
	 */
	public String getInstallationLocation() {
		URL installUrl = PluginUtilities.getInstallUrl(RuntimePlugin.PLUGIN_ID);
		try {
			if (installUrl != null) {
				String filePath = URLUtilities.toFileURL(installUrl).getPath();
				
				// file filePath begins with "/c:" then remove leading "/"
				if (filePath.length() > 3 && filePath.charAt(0) == '/' && filePath.charAt(2) == ':')
					filePath = filePath.substring(1);
				
				// file filePath points to plugins or configuration, then truncate to eclipse directory
				int index = filePath.indexOf("/configuration/");
				if (index == -1)
					index = filePath.indexOf("/plugins/");
				if (index > -1)
					filePath = filePath.substring(0, index);
				
				return filePath;
			}
			else {
				return "unknown";
			}
		}
		catch (Exception ex) {
			//Logger.log(ex);
			return "unknown";
		}
	}
	
	/**
	 * Answer the feedback information for the product's preference page
	 */
	public String getPrefPageFeedback() {
		// TODO I believe that this method can be deleted.
		return "";
	}

	
	/**
	 * Answer the current year
	 */
	public static int getCurrentYear() {
		return (new GregorianCalendar()).get(Calendar.YEAR);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Information
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Append support information to the specified buffer. Subclasses should
	 * override {@link #printSupportInfo(PrintWriter)} rather than this method to provide
	 * additional information
	 * 
	 * @param writer the print writer to which information is appended
	 */
	public void printInfo(PrintWriter writer) {
		printProductInfo(writer);
		printSupportInfo(writer);
		printPlatformInfo(writer);
	}

	/**
	 * Append product support information to the specified buffer.
	 * 
	 * @param writer the print writer to which information is appended
	 */
	protected void printProductInfo(PrintWriter writer) {
		writer.println("Product: WindowTester Pro");
		String str = "Version: ";
		String versionString = String.valueOf(product.getVersion());
		str += versionString;
		String buildString = product.getBuild();
		if (!versionString.endsWith(buildString)) {
			str += " [" + buildString + "]";
		}
		writer.println(str);

		writer.println("Expected: " + product.getExpectedEclipseText());
		writer.println("Actual: Eclipse " + PlatformInfo.getEclipseVersion());
		writer.println("Actual Eclipse Build Name: " + PlatformInfo.getEclipseBuildName());
		writer.println("Actual Eclipse Build ID: " + PlatformInfo.getEclipseBuildId());

		// TODO [author=Dan] Needs to be properly formatted before it can be included
		// writer.print("Actual Eclipse Build Date: ");
		// writer.println(PlatformInfo.getEclipseBuildDate());
		
		if (!product.isCompatibleWithIDE())
			writer.println("    *** This is the incorrect product version for this version of Eclipse ***");

		writer.println("IDE Actual Name: " + PlatformInfo.getIDEName());
		writer.println("IDE Actual Version: " + PlatformInfo.getIDEVersionString());
		writer.println("IDE Actual NL: " + PlatformInfo.getIDENL());
	}

	/**
	 * Append additional technical support information to the specified buffer. This is
	 * called by {@link #printInfo(PrintWriter)} and may be overridden by subclasses.
	 * 
	 * @param writer the print writer to which information is appended
	 */
	protected void printSupportInfo(PrintWriter writer) {
		// subclasses may override
	}

	/**
	 * Append human readable platform information to the specified stream
	 * 
	 * @param writer the print writer to which the information is appended
	 */
	public static void printPlatformInfo(PrintWriter writer) {
		writer.println("Platform Product: " + PlatformInfo.getIDEName());
		writer.println("Platform Version: " + PlatformInfo.getIDEVersionString());
		writer.println("OS Name: " + System.getProperty("os.name"));
		writer.println("OS Architecture: " + System.getProperty("os.arch"));
		writer.println("OS Version: " + System.getProperty("os.version"));
	}

	protected void printPrefPageInfo(PrintWriter writer) {
		// product name
		writer.print(product.getName());
		
		// product version
		writer.println();
		writer.print("Version ");
		String versionString = product.getVersion().toString();
		writer.print(versionString);
		String buildString = product.getBuild();
		if (!versionString.endsWith(buildString)) {
			writer.print(" [");
			writer.print(buildString);
			writer.print("]");
		}
		writer.print(" for ");
		writer.print(product.getExpectedEclipseText());
		
		if (!product.isCompatibleWithIDE()) {
			writer.println();
			writer.print("*** This is the incorrect product version for this version of Eclipse ***");
		}
		
		// current date and copywrite info
		writer.println();
		// Copyright unicode character.
		writer.println("\u00A9 2003, " + getCurrentYear() + " Google, Inc.");
		
		// author info
		String authorNames = getAuthorNames();
		if (authorNames != null) {
			writer.println("Author: " + authorNames);
		}
		
		// installation information
		//writer.println();
		//writer.print("Installed at ");
	}

	
}