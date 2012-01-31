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

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;


public class AbstractHyperlinkSegmentAccessor implements IHyperlinkFinder {
	
	protected final FormTextLocator textLocator;
		
	protected AbstractHyperlinkSegmentAccessor(FormTextLocator textLocator) {
		this.textLocator = textLocator;
	}
		
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkProvider#getHyperlinks(com.windowtester.runtime.IUIContext)
	 */
	public IHyperlinkReference[] findAllHyperlinks(IUIContext ui) {
		//notice using findAll since find retries and we already retry in the condition test
		//TODO: add diagnostic for search failure cases
		IWidgetLocator[] text = ui.findAll(textLocator);
		if (text.length != 1)
			return new HyperlinkSegmentReference[0];
		return ((FormTextReference)text[0]).getHyperlinks();
	}
}
