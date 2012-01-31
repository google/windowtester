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

import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;


public class HyperlinkControlFinder implements IHyperlinkFinder {

	private final SWTWidgetLocator parentScopeLocator;

	public HyperlinkControlFinder(SWTWidgetLocator parentScopeLocator) {
		this.parentScopeLocator = parentScopeLocator;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkFinder#findAllHyperlinks(com.windowtester.runtime.IUIContext)
	 */
	public IHyperlinkReference[] findAllHyperlinks(IUIContext ui) {
		IWidgetLocator[] widgetRefs = ui.findAll(new SWTWidgetLocator(Hyperlink.class, parentScopeLocator));
		IHyperlinkReference[] links = new IHyperlinkReference[widgetRefs.length];
		for (int i = 0; i < widgetRefs.length; i++) {
			links[i] = HyperlinkControlReference.forControl((Hyperlink)((IWidgetReference)widgetRefs[i]).getWidget());
		}
		return links;
	}

}
