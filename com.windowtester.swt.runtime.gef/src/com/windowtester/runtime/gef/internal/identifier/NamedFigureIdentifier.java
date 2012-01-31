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
package com.windowtester.runtime.gef.internal.identifier;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.reflect.GEFIdentifier;
import com.windowtester.runtime.gef.locator.NamedFigureLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * An identifier for named figures.
 */
public class NamedFigureIdentifier implements IFigureIdentifier {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		String id = GEFIdentifier.forFigure(figure);
		if (id == null)
			return null;
		return new NamedFigureLocator(id);
	}
	

}
