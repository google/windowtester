package com.windowtester.runtime.gef.test.builder;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.internal.FigureInfoBuilder.IConnectionFinder;
import com.windowtester.runtime.gef.internal.hierarchy.BuildContext;
import com.windowtester.runtime.gef.internal.hierarchy.ConnectionList;
import com.windowtester.runtime.gef.internal.hierarchy.IConnectionList;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class TestableConnectFinder implements IConnectionFinder {

	public IConnectionList findConnections(IFigure f, BuildContext context) {
		return new ConnectionList();
	}

}
