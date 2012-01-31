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

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Constants for licensing.
 * <p>
 * 
 * @author Dan Rubel
 */
public class Products
{
	public static final IProduct WINDOWTESTER_PRO = WindowTesterProProduct.getInstance();
	
	/**
	 * No instances
	 */
	private Products() {
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Product Accessors
	//
	////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Answer the all known products.
	 * 
	 * @return an array of all products (not <code>null</code>, contains no
	 *         <code>null</code>s)
	 */
	public static IProduct[] getAllProducts() {
		return WindowTesterProProduct.getAllProducts();
	}

	
	/**
	 * Answer a string in the format "yyyyMMddHHmm" indicating the session start time
	 * @return a string (not null)
	 */
	public static String getStartDateTimeString() {
		if (startTimeString == null)
			startTimeString = getCurrentDateTimeString();
		return startTimeString;
	}
	private static String startTimeString;

	/**
	 * Answer a string in the format "yyyyMMddHHmm" indicating the current time
	 * @return a string (not null)
	 */
	public static String getCurrentDateTimeString() {
		return new SimpleDateFormat("yyyyMMddHHmm").format(new GregorianCalendar().getTime());
	}


	/**
	 * Write the current state for each product
	 * @param writer the writer (not <code>null</code>)
	 */
	private static void writeProducts(PrintWriter writer) {
		TreeSet allProducts = new TreeSet(new Comparator() {
			public int compare(Object p1, Object p2) {
				return ((IProduct) p1).getName().compareTo(((IProduct) p2).getName());
			}
		});
		allProducts.addAll(Arrays.asList(getAllProducts()));
		
		int maxNameLen = 0;
		for (Iterator iterator = allProducts.iterator(); iterator.hasNext();) {
			IProduct product = (IProduct) iterator.next();
			int nameLen = product.getName().length();
			if (maxNameLen < nameLen)
				maxNameLen = nameLen;
		}
		
		writer.println("   <products>");
		for (Iterator iterator = allProducts.iterator(); iterator.hasNext();) {
			IProduct product = (IProduct) iterator.next();
			writer.print("      <product name=\"");
			writer.print(product.getName());
			writer.print('"');
			for(int i = product.getName().length(); i < maxNameLen; i++)
				writer.print(' ');
			writer.print(" build=\"");
			writer.print(product.getBuild());
			writer.println("\" />");
		}
		writer.println("   </products>");
	}

//	/**
//	 * Write the current license information to a file in the workspace metadata area.
//	 */
//	public static void writeLicenseInfo() {
//		File licenseLogFile = null;
//		try {
//			IPath wsLogFilePath = Platform.getLogFileLocation();
//			File wsMetadataDir = wsLogFilePath.removeLastSegments(1).toFile();
//			if (!wsMetadataDir.exists())
//				wsMetadataDir.mkdirs();
//			licenseLogFile = new File(wsMetadataDir, "Instantiations-license.log");
//			if (licenseLogFile.exists())
//				licenseLogFile.delete();
//			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(licenseLogFile)));
//			try {
//				writeLicenseInfo(writer);
//			}
//			finally {
//				writer.close();
//			}
//		}
//		catch (Exception e) {
//			if (!writeLicenseInfoExceptionLogged) {
//				writeLicenseInfoExceptionLogged = true;
//				Logger.log("Failed to write " + licenseLogFile, e);
//			}
//		}
//	}
//	private static boolean writeLicenseInfoExceptionLogged = false;
//
//	/**
//	 * Call the {@link #writeLicenseInfo()} method when the license file changes
//	 * by using {@link UserLicenseFile#addLicenseFileListener(LicenseFileListener)}
//	 */
//	public static void hookWriteLicenseInfo() {
//		UserLicenseFile.getInstance().addLicenseFileListener(new LicenseFileListener() {
//			public void changed() {
//				writeLicenseInfo();
//			}
//		});
//	}
}
