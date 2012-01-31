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
package com.windowtester.internal.runtime;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Encapsulates selection information.
 *
 */
public interface ISelectionTarget {

	IClickDescription getClickDescription();
	IWidgetLocator getWidgetLocator();
	
}
