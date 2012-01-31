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
package com.windowtester.runtime.gef.internal.hierarchy;


/**
 * A cache for connection info.
 */
public class ConnectionCache {

	private final IConnectionSource _connectionSource;

	private IConnectionList _connections; //lazily initialized
	
	/**
	 * A generator for connections
	 *
	 */
	public static interface IConnectionSource {
		IConnectionList findAllConnections();
	}
	
	protected static class FinderBackedConnectionSource implements IConnectionSource {
		public IConnectionList findAllConnections() {
			return null; //TODO: adapt this:
			//return Draw2DFinder.getDefault().findAllFigures(Context.GLOBAL.getUI(), new InstanceOfByClassNameFigureMatcher(Connection.class.getName()));
		}
	}
	
	
	public ConnectionCache(IConnectionSource connectionSource) {
		_connectionSource = connectionSource;
	}

	protected IConnectionSource getConnectionSource() {
		return _connectionSource;
	}
	
	
	public ConnectionCache() {
		this(defaultConnectionSource());
	}

	
	public IConnectionList getAllConnections() {
		if (_connections == null)
			_connections = getConnectionSource().findAllConnections();
		return _connections;
	}
	
	protected static FinderBackedConnectionSource defaultConnectionSource() {
		return new FinderBackedConnectionSource();
	}
	
	
}
