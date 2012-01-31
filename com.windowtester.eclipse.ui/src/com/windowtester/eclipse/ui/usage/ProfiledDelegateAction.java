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
package com.windowtester.eclipse.ui.usage;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * Delegates to a target provides and own id suffix.
 */
public class ProfiledDelegateAction extends ProfiledAction {
	
	private final IAction action;
	private final String idSuffix;

	public ProfiledDelegateAction(IAction action, String idSuffix) {
		this.action = action;
		this.idSuffix = idSuffix;
		importActionValues();
		addEnablementPropertyChangeListener();
	}
	
	private void importActionValues() {
		setId(action.getId() + "_" + idSuffix);
		setImageDescriptor(action.getImageDescriptor());
		setDisabledImageDescriptor(action.getDisabledImageDescriptor());
		setText(action.getText());
		setToolTipText(action.getToolTipText());
		setDescription(action.getDescription());
	}

	private void addEnablementPropertyChangeListener() {
		action.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				String property = event.getProperty();
				if (property == null)
					return;
				if (property.equals("enabled")) {
					Object newValue = event.getNewValue();
					if (!(newValue instanceof Boolean))
						return;
					boolean enabled = ((Boolean)newValue).booleanValue();
					setEnabled(enabled);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
	 */
	public void doRun() {
		if (action instanceof ProfiledAction)
			((ProfiledAction)action).doRun();
		else
			action.run();
	}	
	
}