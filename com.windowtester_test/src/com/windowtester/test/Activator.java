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
package com.windowtester.test;

import org.eclipse.core.runtime.Plugin;

public class Activator extends Plugin {

	
	public static final String PRODUCT_ID = "com.windowtester.test";
	
	private static Activator INSTANCE;
	
	public static Activator getInstance() {
		return INSTANCE;
	}
	
    public void start(org.osgi.framework.BundleContext context) throws Exception {
    	super.start(context);
    	INSTANCE = this;
    }

}
