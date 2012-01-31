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
package com.windowtester.runtime.swt.internal.dnd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import abbot.swt.Robot;
import abbot.tester.swt.WidgetLocator;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.internal.factory.WTRuntimeManager;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.UIContextSWT;
import com.windowtester.runtime.swt.internal.hover.IHoverInfo;
import com.windowtester.runtime.swt.internal.operation.SWTDisplayLocation;
import com.windowtester.runtime.swt.internal.operation.SWTLocation;
import com.windowtester.runtime.swt.internal.operation.SWTMouseOperation;
import com.windowtester.runtime.swt.internal.reveal.RevealStrategyFactory;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;




/**
 * A UIContext helper class to manage drag and drop operations.
 * <p>
 * A few things worth noting.  First, DragAndDropHelper posts primitive events rather than delegating to the UIContext.
 * This is important because:
 * <ol>
 *    <li> We do not want conditions to be checked mid drag and 
 *    <li> nor do we want any syncExecs to be called once the drag (mouseDown) has begun 
 *         (such syncExecs would lead to deadlock). 
 * </ol>
 * Second, it is the UIContext's responsibility to track the drag source.  
 * The WindowTester DND API uses <em>implicit</em> drag source identification: it is
 * the user's responsibility to move the mouse to the source of the drag by calling 
 * one of the family of {@link IUIContext#mouseMove(com.windowtester.runtime.locator.ILocator)} methods.
 * 
 * The UIContext caches these mouse moves (and potential drag sources) by calling 
 * {@link DragAndDropHelper#setDragSourceLocation(Point)} from within
 * {@link UIContextSWT#mouseMove(int, int)}.  
 * <p>
 * Notice that a source is set <em>on all moves</em>
 * whether they are followed by a drag event or not.  Source location is only
 * used in cases of drag events.  (Drag source is roughly equivalent to "lastMouseLocation").
 *
 */
public class DragAndDropHelper {

	/** OS-identifying flag */
	private static final boolean IS_LINUX = SWT.getPlatform().equals("gtk");

	/** Amount to wiggle to trigger drag/drop events in the native OS */
	private static final int WIGGLE = IS_LINUX ? 11 : 1;
	
	private static final Robot wiggleBot = new Robot();
	
	/** A back-pointer to the host UIContext */
	private final UIContextSWT ui;
	
	/** An OS-specific DND strategy */
	private IDNDAction _dndStrategy;
	
	/**
	 * Create an instance.
	 * @param ui the host UIContext
	 */
	public DragAndDropHelper(UIContextSWT ui) {
		this.ui = ui;
	}
	
	
	///////////////////////////////////////////////////////////////////
	//
	// Accessors.
	//
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Get the associated UIDriver for posting low-level UI events.
	 * @return the associated UIDriver instance
	 */
	protected UIDriver getDriver() {
		return ui.getDriver();
	
	}
		
	/**
	 * Get the OS-Specific mouse mask associated with drag gestures.
	 * @return the drag/drop mouse mask
	 */
	protected int getDragMouseMask() {
		/*
		 * TODO: ultimately, this should come from a service (in UIDriver?) that handles OS-specific details 
		 */
		return SWT.BUTTON1;
	}
	
	/**
	 * Get the drag source location.
	 * @return a point describing the drag source.
	 */
	protected Point getDragSourceLocation() {
		return getDragSource().getLocation();
	}
	
	/**
	 * Get the drag source location.
	 * @return a point describing the drag source.
	 */
	protected IHoverInfo getDragSource() {
		return getDriver().getCurrentMouseHoverInfo();
	}
	

	///////////////////////////////////////////////////////////////////
	//
	// Drag/drop API support.
	//
	// The key to this support is that NO syncExecs are called once the 
	// drag has begun (hence the calculation of the target before mousDown)
	//
	///////////////////////////////////////////////////////////////////

