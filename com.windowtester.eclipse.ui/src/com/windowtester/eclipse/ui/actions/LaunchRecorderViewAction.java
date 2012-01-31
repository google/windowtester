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
package com.windowtester.eclipse.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchWindow;

import com.windowtester.eclipse.ui.usage.ProfiledAction;

/**
 * Launch recorder pull-down action.
 */
public class LaunchRecorderViewAction extends ProfiledAction implements IMenuCreator {
	
	static class EmptySelection implements ISelection {

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
		 */
		public boolean isEmpty() {
			return true;
		}
	}
	
	private RecordToolbarAction launchAction;

	public LaunchRecorderViewAction(ImageDescriptor imageDescriptor) {
		this(new RecordToolbarAction(), imageDescriptor);
	}
	
	public LaunchRecorderViewAction(RecordToolbarAction launchAction, ImageDescriptor imageDescriptor) {
		super("", IAction.AS_DROP_DOWN_MENU);
		this.launchAction = launchAction;
		setImageDescriptor(imageDescriptor);
		setMenuCreator(this);
		//hooks up this action as the provider of tooltip details
		launchAction.selectionChanged(this, new EmptySelection());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#dispose()
	 */
	public void dispose() { }
	
	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
	 */
	public void doRun() {
		launchAction.run(null);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Menu)
	 */
	public Menu getMenu(Menu parent) {
		return launchAction.getMenu(parent);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.action.IMenuCreator#getMenu(org.eclipse.swt.widgets.Control)
	 */
	public Menu getMenu(Control parent) {
		return launchAction.getMenu(parent);
	}
	
	public void init(IWorkbenchWindow window) {
		launchAction.init(window);	
	}
	
	public String getLastLaunchName() {
		return launchAction.getLastLaunchName();
	}
	
	
}