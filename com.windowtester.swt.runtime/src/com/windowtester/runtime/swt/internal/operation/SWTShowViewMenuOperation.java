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
package com.windowtester.runtime.swt.internal.operation;

import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.internal.runtime.util.ReflectionUtils;
import com.windowtester.runtime.WidgetNotFoundException;

/**
 * A specialized operation that opens a view's menu programmatically and waits for it to
 * appear because it is impractical to show a view's menu using mouse operations because
 * it can be in different locations based upon the views's toolbar items and the view's
 * width.
 */
public class SWTShowViewMenuOperation extends SWTShowMenuOperation
{
	public SWTShowViewMenuOperation() {
		super(null);
	}

	/**
	 * Open the menu associated with the specified view
	 * 
	 * @param viewId the identifier of the view
	 * @return this operation so that calls can be cascaded on a single line such as
	 *         <code>new SWTShowMenuOperation().openMenu(...).execute();</code>
	 */
	public SWTShowViewMenuOperation openViewMenu(final String viewId) {
		queueStartMenuFilter();
		queueStep(new Step() {
			public void executeInUI() throws Exception {
				showViewMenu(viewId);
			}
		});
		queueWaitForMenu();
		return this;
	}

	/**
	 * Cannot open the view menu using a click because it can be in different locations
	 * based upon the views's toolbar items and the view's width so open the menu
	 * programmatically instead
	 * 
	 * @param viewId the identifier of the view
	 */
	private void showViewMenu(final String viewId) throws Exception {
		IViewReference viewRef = getViewReference(viewId);

		//			Method method = viewRef.getClass().getMethod("getPane", new Class[]{});
		//			method.setAccessible(true);
		//			return method.invoke(viewRef, (Object[]) null);

		Object partPane = ReflectionUtils.invoke(viewRef, "getPane");

		//			Method method = partPane.getClass().getMethod("showPaneMenu", new Class[]{});
		//			method.setAccessible(true);
		//			method.invoke(partPane, (Object[]) null);

		ReflectionUtils.invoke(partPane, "showPaneMenu");
	}

	/**
	 * Find the view with the specified identifier
	 * 
	 * @param viewId the identifier of the view
	 * @throws WidgetNotFoundException
	 */
	private IViewReference getViewReference(final String viewId) throws WidgetNotFoundException {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
			throw new WidgetNotFoundException("Cannot find workbench");
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		if (window == null)
			throw new WidgetNotFoundException("No active window");
		IWorkbenchPage page = window.getActivePage();
		if (page == null)
			throw new WidgetNotFoundException("No active page");
		IViewReference[] viewReferences = page.getViewReferences();
		for (int i = 0; i < viewReferences.length; i++) {
			IViewReference ref = viewReferences[i];
			if (ref.getId().equals(viewId)) {
				return ref;
			}
		}
		throw new WidgetNotFoundException("Cannot find view with id = " + viewId);
	}
}
