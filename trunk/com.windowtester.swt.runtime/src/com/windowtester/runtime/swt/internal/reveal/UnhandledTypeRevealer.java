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
package com.windowtester.runtime.swt.internal.reveal;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.debug.LogHandler;

/**
 * A "null object" for revealing unhandled types.
 */
public class UnhandledTypeRevealer implements IRevealStrategy {

	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, java.lang.String, int, int)
	 */
	public Widget reveal(Widget w, String path, int x, int y) {
		return doReveal(w);
	}

	/**
	 * @see com.windowtester.runtime.swt.internal.reveal.IRevealStrategy#reveal(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public Widget reveal(Widget w, int x, int y) {
		return doReveal(w);
	}
	
	private Widget doReveal(Widget w) {
		logAttempt(w);
		return null;
	}	
	
	private void logAttempt(Widget w) {
		LogHandler.log("reveal called on a type(" + describe(w) +") for which there is no reveal strategy");
	}

	private String describe(Widget w) {
		if (w == null)
			return "<null widget>";
		return w.getClass().toString();
	}
	

	
}
