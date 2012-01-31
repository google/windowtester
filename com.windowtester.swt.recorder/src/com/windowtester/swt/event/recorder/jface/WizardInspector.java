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
package com.windowtester.swt.event.recorder.jface;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.internal.runtime.reflect.FieldAccessor;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.swt.locator.jface.WizardPageLocator;

/**
 * An inspector for Wizard properties.
 * 
 * TODO: this could be expanded to handle (some) dialogs as well...
 * 
 */
public class WizardInspector {

	static interface IControlAccessor {
		Widget getMessageControl();
		Widget getErrorMessageControl();
		Widget getDescriptionControl();
		Widget getTitleControl();
		String getMessageText();
		String getErrorMessageText();
		String getDescriptionText();
		String getTitleText();
	}
	
	private static final IControlAccessor NULL_ACCESSOR = new IControlAccessor() {
		public Widget getDescriptionControl() {
			return null;
		}
		public Widget getErrorMessageControl() {
			return null;
		}
		public Widget getMessageControl() {
			return null;
		}
		public Widget getTitleControl() {
			return null;
		}
		public String getDescriptionText() {
			return null;
		}
		public String getErrorMessageText() {
			return null;
		}
		public String getMessageText() {
			return null;
		}
		public String getTitleText() {
			return null;
		}
	};
	
	private static class WizardPageAccessor implements IControlAccessor {

		private static FieldAccessor TITLE         = FieldAccessor.forField("titleLabel").inClass(TitleAreaDialog.class);	
		//notice message, description and error message share the same control
		private static FieldAccessor MESSAGE       = FieldAccessor.forField("messageLabel").inClass(TitleAreaDialog.class);
		private static FieldAccessor ERROR_MESSAGE = FieldAccessor.forField("messageLabel").inClass(TitleAreaDialog.class);
		private static FieldAccessor DESCRIPTION   = FieldAccessor.forField("messageLabel").inClass(TitleAreaDialog.class);

		
		private final WizardDialog dialog;
		public WizardPageAccessor(WizardDialog page) {
			this.dialog = page;
		}
		public Widget getDescriptionControl() {
			return (Widget) DESCRIPTION.access(dialog);
		}
		public Widget getErrorMessageControl() {
			return (Widget) ERROR_MESSAGE.access(dialog);
		}
		public Widget getMessageControl() {
			return (Widget) MESSAGE.access(dialog);
		}
		public Widget getTitleControl() {
			return (Widget) TITLE.access(dialog);
		}
		public String getDescriptionText() {
			IWizardPage page = getCurrentPage();
			if (page == null)
				return null;
			return page.getDescription();
		}
		public String getErrorMessageText() {
			IWizardPage page = getCurrentPage();
			if (page == null)
				return null;
			return page.getErrorMessage();
		}
		public String getMessageText() {
			IWizardPage page = getCurrentPage();
			if (page == null)
				return null;
			return page.getMessage();
		}
		public String getTitleText() {
			IWizardPage page = getCurrentPage();
			if (page == null)
				return null;
			return page.getTitle();
		}	
		
		IWizardPage getCurrentPage() {
			return dialog.getCurrentPage();
		}
				
	}
	
	
	private static class WizardElementDescription extends SemanticWidgetInspectionEvent {

		private static final long serialVersionUID = -6789482180282671539L;
		
		public WizardElementDescription() {
			super(getWizardPageInfo());
		}
		
		static EventInfo getWizardPageInfo() {
			EventInfo info = new EventInfo();
			info.hierarchyInfo = new IdentifierAdapter(new WizardPageLocator());
			return info;
		}
			
		public String toString() {
			return "Wizard Element [" + getProperties() + "]";
		}
	}
	

	private final IControlAccessor accessor;
	
	public WizardInspector(WizardDialog wizardDialog) {
		this(new WizardPageAccessor(wizardDialog));
	}

	public WizardInspector(IControlAccessor accessor) {
		this.accessor = accessor;
	}
	
	public static WizardInspector forDialog(Dialog dialog) {
		if (dialog instanceof WizardDialog)
			return new WizardInspector((WizardDialog)dialog);
		return new WizardInspector(NULL_ACCESSOR);
	}
	
	public SemanticWidgetInspectionEvent getDescription(Widget pageComponent) {
		//error trumps (it overrides all other values until it is cleared)
		//SEE: org.eclipse.jface.dialogs.DialogPage.setMessage(String, int)
		if (accessor.getErrorMessageControl() == pageComponent) {
			String message = accessor.getErrorMessageText();
			if (message != null)
				return new WizardElementDescription().withProperties(WizardProperty.hasErrorMessage(message));
		}
	
		if (accessor.getDescriptionControl() == pageComponent) {
			String description = accessor.getDescriptionText();
			if (description != null)
				return new WizardElementDescription().withProperties(WizardProperty.hasDescription(description));
		}
		if (accessor.getMessageControl() == pageComponent) {
			String message = accessor.getMessageText();
			if (message != null)
				return new WizardElementDescription().withProperties(WizardProperty.hasMessage(message));
		}
		if (accessor.getTitleControl() == pageComponent) {
			String title = accessor.getTitleText();
			if (title != null)
				return new WizardElementDescription().withProperties(WizardProperty.hasTitle(title));
		}
		return null;
	}

	
}
