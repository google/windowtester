package com.windowtester.test.eclipse.condition;

import static com.windowtester.test.eclipse.helpers.WorkBenchHelper.openView;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.ViewShowingCondition;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.helpers.WorkBenchHelper.View;

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
public class ViewShowingConditionSmokeTest extends BaseTest {

	@Override
	protected void setUp() throws Exception {
		IShellMonitor sm = (IShellMonitor) getUI().getAdapter(IShellMonitor.class);
		sm.add(new ConfirmPerspectiveSwitchShellHandler(false));
		saveAllIfNecessary();
		closeAllPerspectives();
		openJavaPerspective();
		closeWelcomePageIfNecessary();
	}


	private void openJavaPerspective() throws Exception {
		IUIContext ui = getUI();
	
		ui.ensureThat(PerspectiveLocator.forName("Java").isActive());
//		summonOpenPerspectiveDialog(ui);
		
//		ui.wait(new ShellShowingCondition("Open Perspective"));
//		ui.click(new TableItemLocator("Java (default)"));
//		ui.click(new ButtonLocator("OK"));
//		ui.wait(new ShellDisposedCondition("Open Perspective"));
	}

//	private void summonOpenPerspectiveDialog(IUIContext ui)
//			throws WidgetSearchException {
//		if (EclipseUtil.isAtLeastVersion_34())  //tool item id appears to have changed
//			ui.click(new MenuItemLocator("Window/Open Perspective/Other..."));
//		else
//			ui.click(new ContributedToolItemLocator("openPerspectiveDialog"));
//	}
	
	private void closeAllPerspectives() throws WidgetSearchException {
		getUI().click(new MenuItemLocator("Window/Close All Perspectives"));
	}
	
	public void testViewsShowing() throws Exception {
		/*
		 * just test a handful of views... (some require special-handling and for now we avoid those)
		 */
		testView(View.BASIC_CONSOLE);
		testView(View.BASIC_NAVIGATOR);
		testView(View.BASIC_PROBLEMS);
		testView(View.JAVA_PACKAGEEXPLORER);
		testView(View.BASIC_PROPERTIES);
	}

	private void testView(View view) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.assertThat(new ViewShowingCondition(view.getViewID(), false));
		openView(ui, view);
		ui.assertThat(new ViewShowingCondition(view.getViewID(), true));
	}
	
	
}
