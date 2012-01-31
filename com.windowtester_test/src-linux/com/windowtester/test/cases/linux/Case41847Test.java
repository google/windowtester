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
package com.windowtester.test.cases.linux;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;

import abbot.tester.swt.ButtonTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.condition.eclipse.CompilerSettingsChangedShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.matchers.ByTextMatcher;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.LinkReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.BaseTest;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.eclipse.ModifyCompilerSettingsTest;
import com.windowtester.test.util.TypingLinuxHelper;

/**
 *
 * @author Phil Quitslund
 * @author Jaime Wren
 */
public class Case41847Test extends BaseTest {
	
	private static class ExecutionEnvironmentComplianceButtonLocator extends ButtonLocator {

		private static final long serialVersionUID = 1L;
				
		public ExecutionEnvironmentComplianceButtonLocator() {
			super("");
		}

		@Override
		protected ISWTWidgetMatcher buildMatcher() {
			return new ISWTWidgetMatcher() {
				public boolean matches(ISWTWidgetReference<?> ref) {
					final Object widget = ref.getWidget();
					return DisplayReference.getDefault().execute(new Callable<Boolean>() {

						public Boolean call() throws Exception {
							if (!(widget instanceof Button))
								return false;
							Button button = (Button)widget;
							Composite parent = button.getParent();
							Control[] children = parent.getChildren();
//							for (int i = 0; i < children.length; i++) {
//								System.out.println(children[i]);
//							}
							if (children.length < 2)
								return false;
							if (!(children[1] instanceof Link))
								return false;
							Link link = (Link)children[1];
							return new ByTextMatcher("Use compliance .*").matches(new LinkReference(link));
//							return TextMatcher.create("Use compliance .*").matches(link);
						}
					});
				}
			};
		}
		
	}
	
	@Override
	protected void setUp() throws Exception {
		IShellMonitor sm = (IShellMonitor) getUI().getAdapter(
				IShellMonitor.class);
		sm.add(new CompilerSettingsChangedShellHandler(true));
		sm.add(new ConfirmPerspectiveSwitchShellHandler(false));
		sm.add(new WizardClosingShellHandler());
		closeWelcomePageIfNecessary();
	}


	//public void testToggleCompilerSettings() throws Exception {
	public void test41847() throws Exception {

		for(int i = 0; i < 15; i++) {
			final String projectName = getClass().getName() + '_' + i;
			
			//System.out.println("-------------------------------------------------------------------------------------");
			//System.out.println("-------------------------------------------------------------------------------------");
			//System.out.println("number " + (i+1) + "--->");
			
			createJavaProject(projectName);

			// note that combo selection is self-verifying
			setCompilerSettings(projectName, "1.3");

			setCompilerSettings(projectName, "1.4");
			
			setCompilerSettings(projectName, "1.5");
			
			setCompilerSettings(projectName, "1.4");
			
			setCompilerSettings(projectName, "1.3");
			
		}
	}

	public void setCompilerSettings(String projectName, String javaVersion)
			throws WidgetSearchException, Exception {
		IUIContext ui = getUI();

		ensurePackageExplorerVisible(ui);

		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")),
				"Properties");
		ui.wait(new ShellShowingCondition("Properties for " + projectName));
		ui.click(new TreeItemLocator("Java Compiler"));

		ensureButtonSelected("Enable pr&oject specific settings", true);

		//new DebugHelper().printWidgets();

		if (EclipseUtil.isAtLeastVersion_35())
			ensureButtonSelected(new ExecutionEnvironmentComplianceButtonLocator(), false);

		ui.click(new ComboItemLocator(javaVersion, new SWTWidgetLocator(
				Combo.class, 0, new SWTWidgetLocator(Group.class,
						"JDK Compliance"))));
		ui.click(new ButtonLocator("&Apply"));
		// possible shell...
		ui.wait(new ShellDisposedCondition("Rebuilding"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Properties for " + projectName));
	}

	private boolean ensureButtonSelected(String label, boolean selected)
			throws WidgetSearchException {
		return ensureButtonSelected(new ButtonLocator(label), selected);
	}

	private boolean ensureButtonSelected(ButtonLocator buttonLocator,
			boolean selected) throws WidgetSearchException {
		Button button = (Button) ((IWidgetReference) getUI().find(buttonLocator)).getWidget();
		boolean isSelected = new ButtonTester().getSelection(button);
		boolean click = selected != isSelected;
		if (click)
			getUI().click(buttonLocator);
		return click;

	}


	private void createJavaProject(String projectName)
			throws WidgetSearchException, WaitTimedOutException {
		try{
		TypingLinuxHelper.switchToInsertStrategyIfNeeded();
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new TreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new LabeledTextLocator("&Project name:"));
		ui.enterText(projectName);
		ui.click(new ButtonLocator("Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
		}finally{
			TypingLinuxHelper.restoreOriginalStrategy();
		}
	}

	private void ensurePackageExplorerVisible(IUIContext ui) throws Exception {
		ui.ensureThat(view("Package Explorer").isShowing());
	}

}