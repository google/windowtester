package com.windowtester.test.locator.swt.forms;

import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

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
public class FormDialog extends TrayDialog {

	public static String DEFAULT_TITLE = "FormDialog";

	private FormToolkit toolkit;
	
	/**
	 * Creates a new form dialog for a provided parent shell.
	 * 
	 * @param shell
	 *            the parent shell
	 */
	public FormDialog(Shell shell) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	/**
	 * Creates a new form dialog for a provided parent shell provider.
	 * 
	 * @param parentShellProvider
	 *            the parent shell provider
	 */
	public FormDialog(IShellProvider parentShellProvider) {
		super(parentShellProvider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TrayDialog#close()
	 */
	public boolean close() {
		boolean rcode = super.close();
		toolkit.dispose();
		return rcode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm sform = toolkit.createScrolledForm(parent);
		sform.setLayoutData(new GridData(GridData.FILL_BOTH));
		ManagedForm mform = new ManagedForm(toolkit, sform);
		createFormContent(mform);
		applyDialogFont(sform.getBody());
		return sform;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.TrayDialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createButtonBar(Composite parent) {
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		//Composite sep = new Composite(parent, SWT.NULL);
		//sep.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW));
		//gd.heightHint = 1;
		Label sep = new Label(parent, SWT.HORIZONTAL|SWT.SEPARATOR);
		sep.setLayoutData(gd);
		Control bar = super.createButtonBar(parent);
		return bar;
	}

	/**
	 * Configures the dialog form and creates form content. Clients should
	 * override this method.
	 * 
	 * @param mform
	 *            the dialog form
	 */
	protected void createFormContent(IManagedForm mform) {
		mform.getForm().setText(DEFAULT_TITLE);
	}
}