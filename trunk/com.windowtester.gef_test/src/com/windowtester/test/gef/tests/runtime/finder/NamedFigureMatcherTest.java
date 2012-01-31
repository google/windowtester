package com.windowtester.test.gef.tests.runtime.finder;

import junit.framework.TestCase;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.matchers.NamedFigureMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class NamedFigureMatcherTest extends TestCase {

	private static final String FIGURE_NAME = "my.figure";
	
	class MyFigure extends Figure {
		public String getFigureId() {
			return FIGURE_NAME;
		}

	}
	
	class FakeFigureReference implements IFigureReference {
		private final IFigure figure;
		public FakeFigureReference(IFigure figure) {
			this.figure = figure;
		}
		
		public IFigureReference[] getChildren() {
			// TODO Auto-generated method stub
			return null;
		}
		public EditPart getEditPart() {
			// TODO Auto-generated method stub
			return null;
		}
		public IFigure getFigure() {
			return figure;
		}
		public IFigureReference getParent() {
			// TODO Auto-generated method stub
			return null;
		}
		public IWidgetLocator[] findAll(IUIContext ui) {
			// TODO Auto-generated method stub
			return null;
		}
		public boolean matches(Object widget) {
			// TODO Auto-generated method stub
			return false;
		}
		
	}

	public void testBasic() {
		assertTrue(new NamedFigureMatcher(FIGURE_NAME).matches(new FakeFigureReference(new MyFigure())));
	}
	
	
}
