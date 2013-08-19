package com.windowtester.test.gef.tests.common;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;
import static com.windowtester.test.gef.factories.LocatorFactory.menuItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.eclipse.WizardClosingShellHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.DirtyEditorCondition;
import com.windowtester.runtime.swt.internal.util.EclipseLogUtil;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.gef.helpers.WorkBenchHelper;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.*;

/**
 * Base class for driving GEF examples.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class AbstractGEFDrivingTest extends UITestCaseSWT {

	final WorkBenchHelper _wb = new WorkBenchHelper();
	
	private EclipseLogUtil _logUtil = EclipseLogUtil.NULL;
		
	
	@Override
	protected void setUp() throws Exception {
		//occasionally this shell pops -- in case it does, dismiss it!
		IShellMonitor sm = (IShellMonitor)getUI().getAdapter(IShellMonitor.class);
		sm.add(new WizardClosingShellHandler());
		closeWelcomePageIfNecessary();
		getUI().ensureThat(workbench().hasFocus());
		getUI().ensureThat(workbench().isMaximized());
		setLogUtil(new EclipseLogUtil());
		getLogUtil().setUp();
	}
	
	@Override
	protected void tearDown() throws WidgetSearchException {
		saveAllIfNecessary();
		getLogUtil().tearDown();
	}
		
	private void setLogUtil(EclipseLogUtil logUtil) {
		_logUtil = logUtil;
	}

	private EclipseLogUtil getLogUtil() {
		return _logUtil;
	}
	
	protected void assertNoLoggedExceptions() {
		getLogUtil().assertNoLoggedExceptions();
	}
	
	protected void openNewWizard(String projectName, IUIContext ui) throws WaitTimedOutException, Exception {
		ui.ensureThat(view("Package Explorer").isShowing());
		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "New/Other...");
		ui.wait(new ShellShowingCondition("New"));
	}
	
	protected void XopenView(View view) throws WidgetSearchException {
		getWB().openView(getUI(), view);
	}

	protected void createSimpleProject(String projectName) throws WidgetSearchException {
		getWB().createSimpleProject(getUI(), projectName);
	}

	protected WorkBenchHelper getWB() {
		return _wb;
	}


	protected void save() throws WidgetSearchException {
		click(menuItem("&File/&Save\tCtrl+S"));
		//ui.click(new MenuItemLocator("File/Save"));
	}


	protected void pause(int ms) {
		getUI().pause(ms);
	}
	
	protected void pauseForModel() {
		//TODO: tentatively removed to test wait for command stack impl.
		
		// eventually, we want to have eventually is a method call to the GEF
		// framework to determine when the model has been updated with any changes
		//pause(750);
	}


	protected void click(ILocator locator) throws WidgetSearchException {
		getUI().click(locator);
	}


	protected void wait(ICondition condition) {
		getUI().wait(condition);
	}


	protected void enterText(String txt) {
		getUI().enterText(txt);
	}


	protected void click(int clicks, ILocator locator)
			throws WidgetSearchException {
				getUI().click(clicks, locator);
			}


	protected void closeWelcomePageIfNecessary() throws Exception {
		getUI().ensureThat(view("Welcome").isClosed());
	}
	
	protected void saveAllIfNecessary() throws WidgetSearchException {
		if (anyUnsavedChanges())
			click(new MenuItemLocator("File/Save All"));
	}

	private boolean anyUnsavedChanges() {
		return new DirtyEditorCondition().test();
	}
	
	protected void clickPalette(String palettePath) throws WidgetSearchException {
		getUI().click(new PaletteItemLocator(palettePath));
	}
	
	
}
