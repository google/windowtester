package com.windowtester.runtime.gef.internal;

import static com.windowtester.runtime.gef.test.builder.FigureBuilder.connect;
import static com.windowtester.runtime.gef.test.builder.FigureBuilder.figure;
import junit.framework.TestCase;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;

/**
 * Basic {@link ConnectionInfo} tests.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ConnectionInfoTest extends TestCase {


	public void testInitialization() {
		
		IFigure src    = figure();
		IFigure target = figure();
		
		Connection con      = connect(src, target);
		ConnectionInfo info = new ConnectionInfo(con);
		assertEquals(src, info.getSource().getFigure());
		assertEquals(target, info.getTarget().getFigure());
	}
	
	
}
