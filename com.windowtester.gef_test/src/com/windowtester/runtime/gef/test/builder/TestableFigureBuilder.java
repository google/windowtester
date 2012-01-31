package com.windowtester.runtime.gef.test.builder;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureInfoBuilder;
import com.windowtester.runtime.gef.internal.hierarchy.BuildContext;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class TestableFigureBuilder extends FigureInfoBuilder {

	public TestableFigureBuilder() {
		super(new BuildContext(), new TestablePartFinder(), new TestableConnectFinder(), new FigureCache());
	}
	
	@Override
	protected IFigureReference build(IFigure figure) {
		return new TestableFigureInfo(figure);
	}
	
	
}
