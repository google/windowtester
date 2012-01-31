package com.windowtester.test.util;


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
public class Logger {

	public static void log(String msg) {
		com.windowtester.internal.debug.Logger.log(msg);
		//Activator.getInstance().getLog().log(new Status(Status.ERROR, Activator.PRODUCT_ID, Status.ERROR, msg, null));
	}

}
