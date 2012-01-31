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
package com.windowtester.runtime.swt.internal.legacy.util;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Adapts old {@link WidgetLocator} to new {@link IWidgetLocator}s. 
 * <p>
 * @author Phil Quitslund
 */
public interface ILegacyLocatorAdapter extends IWidgetLocator, IUISelector {

}