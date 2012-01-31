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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;

/**
 * An {@link ScrollBar} reference.
 */
public class ScrollBarReference extends SWTWidgetReference<ScrollBar> {
	
	public ScrollBarReference(ScrollBar item) {
		super(item);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public ScrollableReference<Scrollable> getParent() {
		return displayRef.execute(new Callable<ScrollableReference<Scrollable>>() {
			public ScrollableReference<Scrollable> call() throws Exception {
				return new ScrollableReference<Scrollable>(widget.getParent());
			}
		});
	}
	
}
