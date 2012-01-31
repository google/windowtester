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
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.matchers.InstanceOfByClassNameFigureMatcher;
import com.windowtester.runtime.swt.internal.Context;

public class ConnectionFinder {

	
	private static final ConnectionFinder INSTANCE = new ConnectionFinder();
	private static final IFigure[] EMPTY_LIST = new IFigure[]{};
	
	private ConnectionFinder() {}
	
	public static ConnectionFinder getDefault() {
		return INSTANCE;
	}

	public IFigure[] findAllConnectedFigures(IFigure figure) {
		IFigure[] connections = findAllConnections();
		List matches = new ArrayList();
		for (int i = 0; i < connections.length; i++) {
			Connection connection = (Connection) connections[i];
			if (isConnectedTo(figure, connection)){
				IFigure target = getOtherEndOfConnection(figure, connection);
				if (target != null)
					matches.add(target);
			}
		}
		return (IFigure[]) matches.toArray(EMPTY_LIST);
	}

	private IFigure getOtherEndOfConnection(IFigure figure, Connection connection) {
		ConnectionAnchor sourceAnchor = connection.getSourceAnchor();
		ConnectionAnchor targetAnchor = connection.getTargetAnchor();
		if (owns(sourceAnchor, figure))
			return targetAnchor.getOwner();
		return sourceAnchor.getOwner();
	}

	private boolean isConnectedTo(IFigure figure, Connection connection) {
		return (owns(connection.getSourceAnchor(), figure)) || (owns(connection.getTargetAnchor(), figure));
	}

	private boolean owns(ConnectionAnchor source, IFigure figure) {
		IFigure owner = source.getOwner();
		if (owner == null)
			return false;
		return owner.equals(figure);
		
	}

	public IFigure[] findAllConnections() {
		return Draw2DFinder.getDefault().findAllFigures(Context.GLOBAL.getUI(), new InstanceOfByClassNameFigureMatcher(Connection.class.getName()));
	}

	
	
	
}
