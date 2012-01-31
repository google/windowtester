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

import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.util.Invariants;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * An operation on an SWT widget.
 */
public abstract class SWTWidgetOperation<T extends ISWTWidgetReference<?>> extends SWTOperation
{
	private final T widgetRef;

	public SWTWidgetOperation(T widgetRef) {
		Invariants.notNull(widgetRef);
		this.widgetRef = widgetRef;
	}

	protected T getWidgetRef() {
		return widgetRef;
	}

	/**
	 * Queue an event for the specified widget. This event will be sent directly to the
	 * specified widget SWTBot style rather than through the OS event queue. To insert a
	 * "break" between steps where the UI thread can process other events already on the
	 * OS event queue and other calls to {@link Display#asyncExec(Runnable)}, see
	 * {@link #queueStep(Step)}.
	 */
	protected void queueWidgetEvent(int eventType) {
		queueWidgetEvent(getWidgetRef().getWidget(), eventType);
	}

	/**
	 * Queue an event for the specified widget. This event will be sent directly to the
	 * specified widget SWTBot style rather than through the OS event queue. To insert a
	 * "break" between steps where the UI thread can process other events already on the
	 * OS event queue and other calls to {@link Display#asyncExec(Runnable)}, see
	 * {@link #queueStep(Step)}.
	 */
	protected void queueWidgetEvent(int eventType, int eventDetail) {
		queueWidgetEvent(getWidgetRef().getWidget(), eventType, eventDetail);
	}

	/**
	 * Queue a step to assert that the receiver's widget is enabled
	 */
	protected void queueAssertIsEnabled() {
		queueAssertIsEnabled(getWidgetRef());
	}

	/**
	 * Queue a step to assert that the specified widget is enabled
	 */
	protected void queueAssertIsEnabled(final ISWTWidgetReference<?> widgetRef) {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (!widgetRef.isEnabled())
					throw new WidgetNotificationException("Widget " + widgetRef + " is not enabled");
			}
		});
	}

	/**
	 * Queue a step to assert that the receiver's widget has the specified style
	 */
	protected void queueAssertHasStyle(final int style) {
		queueAssertHasStyle(getWidgetRef(), style);
	}

	/**
	 * Queue a step to assert that the specified widget has the specified style
	 */
	protected void queueAssertHasStyle(final ISWTWidgetReference<?> widgetRef, final int style) {
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				if (!widgetRef.hasStyle(style))
					throw new WidgetNotificationException("Widget " + widgetRef + " does not have style " + style);
			}
		});
	}
}
