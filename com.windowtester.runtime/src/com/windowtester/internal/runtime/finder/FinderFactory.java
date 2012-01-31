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

import com.windowtester.runtime.IUIContext;

/**
 * A factory for building finders.
 */
public class FinderFactory {

	/**
	 * Get the widget finder associated with the given UI context.
	 */
	public static IWidgetFinder getFinder(IUIContext ui) {
		return (IWidgetFinder) ui.getAdapter(IWidgetFinder.class);
	}
	
}
