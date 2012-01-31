package com.windowtester.test.gef.tests.codegen;

import static com.windowtester.test.codegen.CodeGenFixture.mockSelect;
import static com.windowtester.test.codegen.CodeGenFixture.stream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.widgets.Button;

import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.user.SemanticDropEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;
import com.windowtester.runtime.draw2d.matchers.ByClassNameFigureMatcher;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.test.gef.tests.recorder.BaseGEFCodegenTest;
import com.windowtester.ui.internal.corel.model.Event;

/**
 * http://fogbugz.instantiations.com//default.php?34960
 * 
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class GEFDNDCodegenTest extends BaseGEFCodegenTest {

	static class MyFigureLocator extends FigureLocator {

		private static final long serialVersionUID = 1066569536705381016L;

		public MyFigureLocator() {
			super(new ByClassNameFigureMatcher("Foo"));
		}
		
	}

	private static final String UI_GET = "IUIContext ui = getUI();";

	
	public void testMissingSelectionIsRepaired() throws Exception {
		
		SemanticWidgetSelectionEvent source = mockSelect(IFigure.class, new IdentifierAdapter(new MyFigureLocator()));
		SemanticWidgetSelectionEvent target = mockSelect(FigureCanvas.class, new IdentifierAdapter(new FigureCanvasXYLocator(10,10)));
		
		UISemanticEvent drop = new SemanticDropEvent(target).withSource(source);
		
		codegenerator().generate(stream(new Event(drop)));
		assertMainEqualsIgnoringWS(UI_GET + click("GEFDNDCodegenTest$MyFigureLocator()") + dragTo("FigureCanvasXYLocator(10,10)") + "");
	}


	public void testMissingSelectionIsRepairedWithPreviousDifferentSelect() throws Exception {
		
		SemanticWidgetSelectionEvent previous = mockSelect(Button.class, new ButtonLocator("OK"));
		
		SemanticWidgetSelectionEvent source = mockSelect(IFigure.class, new IdentifierAdapter(new MyFigureLocator()));
		SemanticWidgetSelectionEvent target = mockSelect(FigureCanvas.class, new IdentifierAdapter(new FigureCanvasXYLocator(10,10)));
		
		UISemanticEvent drop = new SemanticDropEvent(target).withSource(source);
		
		codegenerator().generate(stream(new Event(previous), new Event(drop)));
		assertMainEqualsIgnoringWS(UI_GET + click("ButtonLocator(\"OK\")") + click("GEFDNDCodegenTest$MyFigureLocator()") + dragTo("FigureCanvasXYLocator(10,10)") + "");
	}
	
	public void testExistingSelectionIsNotRepeated() throws Exception {
		
		assertTrue("test requires platform to be running", Platform.isRunning());
		
		SemanticWidgetSelectionEvent previous = mockSelect(IFigure.class, new IdentifierAdapter(new MyFigureLocator()));
		
		SemanticWidgetSelectionEvent source = mockSelect(IFigure.class, new IdentifierAdapter(new MyFigureLocator()));
		SemanticWidgetSelectionEvent target = mockSelect(FigureCanvas.class, new IdentifierAdapter(new FigureCanvasXYLocator(10,10)));
		
		UISemanticEvent drop = new SemanticDropEvent(target).withSource(source);
		
		codegenerator().generate(stream(new Event(previous), new Event(drop)));
		assertMainEqualsIgnoringWS(UI_GET + click("GEFDNDCodegenTest$MyFigureLocator()") + dragTo("FigureCanvasXYLocator(10,10)") + "");
	}
	


	private String click(String locator) {
		return "ui.click( new " + locator + ");";
	}

	private void assertMainEqualsIgnoringWS(String body) {
		assertEquals(body.replaceAll("\\s", ""), getMainNoWS());
	}

	private String dragTo(String consCall) {
		return "ui.dragTo( new " +  consCall + ");";
	}

	private String getMainNoWS() {
		MethodUnit main = getMainMethod();
		return main.getMethodBodyContents().toString().replaceAll("\\s", "");
	}

	private MethodUnit getMainMethod() {
		return (MethodUnit) codegenerator().getTestBuilder().getMethods().iterator().next();
	}
		
}
