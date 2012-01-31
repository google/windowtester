package com.windowtester.test.gef.helpers;

import static com.windowtester.test.gef.tests.common.BaseShapeDrivingTest.NEW_SHAPE_EXAMPLE_PATH;
import static com.windowtester.test.gef.tests.common.BaseLogicDrivingTest.NEW_LOGIC_EXAMPLE_PATH;



import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.locator.FigureClassLocator;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFExampleHelper {

	public static final String OR_FIGURE_CLASS = "org.eclipse.gef.examples.logicdesigner.figures.OrGateFigure";
	public static final String AND_FIGURE_CLASS = "org.eclipse.gef.examples.logicdesigner.figures.AndGateFigure";
	
	
	
	final static WorkBenchHelper WB = new WorkBenchHelper();
	
	public static void createShapeDiagramExample(IUIContext ui, String projectName) throws WidgetSearchException {
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(NEW_SHAPE_EXAMPLE_PATH));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}


	public static void createShapeDiagramExampleWithNameInProject(IUIContext ui, String diagramName, String projectName) throws WidgetSearchException {
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(NEW_SHAPE_EXAMPLE_PATH));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(2, new XYLocator(new LabeledTextLocator("File na&me:"), 140, 4));
		ui.enterText(diagramName);
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}
	
	
	public static void createLogicDiagramExampleWithNameInProject(IUIContext ui, String diagramName, String projectName) throws WidgetSearchException {
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(NEW_LOGIC_EXAMPLE_PATH));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(2, new XYLocator(new LabeledTextLocator("File na&me:"), 140, 4));
		ui.enterText(diagramName);
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}
	
	
	public static void openNewWizard(String projectName, IUIContext ui) throws WidgetSearchException {
		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "New/Other...");
		ui.wait(new ShellShowingCondition("New"));
	}

	public static void openView(IUIContext ui, View view) throws WidgetSearchException {
		getWB().openView(ui, view);
	}

	public static void createSimpleProject(IUIContext ui, String projectName) throws WidgetSearchException {
		getWB().createSimpleProject(ui, projectName);
	}
	
	private static WorkBenchHelper getWB() {
		return WB;
	}
	
	public static Rectangle getBounds(IWidgetLocator figureRef) {
		if (!(figureRef instanceof IFigureReference))
			throw new IllegalArgumentException("reference must be to a figure and must be resolved first (e.g., \"found\")");
		IFigureReference ref = (IFigureReference)figureRef;
		IFigure figure = ref.getFigure();
		if (figure == null)
			return null;
		return figure.getBounds();
	}
	
	
	public static FigureClassLocator orFigure() {
		return new FigureClassLocator(OR_FIGURE_CLASS);
	}

	public static FigureClassLocator andFigure() {
		return new FigureClassLocator(AND_FIGURE_CLASS);
	}
	
	
}
