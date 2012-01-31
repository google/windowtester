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

import org.eclipse.core.runtime.IStatus;


public interface ISupport
{
	
	
	/**
	 * Answer the product associated with the receiver
	 * 
	 * @return the product (not <code>null</code>)
	 */
	IProduct getProduct();
	/**
	 * Answer the last product exception that occurred.
	 * 
	 * @return the exception or <code>null</code> if none
	 */
	IStatus getLastStatus();

	/**
	 * Answer the product information to be displayed on the product's
	 * preference page
	 */
	String getPrefPageInfo();

	/**
	 * Return the installation location.
	 * 
	 * @return a string representing the installation location
	 */
	String getInstallationLocation();
	
	/**
	 * Answer the feedback information for the product's preference page
	 */
	String getPrefPageFeedback();

	/**
	 * Append support information using the specified writer.
	 * 
	 * @param writer the print writer to which information is appended (not <code>null</code>)
	 */
	void printInfo(PrintWriter writer);
}