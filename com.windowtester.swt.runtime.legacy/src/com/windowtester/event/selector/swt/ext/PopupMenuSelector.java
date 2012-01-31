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
package com.windowtester.event.selector.swt.ext;

import org.eclipse.swt.widgets.Display;

/**
 * Selector helper for context menus.
 * 
 * @author Phil Quitslund
 * @deprecated prefer {@link com.windowtester.runtime.swt.internal.selector.PopupMenuSelector}
 */
public class PopupMenuSelector extends com.windowtester.runtime.swt.internal.selector.PopupMenuSelector {
	
   public PopupMenuSelector() {
       super(); 
    }
    
    public PopupMenuSelector(Display display) {
       super(display);
    }
 
}
