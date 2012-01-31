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
package com.windowtester.swt.locator.eclipse;

import org.eclipse.swt.widgets.Control;

import com.windowtester.swt.WidgetLocator;

/**
 * 
 * Custom locator used for scoping widget searches by eclipse workbench views.
 * <p>
 * Views are identified by their <code>view-id</code> which is specified
 * when the view is contributed to the workbench via the <code>org.eclipse.ui.views</code>
 * extension point.
 * 
 * For instance, given that the resource navigator view has an id of "org.eclipse.ui.views.ResourceNavigator",
 * the locator to identify the tree in the resource navigator could be written like this:
 * 
 * <pre>
 * new WidgetLocator(Tree.class, 
 *    new ViewLocator("org.eclipse.ui.views.ResourceNavigator");
 * </pre>
 * 
 * 
 * 
 * @see org.eclipse.ui.IViewPart
 * @deprecated Use {@link com.windowtester.runtime.swt.locator.eclipse.ViewLocator} instead
 */
public class ViewLocator extends WidgetLocator {

	private static final long serialVersionUID = 517670073860268199L;

	/**
	 * Create an instance that locates the given view.
	 * @param viewId the id of the view to locate
	 */
	public ViewLocator(String viewId) {
		super(Control.class, viewId);
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ViewLocator ["+ getNameOrLabel() +"]";
	}
	
	
}
