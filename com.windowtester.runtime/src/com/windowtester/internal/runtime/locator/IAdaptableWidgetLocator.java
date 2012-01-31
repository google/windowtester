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

import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Implementers can locate widgets and optionally provide other services
 * via the {@link IAdaptable} interface.
 */
public interface IAdaptableWidgetLocator extends IWidgetLocator, IAdaptable {

}
