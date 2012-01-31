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
package com.windowtester.runtime.gef.internal.finder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.ui.IEditorPart;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.draw2d.internal.matchers.FigureInstanceMatcher;
import com.windowtester.runtime.gef.internal.GEFEditPartReference;
import com.windowtester.runtime.gef.internal.IGEFEditPartMatcher;
import com.windowtester.runtime.gef.internal.IGEFEditPartReference;
import com.windowtester.runtime.gef.internal.IGEFFinder;
import com.windowtester.runtime.gef.internal.helpers.EditPartHelper;
import com.windowtester.runtime.gef.internal.helpers.EditorAccessor;
import com.windowtester.runtime.gef.internal.helpers.PaletteAccessor;
import com.windowtester.runtime.gef.internal.helpers.EditPartHelper.IEditPartVisitor;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;


/**
 * Basic finder utility for GEF.
 */
public class GEFFinder implements IGEFFinder {


	static class MatchAccumulatingPartVisitor implements IEditPartVisitor {
		private final IGEFEditPartMatcher _matcher;
		private final List _matches = new ArrayList();

		public MatchAccumulatingPartVisitor(IGEFEditPartMatcher matcher) {
			_matcher = matcher;
		}

		public boolean visit(EditPart part) {
			if (_matcher.matches(part))
				_matches.add(part);
			return true;
		}
		
		public EditPart[] getMatches() {
			return (EditPart[]) _matches.toArray(new EditPart[]{});
		}
	}
	
	private static final IGEFFinder DEFAULT = new GEFFinder();
	
	public static IGEFFinder getDefault() {
		return DEFAULT;
	}

	
	public EditPart[] findAllEditParts(GraphicalViewer viewer, IGEFEditPartMatcher matcher) {
		MatchAccumulatingPartVisitor collector = new MatchAccumulatingPartVisitor(matcher);
		accumulateViewerContents(viewer, collector);
		accumulatePaletteContents(viewer, collector);
		
		return collector.getMatches();
	}


	private void accumulatePaletteContents(GraphicalViewer viewer, MatchAccumulatingPartVisitor collector) {
		PaletteViewer palette = getPalette(viewer);
		if (palette == null)
			return;
		accumulate(palette.getContents(), collector);
	}


	private PaletteViewer getPalette(GraphicalViewer viewer) {
		return PaletteAccessor.forViewer(viewer).getPalette();
	}

	public PaletteAccessor getPaletteForEditor(String editorId) {
		GraphicalViewer viewer = findViewer(editorId);
		if (viewer == null)
			return null;
		return PaletteAccessor.forViewer(viewer);
	}
	
	public PaletteAccessor findPaletteForActiveEditor() {
		GraphicalViewer viewer = findViewerForActiveEditor();
		return PaletteAccessor.forViewer(viewer);
	}

	public GraphicalViewer findViewerForActiveEditor() {
		IEditorPart activeEditor = EditorFinder.getActiveEditor();
		if (activeEditor == null)
			return null;
		return adaptToGraphicalViewer(activeEditor);
	}
	
	private void accumulateViewerContents(GraphicalViewer viewer, MatchAccumulatingPartVisitor collector) {
		accumulate(viewer.getContents(), collector);
	}

	private void accumulate(EditPart contents, MatchAccumulatingPartVisitor collector) {
		EditPartHelper.visit(contents, collector);
	}
	
	private static IGEFEditPartReference[] adaptPartsToLocators(EditPart[] parts) {
		List locators = new ArrayList();
		for (int i = 0; i < parts.length; i++) {
			//TODO fix this icky downcast
			locators.add(GEFEditPartReference.create((GraphicalEditPart)parts[i]));
		}
		return (IGEFEditPartReference[]) locators.toArray(new IGEFEditPartReference[]{});
	}
	