	/**
	 * Perform a drag to this widget at this offset using the source point retrieved
	 * from {@link #getDragSource()}.
	 * <p>
	 * Note that all widget relative dragTos ultimately call this one.
	 * @param target the target of the drop
	 * @param x the x offset
	 * @param y the y offset
	 */
	public Widget dragTo(Widget target, int x, int y) {
		//check to see if target is visible and make it so if not
		handleReveal(target, x, y);
		Point dest = getLocation(target);
		dragTo(dest.x+x, dest.y+y);
		return target;
	}
	

	/**
	 * Perform a drag operation to the item in this widget identified
	 * by path.
	 * @param w the parent widget (e.g., Tree)
	 * @param path the path string (e.g., "parent/child")
	 * @param x the x offset
	 * @param y the y offset
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 */
	public Widget dragTo(Widget w, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		Widget target = handleReveal(w, path, x, y);
		Point dest = getLocation(target);
		dragTo(dest.x+x, dest.y+y);		
		return target;
	}




	/**
	 * Perform a drag to the center of this widget using the source point retrieved
	 * from {@link #getDragSource()}.
	 * @param target the target of the drop
	 */
	public Widget dragTo(Widget target) {
		Rectangle rect = getBounds(target);
		dragTo(target, rect.width/2, rect.height/2);
		return target;
	}

	/**
	 * Perform a drag to this absolute x,y coordinate using the source point retrieved
	 * from {@link #getDragSource()}.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 */
	public void dragTo(int x, int y) {
		dragTo(new Point(x, y));
	}

	/**
	 * Perform a drag to this destination using the source point retrieved
	 * from {@link #getDragSource()}.
	 * <p>
	 * All drag operations ultimately call this one.
	 * <p>
	 * Note: all reveal operations are expected to have been done before calling this method.
	 * @param dest the target of the drag
	 * @throws IllegalStateException if no drag source is set
	 */
	public void dragTo(final Point dest) {
		
		Point src = getDragSourceLocation();
		if (src == null)
			throw new IllegalStateException("no drag source set in drag/drop operation");
		//delegate to OS-specific strategy
		getDNDStrategy().doDND(src, dest);	
	}

	
	///////////////////////////////////////////////////////////////////
	//
	// Internal
	//
	///////////////////////////////////////////////////////////////////
	
	private IDNDAction getDNDStrategy() {
		if (_dndStrategy == null) {
			if (isLinux())
				_dndStrategy = new LinuxDNDAction();
			else
				_dndStrategy = new WindowsDNDAction();
		}
		return _dndStrategy;
	}

	private void syncExec(Runnable runnable) {
		ui.getDisplay().syncExec(runnable);
	}
	
	private void pause(int ms) {
		ui.pause(ms);
	}

	/**
	 * Wait for the ui thread to be idle.
	 */
	private void waitForIdle() {
//		_ui.waitForIdle();
		new SWTIdleCondition(ui.getDisplay()).waitForIdle();
	}

	/**
	 * Test to see if we're running on GTK-linux.
	 */
	private boolean isLinux() {
		return IS_LINUX;
	}

	/**
	 * Wiggle around this point.  Required to ensure drag and drop gestures
	 * are recognized by the OS.
	 * @param pt the point around which to wiggle
	 */
	private void wiggle(Point pt) {
		moveTo(getWiggle(pt));
		//TODO: consider moving back to the origin?
	}

	/**
	 * Wiggle around this point.  Required to ensure drag and drop gestures
	 * are recognized by the OS.
	 * @param pt the point around which to wiggle
	 */
	private void awtWiggle(Point pt) {
		Point to = getWiggle(pt);
		wiggleBot.mouseMove(to.x, to.y);
	}

	/**
	 * Get a point offset from this one that is sufficient to signal a 
	 * drag or drop gesture.
	 * @param pt the target point
	 */
	private Point getWiggle(Point pt) {
		int wx = (pt.x > WIGGLE) ? pt.x-WIGGLE : pt.x +WIGGLE;
		return new Point(wx, pt.y);
	}

	
	/**
	 * Ensure that the target is visible, revealing if necessary.
	 * @param target
	 * @param x
	 * @param y
	 */
	private Widget handleReveal(Widget target, int x, int y) {
		Widget revealed = RevealStrategyFactory.getRevealer(target).reveal(target, x, y);
		//reposition the mouse over our drag source:
		moveTo(getDragSourceLocation()); //ideally this would only happen in case of a reveal; for now it always happens	
		return revealed;
	}
	
