package com.windowtester.runtime.gef.internal;

import static com.windowtester.runtime.swt.internal.display.DisplayExec.sync;
import junit.framework.TestCase;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.runtime.gef.test.builder.TestableFigureInfo;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * Basic {@link FigureReference} tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class FigureInfoTest extends TestCase {

	
	static class MyFigure extends Figure { }
	
//	public void testSetChildrenInvariant() {
//		TestableFigureInfo info = new TestableFigureInfo(figure());
//		try {
//			info.setChildren(null);
//			fail("invariant unchecked");
//		} catch(IllegalArgumentException e) {
//			//pass
//		}
//	}
	
	public void testInitInvariant() {
		try {
			new TestableFigureInfo(null);
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testGetType() {
		IFigure myFig = (IFigure) sync(new RunnableWithResult() {
			@Override
			public Object runWithResult() {
				return new MyFigure();
			}
		});
		
		TestableFigureInfo info = new TestableFigureInfo(myFig);
		assertEquals(new ClassReference(MyFigure.class), info.getType());
	}
	
}
