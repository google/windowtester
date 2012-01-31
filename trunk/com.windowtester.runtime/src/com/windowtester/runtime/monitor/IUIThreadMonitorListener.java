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
package com.windowtester.runtime.monitor;

/**
 * Interfaced used by {@link com.windowtester.swt.monitor.IUIThreadMonitor} instances to
 * notify objects that the user interface thread is either idle or unresponsive longer
 * than expected.
 */
public interface IUIThreadMonitorListener
{
	/**
	 * The user interface thread is either idle or unresponsive longer than expected.
	 * 
	 * @param isResponsive <code>true</code> if the UI thread is responsive to new user
	 *            interface events, {@link org.eclipse.swt.widgets.Display#syncExec} and
	 *            {@link org.eclipse.swt.widgets.Display#asyncExec}, and
	 *            <code>false</code> if the user interface has not processed any new
	 *            events recently as may be hung.
	 */
	void uiTimeout(boolean isResponsive);
}