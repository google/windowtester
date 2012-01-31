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
import com.windowtester.runtime.gef.internal.locator.AnchorLocatorDelegate;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.IPositioningLocator;
import com.windowtester.runtime.gef.locator.IFigureLocator;

/**
 * Locates connection anchors by position relative to a given figure.
 */
public class AnchorLocator extends DelegatingLocator implements IPositioningLocator {

	private static final long serialVersionUID = -1260810991525122262L;
	
	private final Position position;
	private final IFigureLocator host;

	/**
	 * Create an anchor locator for the anchor at the given location
	 * relative to the given host figure. 
	 * 
	 * @param position the position of the anchor
	 * @param owner the figure that owns the anchor
	 * @see Position
	 */
	public AnchorLocator(Position position, IFigureLocator owner) {
		super(AnchorLocatorDelegate.forPositionRelativeToHost(position, owner));
		this.position = position;
		this.host = owner;
	}
	
	/**
	 * Get the owner of this anchor.
	 */
	public IFigureLocator getOwner() {
		return host;
	}
	
	/**
	 * Get the position of this anchor relative to its owner.
	 */
	public Position getPosition() {
		return position;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return  "AnchorLocator(" + position + ", " + host.toString() + ")";
	}
	
}
