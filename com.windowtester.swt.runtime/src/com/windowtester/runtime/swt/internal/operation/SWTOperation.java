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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.WT;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.swt.internal.state.MouseConfig;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.SWTUIException;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * The root of a class hierarchy used to wait for UI thread to be in the appropriate
 * state, gather widget information on the UI thread, and perform the operation.
 * Operations are comprised of individual "steps" such as pushing an event on the OS event
 * queue, sending a notification directly to a widget in SWTBot style, or performing an
 * arbitrary widget manipulation such as setting the expansion state of a tree.
 */
public abstract class SWTOperation
{
	/**
	 * The maximum number of milliseconds to wait for the UI to be in the appropriate
	 * state or to execute a particular step
	 */
	private static final int MAX_RETRY_PERIOD = 30000;

	/**
	 * The display for this operation.
	 */
	protected static final DisplayReference displayRef = DisplayReference.getDefault();
	// TODO [Dan] Initialize this via a constructor

	/**
	 * The keyboard modifier keys
	 */
	private static final int[] MODIFIERS = new int[]{
		WT.ALT, WT.SHIFT, WT.CTRL, WT.COMMAND
	};

	/**
	 * The queue used to hold step waiting to be executed.<br/>
	 * Synchronize against this field before accessing it.
	 */
	private final List<Step> queue = new ArrayList<Step>(10);

	/**
	 * The starting time used by {@link #execute()} and others determine when the retry
	 * period has ended, or zero if {@link #execute()} has not yet been called.
	 */
	private long retryStartTime = 0;

	/**
	 * The callable used to execute steps on the UI thread. Returns <code>true</code> if
	 * execution is complete, or <code>false</code> if the callable should be called again
	 * to finish executing after a brief delay up to the maximum number of retries.
	 */
	private final Callable<Boolean> callable = new Callable<Boolean>() {
		public Boolean call() throws Exception {
			return executeInUI();
		}
	};

	private int waitForEnabled = 0;

	//=======================================================================
	// Processing

	/**
	 * Execute the operation by calling {@link #executeInUI()} on the UI thread to perform
	 * the operation. This method does not return until all queued steps have been
	 * executed, but that does not mean that all events pushed on the OS event queue will
	 * have been processed.
	 * 
	 * @throws RuntimeException if there is an exception when executing the various steps
	 *             comprising the operation
	 * @throws IllegalStateException if this method has already been called
	 * @throws WaitTimedOutException if the UI thread does not execute the callable with
	 *             specified number of milliseconds
	 */
	public void execute() {
		if (retryStartTime != 0)
			throw new IllegalStateException("execute() has already been called: " + this);
		resetRetryStartTime();
		SWTOperationStepException cause = null;
		while (System.currentTimeMillis() - retryStartTime < MAX_RETRY_PERIOD) {
			try {
				if (executeCallable(MAX_RETRY_PERIOD)) {
					retryStartTime = 0;
					return;
				}
			}
			catch (SWTUIException e) {
				if (e.getCause() instanceof SWTOperationStepException)
					cause = (SWTOperationStepException) e.getCause();
				else
					throw e;
			}
			catch (SWTOperationStepException e) {
				cause = e;
			}
			try {
				// Sleep just long enough for the UI thread to gain the upper hand
				// and process some OS events
				Thread.sleep(10);
			}
			catch (InterruptedException e) {
				// Ignored... fall through
			}
		}
		throw new WaitTimedOutException("Max retry period (" + MAX_RETRY_PERIOD + " milliseconds) exceeded. ", cause);
	}

	/**
	 * Reset the starting time used by {@link #execute()} and others determine when the
	 * retry period has ended.
	 */
	private void resetRetryStartTime() {
		retryStartTime = System.currentTimeMillis();
	}

	/**
	 * Execute the callable on the UI thread. This method is repeatedly called by
	 * {@link #execute()} until this method returns <code>true</code> or the
	 * {@link #MAX_RETRY_PERIOD} has expired.
	 * 
	 * @param maxWaitTime the maximum wait time before abort
	 * @return <code>true</code> if execution is complete, or <code>false</code> if
	 *         {@link #executeInUI()} should be called again to finish executing after a
	 *         brief delay up to the maximum number of retries.
	 * @throws SWTOperationStepException if step execution failed but the step should be
	 *             re-executed after a brief delay up to the maximum number of retries.
	 * @throws IllegalStateException if the receiver is already executing
	 * @throws WaitTimedOutException if the UI thread does not execute the callable with
	 *             specified number of milliseconds
	 */
	protected boolean executeCallable(int maxWaitTime) {
		return displayRef.execute(callable, maxWaitTime);
	}

