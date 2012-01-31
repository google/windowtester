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
package com.windowtester.runtime.swt.internal;

import com.windowtester.internal.runtime.finder.IWidgetFinder;
import com.windowtester.internal.runtime.selector.IWidgetSelectorService;
import com.windowtester.internal.runtime.selector.WidgetSelectorService;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.internal.condition.shell.ShellMonitor;
import com.windowtester.runtime.swt.internal.finder.legacy.SWTWidgetFinder;
import com.windowtester.runtime.swt.internal.settings.IRuntimeSettings;
import com.windowtester.runtime.swt.internal.settings.TestSettings;


/**
 * The adapter factory class manages behavioral extensions for
 * windowtester runtime classes.
 * 
 */
public class AdapterFactory {
	
	/** The singleton instance */
	private static final AdapterFactory _instance = new AdapterFactory();
	
	/**
	 * Get the singleton Adapter Factory instance.
	 * @return the Adapter Factory instance.
	 */
	public static AdapterFactory getInstance() {
		return _instance;
	}

	/**
	 * Returns an object which is an instance of the given class associated
	 * with this object. Returns <code>null</code> if no such object can
	 * be found.
	 * 
	 * @param adaptable the adaptable object being queried
	 * @param adapter the type of adapter to look up
	 * 
	 * @return an object castable to the given class, or <code>null</code>
	 *         if this object does not have an adapter for the given class
	 */
	public Object getAdapter(Object adaptable, Class<?> adapter) {
		if (adaptable == null)
			throw new IllegalArgumentException("adaptable object must not be null");
		if (adapter == null)
			throw new IllegalArgumentException("adapter class must not be null");

		if (adaptable instanceof com.windowtester.runtime.IUIContext && adapter.equals(IWidgetFinder.class))
			return new SWTWidgetFinder((com.windowtester.runtime.IUIContext)adaptable);
		
		if (adaptable instanceof UIContextSWT && adapter.equals(IShellMonitor.class))
			return ShellMonitor.getInstance();
		
		// ... this has been moved into UIContextSWT#getAdapter(...) ...
		//if (adaptable instanceof com.windowtester.runtime.IUIContext && adapter.equals(com.windowtester.runtime.swt.condition.shell.IShellMonitor.class))
		//	return com.windowtester.runtime.swt.internal.condition.shell.ShellMonitor.getInstance();
		
//		if (adaptable instanceof UIContextSWT && adapter.equals(IWidgetResolverService.class))
//			return WidgetResolverService.getInstance();
			
		if (adaptable instanceof UIContextSWT && adapter.equals(IWidgetSelectorService.class))
			return WidgetSelectorService.getInstance();
		
		if (adaptable instanceof UIContextSWT && adapter.equals(IRuntimeSettings.class))
			return TestSettings.getInstance();
			
//pq: removing presentation bits		
//		if (adaptable instanceof com.windowtester.runtime.IUIContext && adapter.equals(IPresentationContext.class)){
//			// FIXME: here may be we want to use UIContextSWT and define getDisplay to receive display from it?
//			return new SwtPresentationContext(((com.windowtester.runtime.IUIContext)adaptable), Display.getDefault());
//		}
		
		/*
		 * TODO: Fill this in!
		 */
		return null;
	}

}
