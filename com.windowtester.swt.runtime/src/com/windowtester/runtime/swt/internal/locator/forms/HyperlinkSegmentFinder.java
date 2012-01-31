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

import java.util.ArrayList;
import java.util.List;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public class HyperlinkSegmentFinder implements IHyperlinkFinder {

	private final FormTextLocator textLocator;

	public HyperlinkSegmentFinder(FormTextLocator textLocator) {
		this.textLocator = textLocator;
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkFinder#findAllHyperlinks(com.windowtester.runtime.IUIContext)
	 */
	public IHyperlinkReference[] findAllHyperlinks(IUIContext ui) {
		
		//TODO: add diagnostic for search failure cases
		IWidgetLocator[] text = findAllFormTexts(ui);
		if (text.length == 0)
			return new IHyperlinkReference[0];
		
		List<HyperlinkSegmentReference> links = new ArrayList<HyperlinkSegmentReference>();
		for (int i = 0; i < text.length; i++) {
			HyperlinkSegmentReference[] hyperlinks = ((FormTextReference)text[i]).getHyperlinks();
			for (int j = 0; j < hyperlinks.length; j++) {
				links.add(hyperlinks[j]);
			}
		}
		return (IHyperlinkReference[]) links.toArray(new IHyperlinkReference[]{});		
	}

	private IWidgetLocator[] findAllFormTexts(IUIContext ui) {
		//notice using findAll since find retries and we already retry in the condition test
		return ui.findAll(textLocator);
	}

}
