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
package com.windowtester.event.selector.swt;


/**
 * Adapts a core runtime {@link com.windowtester.runtime.IWidgetSelectorDelegate} 
 * to an SWT-specific {@link com.windowtester.event.swt.ISWTWidgetSelectorDelegate}.
 * <p>It is the clients responsibility to ensure that the adaptation is compatible.
 * 
 * 
 * @author Phil Quitslund
 * @deprecated prefer {@link com.windowtester.runtime.swt.internal.selector.SWTWidgetSelectorAdapter}
 */
public class SWTWidgetSelectorAdapter extends com.windowtester.runtime.swt.internal.selector.SWTWidgetSelectorAdapter {

	public SWTWidgetSelectorAdapter(com.windowtester.runtime.IWidgetSelectorDelegate delegate) {
		super(delegate);
	}
	
}
