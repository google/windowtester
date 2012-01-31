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
 * Basic (un-optimized) implementation of the {@link IWidgetResolverService} interface.
 * <p>
 * @see com.windowtester.swt.resolver.IWidgetResolverService
 * 
 *
 */
public class WidgetResolverService implements IWidgetResolverService {
 	
	/** Backing store for (handle, resolver) associations. */
	private final Map _map = new HashMap();
	
	/** The singleton factory instance. */
	private static final WidgetResolverService _instance = new WidgetResolverService();
	
	/**
	 * Get the singleton resolver service instance.
	 * @return the singleton resolver service instance.
	 */
	public static WidgetResolverService getInstance() {
		return _instance;
	}
	
	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverService#add(com.windowtester.swt.resolver.IWidgetResolverFactory)
	 */
	public void add(IWidgetResolverFactory factory) {
		IWidgetResolver[] resolvers = factory.getAll();
		for (int i = 0; i < resolvers.length; i++) {
			add(factory.getHandle(resolvers[i]), resolvers[i]);
		}
	}

	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverService#remove(com.windowtester.swt.resolver.IWidgetResolverFactory)
	 */
	public void remove(IWidgetResolverFactory factory) {
		IWidgetResolver[] resolvers = factory.getAll();
		for (int i = 0; i < resolvers.length; i++) {
			remove(factory.getHandle(resolvers[i]));
		}
	}
	
	
	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverService#remove(java.lang.String)
	 */
	public void remove(String handle) {
		_map.remove(handle);
	}

	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverService#add(java.lang.String, com.windowtester.swt.resolver.IWidgetResolver)
	 */
	public void add(String handle, IWidgetResolver resolver) {
		_map.put(handle, resolver);
	}

	/**
	 * @see com.windowtester.swt.resolver.IWidgetResolverService#get(java.lang.String)
	 */
	public IWidgetResolver get(String handle) {
		return (IWidgetResolver) _map.get(handle);
	}

	
}
