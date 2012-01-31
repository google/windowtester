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

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.ofClass;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.MessageLine;

import com.windowtester.internal.runtime.reflect.FieldAccessor;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;

/**
 * Reflection utility for accessing details about a given dialog.
 */
@SuppressWarnings("restriction")
public class DialogInspector {
	
	//TODO: refactor to provide Message control
	//rub: will be used by inspector...
	
//	public static DialogMessage getMessenger(Dialog dialog) {
//		return DialogMessage.forControl(getMessageControl(dialog));
//	}
	

	//TODO: make this handle SelectionStatusDialog(s) by unpacking their MessageLines

	//Input dialog Label is transient...
	
	//private static final FieldAccessor INPUT_DIALOG_MESSAGE = FieldAccessor.forField("message").inClass(InputDialog.class);
	
	private static final FieldAccessor ICON_MESSAGE_DIALOG_MESSAGE_LABEL = FieldAccessor.forField("messageLabel").inClass(IconAndMessageDialog.class);
	private static final FieldAccessor INPUT_DIALOG_MESSAGE_TEXT = FieldAccessor.forField("errorMessageText").inClass(InputDialog.class);
	
	
	
	public static Control getMessageControl(Dialog dialog) {
		if (dialog instanceof IconAndMessageDialog) {
			//label is a protected field so we consider it API...
			return (Control)ICON_MESSAGE_DIALOG_MESSAGE_LABEL.access(dialog);
		}
		if (dialog instanceof InputDialog) {
			//RISKY: this field is private
			return (Control)INPUT_DIALOG_MESSAGE_TEXT.access(dialog);
		}
		if (dialog instanceof SelectionStatusDialog) {
			ISWTWidgetReference<?>[] matches = SWTWidgetFinder.forActiveShell().findAll(ofClass(MessageLine.class));
			if (matches.length == 1)
				return (Control) matches[0].getWidget();
			
//			List matches = new WidgetFinderService(Display.getDefault()).collectMatchesIn( new AdapterFactory().adapt(new MessageLineLocator()), dialog.getShell());
//			if (matches.size() == 1)
//				return (Control) matches.get(0);
			
		}
		
		return null;
	}
	
	
}