	/**
	 * Perform the operation by pulling steps off the {@link #queue} and executing them.
	 * This method is repeatedly called on the UI thread from {@link #callable} until this
	 * method returns <code>true</code> or the {@link #MAX_RETRY_PERIOD} has expired.
	 * 
	 * @return <code>true</code> if execution is complete, or <code>false</code> if
	 *         {@link #executeInUI()} should be called again to finish executing after a
	 *         brief delay up to the maximum number of retries.
	 * @throws SWTOperationStepException if step execution failed but the step should be
	 *             re-executed after a brief delay up to the maximum number of retries.
	 */
	protected boolean executeInUI() throws Exception {
		while (true) {

			// Pop the next step off the queue

			Step currentStep;
			synchronized (queue) {
				if (queue.size() == 0)
					return true;
				currentStep = queue.remove(0);
			}

			// If the step is null, then return false
			// so that the UI thread is given a chance to process the events currently in the OS event queue
			// and so that this method should be called again to process subsequent steps

			if (currentStep == null)
				//throw new SWTOperationStepException("Brief wait for UI thread to process OS events and other asyncExec");
				return false;

			// Execute the current step
			// If the execution fails, then save the step for later 
			// and let the UI thread process events already in the OS event queue

			try {
				currentStep.executeInUI();
			}
			catch (SWTOperationStepException e) {
				synchronized (queue) {
					queue.add(0, currentStep);
				}
				throw e;
			}

			// Step executed successfully, so reset retry start time and loop for more steps

			resetRetryStartTime();
		}
	}

	//=======================================================================
	// Steps

	/**
	 * Operations are comprised of individual "steps" such as pushing an event on the OS
	 * event queue, sending a notification directly to a widget in SWTBot style, or
	 * performing an arbitrary widget manipulation such as setting the expansion state of
	 * a tree.
	 */
	protected interface Step
	{
		/**
		 * Called by {@link SWTOperation#executeInUI()} on the UI thread to perform a step
		 * in the operation.
		 * 
		 * @throws SWTOperationStepException if step execution failed but the step should
		 *             be re-executed after a brief delay up to the maximum number of
		 *             retries.
		 */
		void executeInUI() throws Exception;
	}

	/**
	 * Subclasses call this method to queue the specified step in the operation. Calling
	 * this method with a <code>null</code> argument causes subsequent steps to be
	 * executed in a separate call to {@link Display#asyncExec(Runnable)} from the prior
	 * steps, allowing the UI thread to process OS events and other
	 * {@link Display#asyncExec(Runnable)} calls.
	 * 
	 * @param step the step to be executed when {@link #executeInUI()} is called or
	 *            <code>null</code> if there should be a "break" between the prior steps
	 *            and the following steps
	 */
	protected void queueStep(Step step) {
		synchronized (queue) {
			queue.add(step);
		}
	}

	//=======================================================================
	// SWTBot style steps

	/**
	 * Queue an event for the specified widget. This event will be sent directly to the
	 * specified widget SWTBot style rather than through the OS event queue. To insert a
	 * "break" between steps where the UI thread can process other events already on the
	 * OS event queue and other calls to {@link Display#asyncExec(Runnable)}, see
	 * {@link #queueStep(Step)}.
	 */
	protected void queueWidgetEvent(Widget widget, int eventType) {
		queueWidgetEvent(widget, eventType, 0);
	}

	/**
	 * Queue an event for the specified widget. This event will be sent directly to the
	 * specified widget SWTBot style rather than through the OS event queue. To insert a
	 * "break" between steps where the UI thread can process other events already on the
	 * OS event queue and other calls to {@link Display#asyncExec(Runnable)}, see
	 * {@link #queueStep(Step)}.
	 */
	protected void queueWidgetEvent(Widget widget, int eventType, int eventDetail) {
		queueWidgetEvent(widget, null, eventType, eventDetail);
	}
	
