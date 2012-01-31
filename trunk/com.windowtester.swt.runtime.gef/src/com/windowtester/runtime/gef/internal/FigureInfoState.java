/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.gef.internal;

import org.eclipse.draw2d.IFigure;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.hierarchy.IConnectionList;
import com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder;

/**
 * A container for state that may be pre- or lazily computed.
 */
public abstract class FigureInfoState {
	
	public static FigureInfoState lazy(IFigure figure) {
		return new LazyState(figure, new FigureInfoBuilder());
	}
	
	public static FigureInfoState eager(IFigure figure) {
		return new EagerState(figure, new FigureInfoBuilder());
	}
	
	protected IEditPartReference _part;
	protected IFigureList _children;
	protected IFigureReference _parent;
	protected IConnectionList _connections;
	
	public abstract IEditPartReference getEditPart();
	public abstract IFigureList getChildren();
	public abstract IFigureReference getParent();
	public abstract IConnectionList getConnections();
	
	protected static class EagerState extends FigureInfoState {
		protected EagerState(IFigure figure, IFigureInfoBuilder builder) {
			Invariants.notNull(figure);
			Invariants.notNull(builder);
			_children    = getChildren(figure, builder);
			//_parent      = getParent(figure, builder); //TODO: this is causing stack overflows...
			_part        = getPart(figure, builder);
			_connections = getConnections(figure, builder);
		}
		public IFigureList getChildren() {
			return _children;
		}
		public IEditPartReference getEditPart() {
			return _part;
		}
		public IConnectionList getConnections() {
			return _connections;
		}
		public IFigureReference getParent() {
			return _parent;
		}
	}
	
	protected static class LazyState extends FigureInfoState {
		private final IFigure _figure;
		private final IFigureInfoBuilder _builder;
		protected LazyState(IFigure figure, IFigureInfoBuilder builder) {
			Invariants.notNull(figure);
			Invariants.notNull(builder);
			_figure = figure;
			_builder = builder;
		}
		public IFigureList getChildren() {
			if (_children == null)
				_children = getChildren(getFigure(), getBuilder());				
			return _children;
		}
		public IFigureReference getParent() {
			if (_parent == null)
				_parent = getParent(getFigure(), getBuilder());
			return _parent;
		}
		public IEditPartReference getEditPart() {
			if (_part == null)
				_part = getPart(getFigure(), getBuilder());
			return _part;
		}
		public IConnectionList getConnections() {
			if (_connections == null)
				_connections = getConnections(getFigure(), getBuilder());
			return _connections;
		}
		private IFigure getFigure() {
			return _figure;
		}
		private IFigureInfoBuilder getBuilder() {
			return _builder;
		}
	}

	protected IFigureList getChildren(IFigure figure, IFigureInfoBuilder builder) {
		return builder.getChildren(figure);
	}
	
	protected IFigureReference getParent(IFigure figure, IFigureInfoBuilder builder) {
		return builder.getParent(figure);
	}
	
	protected IEditPartReference getPart(IFigure figure, IFigureInfoBuilder builder) {
		return builder.getPart(figure);
	}
	
	protected IConnectionList getConnections(IFigure figure, IFigureInfoBuilder builder) {
		return builder.getConnections(figure);
	}

	
	

}
