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
package com.windowtester.internal.runtime.locator;

import com.windowtester.internal.runtime.ISelectionTarget;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Provides extended selector support.
 * 
 *
 */
public interface IUISelector2 extends IUISelector {

	IWidgetLocator mouseMove(IUIContext ui, ISelectionTarget target) throws WidgetSearchException;
	
	IWidgetLocator dragTo(IUIContext ui, ISelectionTarget target) throws WidgetSearchException;

}
