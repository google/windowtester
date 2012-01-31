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

import org.eclipse.swt.widgets.MenuItem;

import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.WTRuntimeFactorySWT;


public class WTRuntimeFactorySWT_carbon_x86 extends WTRuntimeFactorySWT {
	
	@Override
	public IWidgetReference createReference(Object widget) {
		if (widget instanceof MenuItem)
			return new MenuItemReference_carbon_x86((MenuItem)widget);
		/* $if eclipse.version < 3.4 $
		if (widget instanceof org.eclipse.swt.widgets.TabItem)
			return new TabItemReference_carbon_x86((org.eclipse.swt.widgets.TabItem)widget);
		$endif $ */
		return super.createReference(widget);
	}

}
