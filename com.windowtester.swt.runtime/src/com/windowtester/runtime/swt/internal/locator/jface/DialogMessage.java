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
package com.windowtester.runtime.swt.internal.locator.jface;

import java.util.concurrent.Callable;

import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.swt.internal.widgets.DisplayReference;

/**
 * Messengers carry the message of a dialog.
 *
 */
public class DialogMessage {

	//notice that a null arg is OK
	public static DialogMessage forControl(Control control) {
		DialogMessage messenger = new DialogMessage();
		messenger.control = control;
		return messenger;
	}

	private Control control;

	private DialogMessage() {}
	
	/**
	 * Get the control that carries the message.
	 */
	public Control getControl() {
		return control;
	}
	
	/**
	 * Get the message.
	 */
	public String getMessage() {
		return DisplayReference.getDefault().execute(new Callable<String>(){
			public String call() throws Exception {
				//TODO: this is a short list.... should there be more?
				if (control instanceof Label)
					return ((Label)control).getText();
				if (control instanceof CLabel)
					return ((CLabel)control).getText();
				if (control instanceof Text)
					return ((Text)control).getText();
				return null;
			}
		});		
	}
	
}
