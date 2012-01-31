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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.locator.ILocator;

/**
 * An ordered list of Figure Identifiers.
 */
public class FigureLocatorProposerList implements IScopedFigureIdentifier {

	
	private final List _ids = new ArrayList();
	
	public void add(IFigureIdentifier id) {
		if (id != null)
			getIds().add(id);
	}
	
	public void adaptAndAdd(IAdaptable adaptable) {
		add((IFigureIdentifier) adaptable.getAdapter(IFigureIdentifier.class));
	}

	public void addIfAdaptable(Object o) {
		if (o instanceof IAdaptable) 
			adaptAndAdd((IAdaptable)o);
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IScopedFigureIdentifier#identify(com.windowtester.runtime.draw2d.internal.internal.finder.IFigureSearchScope, org.eclipse.draw2d.IFigure)
	 */
	public ILocator identify(IFigureSearchScope scope, IFigure figure, Event event) {
		for (Iterator iter = getIds().iterator(); iter.hasNext();) {
			IFigureIdentifier id = (IFigureIdentifier) iter.next();
			ILocator locator = id.identify(figure, event);
			if (locator != null)
				return locator;
		}
		return null;
	}

	private List getIds() {
		return _ids;
	}

}
