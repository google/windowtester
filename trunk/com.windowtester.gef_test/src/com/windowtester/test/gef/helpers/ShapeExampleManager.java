package com.windowtester.test.gef.helpers;

import static com.windowtester.test.gef.helpers.GEFExampleHelper.createShapeDiagramExampleWithNameInProject;
import static com.windowtester.test.gef.helpers.GEFExampleHelper.createSimpleProject;
import static com.windowtester.test.gef.helpers.GEFExampleHelper.openView;
import static com.windowtester.test.gef.helpers.WorkBenchHelper.saveAllIfNecessary;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;


/**
 * A helper that is used to ensure that an example project and shape diagram
 * exist for testing.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ShapeExampleManager {

	private static boolean IS_EXAMPLE_CREATED = false;
	
	final String projectName;
	final String diagramName = "shapeExample" + EXAMPLE_COUNTER++ + ".shapes";
	private static int EXAMPLE_COUNTER = 1;
	
	private final UITestCaseSWT test;
	
	public ShapeExampleManager(UITestCaseSWT test) {
		this.test = test;
		this.projectName = test.getClass().getName() + "Project";
	}

	
	public void setUp() throws Exception {
		createShapeExampleProjectIfNecessary();
		createShapeDiagramExampleWithNameInProject(getUI(), diagramName, projectName);
	}
	
	public void tearDown() throws Exception {
		saveAllIfNecessary(getUI());
	}
	
	
	private IUIContext getUI() {
		return test.getUI();
	}


	protected void createShapeExampleProjectIfNecessary() throws WidgetSearchException {
		if (IS_EXAMPLE_CREATED)
			return;
		
		doCreateShapeExampleProject();
		IS_EXAMPLE_CREATED = true;
	}
	
	private void doCreateShapeExampleProject() throws WidgetSearchException {
		IUIContext ui = getUI();
		createSimpleProject(ui, projectName);
		openView(ui, View.JAVA_PACKAGEEXPLORER);
	}
	
}
