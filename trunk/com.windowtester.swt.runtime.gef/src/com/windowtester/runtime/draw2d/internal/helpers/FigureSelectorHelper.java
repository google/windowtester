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

import junit.framework.TestCase;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.ScrollBarTester;
import abbot.tester.swt.ScrollableTester;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.draw2d.internal.finder.Draw2DFinder;
import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;



public class FigureSelectorHelper {

	
	
	
	private final IUIContext _ui;

	public FigureSelectorHelper(IUIContext ui) {
		_ui = ui;
	}

	protected final IUIContext getUI() {
		return _ui;
	}
	
	private final BasicWidgetSelector _selector = new BasicWidgetSelector();

	
	public BasicWidgetSelector getSelector() {
		return _selector;
	}
	
	private void clickFigureAtPoint(final FigureCanvas parent,
			org.eclipse.draw2d.geometry.Rectangle bounds, int count) {
		TestCase.assertTrue((count > 0) && (count < 3));

		Point p = scrollForClick(parent, bounds);
		//TODO: pass in button as param
		getSelector().click(parent, p.x, p.y, SWT.BUTTON1, count);

	}

	public void clickLabel(final FigureCanvas parent, Label target) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		org.eclipse.draw2d.geometry.Rectangle bounds = target.getTextBounds();
		clickFigureAtPoint(parent, bounds, 1);
	}

	
	public void contextClickFigure(
			IFigure target, String menuText) {
		contextClickFigure(findParentCanvas(target), target, menuText);
	}
	
	public void contextClickFigure(
			IFigure target, int x, int y, String menuText) {
		contextClickFigure(findParentCanvas(target), target, x, y, menuText);
	}
	
	
	public void contextClickFigure(FigureCanvas parent, IFigure target, int x, int y, String menuText) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);
		TestCase.assertNotNull(menuText);

		//TODO: verify that this works!
		
		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		org.eclipse.draw2d.geometry.Rectangle bounds = getBounds(target);
		final org.eclipse.draw2d.geometry.Point point = new org.eclipse.draw2d.geometry.Point(bounds.x + x, bounds.y + y);

		contextClickPoint(parent, point, menuText);
	}

	private org.eclipse.draw2d.geometry.Rectangle getBounds(IFigure target) {
		return Bounds.forFigure(target).asRectangle();
	}

	public void contextClickFigure(final FigureCanvas parent,
			IFigure target, String menuText) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);
		TestCase.assertNotNull(menuText);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		org.eclipse.draw2d.geometry.Rectangle bounds = getBounds(target);
		final org.eclipse.draw2d.geometry.Point center = bounds.getCenter();

		contextClickPoint(parent, center, menuText);
	}

	public void contextClickLabel(final FigureCanvas parent,
			Label target, String menuText) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);
		TestCase.assertNotNull(menuText);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		org.eclipse.draw2d.geometry.Rectangle bounds = target.getTextBounds();
		final org.eclipse.draw2d.geometry.Point center = bounds.getCenter();

		contextClickPoint(parent, center, menuText);
	}


	public void clickPolyline(final FigureCanvas parent,
			Polyline target) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		org.eclipse.draw2d.geometry.Point start = target.getStart();
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				start.x, start.y, 1, 1);
		clickFigureAtPoint(parent, bounds, 1);
	}

	
	public void contextClickPolyline(final FigureCanvas parent,
			Polyline target, String menuText) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);
		TestCase.assertNotNull(menuText);

		contextClickPoint(parent, target.getStart(), menuText);
	}

	private void contextClickPoint(final FigureCanvas parent,
			final org.eclipse.draw2d.geometry.Point clickPoint,
			final String menuText) {
		// adjust the point to click based upon the figure canvas viewport location
		final Point p = scrollForClick(parent, clickPoint);

		// Use the basic com.windowtester.runtime.gef.internal.selectors to perform the context click
		try {
			getBasicSelector().contextClick(parent, p.x, p.y, menuText);
		} catch (WidgetSearchException wse) {
			TestCase.fail(wse.getLocalizedMessage());
		}
	}


	public Widget clickPoint(FigureCanvas canvas, IClickDescription click) {
		Point toClick = scrollForClick(canvas, new org.eclipse.draw2d.geometry.Point(click.x(), click.y()));
		return getBasicSelector().click(canvas, toClick.x, toClick.y, click.modifierMask(), click.clicks());
	}
	
	public Widget contextClickPoint(FigureCanvas canvas, IClickDescription click, String menuItemPath) throws WidgetSearchException {
		Point toClick = scrollForClick(canvas, new org.eclipse.draw2d.geometry.Point(click.x(), click.y()));
		return getBasicSelector().contextClick(canvas, toClick.x, toClick.y, menuItemPath);
	}
	
	public Widget clickPoint(FigureCanvas canvas, Point point) {
		Point toClick = scrollForClick(canvas, new org.eclipse.draw2d.geometry.Point(point.x, point.y));
		return getBasicSelector().click(canvas, toClick.x, toClick.y, SWT.BUTTON1);
	}
	
	
	private BasicWidgetSelector getBasicSelector() {
		return _selector;
	}


	public Rectangle getBounds(final FigureCanvas parent, IFigure figure) {
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(figure);

		// from the top of the canvas to the figure  
		Rectangle figureBounds = toFigureCanvas(figure);

		// from top of canvas to top of scrollable viewport 
		int scrolledOffsetY = getScrolledOffsetY(parent);
		int scrolledOffsetX = getScrolledOffsetX(parent);

		// from the top of viewport to top of item
		Rectangle adjustedFigureBounds = new Rectangle(figureBounds.x
				+ scrolledOffsetX, figureBounds.y - scrolledOffsetY,
				figureBounds.width, figureBounds.height);

		// Place the parent canvas on the screen
		final Rectangle[] canvasBounds = new Rectangle[1];
		Runnable r = new Runnable() {
			public void run() {
				canvasBounds[0] = WidgetLocator.getBounds(parent, false);
			}
		};
		ensureRunOnUIThread(r);

		Rectangle resultingBounds = new Rectangle(canvasBounds[0].x
				+ adjustedFigureBounds.x, canvasBounds[0].y
				+ adjustedFigureBounds.y, adjustedFigureBounds.width,
				adjustedFigureBounds.height);

		return resultingBounds;
	}

	
	private void ensureRunOnUIThread(Runnable r) {
		Display.getDefault().syncExec(r);
	}

	public int getScrolledOffsetX(FigureCanvas canvas) {
		TestCase.assertNotNull(canvas);

		ScrollableTester scrollableTester = new ScrollableTester();

		ScrollBar scrollBar = scrollableTester.getHorizontalBar(canvas);

		ScrollBarTester scrollBarTester = new ScrollBarTester();
		return scrollBarTester.getSelection(scrollBar);
	}


	public int getScrolledOffsetY(FigureCanvas canvas) {
		TestCase.assertNotNull(canvas);

		ScrollableTester scrollableTester = new ScrollableTester();
		scrollableTester.getClientArea(canvas);

		ScrollBar scrollBar = scrollableTester.getVerticalBar(canvas);

		ScrollBarTester scrollBarTester = new ScrollBarTester();
		return scrollBarTester.getSelection(scrollBar);
	}


	public Point scrollForClick(final FigureCanvas parent,
			final org.eclipse.draw2d.geometry.Point clickPoint) {
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				clickPoint.x, clickPoint.y, 1, 1);
		return scrollForClick(parent, bounds);
	}


	public Point scrollForClick(final FigureCanvas parent,
			final org.eclipse.draw2d.geometry.Rectangle bounds) {
		final org.eclipse.draw2d.geometry.Point clickPoint = bounds.getCenter();

		// adjust the point to click based upon the figure canvas viewport location
		final Point[] p = new Point[1];
		Runnable runner = new Runnable() {
			public void run() {
				Viewport viewport = parent.getViewport();

				// before clicking, make sure the point we want to click is visible
				org.eclipse.draw2d.geometry.Point viewportLoc = viewport
						.getViewLocation().getCopy();
				Dimension viewportSize = viewport.getSize().getCopy();
				if (bounds.x < viewportLoc.x
						|| bounds.x + bounds.width > viewportLoc.x
								+ viewportSize.width
						|| bounds.y < viewportLoc.y
						|| bounds.y + bounds.height > viewportLoc.y
								+ viewportSize.height) {

					// try to scroll so that the point is in the view - for each axis that
					// is not visible in the viewport, try to scroll so that the click
					// point is centered; if that is not possible, scroll as far as possible
					// based on the size of the viewport contents.
					Dimension contentsSize = viewport.getContents().getSize()
							.getCopy();
					int vpx = viewportLoc.x;
					int vpy = viewportLoc.y;
					int vpHeight = viewportSize.height;
					int vpWidth = viewportSize.width;

					int dx = 0;
					if (bounds.x + bounds.width > vpx + vpWidth) {
						// click point is to the right of the viewport - scroll right
						// by the amount past the viewport plus half the viewport
						// width, or as far as possible
						int diff = (bounds.x + bounds.width) - (vpx + vpWidth)
								+ (vpWidth / 2);
						dx = Math.min(contentsSize.width - (vpx + vpWidth),
								diff);
					} else if (bounds.x < vpx) {
						// click point is to the left of the viewport - scroll
						// left by the amount past the viewport plus half the
						// viewport width, or as far as possible
						int diff = (vpx - bounds.x) + (vpWidth / 2);
						dx = -1 * Math.min(vpx, diff);
					}

					int dy = 0;
					if (bounds.y + bounds.height > vpy + vpHeight) {
						// click point is below the viewport - scroll down
						// by the amount past the viewport plus half the viewport
						// height, or as far as possible
						int diff = (bounds.y + bounds.height)
								- (vpy + vpHeight) + (vpHeight / 2);
						dy = Math.min(contentsSize.height - (vpy + vpHeight),
								diff);
					} else if (bounds.y < vpy) {
						// click point is above the viewport - scroll up
						// by the amount past the viewport plus half the
						// viewport height, or as far as possible
						int diff = (vpy - bounds.y) + (vpHeight / 2);
						dy = -1 * Math.min(vpy, diff);
					}

//					TestCase
//							.assertTrue(
//									"click point was not in viewport, should have to scroll somewhere",
//									dx != 0 || dy != 0);
					viewportLoc.translate(dx, dy);
					viewport.setViewLocation(viewportLoc);
//					TestCase.assertEquals(
//							"viewport was not scrolled the expected amount",
//							viewportLoc, viewport.getViewLocation());
				}
				p[0] = new Point(clickPoint.x - viewportLoc.x, clickPoint.y
						- viewportLoc.y);
			}
		};
		ensureRunOnUIThread(runner);
		TestCase.assertNotNull(p[0]);

		return p[0];
	}


	public Rectangle toFigureCanvas(IFigure figure) {
		TestCase.assertNotNull(figure);

		org.eclipse.draw2d.geometry.Rectangle draw2dBounds = getBounds(figure);

		Rectangle rectangleBounds = new Rectangle(draw2dBounds.x,
				draw2dBounds.y, draw2dBounds.width, draw2dBounds.height);

		IFigure parent = figure.getParent();
		if ((draw2dBounds.x == 0) && (draw2dBounds.y == 0) && (parent != null)) {
			Rectangle parentBounds = toFigureCanvas(parent);

			rectangleBounds.x += parentBounds.x;
			rectangleBounds.y += parentBounds.y;
		}

		return rectangleBounds;
	}


	public Rectangle toFigureCanvas(Label label) {
		TestCase.assertNotNull(label);

		org.eclipse.draw2d.geometry.Rectangle draw2dBounds = label
				.getTextBounds();

		Rectangle rectangleBounds = new Rectangle(draw2dBounds.x,
				draw2dBounds.y, draw2dBounds.width, draw2dBounds.height);

		return rectangleBounds;
	}

	
	/**
	 * NOTE: x,y offset FROM CENTER!
	 * @param target
	 * @param x
	 * @param y
	 * @throws ParentCanvasNotFoundException 
	 */
	public void clickFigure(IFigure target, int x, int y) throws ParentCanvasNotFoundException {
		clickFigure(1, target, x, y);
	}

	/**
	 * NOTE: x,y offset FROM CENTER!
	 * @param clicks
	 * @param target
	 * @param x
	 * @param y
	 * @throws ParentCanvasNotFoundException 
	 */
	public void clickFigure(int clicks, IFigure target, int x, int y) throws ParentCanvasNotFoundException {
		TestCase.assertNotNull(target);

		FigureCanvas parent = findParentCanvas(target);		
		if (parent == null)
			throw ParentCanvasNotFoundException.forFigure(target);
		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		Rectangle figureBounds = toFigureCanvas(target);
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				figureBounds.x+x, figureBounds.y+y, figureBounds.width,
				figureBounds.height);
		clickFigureAtPoint(parent, bounds, clicks);
	}
	
	
	
	public void clickFigure(IFigure target) {
		clickFigure(1, target);
	}

	public void clickFigure(int clicks, IFigure target) {
		FigureCanvas parent = findParentCanvas(target);
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		Rectangle figureBounds = toFigureCanvas(target);
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				figureBounds.x, figureBounds.y, figureBounds.width,
				figureBounds.height);
		clickFigureAtPoint(parent, bounds, clicks);
	}

	private FigureCanvas findParentCanvas(IFigure target) {
		return Draw2DFinder.getDefault().findParentCanvas(getUI(), target);
	}

	public void mouseMove(IFigure target, IClickDescription click) {
		FigureCanvas parent = findParentCanvas(target);
		TestCase.assertNotNull(parent);
		TestCase.assertNotNull(target);

		// Get the draw2d bounds and convert them to eclipse swt screen
		// coordinates
		Rectangle figureBounds = toFigureCanvas(target);
		org.eclipse.draw2d.geometry.Rectangle bounds = new org.eclipse.draw2d.geometry.Rectangle(
				figureBounds.x + click.x(), figureBounds.y + click.y(), figureBounds.width,
				figureBounds.height);
		
		Point p = scrollForClick(parent, bounds);
		getSelector().mouseMove(parent, p.x, p.y);
	}




}
