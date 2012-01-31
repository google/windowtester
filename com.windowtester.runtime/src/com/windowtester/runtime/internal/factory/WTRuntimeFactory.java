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
package com.windowtester.runtime.internal.factory;

import com.windowtester.runtime.locator.IWidgetReference;

/**
 * Interface used by WindowTester to instantiate implementors of {@link IWidgetReference}
 * and native conditions.
 * 
 */
public interface WTRuntimeFactory
{
	/**
	 * Instantiates a new {@link IWidgetReference} for the specified widget
	 * if possible, or returns <code>null</code> if not.
	 * 
	 * @param widget the widget
	 * @return the widget reference or <code>null</code> if no widget reference can be
	 *         created for the specified widget.
	 */
	IWidgetReference createReference(Object widget);
}
