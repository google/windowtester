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
package com.windowtester.swt.resolver;

import java.util.HashMap;
import java.util.Map;


/**
 * A reference implementation of the {@link com.windowtester.swt.resolver.IWidgetResolverFactory} 
 * interface.
 * 
 *
 */
public class WidgetResolverFactory implements IWidgetResolverFactory {

	/** Backing store for (handle, resolver) associations. */
	private final Map _map = new HashMap();
	
	/** Backing store for (resolver, handle) associations. */
	private final Map _backMap = new HashMap();
	
	
	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverFactory#get(java.lang.String)
	 */
	public IWidgetResolver get(String handle) {
		return (IWidgetResolver) _map.get(handle);
	}

	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverFactory#add(java.lang.String, com.windowtester.swt.resolver.IWidgetResolver)
	 */
	public void add(String handle, IWidgetResolver resolver) {
		_map.put(handle, resolver);
		_backMap.put(resolver, handle);
	}

	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverFactory#getAll()
	 */
	public IWidgetResolver[] getAll() {
		return (IWidgetResolver[]) _map.values().toArray(new IWidgetResolver[]{});
	}
	
	
	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverFactory#getHandle(com.windowtester.swt.resolver.IWidgetResolver)
	 */
	public String getHandle(IWidgetResolver resolver) {
		return (String) _backMap.get(resolver);
	}
	
}
