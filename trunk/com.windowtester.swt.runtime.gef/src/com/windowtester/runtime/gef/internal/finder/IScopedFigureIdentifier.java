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

import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.IWidgetLocator;

public interface IScopedFigureIdentifier {

	
	/**
	 * Generates a {@link IWidgetLocator} that identifies this figure
	 * relative to the current figure hierarchy.  If no uniquely identifying locator is found
	 * <code>null</code> is returned.
	 * @param scope the scope in which to bound the search
	 * @param figure the figure to identify
	 * @param event an optional event to use in identification
	 * @return a uniquely identifying <code>WidgetLocator</code> or <code>null</code> if none can be infered
	 */
	public ILocator identify(IFigureSearchScope scope, IFigure figure, Event event);

	
}
