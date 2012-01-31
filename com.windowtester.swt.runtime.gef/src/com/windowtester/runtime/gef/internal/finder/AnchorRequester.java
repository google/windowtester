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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;


/**
 * Gets anchors from a given part.
 */
public class AnchorRequester {

	private static final ConnectionAnchor[] NONE = new ConnectionAnchor[]{};

	private static final int NUM_ATTEMPTS_PER_REQUEST = 2;
	

	public ConnectionAnchor[] requestAnchorsForFigureWithNodePart(
			IFigure figure, NodeEditPart part) throws MultipleAnchorsForLocationException {
		Rectangle bounds = figure.getBounds();
		Set anchors      = new HashSet();
		for (int y = 0; y < bounds.height; ++y) {
			for (int x = 0; x < bounds.width; ++x) {
				addAnchorsForPoint(part, anchors, new Point(bounds.x + x, bounds.y + y));
			}
		}
		return (ConnectionAnchor[]) anchors.toArray(NONE);
	}


	public ConnectionAnchor[] requestAnchorsForPointOnNode(Point point, NodeEditPart part) throws MultipleAnchorsForLocationException {
		Set anchors      = new HashSet();
		addAnchorsForPoint(part, anchors, point);
		return (ConnectionAnchor[]) anchors.toArray(NONE);
	}
	

	private void addAnchorsForPoint(NodeEditPart part, Set anchors, Point point)
			throws MultipleAnchorsForLocationException {
		checkForAndAddSourceConnectAnchors(part, anchors, point);
		checkForAndAddTargetConnectAnchors(part, anchors, point);
		checkForAndAddSourceReconnectionAnchors(part, anchors, point);
		checkForAndAddTargetReconnectionAnchors(part, anchors, point);
	}
	

	
	private static void checkForAndAddSourceConnectAnchors(NodeEditPart part, Set anchors, Point point) throws MultipleAnchorsForLocationException {
		for (int i=0; i < NUM_ATTEMPTS_PER_REQUEST; ++i) {
			addIfNotNull(anchors, getSourceConnectionAnchor(part, point));
		}		
		assertContains(anchors, getSourceConnectionAnchor(part, point), point);
	}

	private static void checkForAndAddTargetConnectAnchors(NodeEditPart part, Set anchors, Point point) throws MultipleAnchorsForLocationException {
		for (int i=0; i < NUM_ATTEMPTS_PER_REQUEST; ++i) {
			addIfNotNull(anchors, getTargetConnectionAnchor(part, point));
		}	
		assertContains(anchors, getTargetConnectionAnchor(part, point), point);
	}
	
	private static void checkForAndAddTargetReconnectionAnchors(
			NodeEditPart part, Set anchors, Point point)
			throws MultipleAnchorsForLocationException {
		for (int i=0; i < NUM_ATTEMPTS_PER_REQUEST; ++i) {
			addIfNotNull(anchors, getReconnectionTargetAnchor(part, point));
		}	
		assertContains(anchors, getReconnectionTargetAnchor(part, point), point);
	}
	
	private static void checkForAndAddSourceReconnectionAnchors(
			NodeEditPart part, Set anchors, Point point)
			throws MultipleAnchorsForLocationException {
		for (int i=0; i < NUM_ATTEMPTS_PER_REQUEST; ++i) {
			addIfNotNull(anchors, getReconnectionSourceAnchor(part, point));
		}	
		assertContains(anchors, getReconnectionSourceAnchor(part, point), point);
	}
	

	private static void assertContains(Set anchors, ConnectionAnchor anchor, Point point) throws MultipleAnchorsForLocationException {
		if (!anchors.contains(anchor))
			throw new MultipleAnchorsForLocationException(anchor.getOwner(), point);
	}

	
	private static ConnectionAnchor getSourceConnectionAnchor(NodeEditPart part, Point point) {
		try {
			return part.getSourceConnectionAnchor(createConnectRequestForLocation(point));
		} catch (Throwable th) {
			return null; 	//since we're peeking behind the covers here, we might ask for a connection where it is not 
							//expected, causing exceptions...  to be safe we catch and ignore
		}
	}
 
	private static ConnectionAnchor getTargetConnectionAnchor(NodeEditPart part, Point point) {
		try {
			return part.getTargetConnectionAnchor(createConnectRequestForLocation(point));
		} catch (Throwable th) {
			return null; 	//since we're peeking behind the covers here, we might ask for a connection where it is not 
							//expected, causing exceptions...  to be safe we catch and ignore
		}
	}

	private static ConnectionAnchor getReconnectionTargetAnchor(NodeEditPart part, Point point) {
		try {
			return part.getTargetConnectionAnchor(createReconnectRequestForLocation(point));
		} catch (Throwable th) {
			return null; 	//since we're peeking behind the covers here, we might ask for a connection where it is not 
							//expected, causing exceptions...  to be safe we catch and ignore
		}
	}
	
	private static ConnectionAnchor getReconnectionSourceAnchor(NodeEditPart part, Point point) {
		try {
			return part.getSourceConnectionAnchor(createReconnectRequestForLocation(point));
		} catch (Throwable th) {
			return null; 	//since we're peeking behind the covers here, we might ask for a connection where it is not 
							//expected, causing exceptions...  to be safe we catch and ignore
		}
	}
		
	private static void addIfNotNull(Set anchors, ConnectionAnchor connectAnchor) {
		if (connectAnchor != null)
			anchors.add(connectAnchor);
	}

	private static CreateConnectionRequest createConnectRequestForLocation(Point location) {
		CreateConnectionRequest req = new CreateConnectionRequest();
		req.setLocation(location);
		return req;
	}
	
	private static ReconnectRequest createReconnectRequestForLocation(Point location) {
		ReconnectRequest req = new ReconnectRequest();
		req.setLocation(location);
		return req;
	}

}