	private Widget handleReveal(Widget target, String path, int x, int y) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		Widget revealed = RevealStrategyFactory.getRevealer(target).reveal(target, path, x, y);
		//reposition the mouse over our drag source:
		moveTo(getDragSourceLocation()); //ideally this would only happen in case of a reveal; for now it alwasy happens
		return revealed;
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////
	//
	// Primitive event generation
	//
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Move the mouse to this point using primitive driver operations.
	 * @param dest the destination point
	 */
	private void moveTo(Point dest) {
		getDriver().mouseMove(dest.x, dest.y);
	}

	/**
	 * Start the drag operation.
	 */
	private void startDrag() {
		getDriver().mouseDown(getDragMouseMask());
	}
	
	/**
	 * Finish the drag operation.
	 */
	private void finishDrag() {
		//a slight pause before dropping
		UIDriver.pause(500); //NOTE: using driver and NOT context here
		//drop
		getDriver().mouseUp(getDragMouseMask());
	}
	
	
	///////////////////////////////////////////////////////////////////
	//
	// Location calculating helpers.
	//
	///////////////////////////////////////////////////////////////////
	
	/**
	 * Get the absolute location of this widget.
	 * @param w - the widget in question
	 * @return the widget's point in space
	 */
	private static Point getLocation(final Widget w) {
		final Point[] point = new Point[1];
		// TODO[pq]: getLocation should be in the widget ref
		w.getDisplay().syncExec(new Runnable(){
			public void run(){
				point[0] = WidgetLocator.getLocation(w);		
			}
		});
		return point[0];
	}
	
	/**
	 * Get this target widget's bounds.
	 * @param target the widget in question
	 * @return the widget's bounds
	 */
	private static Rectangle getBounds(Widget target) {
		// TODO[pq]: this reference should be pre-calculated and cached
		ISWTWidgetReference<?> ref = (ISWTWidgetReference<?>) WTRuntimeManager.asReference(target);
		return ref.getDisplayBounds();
	}	
	
	interface IDNDAction {
		void doDND(Point src, Point dest);
	}
	
	class WindowsDNDAction implements IDNDAction {
		public void doDND(Point src, Point dest) {
//			//start dragging
//			startDrag();
//			
//			//wiggle to ensure drag gesture is recognized
//			wiggle(src);
//			
//			//moveTo and wiggle to ensure drop gesture is recognized
//			wiggle(dest);
//			
//			//settle on target
//			moveTo(dest);
//			
//			//end drag
//			finishDrag();
			
			SWTLocation start = new SWTDisplayLocation().offset(src);
			SWTLocation end = new SWTDisplayLocation().offset(dest);
			new SWTMouseOperation(getDragMouseMask()).at(start).dragTo(end).execute();
		}
	}
	
	class LinuxDNDAction implements IDNDAction {
		
		final int NUM_DROP_WIGGLES = 4;
		final int DROP_PAUSE       = 250;
		public void doDND(final Point src, final Point dest) {

			/*
			 * Start drag
			 */
			syncExec(new Runnable() {
				public void run() {
					//start dragging
					startDrag();
					//wiggle to ensure drag gesture is recognized
					wiggle(src);	
				}
			});
			waitForIdle();
			
			/*
			 * Initiate drop.
			 * 
			 * N.B.: Linux is very finnicky and occasionally does not
			 * register the drop event.  To attempt to address this we
			 * hover, pause and wiggle a number of times before making the
			 * drop.  This _seems_ to do the trick.  
			 * Caveat: the number and pause amount constants are derived
			 * from experimentation.  
			 */			
			for (int i=0; i <= NUM_DROP_WIGGLES; ++i) {
				awtWiggle(dest);
				waitForIdle();	
				pause(DROP_PAUSE);
			}

			//end drag
			syncExec(new Runnable() {
				public void run() {
					finishDrag();	
				}
			});
			waitForIdle();
		}
		
	}	
	
}
