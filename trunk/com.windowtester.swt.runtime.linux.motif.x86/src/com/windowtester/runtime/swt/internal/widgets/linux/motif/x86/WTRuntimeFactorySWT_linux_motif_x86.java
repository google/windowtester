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
package com.windowtester.runtime.swt.internal.widgets.linux.motif.x86;

import org.eclipse.swt.widgets.TabItem;

import com.windowtester.runtime.swt.internal.widgets.TabItemReference;
import com.windowtester.runtime.swt.internal.widgets.WTRuntimeFactorySWT;

/**
 * A factory for creating instances of SWT widget references specific to Linux Motif.
 */
public class WTRuntimeFactorySWT_linux_motif_x86 extends WTRuntimeFactorySWT
{
	@Override
	protected TabItemReference createTabItemReference(TabItem widget) {
		/* $if eclipse.version < 3.4 $
		return new TabItemReference_linux_motif_x86((org.eclipse.swt.widgets.TabItem)widget);
		$else $ */
		return super.createTabItemReference(widget);
		/* $endif $ */
	}
}
