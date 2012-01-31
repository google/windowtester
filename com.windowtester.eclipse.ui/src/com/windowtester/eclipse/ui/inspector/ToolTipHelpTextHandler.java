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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.swt.widgets.Widget;


/**
 * (From HoverHelp example)
 * 
 * ToolTip help handler
 */
public interface ToolTipHelpTextHandler {
	/**
	 * Get help text
	 * @param widget the widget that is under help
	 * @return a help text string
	 */
	public String getHelpText(Widget widget);
}	