	public IGEFEditPartReference[] findAllEditPartReferences(GraphicalViewer viewer, IGEFEditPartMatcher matcher) {
		return adaptPartsToLocators(findAllEditParts(viewer, matcher));
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFFinder#findEditPart(org.eclipse.draw2d.IFigure)
	 */
	public EditPart findEditPart(final IFigure figure) {
		EditPart[] parts = findAllEditParts(figure);
		if (parts.length > 1)
			throw new IllegalStateException("more than one edit part match");
		if (parts.length == 0)
			return null;
		return parts[0];
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFFinder#findAllEditParts(org.eclipse.draw2d.IFigure)
	 */
	public EditPart[] findAllEditParts(final IFigure figure) {
		EditPart[] parts = findAllExactMatchEditParts(figure);
		if (parts.length == 0)
			parts = findMostExactMatchEditParts(figure);
		return parts;
	}

	//TODO: FIX THIS!  (should delegate to new edit part hierarchy abstraction)
	//should return a singleton list
	private EditPart[] findMostExactMatchEditParts(final IFigure figure) {
		if (figure == null)
			return new EditPart[]{};
		EditPart[] parts = findAllEditParts(new IGEFEditPartMatcher() {
			public boolean matches(EditPart part) {
				if (!(part instanceof GraphicalEditPart))
					return false;				
				IFigure partFigure = ((GraphicalEditPart)part).getFigure();
				if (partFigure == null)
					return false;
				if (figure == partFigure)
					return true;
				return Draw2DFinder.getDefault().isContainedIn(figure, new FigureInstanceMatcher(partFigure));
			}
		});
		return new EditPart[]{selectMostExact(parts)};
	}

	private EditPart selectMostExact(EditPart[] parts) {
		for (int i = 0; i < parts.length; i++) {
			EditPart candidate = parts[i];
			boolean match = true;
			for (int j=0; j < parts.length; ++j) {
				EditPart otherPart = parts[j];
				if (isDescendantOf(otherPart, candidate))
					match = false;
			}
			if (match)
				return candidate;
				
		}
		return null;
	}


	private boolean isDescendantOf(EditPart child, EditPart parent) {
		//System.out.println("child: " + child + " parent: " + parent);
		List children = parent.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			EditPart part = (EditPart) iter.next();
			if (child == part)
				return true;
			if (isDescendantOf(child, part))
				return true;
		}
		return false;
	}


	public EditPart[] findAllExactMatchEditParts(final IFigure figure) {
		return findAllEditParts(new IGEFEditPartMatcher() {
			public boolean matches(EditPart part) {
				if (!(part instanceof GraphicalEditPart))
					return false;
				return figure == ((GraphicalEditPart)part).getFigure();
			}
		});
	}
	
	//note this selects only GRAPHICAL edit parts
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFFinder#findAllEditParts()
	 */
	public EditPart[] findAllEditParts() {
		return findAllEditParts(new IGEFEditPartMatcher() {
			public boolean matches(EditPart part) {
				return part instanceof GraphicalEditPart;
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFFinder#getFigure(org.eclipse.gef.EditPart)
	 */
	public IFigure getFigure(EditPart part) {
		if (!(part instanceof GraphicalEditPart))
			return null;
		return ((GraphicalEditPart)part).getFigure();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.gef.IGEFFinder#findAllEditParts(com.windowtester.runtime.gef.IGEFEditPartMatcher)
	 */
	public EditPart[] findAllEditParts(IGEFEditPartMatcher matcher) {
		GraphicalViewer[] viewers = findAllViewers();
		List parts = new ArrayList();
		for (int i = 0; i < viewers.length; i++) {
			EditPart[] matches = findAllEditParts(viewers[i], matcher);
			for (int j = 0; j < matches.length; j++) {
				parts.add(matches[j]);
			}
		}
		return (EditPart[]) parts.toArray(new EditPart[]{});
	}
	
	
	public GraphicalViewer findViewer(String partNameOrPattern) {
		IEditorPart editor = EditorFinder.getEditorPart(partNameOrPattern);
		if (editor == null)
			return null;
		return adaptToGraphicalViewer(editor);
	}


	private GraphicalViewer adaptToGraphicalViewer(IEditorPart editor) {
		return EditorAccessor.adaptToGraphicalViewer(editor);
	}
	
	
	public GraphicalViewer[] findAllViewers() {
		IEditorPart[] parts = EditorFinder.getEditorParts(".*");
		List viewers = new ArrayList();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] == null)
				continue;
			Object adapter = adaptToGraphicalViewer(parts[i]);
			if (adapter != null)
				viewers.add(adapter);
		}
		return (GraphicalViewer[]) viewers.toArray(new GraphicalViewer[]{});
		
	}
	
	
//	private Control[] collectAllControls(IUIContext ui) {
//		IWidgetLocator[] found = ui.findAll(new WidgetMatcher(new IWidgetMatcher() {
//			public boolean matches(Object widget) {
//				return (Control.class.isAssignableFrom(widget.getClass()));
//			}
//		}));
//		Control[] controls = new Control[found.length];
//		for (int i = 0; i < controls.length; i++) {
//			controls[i] = (Control) ((WidgetReference)found[i]).getWidget();
//		}
//		return controls;
//	}
//	class WidgetMatcher extends SWTWidgetLocator {
	//
//			private static final long serialVersionUID = 1L;
	//
//			private final IWidgetMatcher _matcher;
	//
//			public WidgetMatcher(IWidgetMatcher matcher) {
//				super(Widget.class); //ignored
//				_matcher = matcher;
//			}
	//
//			@Override
//			protected IWidgetMatcher buildMatcher() {
//				return _matcher;
//			}
//		}

	
}
