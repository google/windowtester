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
package com.windowtester.runtime.swt.internal.widgets.win32.win32.x86;

import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;
import com.windowtester.runtime.swt.internal.widgets.WTRuntimeFactorySWT;

/**
 * A factory for creating instances of SWT widget references specific to Windows.
 */
public class WTRuntimeFactorySWT_win32_win32_x86 extends WTRuntimeFactorySWT
{
	@Override
	protected TabItemReference createTabItemReference(TabItem widget) {
		/* $if eclipse.version < 3.4 $
		return new TabItemReference_win32_win32_x86(widget);
		$else $ */
		return super.createTabItemReference(widget);
		/* $endif $ */
	}
}
