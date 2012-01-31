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
package com.windowtester.runtime.swt.internal.locator;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.util.StringComparator;

/** 
 * Locator for the title in a wizard. Typically this is used with the
 * {@link IUIContext#assertThat(com.windowtester.runtime.condition.ICondition)} method to
 * assert that a title is displayed or not.
 * <pre>
 * ui.assertThat(new HasTextCondition(new WizardTitleLocator(), &quot;Some message here&quot;));
 * </pre>
 */
public class WizardTitleLocator
	implements HasText
{
	
	
	
	public String getText(IUIContext ui) throws WidgetSearchException {
		final String[] title        = new String[1];
		final Exception[] exception = new Exception[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				Shell activeShell = Display.getDefault().getActiveShell();
				if (activeShell == null) {
					exception[0] = new WidgetSearchException("No active shell");
					return;
				}
				Object dialog = activeShell.getData();
				if (!(dialog instanceof WizardDialog)) {
					exception[0] = new WidgetSearchException("Expected WizardDialog but found " + dialog);
					return;
				}
				final IWizardPage page = ((WizardDialog) dialog).getCurrentPage();
				if (page == null) {
					exception[0] = new WidgetSearchException("WizardDialog current page is null");
					return;
				}
				title[0] = page.getTitle();
			}
		});
		if (exception[0] != null)
			throw ((WidgetSearchException) exception[0]);
		return title [0];
	}
	
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
}
