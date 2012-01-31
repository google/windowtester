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
package com.windowtester.runtime.gef.internal.finder;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.finder.IWidgetIdentifierStrategy;

/**
 * Translates figures into locators.
 * @see IWidgetIdentifierStrategy
 *
 */
public interface IFigureIdentifier {

	
	/**
	 * Generates a {@link IWidgetLocator} that uniquely identifies this figure
	 * relative to the current widget hierarchy.  If no uniquely identifying locator is found
	 * <code>null</code> is returned.
	 * @param figure the figure to identify (note: may be null)
	 * @param event an optional event that may be used to help in identification
	 * @return a uniquely identifying <code>WidgetLocator</code> or <code>null</code> if none can be infered
	 */
	ILocator identify(IFigure figure, Event event);
	
	
}
