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

import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openDynamicHelp;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openView;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.View.JUNIT;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.locator.SectionLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.test.eclipse.BaseTest;

public class HelpPageHyperlinkTest extends BaseTest {

	private static final String HELP_VIEW_ID = "org.eclipse.help.ui.HelpView";
	private static final String JUNIT_DOC_HREF = "/org.eclipse.jdt.doc.user/gettingStarted/qs-junit.htm";
		
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		openJUnitHelpPage(getUI());
	}
	
	public void testDriveHelpPage() throws Exception {
		IUIContext ui = getUI();		
		
		//verify expected section is showing
		ui.assertThat(new SectionLocator("About JUnit", new ViewLocator(HELP_VIEW_ID)).isVisible());
		//NOTICE that SectionLocator uses the legacy constructor form...  consider refactoring to inView(..)

		//verify contents
		ui.assertThat(new HyperlinkLocator("Using JUnit").inSection("About JUnit").inView(HELP_VIEW_ID).isVisible());
		ui.assertThat(new HyperlinkLocator("Using JUnit").withHRef(JUNIT_DOC_HREF).inSection("About JUnit").inView(HELP_VIEW_ID).isVisible());
		
	
		
//debugging/spelunking...
//
//		IViewReference view = ViewFinder.findWithId(HELP_VIEW_ID);
//		view.getPart(false);
//		
//		
//		ui.pause(2000);
		
//		//verify navigated contents?
//		ui.click(new SectionFormTextLocator("About JUnit", new ViewLocator(HELP_VIEW_ID)).hyperlink().withText("Using JUnit"));		
//		//TODO:
//		//wait for page to render...
//		//inspect page?
		
	}

	private void openJUnitHelpPage(IUIContext ui) throws WidgetSearchException, WaitTimedOutException {
		openView(ui, JUNIT);
		openDynamicHelp(ui);		
	}


	
}
