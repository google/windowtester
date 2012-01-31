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
package com.windowtester.eclipse.ui.pde.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.eclipse.ui.launcher.RecorderWorkbenchLaunchConfDelegate;
import com.windowtester.internal.debug.LogHandler;

public class RecorderTab extends AbstractLaunchConfigurationTab {

	private static final String ICON_RECORDER = "/icons/full/obj16/record.gif";//$NON-NLS-1$
	private Button check;

	/**
	 * @see ILaunchConfigurationTab#getImage()
	 */
	public Image getImage() {
		return UiPlugin.getDefault().getImage(ICON_RECORDER); 
	}
	
	public void createControl(Composite parent) {
		Composite content=new Composite(parent,SWT.NONE);
		check = new Button(content, SWT.CHECK);
		check.setText(Messages.getString("RecorderTab.INJECT_TEXT")); //$NON-NLS-1$
		check.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
			
		});
		content.setLayout(new GridLayout(1,false));		
		setControl(content);
	}

	public String getName() {
		return Messages.getString("RecorderTab.NAME"); //$NON-NLS-1$
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			check.setSelection(configuration.getAttribute(RecorderWorkbenchLaunchConfDelegate.INJECT_BUNDLES_KEY,false));
		} catch (CoreException e) {
			LogHandler.log(e);
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RecorderWorkbenchLaunchConfDelegate.INJECT_BUNDLES_KEY,check.getSelection());
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RecorderWorkbenchLaunchConfDelegate.INJECT_BUNDLES_KEY,false);
	}

}
