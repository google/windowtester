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
package com.windowtester.runtime.swt.internal.text;


/**
 * (Temporary) point of entry for varying text entry strategies.
 */
public class TextEntryStrategy {

	private static ITextEntryStrategy _default     = new SWTOperationTextEntryStrategy();
//	private static ITextEntryStrategy _default     = new DelegatingTextEntryStrategy();
//	private static ITextEntryStrategy _incremental = new IncrementalSetTextEntryStrategy();
	private static ITextEntryStrategy _current     = _default;
	
	
//	/**
//	 * Set the current strategy to incremental set selection.
//	 */
//	public void setToIncrementalSetSelection() {
//		set(getIncrementalSet());
//	}
	
	
	/**
	 * (Re)-sets the current strategy to the default UIDriver approach.
	 */
	public static void reset() {
		set(getDefault());
	}

	public static ITextEntryStrategy getCurrent() {
		return _current;
	}

	public static void set(ITextEntryStrategy strat) {
		_current = strat;
	}
	

//	public void enterText(UIContext ui, String txt) {
//		_current.enterText(ui, txt);
//	}


	public static ITextEntryStrategy getDefault() {
		return _default;
	}


//	public static ITextEntryStrategy getIncrementalSet() {
//		return _incremental;
//	}
	
	
}
