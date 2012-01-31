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
 * Widget resolver factories group and manage widget resolvers. 
 * 
 * <p>
 * Widget resolver factories are added to the runtime via the <code>IWidgetResolverService</code>
 * associated with the <code>UIContext</code>:
 * <pre>
 *   IWidgetResolverFactory wrf = ...;
 *   IWidgetResolverService wrs = (IWidgetResolverService)ui.getAdapter(IWidgetResolverService.class);
 *   wrs.add(wrf);
 * 
 * </pre>
 * 
 * Adding a widget resolver factory to a UIContext, effectively adds all of the widget resolvers managed
 * by the factory to the UIContext.
 * 
 * <p>
 * 
 * @see com.windowtester.swt.resolver.IWidgetResolver
 *
 */
public interface IWidgetResolverFactory {

	
	/*
	 * TODO: fill in recorder story:
	 * 
	 * Widget resolver factories
	 * are used to contribute widget resolvers to the recorder via the ---TBD--- extension point.
	 * Widget resolvers that are made available to the recorder via contributed resolver factories
	 * will be used to identify widgets in recording.  
	 */
	
	
	
//	/**
//	 * Resolve the widget associated with this handle.
//	 * @param handle - the handle for the widget
//	 * @return the associated widget
//	 */
//	Widget resolve(String handle);

	
	/**
	 * Get the widget resolver associated with this name handle.
	 * @param handle - the handle naming the widget resolver
	 * @return the widget resolver associated with the given handle
	 */
	IWidgetResolver get(String handle);
	
	/**
	 * Add the association of this handle to the given resolver to this factory instance. 
	 * @param handle the name to label the resolver 
	 * @param resolver the resolver to associate with the given name
	 */
	void add(String handle, IWidgetResolver resolver);

	
	/**
	 * Get all of the resolvers registered to this factory.
	 * @return all of the resolvers registered to this factory.
	 */
	IWidgetResolver[] getAll();
	
	/**
	 * Get the handle to whic this reolver is registered.
	 * @param resolver  the resolver in question
	 * @return the associated handle
	 */
	String getHandle(IWidgetResolver resolver);
	
	
}
