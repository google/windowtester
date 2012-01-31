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

import org.eclipse.core.runtime.PluginVersionIdentifier;

/**
 * Common interface for interacting with each product.
 * <p>
 * 
 * @author Dan Rubel
 */
public interface IProduct
{
	
	final String UNKNOWN_BUILD = "UNKNOWN";
	final PluginVersionIdentifier UNKNOWN_VERSION = new PluginVersionIdentifier(0, 0, 0);

	final IProduct[] EMPTY_PRODUCTS = new IProduct[0];

	////////////////////////////////////////////////////////////////////////////
	//
	// Basic Product Information
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the name of this product
	 * 
	 * @return the name (not <code>null</code>)
	 */
	String getName();

	/**
	 * Answer a description of this product
	 * 
	 * @return the description (not <code>null</code>)
	 */
	String getDescription();

	/**
	 * Answer the product's primary plug-in identifier
	 * 
	 * @return the plug-in identifier (not <code>null</code>, not empty)
	 */
	String getPluginId();

	/**
	 * Answer the version number for this product
	 * 
	 * @return the version (not <code>null</code>)
	 */
	PluginVersionIdentifier getVersion();

	/**
	 * Answer the build string for this product. This is read from the plugin.properties
	 * file.
	 * 
	 * @return the build string or {@linkplain #UNKNOWN_BUILD} if it cannot be determined
	 */
	String getBuild();

	
	/**
	 * Check compatibility between this product and the version of the Eclipse IDE in
	 * which it is executing. Subclasses may override to provide additional behavior such
	 * as checking the Eclipse build date.
	 * 
	 * @return <code>true</code> if this product is compatible, else <code>false</code>
	 */
	boolean isCompatibleWithIDE();

	/**
	 * Answer the version of Eclipse for which this product is compiled.
	 * 
	 * @return the version (not <code>null</code>)
	 */
	String getExpectedEclipseText();

	/**
	 * Print a message detailing the incompatibility between the currently installed
	 * product and the IDE in which it is currently executing. Subclasses may override to
	 * change the generated message.
	 * 
	 * @param writer the writer to which the incompatibility message is appended
	 */
	void printIDECompatibilityWarningMessage(PrintWriter writer);
}
