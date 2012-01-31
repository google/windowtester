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

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.finder.position.Points;
import com.windowtester.runtime.gef.internal.finder.position.PositionFinder;

public class AnchorInfo implements IAnchorInfo {

		
	private final ConnectionAnchor anchor;
	private final Position position;

	public AnchorInfo(ConnectionAnchor anchor, Position position) {
		this.anchor = anchor;
		this.position = position;
	}

	public static IAnchorInfo forAnchor(ConnectionAnchor anchor, ConnectionAnchor[] anchors) {
		Point anchorPoint = Points.forAnchor(anchor);
		Points neighborPoints = Points.forAnchors(anchors).excluding(anchorPoint);
		
		IFigure owner = anchor.getOwner();
		
		
		Rectangle boundingBox = anchor.getOwner().getBounds().getCopy();
		//System.out.println("pre-translate:  " + boundingBox);
		
		owner.translateToAbsolute(boundingBox);
		//System.out.println("post-translate: " + boundingBox);
		
		Position position = PositionFinder.findIdentifyingPosition(anchorPoint, boundingBox, neighborPoints);
		return new AnchorInfo(anchor, position);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IAnchorInfo#getAnchor()
	 */
	public ConnectionAnchor getAnchor() {
		return anchor;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IAnchorInfo#getPosition()
	 */
	public Position getPosition() {
		return position;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.finder.IAnchorInfo#hasPosition(com.windowtester.runtime.gef.Position)
	 */
	public boolean hasPosition(Position position) {
		return this.position == position;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Anchor: " + anchor + " -> " + position + " [figure: " + anchor.getOwner() + " ]"; 
	}
	
	
}
