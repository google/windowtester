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
package com.windowtester.internal.runtime.finder;

import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetMatcher;

/**
 * Finds widgets in the current widget hierarchy that match a given locator's
 * matching criteria.
 * @see IWidgetLocator
 * @see IWidgetMatcher
 */
public interface IWidgetFinder {

	/**
	 * Find the widgets identified by the receiver.
	 * @param locator the locator to use to identify matches
	 * @return the widgets (e.g. SWT Component or Swing JComponent) identified by the receiver
	 * 			(not <code>null</code>, contains no <code>null</code>s but may be empty)
	 */
	IWidgetLocator[] findAll(IWidgetLocator locator);
		
}
