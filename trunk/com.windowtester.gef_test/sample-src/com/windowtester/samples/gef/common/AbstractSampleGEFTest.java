package com.windowtester.samples.gef.common;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * Base class for driving GEF examples.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 */
public class AbstractSampleGEFTest extends UITestCaseSWT {

	/**
	 * CONFIGURE ME!
	 * <p>
	 * (Might be "Examples" in your env.)
	 */
	protected static final String GEF_EXAMPLES_ROOT = "Examples/GEF Team Examples/";
	
	
	@Override
	protected void setUp() throws Exception {
		//occasionally this shell pops -- in case it does, dismiss it!
		IShellMonitor sm = (IShellMonitor)getUI().getAdapter(IShellMonitor.class);
		sm.add(new WizardClosingShellHandler());
		closeWelcomePageIfNecessary();
	}
	
	protected void openNewWizard(String projectName) throws WidgetSearchException {
		IUIContext ui = getUI();
		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "Ne&w/&Other...");
		ui.wait(new ShellShowingCondition("New"));
	}


	protected void save() throws WidgetSearchException {
		getUI().click(new MenuItemLocator("File/Save"));
	}

	protected void pauseForModel() {
		// eventually, we want to have eventually is a method call to the GEF
		// framework to determine when the model has been updated with any changes
		getUI().pause(750);
	}


	protected void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(
				new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		getUI().click(new XYLocator(welcomeTab[0], 78, 12));
	}
	
	protected void clickPalette(String palettePath) throws WidgetSearchException {
		getUI().click(new PaletteItemLocator(palettePath));
	}
	
}
