package com.windowtester.test.gef.tests.recorder;


import static com.windowtester.test.gef.helpers.GEFExampleHelper.createShapeDiagramExampleWithNameInProject;
import static com.windowtester.test.gef.helpers.GEFExampleHelper.createSimpleProject;
import static com.windowtester.test.gef.helpers.GEFExampleHelper.openView;
import static com.windowtester.test.gef.helpers.WorkBenchHelper.saveAllIfNecessary;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.Position;
import com.windowtester.runtime.gef.internal.locator.provisional.api.AnchorLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.ResizeHandleLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.test.eclipse.codegen.AbstractRecorderSmokeTest;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;

/**
 * Basic GEF recorder/codegen sanity tests.
 *
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFRecorderSmokeTests extends AbstractRecorderSmokeTest {

	private static boolean IS_EXAMPLE_CREATED = false;
	private static int EXAMPLE_COUNTER = 1;
	
	final String projectName = getClass().getName() + "Project";
	final String diagramName = "shapeExample" + EXAMPLE_COUNTER++ + ".shapes";
	
	@Override
	protected void setUp() throws Exception {
		closeWelcomePageIfNecessary();
		createShapeExampleProjectIfNecessary();
		createShapeDiagramExampleWithNameInProject(getUI(), diagramName, projectName);
		ensurePaletteIsOpen();
		super.setUp();
	}
	

	private void ensurePaletteIsOpen() throws WidgetSearchException {
		//a bit cheesy but the pinning revealer will make sure the palette is open
		getUI().click(new PaletteItemLocator("Select"));
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		saveAllIfNecessary(getUI());
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
	
	
	@Override
	protected String getBundleName() {
		return "com.windowtester.gef.test";
	}
	
	
	//////////////////////////////////////////////////////////////////////////
	//
	// Tests
	//
	//////////////////////////////////////////////////////////////////////////
	
	public void testClickPaletteButton() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteButtonLocator());
	}

	public void testSingleShapeCreate() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(100, 100));	
	}

	public void testSingleShapeCreateAndClick() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(100, 100));
		ui.click(new FigureClassLocator("org.eclipse.draw2d.Ellipse"));
	}
	
	public void testMultipleShapeCreateAndClick() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(50, 50));
		//do we need a pause here --- and can it be a condition?
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(150, 150));
		ui.click(new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse")));
		ui.click(new LRLocator(1, new FigureClassLocator("org.eclipse.draw2d.Ellipse")));
	}
	
	
	public void testSingleShapeCreateAndDragTo() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(100, 100));
		ui.click(new FigureClassLocator("org.eclipse.draw2d.Ellipse"));
		ui.dragTo(new FigureCanvasXYLocator(170,150)); //150,150 --> ui.dragTo(new LRLocator(6, new ResizeHandleLocator()));
		/*
		 * Issue: should avoid drags onto self?...
		 */
	}
	

	public void testAnchorConnect() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(200, 200));
		//do we need a pause here --- and can it be a condition?
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(50, 50));
		
		ui.click(new PaletteItemLocator("Solid connection"));
		
		ui.click(new AnchorLocator(Position.CENTER, new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse"))));
		ui.click(new AnchorLocator(Position.CENTER, new LRLocator(1, new FigureClassLocator("org.eclipse.draw2d.Ellipse"))));

		ui.pause(2000);
		
	}
	
	
	public void testResizeHandleSelect() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(200, 200));
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(50, 50));
		
		ui.click(new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse")));
			
		ui.click(new ResizeHandleLocator(Position.TOP, new LRLocator(0, new FigureClassLocator("org.eclipse.draw2d.Ellipse"))));
		ui.dragTo(new FigureCanvasXYLocator(60,10));
		
		
	}
	

}
