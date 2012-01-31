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
package com.windowtester.runtime.swt.internal.settings;

/**
 * 
 * A collection of user-overridable runtime settings.
 */
public interface IRuntimeSettings {

	/**
	 * Set the delay used to wait before making a context click selection.
	 * @param ms delay in milliseconds
	 */
	void setPreContextClickDelay(int ms);

	/**
	 * Get the delay used to wait before making a context click selection.
	 * @param ms delay in milliseconds
	 */
	int getPreContextClickDelay();

	/**
	 * Set the number of times the widget finder should retry before giving up 
	 * on a widget not found condition.
	 * @param numberOfRetries the number of retries
	 */
	void setFinderRetries(int numberOfRetries);
	
	/**
	 * Get the number of times the widget finder should retry before giving up 
	 * on a widget not found condition.
	 * @return the number of retries
	 */
	int getFinderRetries();
	
	/**
	 * Set the interval (in milliseconds) between widget finder retries in the event of 	
	 * a widget not found condition.
	 * @param interval the interval between retries (in ms)
	 */
	void setFinderRetryInterval(int interval);
	
	/**
	 * Get the interval (in milliseconds) between widget finder retries in the event of 	
	 * a widget not found condition.
	 * @return interval the interval between retries (in ms)
	 */
	int getFinderRetryInterval();
	
	/**
	 * Get the maximum wait for a context menu to show.
	 * @return maximum wait for a context menu to show (in ms)
	 */
	int getWaitForContextMenuTimeOut();
	
	/**
	 * Get the maximum wait for a context menu to show.
	 * @param wait maximum wait for a context menu to show (in ms)
	 */
	void setWaitForContextMenuTimeOut(int wait);
	
	
}
