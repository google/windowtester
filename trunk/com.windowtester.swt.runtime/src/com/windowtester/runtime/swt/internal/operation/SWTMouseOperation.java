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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.swt.graphics.Point;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * Perform a click or drag operation by pushing mouse move, mouse down, and mouse up
 * operations onto the OS event queue.
 */
public class SWTMouseOperation extends SWTOperation
{
	/**
	 * The mouse button ({@link WT#BUTTON1}, ...) and any modifiers ({@link WT#SHIFT},
	 * {@link WT#CTRL}, ...)
	 */
	private final int accelerator;

	/**
	 * The starting location or <code>null</code> if the mouse operation should occur at
	 * the current mouse location
	 */
	private SWTLocation start = null;

	/**
	 * The ending location or <code>null</code> if the mouse up should occur in the same
	 * location as the mouse down
	 */
	private SWTLocation end = null;

	/**
	 * The number of mouse clicks or zero if the mouse should only be moved
	 */
	private int clickCount = 1;

	/**
	 * Construct a new click operation that clicks the mouse at the current mouse
	 * location.
	 * <p>
	 * WARNING! Use {@link SWTShowMenuOperation} for context clicks (right mouse button) and
	 * menu selection
	 * 
	 * @param accelerator the button to be clicked such as {@link WT#BUTTON1} or
	 *            {@link WT#BUTTON2} bit-wise and with the modifier keys such as
	 *            {@link WT#SHIFT} and {@link WT#CTRL}
	 */
	public SWTMouseOperation(int accelerator) {
		this.accelerator = accelerator;
	}

	/**
	 * Queue a step that waits for a particular menu item to become enabled. Typically,
	 * this is called *before* calling {@link #click(int, SWTLocation, boolean)}
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTMouseOperation().waitForEnabled(...).at(...).execute();</code>
	 */
	public SWTMouseOperation waitForEnabled(SWTWidgetReference<?> widgetRef) {
		return (SWTMouseOperation) super.waitForEnabled(widgetRef);
	}

	/**
	 * Set the location at which the click or drag is to start. If this method is not
	 * called, then the mouse down occurs at the current mouse location.
	 * 
	 * @param location the location where the mouse down should occur
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTMouseClickOperation(WT.BUTTON1).at(10, 20).execute();</code>
	 */
	public SWTMouseOperation at(SWTLocation location) {
		this.start = location;
		return this;
	}

	/**
	 * Set the location to which the mouse is dragged before the mouse up event. If this
	 * method is not called, then the mouse up occurs at the same location as the mouse
	 * down (e.g. a click).
	 * 
	 * @param location the location where the mouse up should occur
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTMouseClickOperation(WT.BUTTON1).at(10, 20).execute();</code>
	 */
	public SWTOperation dragTo(SWTLocation location) {
		this.end = location;
		return this;
	}

	/**
	 * Set the number of times the mouse is clicked in rapid succession. If you call this
	 * with a value of zero, then the mouse is moved but not clicked (no mouse down or
	 * mouse up events are generated).
	 * 
	 * @param clickCount the click count (0 = move only, 1 = single click, 2 = double
	 *            click, ...)
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTMouseClickOperation(WT.BUTTON1).at(10, 20).execute();</code>
	 */
	public SWTOperation count(int clickCount) {
		this.clickCount = clickCount;
		return this;
	}
	
	/**
	 * Perform the mouse operation
	 */
	public void execute() {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				queueMouseEventsInUI();
			}
		});
		super.execute();
	}

	//=======================================================================
	// Internal

	/**
	 * Queue the mouse events for the operation
	 * 
	 * @return <code>true</code> if successful
	 */
	protected void queueMouseEventsInUI() throws Exception {
		int button = getButton(accelerator);
		
		Point startLoc;
		if (start != null) {
			startLoc = start.location();
			// Linux needs a wiggle and not just a move
			queueMouseWiggle(startLoc);
		}
		else {
			// TODO: Get the current mouse location and wiggle
			startLoc = null;
		}

		if (clickCount > 0) {
			queueModifierKeysDown(accelerator);
			queueMouseDown(button, startLoc);

			for (int i = 1; i < clickCount; i++) {
				queueMouseUp(button, startLoc);
				queueMouseDown(button, startLoc);
			}

			Point endLoc = end != null ? end.location() : startLoc;
			// Linux needs a mouse move even for a mouse click
			// and it does not hurt on the Windows side
			if (endLoc != null)
				queueMouseMove(endLoc);

			queueMouseUp(button, endLoc);
			queueModifierKeysUp(accelerator);

			// Linux (and Mac?) needs a wiggle to push through events
			// TODO: If endLoc is null, get the current mouse location and wiggle
			if (endLoc != null)
				queueMouseWiggle(endLoc);
		}
	}
}
