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
package com.windowtester.runtime.swt.internal.locator.forms;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public interface IHyperlinkSelector {

//	IWidgetLocator doContextClick(IUIContext ui, String menuItemPath,
//			IHyperlinkReference link) throws WidgetSearchException;

	IWidgetLocator doClick(IUIContext ui, IClickDescription click,
			IHyperlinkReference link) throws WidgetSearchException;

}
