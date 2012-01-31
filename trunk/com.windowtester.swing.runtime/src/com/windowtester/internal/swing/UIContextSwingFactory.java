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
package com.windowtester.internal.swing;

import com.windowtester.runtime.IUIContext;


/**
 * A factory for creating UIContext instances.
 */
public class UIContextSwingFactory {

	/**
	 * Create a new user interface context.
	 * 
	 * @return a new instance
	 */
		
	public static IUIContext createContext() {
		UIContextSwing context = (UIContextSwing)createContext(IUIContext.class);
		return context;
	}
	
	/**
	 * Create a new user interface context.
	 * 
	 * @param adapter the type of context
	 * @return a new instance or <code>null</code> if it could not be created
	 */
	
	public static IUIContext createContext(Class adapter) {
		if (adapter == IUIContext.class)
			return new UIContextSwing();
		
		return null;
	}
	

}
