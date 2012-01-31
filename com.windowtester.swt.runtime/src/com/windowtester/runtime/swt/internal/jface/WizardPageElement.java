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
package com.windowtester.runtime.swt.internal.jface;

import org.eclipse.jface.wizard.IWizardPage;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;

/**
 * Base class for wizard page conditions.
 */
public abstract class WizardPageElement implements HasText /*, IDiagnosticParticipant */{

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.HasText#getText(com.windowtester.runtime.IUIContext)
	 */
	public abstract String getText(IUIContext ui) throws WidgetSearchException;
	
	protected IWizardPage getPage() throws WidgetSearchException {
		return WizardFinder.getActiveWizardPage();
	}

}
