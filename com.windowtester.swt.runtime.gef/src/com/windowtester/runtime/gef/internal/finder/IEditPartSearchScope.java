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
package com.windowtester.runtime.gef.internal.finder;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * Implementers of this interface can be used to bound an edit part search.
 */
public interface IEditPartSearchScope {

	IWidgetLocator[] findAll(IUIContext ui, IGEFEditPartMatcher matcher);
	
}
