package com.windowtester.runtime.gef.test.builder;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.gef.internal.FigureInfoState;
import com.windowtester.runtime.gef.internal.IEditPartReference;
import com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder;

/**
 * A testable subclass of {@link FigureReference}.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class TestableFigureInfo extends FigureReference {

	private static final TestableFigureBuilder TESTABLE_FIGURE_BUILDER = new TestableFigureBuilder();

	public static abstract class TestableFigureInfoState extends FigureInfoState {
		
		public static class Eager extends com.windowtester.runtime.gef.test.TestableFigureInfoState.Eager {

			public Eager(IFigure figure, IFigureInfoBuilder builder) {
				super(figure, builder);
			}
			
			@Override
			public IEditPartReference getEditPart() {
				return null;
			}
		}
	}
	
	public TestableFigureInfo(IFigure figure) {
		super(figure, new TestableFigureInfoState.Eager(figure, TESTABLE_FIGURE_BUILDER));
	}
	

	
	
}
