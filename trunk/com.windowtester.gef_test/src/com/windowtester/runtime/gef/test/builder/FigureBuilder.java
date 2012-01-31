package com.windowtester.runtime.gef.test.builder;

import static com.windowtester.runtime.swt.internal.display.DisplayExec.sync;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.EllipseAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;

import com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

/**
 * <p>
 * Convention: methods suffixed with '0' such as {@link #anchor0(IFigure)} are NOT run on the UI thread 
 * so need to be appropriately wrapped by the caller.
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class FigureBuilder {

	
	private static final TestableFigureBuilder TESTABLE_FIGURE_BUILDER = new TestableFigureBuilder();


	public static Connection connect(final IFigure src, final IFigure target) {
		return (Connection)sync(new RunnableWithResult(){
			@Override
			public Object runWithResult() {
				PolylineConnection con = new PolylineConnection();	
				con.setSourceAnchor(anchor0(src));
				con.setTargetAnchor(anchor0(target));
				return con;
			}
		});
	}
	
	
	private static ConnectionAnchor anchor0(IFigure owner) {
		return new EllipseAnchor(owner);
	}
	
	public static IFigure figure() {
		return (IFigure) sync(new RunnableWithResult() {
			@Override
			public Object runWithResult() {
				return figure0();
			}
		});
	}

	protected static IFigure figure0() {
		return new Figure();
	}

	public static void addChild(final IFigure parent, final IFigure ...figures) {
		sync(new Runnable() {
			public void run() {
				addChild0(parent, figures);
			}
		});
	}


	protected static void addChild0(IFigure parent, IFigure ... figures) {
		for (IFigure figure : figures) {
			parent.add(figure);
		}
	}
	
	
	public static IFigureInfoBuilder builder() {
		return TESTABLE_FIGURE_BUILDER;
	}
	
	
}
