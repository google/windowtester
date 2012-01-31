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
import org.eclipse.gef.EditPart;
import org.eclipse.gef.NodeEditPart;

import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.GEF;
import com.windowtester.runtime.gef.internal.locator.AnchorNotFoundException;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.RetrySupport;

/**
 * A helper for finding GEF Figure anchors.
 */
public class AnchorFinder {

	private static final ConnectionAnchor[] NONE = new ConnectionAnchor[]{};
	
	
	/**
	 * Get the anchor for this point or <code>null</code> if there is none.
	 */
	public static IAnchorInfo forFigureAtPoint(IFigure figure, Point point) {
		
		NodeEditPart part = getNodePart(figure);
		if (part == null)
			return null;
		ConnectionAnchor anchor = getAnchorForPointOnPart(point, part);
		if (anchor == null)
			return null;
		
		return findPositionforAnchorInFigure(anchor, figure);
	}
	
	private static IAnchorInfo findPositionforAnchorInFigure(ConnectionAnchor anchor, IFigure figure) {
		IAnchorInfo[] positionedAnchors = forFigure(figure);
		for (int i = 0; i < positionedAnchors.length; i++) {
			IAnchorInfo positionedAnchor = positionedAnchors[i];
			if (positionedAnchor.getAnchor() == anchor)
				return positionedAnchor;
		}
		return null;

	}

	private static ConnectionAnchor getAnchorForPointOnPart(Point point, NodeEditPart part) {		
		try {

			ConnectionAnchor[] anchors = new AnchorRequester().requestAnchorsForPointOnNode(point, part);
			// TODO: can this ever return more than 1?
			if (anchors.length == 0)
				return null;
			return anchors[0];
		} catch (MultipleAnchorsForLocationException e) {
			LogHandler.log(e);
		}
		return null;
	}

	private static NodeEditPart getNodePart(IFigure figure) {
		EditPart[] parts = GEF.getFinder().findAllEditParts(figure);
		if (parts.length == 0)
			return null;
		if (!(parts[0] instanceof NodeEditPart))
			return null;
		return (NodeEditPart)parts[0];
	}

	public static IAnchorInfo[] forFigure(IFigure figure) {
		ConnectionAnchor[] anchors;
		try {
			anchors = forFigure0(figure);
		} catch (MultipleAnchorsForLocationException e) {
			return new IAnchorInfo[]{IAnchorInfo.MULTIPLE_ANCHORS};
		}
		
		IAnchorInfo[] info = new IAnchorInfo[anchors.length];
		for (int i = 0; i < anchors.length; i++) {
			info[i] = AnchorInfo.forAnchor(anchors[i], anchors);
		}
		return info;
	}
 	
	public static IAnchorInfo forPositionInFigure(Position position, final IFigure figure) throws AnchorNotFoundException {
		IAnchorInfo[] anchors = (IAnchorInfo[]) RetrySupport.retryUntilArrayResultIsNonEmpty(new RunnableWithResult(){
			public Object runWithResult() {
				return forFigure(figure);
			}
		});
		
		
		for (int i = 0; i < anchors.length; i++) {
			if (anchors[i].getPosition().equals(position))
				return anchors[i];
		}
		throw new AnchorNotFoundException();
	}
	
	
	private static ConnectionAnchor[] forFigure0(IFigure figure) throws MultipleAnchorsForLocationException {
		EditPart[] parts = GEF.getFinder().findAllEditParts(figure);
		if (parts.length == 0)
			return NONE;
		//TODO: handle multiples here?
		return forFigureWithPart(figure, parts[0]);
	}
	
	private static ConnectionAnchor[] forFigureWithPart(IFigure figure, EditPart part) throws MultipleAnchorsForLocationException {
		if (!(part instanceof NodeEditPart))
			return NONE;
		return forFigureWithNodePart(figure, (NodeEditPart) part);
	}

	private static ConnectionAnchor[] forFigureWithNodePart(IFigure figure, NodeEditPart part) throws MultipleAnchorsForLocationException {
		return new AnchorRequester().requestAnchorsForFigureWithNodePart(figure, part);
	}


}
