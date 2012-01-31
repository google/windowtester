package com.windowtester.runtime.gef.internal.hierarchy;

import static com.windowtester.runtime.gef.test.builder.FigureBuilder.addChild;
import static com.windowtester.runtime.gef.test.builder.FigureBuilder.figure;

import java.util.Comparator;

import junit.framework.TestCase;

import org.eclipse.draw2d.IFigure;

import com.instantiations.test.util.TestCollection;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.test.builder.TestableFigureInfo;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class FigureInfoBuilderTest extends TestCase {
	
	private final class FigureToInfoComparator implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			if (o1 instanceof IFigureReference) 
				return compare((IFigureReference)o1, o2);
			if (o2 instanceof IFigureReference)
				return compare ((IFigureReference)o2, o1);
			return -1;
		}

		private int compare(IFigureReference o1, Object o2) {
			if (o2 instanceof IFigureReference)
				return o1.equals(o2) ? 0 : -1;
			if (o2 instanceof IFigure)
				return o1.getFigure() == o2 ? 0 : -1;
			return -1;
		}	
	}

	
	public void testGetChildrenEmptyButNotNull() {
		IFigureReference info = build(figure());
		IFigureReference[] children = info.getChildren();
		assertNotNull(children);
		assertEquals(0, children.length);
	}
	
	
	public void testBuildSimple() {
		IFigure f1 = figure();
		IFigure[] children = new IFigure[]{figure(), figure(), figure()};
		addChild(f1, children);

		IFigureReference info = build(f1);
		assertSame(f1, info);
		assertContainsOnly(children, info.getChildren());
	}

	public void testBuildNested() {
		IFigure f1 = figure();
		IFigure f2 = figure();
		IFigure f3 = figure();
		addChild(f2, f3);
		addChild(f1, f2);

		IFigureReference info = build(f1);
		assertSame(f1, info);
		assertSame(f2, info.getChildren()[0]);
		assertSame(f3, info.getChildren()[0].getChildren()[0]);
		
	}
	
	public void testGetConnections() {
		fail("unimplemented");
	}
	
	
	
	private void assertContainsOnly(IFigure[] expected, IFigureReference[] actual) {
		TestCollection.assertContainsOnly(expected, actual, new FigureToInfoComparator());
	}

	private void assertSame(IFigure figure, IFigureReference info) {
		assertEquals(figure, info.getFigure());
	}

	private IFigureReference build(IFigure f1) {
		return new TestableFigureInfo(f1);
	}
	
}
