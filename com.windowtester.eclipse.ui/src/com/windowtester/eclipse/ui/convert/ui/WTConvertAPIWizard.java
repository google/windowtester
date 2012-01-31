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
package com.windowtester.eclipse.ui.convert.ui;

import org.eclipse.ltk.ui.refactoring.RefactoringWizard;

import com.windowtester.eclipse.ui.convert.WTConvertAPIRefactoring;

/**
 * Wizard for converting/migrating all java code in the selected project/package/class to
 * use the new WindowTester API.
 */
public class WTConvertAPIWizard extends RefactoringWizard
{
	public WTConvertAPIWizard(WTConvertAPIRefactoring convertAPIRefactoring) {
		super(convertAPIRefactoring, 0);
		setForcePreviewReview(true);
		setWindowTitle(convertAPIRefactoring.getName());
	}

	protected void addUserInputPages() {
	}
}
