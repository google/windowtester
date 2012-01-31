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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Widget;


/**
 * A Selector for Styled Text widgets.
 * 
 */
public class StyledTextSelector extends BasicWidgetSelector {

	
	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#select(org.eclipse.swt.widgets.Widget, int, int)
	 */
	public void select(final Widget w, final int start, final int stop) {
		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				((StyledText)w).setSelection(start, stop);
			}
		});
	}

	/**
	 * @see com.windowtester.event.swt.ISWTWidgetSelectorDelegate#selectAll(org.eclipse.swt.widgets.Widget)
	 */
	public void selectAll(final Widget w) {
		w.getDisplay().syncExec(new Runnable() {
			public void run() {
				((StyledText)w).selectAll();
			}
		});
	}
	
	
}
