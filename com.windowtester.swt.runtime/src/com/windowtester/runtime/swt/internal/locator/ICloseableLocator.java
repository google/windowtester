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
package com.windowtester.runtime.swt.internal.locator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;

/**
 * Marks locators that can be closed.
 *
 */
public interface ICloseableLocator {


	void doClose(IUIContext ui) throws WidgetSearchException;
	
}
