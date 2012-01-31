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
package com.windowtester.runtime.swt.internal.display;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * UI thread execution convenience helpers.
 *
 */
@Deprecated
public class DisplayExec {

	public static Object sync(RunnableWithResult runnable) {
		return sync(Display.getDefault(), runnable);
	}

	public static Object sync(Display display, RunnableWithResult runnable) {
		display.syncExec(runnable);
		return runnable.getResult();
	}

	public static void sync(Runnable runnable) {
		sync(Display.getDefault(), runnable);
	}

	public static void sync(Display ignored, final Runnable runnable) {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				runnable.run();
			}
		}, 30000);
	}

}
