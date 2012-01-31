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

import com.windowtester.internal.runtime.ClassReference;

/**
 * A context object for caching details relevant to a build.
 */
public class BuildContext {

	private final ClassPool _classPool;
	private final ConnectionCache _connectionCache;
	
	
	public BuildContext() {
		this(new ClassPool(), new ConnectionCache());
	}

	public BuildContext(ClassPool classPool, ConnectionCache connectionCache) {
		_classPool = classPool;
		_connectionCache = connectionCache;
	}
	
	protected ConnectionCache getConnectionCache() {
		return _connectionCache;
	}
	
	protected ClassPool getClassPool() {
		return _classPool;
	}
	
	public ClassReference getClassReference(String className) {
		return getClassPool().get(className);	
	}

	public IConnectionList getAllConnections() {
		return getConnectionCache().getAllConnections();
	}
	
	
	
}
