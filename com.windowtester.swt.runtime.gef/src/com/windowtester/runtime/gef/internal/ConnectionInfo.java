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
package com.windowtester.runtime.gef.internal;

import org.eclipse.draw2d.Connection;

import com.windowtester.runtime.gef.IFigureReference;

public class ConnectionInfo implements IConnectionInfo {

	private final Connection _connection;
	private final IFigureReference _target;
	private final IFigureReference _src;

	public ConnectionInfo(Connection connection) {
		_connection = connection;
		_src    = FigureReference.create(connection.getSourceAnchor().getOwner());
		_target = FigureReference.create(connection.getTargetAnchor().getOwner());
	}

	public Connection getConnection() {
		return _connection;
	}

	public IFigureReference getSource() {
		return _src;
	}

	public IFigureReference getTarget() {
		return _target;
	}

}
