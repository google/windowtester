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

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * A builder for SWT {@link Event}s.
 */
public class SWTEvent {

	private Event event;

	public static SWTEvent forWidget(SWTWidgetReference<?> widget){
		return new SWTEvent(widget);
	}
	
	private SWTEvent(SWTWidgetReference<?> widget) {
		event = new Event();
		event.time = (int) System.currentTimeMillis();
		event.widget = widget.getWidget();
		event.display = widget.getDisplayRef().getDisplay();
	}
	
	public SWTEvent withDetail(int detail){
		event.detail = detail;
		return this;
	}
	
	public SWTEvent withItem(Widget item){
		event.item = item;
		return this;
	}
	
	public SWTEvent withItem(SWTWidgetReference<? extends Widget> item){
		return withItem(item.getWidget());
	}
	
	/**
	 * Return the associated untyped SWT event.
	 */
	public Event asUntypedEvent(){
		return event;
	}
	
}
