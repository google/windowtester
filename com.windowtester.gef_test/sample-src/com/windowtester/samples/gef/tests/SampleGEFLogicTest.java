package com.windowtester.samples.gef.tests;

import static com.windowtester.samples.eclipse.common.WorkBenchHelper.createSimpleProject;
import static com.windowtester.samples.eclipse.common.WorkBenchHelper.openView;
import static com.windowtester.samples.eclipse.common.WorkBenchHelper.View.JAVA_PACKAGEEXPLORER;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.samples.gef.common.AbstractSampleGEFTest;
import com.windowtester.samples.gef.common.provisional.LRLocator;

/**
 * 
 * Sample Logic driving test.
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 */
public class SampleGEFLogicTest extends AbstractSampleGEFTest {
	
	private static final String NEW_LOGIC_EXAMPLE_PATH = GEF_EXAMPLES_ROOT + "Logic Diagram";
	
	private final String PROJECT_NAME = getClass().getName() + "Project";
	

	private static class OrGateLocator extends FigureLocator {
		public OrGateLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.OrGateFigure"));
		}
	}
	
	private static class XORGateLocator extends FigureLocator {
		public XORGateLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.XOrGateFigure"));
		}
	}
	private static class AndGateLocator extends FigureLocator {
		public AndGateLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.AndGateFigure"));
		}
	}
	private static class LEDFigureLocator extends FigureLocator {
		public LEDFigureLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.LEDFigure"));
		}
	}
	private static class CircuitLocator extends FigureLocator {
		public CircuitLocator() {
			super(new ByClassNameFigureMatcher("org.eclipse.gef.examples.logicdesigner.figures.CircuitFigure"));
		}
	}
	
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createLogicDiagramExample();
	}
	
	protected void createLogicDiagramExample(String projectName) throws WidgetSearchException {
		IUIContext ui = getUI();
		
		openNewWizard(projectName);
		ui.click(new TreeItemLocator(NEW_LOGIC_EXAMPLE_PATH));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}

	protected void createLogicDiagramExample() throws WidgetSearchException {
		createSimpleProject(getUI(), PROJECT_NAME);
		openView(getUI(), JAVA_PACKAGEEXPLORER);
		createLogicDiagramExample(PROJECT_NAME);
	}
	
	
	public void testLogic() throws Exception {
		
		final IUIContext ui = getUI();
		
		clickPalette("Components/Circuit");
		ui.mouseMove(new XYLocator(new FigureCanvasLocator(), 100, 100));
		ui.dragTo(new XYLocator(new FigureCanvasLocator(), 300, 250));
		pauseForModel();
		
		clickPalette("Components/And Gate");
		ui.click(new FigureCanvasXYLocator(150, 150));
		pauseForModel();
		
		clickPalette("Components/Or Gate");
		ui.click(new FigureCanvasXYLocator(200, 150));
		pauseForModel();
		
		clickPalette("Components/XOR Gate");
		ui.click(new FigureCanvasXYLocator(250, 150));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new FigureCanvasXYLocator(125, 20));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new FigureCanvasXYLocator(225, 20));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new FigureCanvasXYLocator(100, 300));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new FigureCanvasXYLocator(175, 300));
		pauseForModel();
		
		clickPalette("Components/LED");
		ui.click(new FigureCanvasXYLocator(250, 300));
		pauseForModel();
		
		
		// make some connections:
		
		// bottom far left pin of the top left LED to the first pin on the circuit
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new LEDFigureLocator(), 1),20,20));		
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, -75));
		
		pauseForModel();
		
		// bottom far left pin of the top right LED to the last pin on the circuit
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new LEDFigureLocator(), 3),20,20));		
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), 68, -75));
		pauseForModel();
		
		// first circuit pin to each of the gates
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, -75));
		ui.click(new XYLocator(new LRLocator(new AndGateLocator(), 0), 100, 100));

		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, -75));
		ui.click(new XYLocator(new LRLocator(new AndGateLocator(), 0), 100, 100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, -75));
		ui.click(new XYLocator(new LRLocator(new XORGateLocator(), 0), 100, 100));
		pauseForModel();
		
		// last top circuit pin to each of the gates
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), 71, -75));
		ui.click(new XYLocator(new LRLocator(new AndGateLocator(), 0), 100+10, 100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), 71, -75));
		ui.click(new XYLocator(new LRLocator(new OrGateLocator(), 0), 100+10, 100));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), 71, -75));
		ui.click(new XYLocator(new LRLocator(new XORGateLocator(), 0), 100+10, 100));
		pauseForModel();
		
		// connect the 
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new AndGateLocator(), 0), 100, 100));
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, 75));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new OrGateLocator(), 0), 100, 100));
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68+50, 75));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new XORGateLocator(), 0), 100, 100));
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68+100, 75));
		pauseForModel();
		
		// connect the bottom of the circuit to output LEDs
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68, 75));
		ui.click(new XYLocator(new LRLocator(new LEDFigureLocator(), 0), 20, -20));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68+50, 75));
		ui.click(new XYLocator(new LRLocator(new LEDFigureLocator(), 2), 20, -20));
		pauseForModel();
		
		clickPalette("Connection");
		ui.click(new XYLocator(new LRLocator(new CircuitLocator(), 0), -68+100, 75));
		ui.click(new XYLocator(new LRLocator(new LEDFigureLocator(), 4), 20, -20));
		pauseForModel();
		
		
		clickPalette("Select");
		
		ui.click(new LRLocator(new LEDFigureLocator(), 1));
		ui.click(new ContributedToolItemLocator("Increment"));
		pauseForModel();

		ui.click(new LRLocator(new LEDFigureLocator(), 3));
		ui.click(new ContributedToolItemLocator("Increment"));
		pauseForModel();
		
		ui.click(new LRLocator(new LEDFigureLocator(), 1));
		ui.click(new ContributedToolItemLocator("Decrement"));
		pauseForModel();
		
		
		clickPalette("Marquee");
		ui.mouseMove(new FigureCanvasXYLocator(5, 5));
		ui.dragTo(new FigureCanvasXYLocator(500, 500));
		pauseForModel();
		
		clickPalette("Select");
		ui.mouseMove(new FigureCanvasXYLocator(120, 120));
		ui.dragTo(new FigureCanvasXYLocator(200, 200));
		pauseForModel();
		
		// pause to look at editor:
		getUI().pause(3000);
		
		save();
		
	}
	
}
