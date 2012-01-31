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
package com.windowtester.runtime.draw2d.internal.helpers;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;

/**
 * A strategy for calculating bounds.  Necessitated by panes that force us to 
 * (re)-translate the bounds (GMF).
 */
public abstract class BoundsCalculator {


	static class DefaultCalculator extends BoundsCalculator {
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.draw2d.internal.helpers.BoundsCalculator#getBounds(org.eclipse.draw2d.IFigure)
		 */
		public Rectangle getBounds(IFigure target) {
			Rectangle bounds = target.getBounds();
			if (bounds == null)
				throw new IllegalStateException("bounds unexpectedly null");
			return bounds.getCopy();
		}
	}
	
	static class TranslatingCalculator extends BoundsCalculator {
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.draw2d.internal.helpers.BoundsCalculator#getBounds(org.eclipse.draw2d.IFigure)
		 */
		public Rectangle getBounds(IFigure target) {
			Rectangle original = target.getBounds().getCopy();
			Rectangle translated = original.getCopy();
			/*
			 * there are issues here --- sometimes the translation produces a
			 * bogus value (e.g., negative) why? and when? we can guard against
			 * the obvious (negatives) but what's the general rule?
			 */
			target.translateToAbsolute(translated);
			if (translated.x >= 0 && translated.y >= 0)
				return translated;
			return original;
		}
	}
	
	private static final class RenderingPaneMatcher implements IFigureMatcher {

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.gef.IFigureMatcher#matches(com.windowtester.runtime.gef.IFigureReference)
		 */
		public boolean matches(IFigureReference figureRef) {
			return isRenderedPane(figureRef.getFigure());
		}
		
		private static boolean isRenderedPane(IFigure figure) {
			if (figure == null)
				return false;
			Class cls = figure.getClass();
			String name = cls.getName();
			return name.startsWith("org.eclipse.gmf.runtime.diagram.ui.render.editparts");
		}
	}
	
	
	private static final DefaultCalculator DEFAULT        = new DefaultCalculator();
	private static final TranslatingCalculator TRANSLATOR = new TranslatingCalculator();
	
	private static final IFigureMatcher RENDERING_PANE_MATCHER = new RenderingPaneMatcher();
	

	public static BoundsCalculator forFigure(IFigure figure) {
		if (isOnRenderedPane(figure))
			return TRANSLATOR;
		return DEFAULT;
	}
	

	private static boolean isOnRenderedPane(IFigure figure) {	
		return Draw2DFinder.getDefault().isContainedIn(figure, RENDERING_PANE_MATCHER);
	}


	public abstract Rectangle getBounds(IFigure target);

	
	
}
