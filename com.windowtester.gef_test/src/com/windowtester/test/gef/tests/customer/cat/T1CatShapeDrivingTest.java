package com.windowtester.test.gef.tests.customer.cat;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.util.TextHelper;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.test.gef.factories.FigureMatcherFactory;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Jaime Wren
 */
public class T1CatShapeDrivingTest extends BaseShapeDrivingTest {
	
	public T1CatShapeDrivingTest() {
		super("Examples/WindowTester GEF Examples/Cat Shapes Diagram");
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Matchers
	//
	////////////////////////////////////////////////////////////////////////////
	
	private class ClassByNameFigureMatcher implements IFigureMatcher{
		private final String className;
		public ClassByNameFigureMatcher(String className) {
			assertNotNull(className);
			this.className = className;
		}
		public boolean matches(IFigureReference figure) {
			return figure.getFigure().getClass().getName().equals(className);
		}
	}
	
	private final ClassByNameFigureMatcher ellipseMatcher = new ClassByNameFigureMatcher("com.windowtester.internal.customer.cat.shapes.parts.MyEllipse");
	private final ClassByNameFigureMatcher rectangleMatcher = new ClassByNameFigureMatcher("com.windowtester.internal.customer.cat.shapes.parts.MyRectangle");
	
	private class ShapeNameMatcher implements IFigureMatcher {
		private final String name;
		public ShapeNameMatcher(String name) {
			this.name = name;
		}
		public boolean matches(IFigureReference figure) {
			if(TextHelper.getText(figure.getFigure()).equals(name)) {
				return true;
			}
			return false;
		}
	};
	
	
	public void test1CatShapeDrive() throws Exception {
		
		IUIContext ui = getUI();
		
		//select and drop ellipse
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new XYLocator(new FigureCanvasLocator(), 100, 100));
		
		//select and drop rectangle
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));
		ui.click(new XYLocator(new FigureCanvasLocator(), 10, 10));
		
		ui.pause(2000);
		
		//connect the items
		ui.click(new PaletteItemLocator("Solid connection"));
		
		//ui.click(new FigureLocator(ellipseMatcher));
		ui.click(new FigureLocator(FigureMatcherFactory.and(ellipseMatcher, new ShapeNameMatcher("Ellipse 1"))));
		ui.pause(2000);
		
		//ui.click(new FigureLocator(rectangleMatcher));
		ui.click(new FigureLocator(FigureMatcherFactory.and(rectangleMatcher, new ShapeNameMatcher("Rectangle 2"))));
		ui.pause(2000);
		
		ui.pause(5000);
		
		save();
	}

}
