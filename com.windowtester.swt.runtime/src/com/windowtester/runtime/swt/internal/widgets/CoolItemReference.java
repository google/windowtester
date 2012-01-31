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

import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;

/** 
 * A {@link CoolItem} reference.
 */
public class CoolItemReference extends ItemReference<CoolItem> {

	/**
	 * Constructs a new instance with the given cool item.
	 * 
	 * @param control the cool item.
	 */
	public CoolItemReference(CoolItem control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<CoolBar> getParent() {
		return displayRef.execute(new Callable<CompositeReference<CoolBar>>() {
			public CompositeReference<CoolBar> call() throws Exception {
				return new CompositeReference<CoolBar>(widget.getParent());
			}
		});
	}
}
