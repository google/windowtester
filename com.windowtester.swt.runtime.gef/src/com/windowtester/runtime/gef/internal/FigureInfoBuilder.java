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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.finder.GEFFinder;
import com.windowtester.runtime.gef.internal.hierarchy.BuildContext;
import com.windowtester.runtime.gef.internal.hierarchy.ConnectionList;
import com.windowtester.runtime.gef.internal.hierarchy.IConnectionList;
import com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder;

public class FigureInfoBuilder implements IFigureInfoBuilder {

	
	public static interface IFigureCache {
		IFigureReference get(IFigure figure);
		void put(IFigure figure, IFigureReference info);
	}
	
	public static interface IPartFinder {
		IEditPartReference findPart(IFigure f, BuildContext context);
	}
	
	public static interface IConnectionFinder {
		IConnectionList findConnections(IFigure f, BuildContext context);
	}
	
	protected static class DefaultConnectionFinder implements IConnectionFinder {
		public IConnectionList findConnections(IFigure f, BuildContext context) {
			return new ConnectionList();
		}
	}
	
	protected static class DefaultPartFinder implements IPartFinder {

		public IEditPartReference findPart(IFigure f, BuildContext context) {
			EditPart part = GEFFinder.getDefault().findEditPart(f);
			return new EditPartReference(part);
		}
	}
	
	public static class FigureCache implements IFigureCache {

		private final Map _map = new HashMap();
		
		public IFigureReference get(IFigure figure) {
			return (IFigureReference) _map.get(figure);
		}

		public void put(IFigure figure, IFigureReference info) {
			_map.put(figure, info);
		}
		
	}
	
	
	protected BuildContext _context;
	private final IPartFinder _partFinder;
	private final IConnectionFinder _connectionFinder;
	private final IFigureCache _figureCache;
	
	public FigureInfoBuilder() {
		this(new BuildContext(), new DefaultPartFinder(), new DefaultConnectionFinder(), new FigureCache());
	}
	
	public FigureInfoBuilder(BuildContext context, IPartFinder partFinder, IConnectionFinder connectionFinder, IFigureCache cache) {
		_context = context;
		_partFinder = partFinder;
		_connectionFinder = connectionFinder;
		_figureCache = cache;
	}
	
	public BuildContext getContext() {
		return _context;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder#getChildren(org.eclipse.draw2d.IFigure)
	 */
	public IFigureList getChildren(IFigure figure) {
		return new FigureList(buildChildren(figure));
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder#getParent(org.eclipse.draw2d.IFigure)
	 */
	public IFigureReference getParent(IFigure figure) {
		IFigure parent = figure.getParent();
		if (parent == null)
			return null;
		IFigureReference info = getFigureCache().get(parent);
		if (info != null)
			return info;
		
		return get(parent); //should REALLY not get here but this is a safe fallback
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder#getConnections(org.eclipse.draw2d.IFigure)
	 */
	public IConnectionList getConnections(IFigure figure) {
		return getConnectionFinder().findConnections(figure, getContext());
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.internal.hierarchy.IFigureInfoBuilder#getPart(org.eclipse.draw2d.IFigure)
	 */
	public IEditPartReference getPart(IFigure figure) {
		return getPartFinder().findPart(figure, getContext());
	}

/*	public static interface IPartFinder {
		IEditPartReference findPart(IFigure figure);
	}
	
	protected static class DefaultPartFinder implements IPartFinder {
		public IEditPartReference findPart(IFigure figure) {
			EditPart part = GEFFinder.getDefault().findEditPart(figure);
			return EditPartReference.create(part);
		}
	}
	
	private final BuildContext _context;
	private final com.windowtester.runtime.gef.internal.FigureInfoBuilder.IPartFinder _partFinder;

	public FigureInfoBuilder(BuildContext context, IPartFinder partFinder) {
		_context = context;
		_partFinder = partFinder;
	}
	
	public FigureInfoBuilder() {
		this(new BuildContext(), new DefaultPartFinder());
	}

	public BuildContext getContext() {
		return _context;
	}
	

*/
	
	protected IFigureReference get(IFigure figure) {
		IFigureReference info = getFigureCache().get(figure);
		if (info == null) {
			info = build(figure);
			getFigureCache().put(figure, info);
		}
		return info;
	}

	protected IFigureReference build(IFigure figure) {
		return FigureReference.eager(figure);
	}
	
	protected IFigureReference[] buildChildren(IFigure figure) {
		List children = new ArrayList();
		for (Iterator iter = figure.getChildren().iterator(); iter.hasNext();) {
			children.add(get((IFigure) iter.next()));
		}
		return (IFigureReference[]) children.toArray(new IFigureReference[]{});
	}

	private IPartFinder getPartFinder() {
		return _partFinder;
	}

	private IConnectionFinder getConnectionFinder() {
		return _connectionFinder;
	}

	private IFigureCache getFigureCache() {
		return _figureCache;
	}
	
}
