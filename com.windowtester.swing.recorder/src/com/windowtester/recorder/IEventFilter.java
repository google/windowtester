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
package com.windowtester.recorder;


/**
 * Event filters are used to filter out events from recorder that are
 * not considered significant.
 */
public interface IEventFilter {

	/**
	 * Check whether this event is significant (passes through the filter).
	 * @param event - the event to check
	 * @return true if it is to be included
	 */
	boolean include(Object event);
	
}
