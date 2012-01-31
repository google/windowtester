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

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.IFigureMatcher;

public class FigureMatcherProposerList {
	
	private final List _ids = new ArrayList();
	
	public void add(IFigureMatcherProposer id) {
		if (id != null)
			getIds().add(id);
	}
	
	public void adaptAndAdd(IAdaptable adaptable) {
		add((IFigureMatcherProposer) adaptable.getAdapter(IFigureMatcherProposer.class));
	}

	public void addIfAdaptable(Object o) {
		if (o instanceof IAdaptable) 
			adaptAndAdd((IAdaptable)o);
	}
	


	public IFigureMatcher propose(IFigureSearchScope scope, IFigure figure) {
		for (Iterator iter = getIds().iterator(); iter.hasNext();) {
			IFigureMatcherProposer id = (IFigureMatcherProposer) iter.next();
			IFigureMatcher locator = id.propose(scope, figure);
			if (locator != null)
				return locator;
		}
		return null;
	}

	private List getIds() {
		return _ids;
	}
}
