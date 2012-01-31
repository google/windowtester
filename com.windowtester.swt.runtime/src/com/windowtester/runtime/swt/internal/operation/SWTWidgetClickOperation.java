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

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;

/**
 * Base class for widget mouse clicks.
 */
public abstract class SWTWidgetClickOperation<T extends SWTWidgetReference<?>> {
	
	private final T widget;

	private Point offset  = new Point(5,5); //default
	private int modifiers = WT.BUTTON1;     //default
	private int clicks    = 1;              //default
	
	public SWTWidgetClickOperation(T widget) {
		this.widget = widget;
	}
	
	public T getWidgetRef() {
		return widget;
	}
	
	public Point getOffset() {
		return offset;
	}
	
	public int getModifiers() {
		return modifiers;
	}
	
	public int getClicks() {
		return clicks;
	}
	
	public SWTWidgetClickOperation<T> forClick(IClickDescription click){
		atOffset(click.x(), click.y());
		withModifiers(click.modifierMask());
		withClicks(click.clicks());
		return this;
	}
	
	public SWTWidgetClickOperation<T> withClicks(int clicks){
		this.clicks = clicks;
		return this;
	}
	
	public SWTWidgetClickOperation<T> atOffset(int x, int y){
		this.offset = new Point(x,y);
		return this;
	}
	
	public SWTWidgetClickOperation<T> withModifiers(int modifiers){
		this.modifiers = modifiers;
		return this;
	}
		
	public void execute() {
	  	SWTLocation loc = getLocation();
		new SWTMouseOperation(getModifiers()).at(loc).count(getClicks()).execute();		
	}

	protected abstract SWTLocation getLocation();

	
}
