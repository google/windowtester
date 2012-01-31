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

import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.internal.factory.WTRuntimeManager;

/**
 * A {@link TabItem} reference.
 */
public class TabItemReference extends ItemReference<TabItem>
{
	/**
	 * Constructs a new instance with the given control. Do not access this constructor
	 * directly... use {@link WTRuntimeManager#asReference(Object)} instead because in
	 * Eclipse 3.3 and earlier a subclass of this class should be used instead of this
	 * class.
	 * 
	 * @param control the control.
	 */
	protected TabItemReference(TabItem control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<TabFolder> getParent() {
		return displayRef.execute(new Callable<CompositeReference<TabFolder>>() {
			public CompositeReference<TabFolder> call() throws Exception {
				return new CompositeReference<TabFolder>(widget.getParent());
			}
		});
	}
}