	/**
	 * Queue an event for the specified widget. This event will be sent directly to the
	 * specified widget SWTBot style rather than through the OS event queue. To insert a
	 * "break" between steps where the UI thread can process other events already on the
	 * OS event queue and other calls to {@link Display#asyncExec(Runnable)}, see
	 * {@link #queueStep(Step)}.
	 */
	protected void queueWidgetEvent(Widget widget, Widget item, int eventType, int eventDetail) {
		final Event event = new Event();
		event.widget = widget;
		event.item   = item;
		event.display = widget.getDisplay();
		event.type = eventType;
		event.detail = eventDetail;

		// First send the event directly to the widget using asyncExec
		// so that any widget listeners will not block us

		queueStep(new Step() {
			public void executeInUI() {
				displayRef.getDisplay().asyncExec(new Runnable() {
					public void run() {
						event.time = (int) System.currentTimeMillis();
						event.widget.notifyListeners(event.type, event);
					}
				});
			}
		});

		// Then queue a "break" so that we hop off the UI thread
		// allowing the asyncExec to send the event to the widget
		// before we process any subsequent steps

		queueStep(null);
	}
	
	
	
	//=======================================================================
	// OS Event steps

	/**
	 * Subclasses call this method to queue the specified event to be posted to the OS
	 * event queue. To insert a "break" between steps where the UI thread can process
	 * other events already on the OS event queue and other calls to
	 * {@link Display#asyncExec(Runnable)}, see {@link #queueStep(Step)}.
	 * 
	 * @param event the event to be posted (not <code>null</code>)
	 */
	protected void queueOSEvent(final Event event) {
		if (event == null)
			throw new IllegalArgumentException();
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (!displayRef.getDisplay().post(event))
					throw new SWTOperationStepException("Failed to post OS event: " + event);
			}
		});
	}

	/**
	 * Examine the accelerator bits to determine if any modifier keys (Shift, Alt,
	 * Control, Command) are specified and queue zero or more key down events for those
	 * modifier keys.
	 * 
	 * @param accelerator the accelerator that may specify zero or more modifier keys<br/>
	 *            ({@link WT#SHIFT} , {@link WT#CTRL}, ...)
	 */
	protected void queueModifierKeysDown(int accelerator) {
		for (int i = 0; i < MODIFIERS.length; i++) {
			int mod = MODIFIERS[i];
			if ((accelerator & mod) == mod)
				queueKeyCodeDown(mod);
		}
	}

	/**
	 * Examine the accelerator bits to determine if any modifier keys (Shift, Alt,
	 * Control, Command) are specified and queue zero or more key up events for those
	 * modifier keys.
	 * 
	 * @param accelerator the accelerator that may specify zero or more modifier keys<br/>
	 *            ({@link WT#SHIFT} , {@link WT#CTRL}, ...)
	 */
	protected void queueModifierKeysUp(int accelerator) {
		for (int i = MODIFIERS.length - 1; i >= 0; i--) {
			int mod = MODIFIERS[i];
			if ((accelerator & mod) == mod)
				queueKeyCodeUp(mod);
		}
	}

	/**
	 * Queue key down event for the specified keyCode
	 * 
	 * @param keyCode the code for the key down to be queued such as {@link WT#HOME},
	 *            {@link WT#CTRL}, {@link WT#SHIFT}, {@link WT#END}
	 */
	protected void queueKeyCodeDown(int keyCode) {
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.keyCode = keyCode;
		queueOSEvent(event);
	}

	/**
	 * Queue key up event for the specified keyCode
	 * 
	 * @param keyCode the code for the key up to be queued such as {@link WT#HOME},
	 *            {@link WT#CTRL}, {@link WT#SHIFT}, {@link WT#END}
	 */
	protected void queueKeyCodeUp(int keyCode) {
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.keyCode = keyCode;
		queueOSEvent(event);
	}

	/**
	 * Queue key down event for the specified character
	 * 
	 * @param ch the character
	 */
	protected void queueCharDown(char ch) {
		Event event = new Event();
		event.type = SWT.KeyDown;
		event.character = ch;
		queueOSEvent(event);
	}

	/**
	 * Queue key up event for the specified character
	 * 
	 * @param ch the character
	 */
	protected void queueCharUp(char ch) {
		Event event = new Event();
		event.type = SWT.KeyUp;
		event.character = ch;
		queueOSEvent(event);
	}

	/**
	 * Queue a mouse down event. It is recommended to call {@link #queueMouseMove(Point)}
	 * before calling this method to move the mouse to the location where the click will
	 * occur in addition to passing the mouse down location to this method.
	 * 
	 * @param button the {@link Event#button} button (1, 2, or 3). Use
	 *            {@link #getButton(int)} to convert {@link WT#BUTTON1},
	 *            {@link WT#BUTTON2}, etc to button (1, 2, or 3)
	 * @param pt the mouse down location or <code>null</code> if the mouse down location
	 *            is unspecified. Use {@link SWTLocation} to convert widget relative
	 *            coordinates to global coordinates
	 */
	protected void queueMouseDown(int button, Point pt) {
		Event event;
		event = new Event();
		event.type = SWT.MouseDown;
		event.button = button;
		if (pt != null) {
			event.x = pt.x;
			event.y = pt.y;
		}
		queueOSEvent(event);
	}

	/**
	 * Queue a mouse up event
	 * 
	 * @param button the {@link Event#button} button (1, 2, or 3). Use
	 *            {@link #getButton(int)} to convert {@link WT#BUTTON1},
	 *            {@link WT#BUTTON2}, etc to button (1, 2, or 3)
	 * @param pt the mouse down location or <code>null</code> if the mouse up location is
	 *            unspecified. Use {@link SWTLocation} to convert widget relative
	 *            coordinates to global coordinates
	 */
	protected void queueMouseUp(int button, Point pt) {
		Event event;
		event = new Event();
		event.type = SWT.MouseUp;
		event.button = button;
		if (pt != null) {
			event.x = pt.x;
			event.y = pt.y;
		}
		queueOSEvent(event);
	}

	/**
	 * Queue a mouse move event
	 * 
	 * @param pt the location to which the mouse should be moved. Use {@link SWTLocation}
	 *            to convert widget relative coordinates to global coordinates
	 */
	protected void queueMouseMove(Point pt) {
		Event event;
		event = new Event();
		event.type = SWT.MouseMove;
		event.x = pt.x;
		event.y = pt.y;
		queueOSEvent(event);
	}

	/**
	 * Queue a mouse move event to the specific location offset by 1 pixel horizontally and vertically,
	 * then queue a second mouse move event to the specific location
	 * 
	 * @param pt the location to which the mouse should be moved. Use {@link SWTLocation}
	 *            to convert widget relative coordinates to global coordinates
	 */
	protected void queueMouseWiggle(Point pt) {
		queueMouseMove(new Point(pt.x + 1, pt.y + 1));
		queueMouseMove(pt);
	}

	/**
	 * Queue mouse move, mouse down, and mouse up events.
	 * 
	 * @param button the {@link Event#button} button (1, 2, or 3). Use
	 *            {@link #getButton(int)} to convert {@link WT#BUTTON1},
	 *            {@link WT#BUTTON2}, etc to button (1, 2, or 3)
	 * @param pt the mouse down location or <code>null</code> if the mouse up location is
	 *            unspecified. Use {@link SWTLocation} to convert widget relative
	 *            coordinates to global coordinates
	 */
	protected void queueMouseMoveAndClick(int button, Point pt) {
		// TODO: do we need a wiggle? do we need this method? or should it be inlined?
		queueMouseMove(pt);
		queueMouseDown(button, pt);
		queueMouseMove(pt);
		queueMouseUp(button, pt);
	}

	/**
	 * Queue a step that waits for a particular menu item to become enabled. Typically,
	 * this is called *before* calling {@link #click(int, SWTLocation, boolean)}
	 * 
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTShowMenuOperation().waitForEnabled(...).openMenu(...).execute();</code>
	 */
	public SWTOperation waitForEnabled(final SWTWidgetReference<?> widgetRef) {
		final String className = getClass().getSimpleName();
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (!widgetRef.isEnabled()) {
					// TODO: change 0 to higher number to reduce noise from log
					if (++waitForEnabled > 0)
						System.out.println(className + " waiting for enabled: " + waitForEnabled + " - " + widgetRef);
					throw new SWTOperationStepException("Waiting for item to become enabled: " + widgetRef);
				}
			}
		});
		return this;
	}

	/**
	 * Extract the {@link Event#button} button from the accelerator bits.
	 * 
	 * @param accelerator the bitwise accelerator ({@link WT#BUTTON1}, {@link WT#BUTTON2},
	 *            {@link WT#BUTTON3})
	 * @return the {@link Event#button} button (1, 2, or 3)
	 */
	protected static int getButton(int accelerator) {
		if ((accelerator & WT.BUTTON1) == WT.BUTTON1)
			return MouseConfig.BUTTONS_REMAPPED ? 3 : 1;
		if ((accelerator & WT.BUTTON2) == WT.BUTTON2)
			return 2;
		if ((accelerator & WT.BUTTON3) == WT.BUTTON3)
			return MouseConfig.BUTTONS_REMAPPED ? 1 : 3;
		return MouseConfig.BUTTONS_REMAPPED ? 3 : 1;
	}
}
