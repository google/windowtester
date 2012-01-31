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
package com.windowtester.swt.codegen.wizards;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;

/**
 * Section in a control for displaying and selecting setup handlers.
 */
public class SetupHandlerSection {

	
	private SetupHandlerTable setupHandlerTable;
	private final Composite parent;
	private ExecutionProfile profile;

	public static SetupHandlerSection forParent(Composite parent) {
		return new SetupHandlerSection(parent);
	}
	
	public SetupHandlerSection inContext(ExecutionProfile profile) {
		this.profile = profile;
		return this;
	}
	
	public SetupHandlerSection(Composite parent) {
		this.parent = parent;
	}

	public SetupHandlerSection build() {
		int type = profile.getExecType();
		if (type == ExecutionProfile.RCP_EXEC_TYPE)
			doBuild(); //only RCP has handlers at the moment...
		return this;
	}
	
	private void doBuild() {
		final Label header = new Label(parent, SWT.NONE);
        header.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 4, 1));
        header.setText("Which conditions would you like to ensure at setup?");
		
		final Label optionsLabel = new Label(parent, SWT.NONE);
        optionsLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        optionsLabel.setText("" /*"Setup:"*/);
                
        setupHandlerTable = SetupHandlerTable.forParent(parent).inContext(profile).build();        
        final Button restoreDefaults = new Button(parent, SWT.NONE);
        restoreDefaults.setText("Defaults");
        restoreDefaults.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
        restoreDefaults.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
			public void widgetSelected(SelectionEvent e) {
				setupHandlerTable.setDefaults();
			}
        });
	}

	public SetupHandlerSet getSelectedHandlers() {
		//a genuine null object would be cleaner but for now a bit overkill
		if (setupHandlerTable == null)
			return SetupHandlerSet.EMPTY;
		return setupHandlerTable.getSelectedHandlers();
	}


	public void persistSelections() {
		if (setupHandlerTable == null)
			return;
		setupHandlerTable.persistSelections();
	}

	
}
