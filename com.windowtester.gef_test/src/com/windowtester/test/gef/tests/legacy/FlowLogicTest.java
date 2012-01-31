package com.windowtester.test.gef.tests.legacy;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.gef.EditPart;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.experimental.locator.AbstractGEFPartLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.LabeledTextLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.test.gef.helpers.WorkBenchHelper;
import com.windowtester.test.gef.helpers.WorkBenchHelper.View;
import com.windowtester.test.gef.tests.common.AbstractGEFDrivingTest;

public class FlowLogicTest extends AbstractGEFDrivingTest {
	
	private static final String DIAGRAM_NAME = "MyFlow.flow";
	final WorkBenchHelper _wb = new WorkBenchHelper();
	final String PROJECT_NAME = getClass().getName() + "Project";
	
	
	/**
	 * Locates parts by Model Object Class.
	 */
	static class ByModelObjectClassNameLocator extends AbstractGEFPartLocator {

		private final String _className;

		public ByModelObjectClassNameLocator(String className) {
			_className = className;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.gef.locator.AbstractGEFPartLocator#buildPartMatcher()
		 */
		protected IGEFEditPartMatcher buildMatcher() {
			return new IGEFEditPartMatcher() {
				public boolean matches(EditPart part) {
					return modelObjectMatches(part.getModel());
				}
				private boolean modelObjectMatches(Object model) {
					/*
					 * Here we just have class names but in a "real" model
					 * there would be other features that could be used to
					 * identify instances of interest.
					 */
					return model.getClass().getName().equals(_className);
				}
			};
		}
		
		protected EditorLocator buildViewerLocator() {
			return new EditorLocator(".*"); //TODO: FIX THIS!
		}
	}
	
	static class AndGateLocator extends ByModelObjectClassNameLocator {
		public AndGateLocator() {
			super("org.eclipse.gef.examples.logicdesigner.model.AndGate");
		}
	}
	
	
	static class OrGateLocator extends ByModelObjectClassNameLocator {
		public OrGateLocator() {
			super("org.eclipse.gef.examples.logicdesigner.model.OrGate");
		}
	}

	static class XOrGateLocator extends ByModelObjectClassNameLocator {
		public XOrGateLocator() {
			super("org.eclipse.gef.examples.logicdesigner.model.XORGate");
		}
	}
	
	/**
	 * Main test method.
	 */
	public void testFlowLogic() throws Exception {
		
		createSimpleProject(PROJECT_NAME);
		createFlowLogicExample(PROJECT_NAME);
		
		IUIContext ui = getUI();
	
		//open the shape palette
		ui.click(new PaletteButtonLocator());
		//wait for palette to open --- TODO: this should be a condition and in runtime
		ui.pause(1000);
		
//		select and drop OR gate
		ui.click(new PaletteItemLocator("Or Gate"));		
		ui.click(new XYLocator(new FigureCanvasLocator(DIAGRAM_NAME),10,10));

//		select and drop AND gate
		ui.click(new PaletteItemLocator("And Gate"));		
		ui.click(new XYLocator(new FigureCanvasLocator(DIAGRAM_NAME),80,10));
		
//		select and drop XOR gate
		ui.click(new PaletteItemLocator("XOR Gate"));		
		ui.click(new XYLocator(new FigureCanvasLocator(DIAGRAM_NAME),40,100));

		//connect the items
//		ui.click(new PaletteItemLocator("Connection"));
//		ui.click(new AndGateLocator());
//		ui.click(new XOrGateLocator());
		
		ui.pause(200);
		ui.click(new PaletteItemLocator("Connection"));
		ui.click(new OrGateLocator());
		ui.click(new XOrGateLocator());
		
		ui.click(new AndGateLocator());
		ui.click(new XOrGateLocator());
		
//		ui.click(new MenuItemLocator("File/Exit"));
//		ui.wait(new ShellDisposedCondition("Eclipse Platform"));
	}


	
	private void createFlowLogicExample(String projectName) throws WaitTimedOutException, Exception {
		IUIContext ui = getUI();
		ui.ensureThat(view("Package Explorer").isShowing());
		ui.contextClick(new TreeItemLocator(projectName, new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")), "Ne&w/&Other...");
		
		ui.wait(new ShellShowingCondition("New"));
		ui.click(new TreeItemLocator("Examples/GEF (Graphical Editing Framework)/Flow Diagram"));
		ui.click(new ButtonLocator("&Next >"));
		
		ui.click(2, new XYLocator(new LabeledTextLocator("File na&me:"), 133, 10));
		ui.enterText(DIAGRAM_NAME);
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New"));
	}
	
	
}