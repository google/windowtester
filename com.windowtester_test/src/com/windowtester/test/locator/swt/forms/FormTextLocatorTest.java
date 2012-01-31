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
package com.windowtester.test.locator.swt.forms;

import org.eclipse.ui.forms.widgets.FormText;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.locator.forms.FormTextLocator;
import com.windowtester.runtime.swt.internal.locator.forms.FormTextReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkSegmentReference;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

public class FormTextLocatorTest extends AbstractFormTextLocatorTest {


	public void testAssertHasHyperlinkWithText() throws Exception {		
		getUI().assertThat(new FormTextLocator().hasHyperlink().withText(ECLIPSE_ORG_LINK));
	}
	
	public void testAssertHyperlinkHasHref() throws Exception {		
		getUI().assertThat(new FormTextLocator().hasHyperlink().withText(ACME_LINK_TEXT).withHRef(ACME_HREF));
	}
	
	public void testClickLinks() throws Exception {
		IUIContext ui = getUI();
		ui.click(new FormTextLocator().hyperlink().withText(ECLIPSE_ORG_LINK));
		assertHRefClicked(ECLIPSE_ORG_LINK);
		ui.click(new FormTextLocator().hyperlink().withHRef(ACME_HREF));
		assertHRefClicked(ACME_HREF);
		ui.click(new FormTextLocator().hyperlink().withText(INSTANTIATIONS_LINK));
		assertHRefClicked(INSTANTIATIONS_LINK);
	}


//	public void testClickImage() throws Exception {
//		//do we need this?
//		fail("image clicking is unimplemented!");
//	}
	
	
	public void testDebug() throws Exception {
		
		IUIContext ui = getUI();
		
//		ui.click(new MenuItemLocator("Help/Dynamic Help"));
//		ui.wait(new ViewLocator("org.eclipse.help.ui.HelpView").isVisible());
//		Control ctrl = ViewFinder.getViewControl("org.eclipse.help.ui.HelpView");
//		new DebugHelper().printWidgets(ctrl);
		IWidgetReference text = (IWidgetReference) ui.find(new SWTWidgetLocator(FormText.class));
		final FormText formText = (FormText)text.getWidget();
		HyperlinkSegmentReference[] hyperlinks = FormTextReference.forText(formText).getHyperlinks();
		for (HyperlinkSegmentReference link : hyperlinks) {
			System.out.println(link);
		}		
		ui.pause(3000);
	}
	
	
}
