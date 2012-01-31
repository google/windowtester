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

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

public class HyperlinkFinder implements IHyperlinkFinder {

	private IHyperlinkFinder controlFinder;
	private IHyperlinkFinder segmentFinder;
	
	
	public static IHyperlinkFinder getUnscoped() {
		HyperlinkFinder finder = new HyperlinkFinder();
		finder.controlFinder = new HyperlinkControlFinder(null);
		finder.segmentFinder = new HyperlinkSegmentFinder(new FormTextLocator());
		return finder;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.locator.forms.IHyperlinkFinder#findAllHyperlinks(com.windowtester.runtime.IUIContext)
	 */
	public IHyperlinkReference[] findAllHyperlinks(IUIContext ui) {
		
		IHyperlinkReference[] controls = controlFinder.findAllHyperlinks(ui);
		IHyperlinkReference[] segments = segmentFinder.findAllHyperlinks(ui);
		IHyperlinkReference[] all = new IHyperlinkReference[controls.length + segments.length];
		int i = 0;
		for ( ; i < controls.length; i++) {
			all[i] = controls[i];
		}
		for (int j=0; j < segments.length; j++) {
			all[i++] = segments[j];
		} 
		return all;
	}
	
	public static IHyperlinkReference[] resolveHyperlinks(Widget w) {
		if (w instanceof Hyperlink)
			return new IHyperlinkReference[]{HyperlinkControlReference.forControl((Hyperlink)w)};
		if (w instanceof FormText)
			return FormTextReference.forText((FormText)w).getHyperlinks();
		return new IHyperlinkReference[]{};
	}
	

	public static IHyperlinkReference[] adaptLocatorsToReferences(IWidgetLocator[] linkLocators) {
		IHyperlinkReference[] refs = new IHyperlinkReference[linkLocators.length];
		for (int i = 0; i < refs.length; i++) {
			refs[i] = (IHyperlinkReference)linkLocators[i];
		}
		return refs;
	}

}
