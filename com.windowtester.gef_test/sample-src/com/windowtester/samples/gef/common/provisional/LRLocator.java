package com.windowtester.samples.gef.common.provisional;

import java.util.Comparator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureReference;
import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.provisional.api.IndexedFigureLocator;
import com.windowtester.runtime.gef.locator.FigureLocator;

/**
 * 
 * A locator specifying a left-to-right index. If a locator is specified in 
 * the constructor and, during playback, this locator describes a list of 
 * figures, the index <em>n</em> is used to select the <em>n</em>th figure 
 * in the matched list ordered by their spatial position left-to-right.
 * 
 * <p>
 * <strong>PROVISIONAL</strong>. This class has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API for more than
 * experimental purpose without consulting with the WindowTester team.
 * </p> 
 * 
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 * @author Jaime Wren
 *
 */
public class LRLocator extends DelegatingLocator {

	
	private static final long serialVersionUID = -7186136310469297188L;

	/**
	 * Construct a new locator specifying an index.
     *
	 * @param locator A locator to whose left-to-right indexed match will be located by this locator.
	 * @param index The left-to-right index.
	 */
	public LRLocator(FigureLocator locator, int index) {
		super(new IndexedFigureLocator(index, adaptToMatcher(locator), xyComparator()));
	}

	/**
	 * @author Jaime Wren
	 */
	private static Comparator<?> xyComparator() {
		return new Comparator<Object>() {

			public int compare(Object figRef1, Object figRef2) {
				IFigure fig1 = ((FigureReference)figRef1).getFigure();
				IFigure fig2 = ((FigureReference)figRef2).getFigure();

				Point point1, point2;

				if (fig1 instanceof Figure && fig2 instanceof Figure) {
					point1 = ((Figure) fig1).getLocation();
					point2 = ((Figure) fig2).getLocation();
				} else {
					return 0;
				}

				if (point1.x < point2.x) {
					return -1;
				} else if (point1.x > point2.x) {
					return 1;
				} else if (point1.y < point2.y) {
					return -1;
				} else if (point1.x > point2.x) {
					return 1;
				} else {
					return 0;
				}
			}
		};
	}
	
	private static IFigureMatcher adaptToMatcher(final FigureLocator locator) {
		return new IFigureMatcher() {
			public boolean matches(IFigureReference figureRef) {
				return locator.matches(figureRef);
			}
		};
	}
	
	
}
