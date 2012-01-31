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

import java.util.HashMap;
import java.util.Map;

import com.windowtester.internal.runtime.ClassReference;

/**
 * A pool for {@link ClassReference}s.
 */
public class ClassPool {

	private final Map/*<String,ClassReference>*/ _map = new HashMap();
	
	protected Map getMap() {
		return _map;
	}
	
	public ClassReference get(String className) {
		if (contains(className))
			return fetch(className);
		ClassReference ref = new ClassReference(className);
		add(ref);
		return ref;
	}

	protected void add(ClassReference ref) {
		if (ref == null)
			throw new IllegalStateException("reference must not be null");
		getMap().put(ref.getName(), ref);
	}

	protected ClassReference fetch(String className) {
		return (ClassReference) getMap().get(className);
	}

	protected boolean contains(String className) {
		if (className == null)
			throw new IllegalStateException("class name must not be null");
		return getMap().containsKey(className);
	}

}
