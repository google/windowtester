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

import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openView;
import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.View.JUNIT;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.eclipse.JobsCompleteCondition;
import com.windowtester.runtime.swt.condition.eclipse.ViewShowingCondition;
import com.windowtester.runtime.swt.internal.locator.forms.SectionFormTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;

public class HelpPageFormTextTest extends BaseTest {

	private static final String HELP_VIEW_ID = "org.eclipse.help.ui.HelpView";
	private static final String JUNIT_DOC_HREF = "/org.eclipse.jdt.doc.user/gettingStarted/qs-junit.htm";
		
	public void testDriveHelpPage() throws Exception {
		IUIContext ui = getUI();		
		openView(ui, JUNIT);
		ui.click(new MenuItemLocator("Help/Dynamic Help"));
		ui.wait(new ViewShowingCondition(HELP_VIEW_ID));
		ui.wait(new JobsCompleteCondition());
		
		//verify expected section is showing
		ui.assertThat(new SectionFormTextLocator("About JUnit", new ViewLocator(HELP_VIEW_ID)).isVisible());
		
		//verify contents
		ui.assertThat(new SectionFormTextLocator("About JUnit", new ViewLocator(HELP_VIEW_ID)).hasHyperlink().withText("Using JUnit"));
		ui.assertThat(new SectionFormTextLocator("About JUnit", new ViewLocator(HELP_VIEW_ID)).hasHyperlink().withText("Using JUnit").withHRef(JUNIT_DOC_HREF));
		
		
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

	
}
