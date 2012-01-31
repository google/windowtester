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
package com.windowtester.runtime.swt.internal.widgets.carbon;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;

/**
 * A {@link TabItem} reference that uses native carbon_x86-specific code to access bounds.
 */
public class TabItemReference_carbon_x86 extends TabItemReference {

	public TabItemReference_carbon_x86(TabItem control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.TabItemReference#getDisplayBounds()
	 */
	@Override
	public Rectangle getDisplayBounds() {
		return Carbon.EXTENSIONS.getTabItemBounds(getWidget());
	}
}