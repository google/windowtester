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
package com.windowtester.recorder.ui.remote;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.ToolBar;


public class MinMaxToggleGroup {

	
	private final DashboardRemote remote;


	private class ToggleAction extends Action {
		public ToggleAction() {
			//initial state is minimized...
			setImageDescriptor(RemoteImages.getDescriptor("thin_max_view.gif"));
			setToolTipText("Maximize");
		}
		private void updateImage() {
			if (remote.eventViewer.isHidden()) {
				setImageDescriptor(RemoteImages.getDescriptor("thin_max_view.gif"));
				setToolTipText("Maximize");
			} else {
				setImageDescriptor(RemoteImages.getDescriptor("thin_min_view.gif"));
				setToolTipText("Minimize");
			}
		}
		public void run() {
			updateViewState();
			updateImage();
			remote.pack();
		}
		private void updateViewState() {
			if (remote.eventViewer.isHidden()) {
				remote.eventViewer.show();
			} else {
				remote.eventViewer.hide();
			}
		}
	};
	
	public MinMaxToggleGroup(DashboardRemote remote) {
		this.remote = remote;
		create();
	}
	
	
	private void create() {
		ToolBarManager toolBarManager2 = new ToolBarManager(SWT.FLAT);
		toolBarManager2.add(new ToggleAction());
		ToolBar toolBar2 = toolBarManager2.createControl(remote.mainControlComposite);
		
		GridData layoutData = new GridData();
		layoutData.horizontalAlignment = SWT.RIGHT;
		layoutData.grabExcessHorizontalSpace = true;
		toolBar2.setLayoutData(layoutData);	
	}
}
