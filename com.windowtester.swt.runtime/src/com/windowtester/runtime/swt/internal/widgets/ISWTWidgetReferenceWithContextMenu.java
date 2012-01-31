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
package com.windowtester.runtime.swt.internal.widgets;

import com.windowtester.runtime.IClickDescription;


/**
 * Implemented by {@link ISWTWidgetReference} subclasses that have context menus.
 */
public interface ISWTWidgetReferenceWithContextMenu
{
	/**
	 * Show the context menu associated with the receiver
	 * 
	 * @param click the click description
	 * @return the context menu
	 */
	public abstract MenuReference showContextMenu(IClickDescription click);

}