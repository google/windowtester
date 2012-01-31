package com.windowtester.test.gef.tests.common;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.condition.ModalDialogShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * Base class for shape driving tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 * @author Jaime Wren
 *
 */
public class BaseTextDrivingTest extends AbstractGEFDrivingTest  {

	private static final String NEW_TEXT_EXAMPLE_PATH = "Examples/GEF Team Examples/GEF WYSIWYG Document Example";
	
	private final String textExamplePath;
	
	final String PROJECT_NAME = getClass().getName() + "Project";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createTextDiagramExample();
	}
	
	public BaseTextDrivingTest() {
		super();
		this.textExamplePath = NEW_TEXT_EXAMPLE_PATH;
	}
	
	public BaseTextDrivingTest(String textExamplePath) {
		super();
		this.textExamplePath = textExamplePath;
	}
	
	protected void createTextDiagramExample(String projectName) throws WaitTimedOutException, Exception {
		IUIContext ui = getUI();
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(textExamplePath));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("Browse..."));
		
		ui.pause(500);
		ui.wait(new ShellShowingCondition("Folder Selection"));
		ui.click(new TreeItemLocator(PROJECT_NAME));
		ui.click(new ButtonLocator("OK"));
		ui.pause(500);
		
		ui.click(new ButtonLocator("&Finish"));
		//ui.wait(new ShellDisposedCondition("New"));
		ui.wait(new ModalDialogShowingCondition().not());
	}

	protected void createTextDiagramExample() throws Exception {
		createSimpleProject(PROJECT_NAME);
		createTextDiagramExample(PROJECT_NAME);
		getUI().handleConditions(); //this is a kludge: but may be required if the WizardClosingShellHandler needs to be kicked
	}
}
