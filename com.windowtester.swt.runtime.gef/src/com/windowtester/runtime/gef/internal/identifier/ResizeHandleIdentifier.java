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
import org.eclipse.gef.handles.ResizeHandle;
import org.eclipse.swt.widgets.Event;

import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.finder.IFigureIdentifier;
import com.windowtester.runtime.gef.internal.finder.ResizeHandleFinder;
import com.windowtester.runtime.gef.internal.locator.ByOrientationLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.IFigureLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * An identifier for resize handles.
 */
public class ResizeHandleIdentifier extends AbstractFigureIdentifier {
	
	private IFigureIdentifier delegate;

	public IFigureIdentifier withDelegate(IFigureIdentifier delegate) {
		this.delegate = delegate;
		return this;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IFigureIdentifier#identify(org.eclipse.draw2d.IFigure, org.eclipse.swt.widgets.Event)
	 */
	public ILocator identify(IFigure figure, Event event) {
		if (!(figure instanceof ResizeHandle))
			return null;
		
		ResizeHandle handle   = (ResizeHandle)figure;
		IFigure owner         = getOwner(handle);
			
		Position position     = getPositionRelativeTo(handle, owner);

		ILocator ownerLocator = findHandleOwner(owner);	
		if (!(ownerLocator instanceof IFigureLocator))
			return null;

		return new ResizeHandleLocator(position, (IFigureLocator) ownerLocator);
	}


	private IFigure getOwner(ResizeHandle handle) {
		return ResizeHandleFinder.getOwner(handle);
	}

	private Position getPositionRelativeTo(ResizeHandle handle, IFigure owner) {
		
		int positionConstant = ByOrientationLocator.PositionHelper.getNearestOrientationRelativeTo(handle.getAccessibleLocation(), owner.getBounds());
		return com.windowtester.runtime.gef.internal.finder.position.PositionHelper.getPositionForConstant(positionConstant);
	}

	private ILocator findHandleOwner(IFigure owner) {
		return delegate.identify(owner, new Event()); //notice the event is ignored
	}

}
