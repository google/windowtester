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
package com.windowtester.internal.runtime.provisional;

import com.windowtester.runtime.locator.ILocator;

/**
 * A locator specifying an area. Any concrete implementation of 
 * {@link com.windowtester.runtime2.widget.IWidget} that has bounds 
 * should implement this interface so that it can be used when resolving 
 * {@link com.windowtester.runtime.locator.XYLocator}
 */
public interface IAreaLocator
	extends ILocator
{
	int left();
	int top();
	int width();
	int height();
}
