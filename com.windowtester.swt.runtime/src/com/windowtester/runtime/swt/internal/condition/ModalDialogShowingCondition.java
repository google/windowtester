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
package com.windowtester.runtime.swt.internal.condition;

import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * A condition to test for the presence of modal dialogs.
 */
public class ModalDialogShowingCondition implements ICondition {
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		return DisplayReference.getDefault().execute(new Callable<Boolean>(){
			public Boolean call() throws Exception {
				Shell[] shells = Display.getDefault().getShells();
				//System.out.println("testing: " + anyModal(shells));
				return anyModal(shells);
			}
		});
		
	}
	
	public ICondition not() {
		return new ModalDialogShowingCondition() {
			public boolean test() {
				return !super.test();
			}
		};
	}

	private Boolean anyModal(Shell[] shells) {
		for (int i = 0; i < shells.length; i++) {
			if (isModal(shells[i]))
				return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
	
	/**
	 * Test this shell to see if it's modal.
	 * @return <code>true</code> if the shell is modal, <code>false</code> otherwise
	 */
	static boolean isModal(Shell shell) {
		int mask = SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL;
		return (shell.getStyle() & mask) != 0;
	}
}