package com.windowtester.runtime.gef.test;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureInfoState;
import com.windowtester.runtime.gef.internal.IEditPartReference;
import com.windowtester.runtime.gef.internal.IFigureList;
import com.windowtester.runtime.gef.internal.hierarchy.IConnectionList;
import com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public abstract class TestableFigureInfoState extends FigureInfoState {

	
	public static interface ITestableFigureInfoState {
		IFigureList accessChildren();
		IConnectionList accessConnections();
		IEditPartReference accessPart();
	}
	
	public static class Eager extends FigureInfoState.EagerState implements ITestableFigureInfoState {
		public Eager(IFigure figure, IFigureInfoBuilder builder) {
			super(figure, builder);
		}	
		public IFigureList accessChildren() {
			return super._children;
		}
		public IConnectionList accessConnections() {
			return super._connections;
		}		
		public IEditPartReference accessPart() {
			return super._part;
		}
		public IFigureReference accessParent() {
			return super._parent;
		}
	}

	public static class Lazy extends FigureInfoState.LazyState implements ITestableFigureInfoState {
		public Lazy(IFigure figure, IFigureInfoBuilder builder) {
			super(figure, builder);
		}
		public IFigureList accessChildren() {
			return super._children;
		}
		public IConnectionList accessConnections() {
			return super._connections;
		}
		public IEditPartReference accessPart() {
			return super._part;
		}
		public IFigureReference accessParent() {
			return super._parent;
		}

	}
	
}
