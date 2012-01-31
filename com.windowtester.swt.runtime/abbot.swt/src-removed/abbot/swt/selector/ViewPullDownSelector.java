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
package com.windowtester.runtime.swt.internal.selector;

import java.lang.reflect.Method;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A PROVISIONAL selector for opening view menus.
 *
 * @author Phil Quitslund
 *
 */
public class ViewPullDownSelector {

	private String _viewId;

	public ViewPullDownSelector(ViewLocator loc) {
		_viewId = loc.getViewId();
	}
	
	public String getViewId() {
		return _viewId;
	}
	
	public void openMenu() {
		final IViewReference viewRef = getViewRef(getViewId());
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Object partPane = getPartPane(viewRef); //.showPaneMenu();
				try {
					//NOTE: we are not casting to partpane here because access
					//is restricted...
					Method method = partPane.getClass().getMethod("showPaneMenu", new Class[]{});
					method.setAccessible(true);
					method.invoke(partPane, (Object[])null);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	public /* PartPane */ Object getPartPane(IViewReference viewRef) {
		Method method;
		try {
			method = viewRef.getClass().getMethod("getPane", new Class[]{});

			method.setAccessible(true);
			return method.invoke(viewRef, (Object[])null);
			
		} catch (Exception e) {
			//TODO: should this be a WNFE? (or our own exception?)
			throw new RuntimeException(e);
		}
		
	}

	
	public IViewReference getViewRef(final String id) {
		final IViewReference[] vref = new IViewReference[1];
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				//be safe here since the workbench might be disposed (or not active)
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow(); 
				if (window == null) {
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null) {
					return;
				}
				
				IViewReference[] viewReferences = page.getViewReferences();
				for (int i = 0; i < viewReferences.length; i++) {
					IViewReference ref = viewReferences[i];
					if (ref.getId().equals(id)) {
						vref[0] = ref;
						return;
					}
				}
			}
		});
		return vref[0];
	}
}