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
package com.windowtester.sandbox.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

public class TextInAView extends ViewPart {

	private StyledText testStyledText;
	private Text text1;
	public static final String ID = "case36373_DeletingText.case36373View"; //$NON-NLS-1$

	/**
	 * Create contents of the view part
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(Display.getCurrent());
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);

		text1 = new Text(container, SWT.BORDER);
		text1.setText("Default text more text djfsd weirewio isjfiowe weoijrpew");
		toolkit.adapt(text1, true, true);
		text1.setBounds(44, 38, 186, 25);
		text1.setData("name","text1");

		testStyledText = new StyledText(container, SWT.BORDER);
		testStyledText.setText("dsfjdsf jdsjflweoewjr  thee weriwe qiwroirwe wieuroiew jdshfsd woieuroiew wisjrioerj");
		toolkit.adapt(testStyledText, true, true);
		testStyledText.setBounds(44, 92, 186, 60);
		testStyledText.setData("name","testStyledText");
		//
		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar
	 */
	private void initializeToolBar() {
		//IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu
	 */
	private void initializeMenu() {
		//IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

}
