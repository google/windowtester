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
package com.windowtester.runtime.draw2d.internal.finder;

import com.windowtester.internal.runtime.finder.ISearchScope;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Implementers of this interface can be used to bound a figure search.
 */
public interface IFigureSearchScope extends ISearchScope {

	IWidgetLocator[] findAll(IUIContext ui, IFigureMatcher matcher);
	
}
