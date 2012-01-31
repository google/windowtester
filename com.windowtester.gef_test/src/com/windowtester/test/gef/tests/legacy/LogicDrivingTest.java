package com.windowtester.test.gef.tests.legacy;


import static com.windowtester.test.gef.factories.ConditionFactory.shellDisposed;
import static com.windowtester.test.gef.factories.LocatorFactory.button;
import static com.windowtester.test.gef.factories.LocatorFactory.canvas;
import static com.windowtester.test.gef.factories.LocatorFactory.paletteItem;
import static com.windowtester.test.gef.factories.LocatorFactory.treeItem;
import static com.windowtester.test.gef.factories.LocatorFactory.xy;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;
import com.windowtester.test.gef.tests.common.AbstractGEFDrivingTest;

/**
 * An example that takes the logic example for a spin...
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class LogicDrivingTest extends AbstractGEFDrivingTest {

	//"Examples/Logic Diagram" 3.2.*
	private static final String NEW_LOGIC_EXAMPLE_PATH = "Examples/GEF (Graphical Editing Framework)/Logic Diagram";
	private static final String DIAGRAM_NAME = "MyModel.logic";
	final String PROJECT_NAME = getClass().getName() + "Project";

	
	//TODO: matching should happen on class && ID
	
	
	public void testDrive() throws Exception {
		createLogicDiagramExample();
		click(paletteItem("Components/LED"));
		click(xy(canvas(DIAGRAM_NAME), 50, 50));
		pause(2000);
		save();
	}
	
	protected void createLogicDiagramExample(String projectName, String diagramName) throws Exception {
		IUIContext ui = getUI();
		
		openNewWizard(projectName, ui);
		
		click(treeItem(NEW_LOGIC_EXAMPLE_PATH));
		click(button("&Next >"));
		click(2, new XYLocator(new LabeledTextLocator("File na&me:"), 133, 10));
		enterText(diagramName);
		click(button("&Finish"));
		wait(shellDisposed("New"));
	}



	protected void createLogicDiagramExample() throws Exception {
		closeWelcomePageIfNecessary();
		createSimpleProject(PROJECT_NAME);
		createLogicDiagramExample(PROJECT_NAME, DIAGRAM_NAME);
	}
	
	
}
