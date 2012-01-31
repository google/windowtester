package com.windowtester.runtime.gef.internal;

import static com.windowtester.runtime.gef.test.builder.FigureBuilder.addChild;
import static com.windowtester.runtime.gef.test.builder.FigureBuilder.builder;
import static com.windowtester.runtime.gef.test.builder.FigureBuilder.figure;
import junit.framework.TestCase;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.test.TestableFigureInfoState;
import com.windowtester.runtime.gef.test.TestableFigureInfoState.ITestableFigureInfoState;

/**
 * The class <code>FigureInfoStateTest</code> contains tests for the class
 * {@link <code>FigureInfoState</code>}
 * <p>
 * @author Phil Quitslund
 */
public class FigureInfoStateTest extends TestCase {

	public void testInitInvariantsChecked1() {
		try {
			new TestableFigureInfoState.Eager(figure(), null);
			fail("invariant untested");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testInitInvariantsChecked2() {
		try {
			new TestableFigureInfoState.Eager(null, builder());
			fail("invariant untested");
		} catch (IllegalArgumentException e) {
			//pass
		}
	}
	
	public void testLazyIsNotEager() {
		IFigure f1 = figure();
		IFigure f2 = figure();
		addChild(f1, f2);
		ITestableFigureInfoState state = new TestableFigureInfoState.Lazy(f1, builder());
		assertNull(state.accessConnections());
		assertNull(state.accessChildren());
		assertNull(state.accessPart());
		assertNull(new TestableFigureInfoState.Lazy(f2, builder()).accessParent());
		
	}

	public void testEagerIsNotLazy() {
		IFigure f1 = figure();
		IFigure f2 = figure();
		addChild(f1, f2);
		ITestableFigureInfoState state = new TestableFigureInfoState.Eager(f1, builder());
		assertNotNull(state.accessConnections());
		assertNotNull(state.accessChildren());
		assertNotNull(state.accessPart());
		assertNotNull(new TestableFigureInfoState.Lazy(f2, builder()).accessParent());

	}

	
}