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
package com.windowtester.runtime.swt.internal.widgets.win32.win32.x86_64;

import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;
import com.windowtester.runtime.swt.internal.widgets.WTRuntimeFactorySWT;

public class WTRuntimeFactorySWT_win32_win32_x86_64 extends WTRuntimeFactorySWT
{
	@Override
	protected TabItemReference createTabItemReference(TabItem widget) {
		/* $if eclipse.version < 3.4 $
		return new TabItemReference_win32_win32_x86_64(widget);
		$else $ */
		return super.createTabItemReference(widget);
		/* $endif $ */
	}
}
