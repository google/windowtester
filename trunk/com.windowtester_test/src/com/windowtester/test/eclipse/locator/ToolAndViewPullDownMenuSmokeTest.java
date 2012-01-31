package com.windowtester.test.eclipse.locator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.util.TypingLinuxHelper;

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
public class ToolAndViewPullDownMenuSmokeTest extends BaseTest {

	private static final String LAYOUT_VERTICAL_VIEW_ORIENTATION = "Layout/Vertical.*";

	public void testOpenNewToolItemPullDown() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Navigator").isShowing());
//		ui.click(new MenuItemLocator("&Window/Show &View/Navigator"));

		
		
		ui.click(new PullDownMenuItemLocator("Project...", new ContributedToolItemLocator("newWizardDropDown")));
		ui.click(new ButtonLocator("Cancel"));
	}

	//https://fogbugz.instantiations.com/fogbugz/default.asp?45879
	public void testToolItemSelectionAssertion() throws Exception {
			
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Expressions").isShowing());
		
		ui.click(new PullDownMenuItemLocator(LAYOUT_VERTICAL_VIEW_ORIENTATION, new ViewLocator("org.eclipse.debug.ui.ExpressionView")));
		ui.assertThat(new PullDownMenuItemLocator(LAYOUT_VERTICAL_VIEW_ORIENTATION, new ViewLocator("org.eclipse.debug.ui.ExpressionView")).isSelected());
	}
	
	public void testItemIndexAssertion() throws Exception {
	
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Expressions").isShowing());
		
		ui.assertThat(new PullDownMenuItemLocator("Layout", new ViewLocator("org.eclipse.debug.ui.ExpressionView")).hasIndex(0));
	}
	
	
	public void testToolItemSelectionAssertionFailure() throws Exception {
		
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Expressions").isShowing());
		
		ui.click(new PullDownMenuItemLocator(LAYOUT_VERTICAL_VIEW_ORIENTATION, new ViewLocator("org.eclipse.debug.ui.ExpressionView")));
		try{ 
			ui.assertThat(new PullDownMenuItemLocator(LAYOUT_VERTICAL_VIEW_ORIENTATION, new ViewLocator("org.eclipse.debug.ui.ExpressionView")).isSelected(false));	
			fail("should have thrown a WaitTimedOutException");
		} catch(WaitTimedOutException e){
			//pass
		}
	}
	
	
	public void testToolItemEnablementAssertion() throws Exception {
		
		
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Breakpoints").isShowing());
		
//		IViewPart activeViewPartNoRetries = ViewFinder.getActiveViewPartNoRetries();
		
//		ui.pause(2000);
		
		ui.assertThat(new PullDownMenuItemLocator("Deselect Default Working Set", new ViewLocator("org.eclipse.debug.ui.BreakpointView")).isEnabled(false));		

	}
	
	
	public void testOpenNavigatorViewPullDown() throws Exception {
		IUIContext ui = getUI();
		ui.ensureThat(ViewLocator.forName("Navigator").isShowing());
//		ui.click(new MenuItemLocator("&Window/Show &View/Navigator"));
		ui.click(new PullDownMenuItemLocator("Filters...", new ViewLocator("org.eclipse.ui.views.ResourceNavigator")));
		ui.wait(new ShellShowingCondition("Navigator Filters"));

		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Navigator Filters"));
	}
	
	/**
	 * This verifies nested menus.
	 * TODO: when an attempt is made to record this, the last line is
	 * <code>ui.click(new MenuItemLocator("Java/Show Constants"));</code>
	 * instead of the click below.
	 * 
	 * @see Fogbugz case 41605
	 */
	public void testOpenVariableViewNestedPullDown() throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(2, new FilteredTreeItemLocator("Debug"));
		ui.click(new FilteredTreeItemLocator("Debug/Variables"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
		ui.click(new CTabItemLocator("Variables"));
		ui.click(new PullDownMenuItemLocator("Java/Show Constants", new ViewLocator("org.eclipse.debug.ui.VariableView")));
		// However, the recorded click is:
		//ui.click(new MenuItemLocator("Java/Show Constants"));
	}
	
	/**
	 * This test needs to be revisited -- its contents are the results of a recording 
	 * (which produces a incorrect result).
	 */
	public void INVALIDtestCreateNewProjectPullDownMenu() throws Exception {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		IUIContext ui = getUI();
		ui.click(new PullDownMenuItemLocator("Project...", new MenuItemLocator("Project...")));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("pd2");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}
	
}
