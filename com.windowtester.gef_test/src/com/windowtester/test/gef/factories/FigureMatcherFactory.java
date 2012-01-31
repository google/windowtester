package com.windowtester.test.gef.factories;

import java.util.Comparator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

import junit.framework.Assert;

import com.windowtester.runtime.gef.IFigureMatcher;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.internal.FigureReference;

/**
 * Factory for common matchers and matcher combinations.
 * 
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 * 
 * @author Jaime Wren
 * @author Phil Quitslund
 *
 */
public class FigureMatcherFactory {
	
	

	
	public static class ClassByNameFigureMatcher implements IFigureMatcher {
		private final String className;
		public ClassByNameFigureMatcher(String className) {
			Assert.assertNotNull(className);
			this.className = className;
		}
		public boolean matches(IFigureReference figure) {
			return figure.getFigure().getClass().getName().equals(className);
		}
	}

		
	public static IFigureMatcher figureByClassName(String className) {
		return new ClassByNameFigureMatcher(className);
	}

	public static Comparator<FigureReference> xyComparator() {
		return new Comparator<FigureReference>() {

			public int compare(FigureReference figRef1, FigureReference figRef2) {
				IFigure fig1 = figRef1.getFigure();
				IFigure fig2 = figRef2.getFigure();

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

	public static Comparator<FigureReference> yxComparator() {
		return new Comparator<FigureReference>() {

			public int compare(FigureReference figRef1, FigureReference figRef2) {
				IFigure fig1 = figRef1.getFigure();
				IFigure fig2 = figRef2.getFigure();

				Point point1, point2;

				if (fig1 instanceof Figure && fig2 instanceof Figure) {
					point1 = ((Figure) fig1).getLocation();
					point2 = ((Figure) fig2).getLocation();
				} else {
					return 0;
				}

				if (point1.y < point2.y) {
					return -1;
				} else if (point1.y > point2.y) {
					return 1;
				} else if (point1.x < point2.x) {
					return -1;
				} else if (point1.x > point2.x) {
					return 1;
				} else {
					return 0;
				}
			}
		};
	}
	
	
	/**
	 * Given two {@link IFigureMatcher}s, this method returns an
	 * {@link IFigureMatcher} that {@link IFigureMatcher#matches(IFigureReference)}
	 * <code>true</code> if and only if both the matchers passed to this method
	 * return <code>true</code>. That is, this method is the intersection of
	 * two {@link IFigureMatcher}s.
	 */
	public static IFigureMatcher and(final IFigureMatcher figureMatcher1, final IFigureMatcher figureMatcher2) {
		return new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {
				return figureMatcher1.matches(figure) && figureMatcher2.matches(figure);
			}
		};
	}
	
	/**
	 * Given two {@link IFigureMatcher}s, this method returns an
	 * {@link IFigureMatcher} that {@link IFigureMatcher#matches(IFigureReference)}
	 * <code>true</code> if and only if one the matchers passed to this method
	 * return <code>true</code>. That is, this method is the union of
	 * two {@link IFigureMatcher}s.
	 */
	public static IFigureMatcher or(final IFigureMatcher figureMatcher1, final IFigureMatcher figureMatcher2) {
		return new IFigureMatcher() {
			public boolean matches(IFigureReference figure) {
				return figureMatcher1.matches(figure) || figureMatcher2.matches(figure);
			}
		};
	}
	
}
