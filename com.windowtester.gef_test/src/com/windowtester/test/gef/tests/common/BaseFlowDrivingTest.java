package com.windowtester.test.gef.tests.common;


import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.util.TextHelper;
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
public class BaseFlowDrivingTest extends AbstractGEFDrivingTest  {

	public static final String[] ALL_PALETTE_LOCATIONS = {
			"Select",
			"Marquee",
			"Connection Creation",
			"Components/Activity",
			"Components/Sequential Activity",
			"Components/Parallel Activity"
			};
	
	public static enum Drawer {

		COMPONENTS("Components");
		
		private final String _path;

		private Drawer(String path) {
			_path = path;
		}

		public String getPath() {
			return _path;
		}
	}
	
	protected class LabelNameMatcher implements IFigureMatcher {
		private final String name;
		public LabelNameMatcher(String name) {
			this.name = name;
		}
		public boolean matches(IFigureReference figure) {
			if(TextHelper.getText(figure.getFigure()).equals(name)) {
				return true;
			}
			return false;
		}
	};
	
	protected ByClassNameFigureMatcher simpleActivityLabelMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.flow.figures.SimpleActivityLabel");
	
	protected ByClassNameFigureMatcher polylineConnectionMatcher = new ByClassNameFigureMatcher("org.eclipse.draw2d.PolylineConnection");
	
	protected ByClassNameFigureMatcher startTagMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.flow.figures.StartTag");
	
	protected ByClassNameFigureMatcher endTagMatcher = new ByClassNameFigureMatcher("org.eclipse.gef.examples.flow.figures.EndTag");
	
	//"Examples/Shapes Diagram"
	private static final String NEW_FLOW_EXAMPLE_PATH = "Examples/GEF Team Examples/Flow Diagram";
	
	private final String flowExamplePath;
	
	final String PROJECT_NAME = getClass().getName() + "Project";
	
	public BaseFlowDrivingTest() {
		super();
		this.flowExamplePath = NEW_FLOW_EXAMPLE_PATH;
	}
	
	public BaseFlowDrivingTest(String flowExamplePath) {
		super();
		this.flowExamplePath = flowExamplePath;
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		createFlowDiagramExample();
	}
	
	protected void createFlowDiagramExample(String projectName) throws Exception {
		IUIContext ui = getUI();
		
		openNewWizard(projectName, ui);

		ui.click(new TreeItemLocator(flowExamplePath));
		ui.click(new ButtonLocator("&Next >"));
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ModalDialogShowingCondition().not());
		//ui.wait(new ShellDisposedCondition("New"));
	}

	protected void createFlowDiagramExample() throws Exception {
		createSimpleProject(PROJECT_NAME);
		createFlowDiagramExample(PROJECT_NAME);
	}
}
