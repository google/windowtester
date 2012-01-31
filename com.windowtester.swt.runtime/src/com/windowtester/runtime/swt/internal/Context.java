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

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.IUIContext;

/**
 * A provisional access point for a global UIContext instance.
 * 
 * <p>
 * NOTE: this is INTERNAL and PROVISIONAL.
 * <p>
 * Conditions and handlers SHOULD NOT be attached to the provided context!
=*
 */
public final class Context implements IUIContextProvider {
	
	public static final Context GLOBAL = new Context();

	private IUIContext _ui;

	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.internal.provisional.IUIContextProvider#getUI()
	 */
	public IUIContext getUI() {
		if (_ui == null) {
			_ui = fetchUI();
		}
		return _ui;
	}

	private IUIContext fetchUI() {
		return (IUIContext) UIContextFactory.createContext(Display.getDefault());
	}
	


}
