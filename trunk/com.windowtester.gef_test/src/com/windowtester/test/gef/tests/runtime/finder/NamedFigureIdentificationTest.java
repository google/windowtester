package com.windowtester.test.gef.tests.runtime.finder;

import junit.framework.TestCase;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.gef.internal.finder.FigureIdentifierService;
import com.windowtester.runtime.gef.internal.finder.IGEFPartMapper;
import com.windowtester.runtime.gef.locator.NamedEditPartFigureLocator;
import com.windowtester.runtime.gef.locator.NamedFigureLocator;
import com.windowtester.runtime.locator.ILocator;

/**
 * Smoke test for named figures/edit parts.
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class NamedFigureIdentificationTest extends TestCase {

	//subclassed for testing purposes
	private static class TestableIdService extends FigureIdentifierService {
		
		@Override
		protected ILocator findDetailLocator(IFigure figure, Event event) {
			return super.findDetailLocator(figure, event);
		}
		
		@Override
		protected IGEFPartMapper getPartMapper() {
			return new IGEFPartMapper() {
				public EditPart findEditPart(IFigure figure) {
					if (figure instanceof MyFigureWithNamedPart)
						return new MyPart();
					return null;
				}
			};
		}
	}
	
	
	private static final String FIGURE_NAME = "my.figure";
	
	static class MyFigure extends Figure {
		public String getFigureId() {
			return FIGURE_NAME;
		}
	}
	
		
	private static final String PART_NAME = "my.part";
	
	static class MyPart extends AbstractEditPart {
		@SuppressWarnings("unused")
		private String getEditPartId() {
			return PART_NAME;
		}
		@Override
		protected void addChildVisual(EditPart child, int index) {
			// TODO Auto-generated method stub
		}
		@Override
		protected void createEditPolicies() {
			// TODO Auto-generated method stub
			
		}
		@Override
		protected void removeChildVisual(EditPart child) {
			// TODO Auto-generated method stub	
		}
		public DragTracker getDragTracker(Request request) {
			// TODO Auto-generated method stub
			return null;
		} 
	}
	
	static class MyFigureWithNamedPart extends Figure { }

	
	
	public void testNamedFigure() throws Exception {
		Figure namedFigure = new MyFigure();
		Event e = new Event();
		e.widget = new FigureCanvas(new Shell());
		
		ILocator loc = new TestableIdService().findDetailLocator(namedFigure, e);
		NamedFigureLocator locator = (NamedFigureLocator)loc;
		assertEquals(FIGURE_NAME, locator.getFigureId());
	}

	public void testNamedEditPart() throws Exception {
		Figure namedFigure = new MyFigureWithNamedPart();
		Event e = new Event();
		e.widget = new FigureCanvas(new Shell());
		
		ILocator loc = new TestableIdService().findDetailLocator(namedFigure, e);
		NamedEditPartFigureLocator locator = (NamedEditPartFigureLocator)loc;
		assertEquals(PART_NAME, locator.getEditPartId());
	}
	
	
}
