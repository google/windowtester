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
package com.windowtester.runtime.gef.internal.locator.provisional.api;

import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.ByOrientationLocator;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.IPositioningLocator;
import com.windowtester.runtime.gef.internal.matchers.ResizeHandleMatcher;
import com.windowtester.runtime.gef.locator.IFigureLocator;

/**
 * A locator for resize handles.
 * <p>
 * Resize handles are identified by position relative to a host figure.
 * <p>
 * <strong>PROVISIONAL</strong>. This class has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API for more than
 * experimental purpose without consulting with the WindowTester team.
 * </p> 
 */
public class ResizeHandleLocator extends DelegatingLocator implements IPositioningLocator {

	private static final long serialVersionUID = 8297777538502141388L;

	private final Position position;
	private final IFigureLocator owner;

	/**
	 * Create a resize handle locator for the handle at the given location
	 * relative to the given host figure. 
	 * 
	 * @param position the position of the handle
	 * @param owner the figure that hosts the handle
	 * @see Position
	 */
	public ResizeHandleLocator(Position position, IFigureLocator owner) {
		super(ByOrientationLocator.forPositionMatchInHost(position, new ResizeHandleMatcher(), owner));
		this.position = position;
		this.owner = owner;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return  "AnchorLocator(" + position + ", " + owner.toString() + ")";
	}
	
	public Position getPosition() {
		return position;
	}
	
	public IFigureLocator getOwner() {
		return owner;
	}
	
}
