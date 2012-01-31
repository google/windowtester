package com.windowtester.test.gef.tests.runtime.finder;

import junit.framework.TestCase;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.editparts.AbstractEditPart;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.matchers.NamedEditPartMatcher;
import com.windowtester.runtime.locator.IWidgetLocator;

/**
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class NamedPartMatcherTest extends TestCase {

	private static final String PART_NAME = "my.part";
	
	class MyPart extends AbstractEditPart {
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
	
	class FakeFigureReference implements IFigureReference {
		private final EditPart part;
		public FakeFigureReference(EditPart part) {
			this.part = part;
		}
		
		public IFigureReference[] getChildren() {
			// TODO Auto-generated method stub
			return null;
		}
		public EditPart getEditPart() {
			return part;
		}
		public IFigure getFigure() {
			return null;
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
		assertTrue(new NamedEditPartMatcher(PART_NAME).matches(new FakeFigureReference(new MyPart())));
	}
	
	
}
