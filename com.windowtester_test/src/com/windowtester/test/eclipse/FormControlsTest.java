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
package com.windowtester.test.eclipse;

import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.condition.eclipse.ConfirmPerspectiveSwitchShellHandler;
import com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SectionLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.eclipse.helpers.SimpleProjectHelper;

/**
 * Tests to exercise Eclipse forms.
 * 
 * @author Steve Messick
 */
public class FormControlsTest extends BaseTest {

	private static final String PROJECT_NAME = "controls";

	protected void setUp() throws Exception {
		IUIContext ui = getUI();
		IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
		sm.add(new ConfirmPerspectiveSwitchShellHandler(true));
		sm.add(new WizardClosingShellHandler());
		super.setUp();
		SimpleProjectHelper.createPluginProject(ui, PROJECT_NAME);
	}

	/**
	 * Ensure that the package explorer is visible by opening it
	 * @param ui the UI context to use
	 * @throws WidgetSearchException
	 */
	static public void ensurePackageExplorerVisible(IUIContext ui) throws WidgetSearchException {
		ui.click(new MenuItemLocator("Window/Show View/Other..."));
		ui.wait(new ShellShowingCondition("Show View"));
		ui.click(new TreeItemLocator("Java/Package Explorer"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Show View"));
	}

	/**
	 * Test that an expandable section can be expanded.
	 * @throws Exception
	 */
	public void testSectionExpansion() throws Exception {
		// not sure if this works for older versions
		if (EclipseUtil.isVersion_31() || EclipseUtil.isVersion_32())
			return; // just pretend to succeed
		IUIContext ui = getUI();
		ensurePackageExplorerVisible(ui);
		assertProjectExists(PROJECT_NAME);
		Path filePath = new Path(PROJECT_NAME + "/META-INF/MANIFEST.MF");
		assertFileExists(filePath);
		WidgetLocator view = new ViewLocator("org.eclipse.jdt.ui.PackageExplorer");
		ILocator file = new TreeItemLocator(filePath.toString(), view);
		ui.click(2, file); // double-click manifest file to make sure PDE editor is open
		ui.click(new CTabItemLocator("Dependencies"));
		ui.click(new SectionLocator("Automated Management of Dependencies"));
		ui.click(new ButtonLocator("Require-Bundle"));
		// is there an easier way to check button state?
		WidgetReference ref = (WidgetReference) ui.find(new ButtonLocator("Require-Bundle"));
		final Button button = (Button) ref.getWidget();
		Object win = ui.getActiveWindow();
		Display display = ((Shell) win).getDisplay();
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				assertTrue(button.isVisible());
				assertTrue(button.isEnabled());
				assertTrue(button.getSelection());
			}
		});
		/* the following does not work when the section tab is scrolled off */
		/* if it did work it would be a valid test for any version of eclipse */
//		ui.click(new CTabItemLocator("Build"));
//		ui.click(new SectionLocator("Extra Classpath Entries"));
//		ui.click(new ButtonLocator("Add JARs..."));
//		ui.wait(new ShellShowingCondition("JAR Selection"));
//		ui.click(new ButtonLocator("Cancel"));
//		ui.wait(new ShellDisposedCondition("JAR Selection"));
	}
}
