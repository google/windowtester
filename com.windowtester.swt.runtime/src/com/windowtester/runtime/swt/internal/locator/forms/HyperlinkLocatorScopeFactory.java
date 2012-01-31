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

import org.eclipse.ui.forms.widgets.FormText;

import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator;

public class HyperlinkLocatorScopeFactory {


	public static WidgetLocator addScope(IHyperlinkLocator locator, FormText text) {
		String sectionText = SectionFinder.getParentSectionText(text);
		if (sectionText != null)
			locator = locator.inSection(sectionText);
		String viewId = ViewFinder.findIdForContainingView(text);
		if (viewId != null)
			locator = locator.inView(viewId);
		
		return (WidgetLocator) locator; //TODO: investigate and repair this cast
	}

}
