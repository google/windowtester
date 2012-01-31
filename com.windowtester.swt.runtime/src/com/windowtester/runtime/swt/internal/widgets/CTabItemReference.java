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

import org.eclipse.swt.custom.CTabItem;

/** 
 * A {@link CTabItem} reference.
 */
public class CTabItemReference extends ItemReference<CTabItem> {

	/**
	 * Constructs a new instance with the given item.
	 * 
	 * @param item the item.
	 */
	public CTabItemReference(CTabItem item) {
		super(item);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CTabFolderReference getParent() {
		return displayRef.execute(new Callable<CTabFolderReference>() {
			public CTabFolderReference call() throws Exception {
				return new CTabFolderReference(widget.getParent());
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				// TODO[pq]: this is the legacy behavior and could be reconsidered
				return widget.getParent().isVisible();
			}
		});
	}
	
}
