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
package com.windowtester.runtime.swt.internal.application;

import com.windowtester.runtime.internal.application.IApplicationContext;
import com.windowtester.runtime.internal.application.IApplicationContextActionProvider;

/**
 * A smart pointer to the current application context.
 */
public class ApplicationContext implements IApplicationContext {

	private IApplicationContextActionProvider current = DEFAULT;
	
	private static final IApplicationContextActionProvider NATIVE  = new NativeContext();
	private static final IApplicationContextActionProvider DEFAULT = new DefaultContext();
	
	public static IApplicationContext getDefault() {
		return new ApplicationContext().setDefault();
	}
	
	public static IApplicationContext getNative() {
		return new ApplicationContext().setNative();
	}
	
	public IApplicationContext setNative() {
		this.current = NATIVE;
		return this;
	}
	
	public IApplicationContext setDefault() {
		this.current = DEFAULT;
		return this;
	}

	public boolean isNative() {
		return current == NATIVE;
	}
	
	public boolean isDefault() {
		return current == DEFAULT;
	}
	
	
	
}
