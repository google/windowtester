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


/**
 * Service through which custom widget resolvers and factories can be registered with the
 * runtime.
 * <pre>
 * //get the resolver service
 * IWidgetResolverService wrs = (IWidgetResolverService)ui.getAdapter(IWidgetResolverService.class);
 * //add a new resolver and associate with the "widget.label" handle
 * wrs.add("widget.label", new WidgetResolver() {
 *     public boolean matches(Widget w) { ... }
 *     public Widget resolve() { ... }
 * });
 * 
 * //add a resolver factory to the service
 * IWidgetResolverFactory wrf = ...;
 * wrs.add(wrf);
 * </pre>
 * 
 * 
 * @see com.windowtester.swt.resolver.IWidgetResolver
 * @see com.windowtester.swt.resolver.IWidgetResolverFactory
 *
 */
public interface IWidgetResolverService {

	/**
	 * Add this widget resolver factory to the runtime.
	 * @param factory the factory to add
	 */
	void add(IWidgetResolverFactory factory);
	
	/**
	 * Remove this widget resolver factory from the runtime.
	 * @param factory the factory to remove
	 */
	void remove(IWidgetResolverFactory factory);
	
	/**
	 * Add this widget resolver to the runtime.
	 * @param handle the handle used to identify the resolver
	 * @param resolver the resolver associated with the handle
	 */
	void add(String handle, IWidgetResolver resolver);
		
	/**
	 * Remove the widget resolver identified by this handle from the runtime.
	 * @param handle the handle of the resolve to remove
	 */
	public void remove(String handle);
	
	/**
	 * Get the widget resolver associated with this handle. 
	 * @param handle the name of the resolver
	 * @return the resolver associated with this name
	 */
	IWidgetResolver get(String handle);


	
}
