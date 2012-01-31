package com.windowtester.event.swt;

import org.eclipse.swt.widgets.Widget;

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
public abstract class SystemEventMonitor extends com.windowtester.runtime.swt.internal.selector.SystemEventMonitor {

	public SystemEventMonitor(Widget widget, int eventType){
		super(widget, eventType);
	}
	
	
}
