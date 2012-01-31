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
package com.windowtester.eclipse.ui.inspector.notifications;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.eclipse.ui.inspector.ImageManager;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.ui.internal.corel.model.Event;
import com.windowtester.ui.internal.corel.model.EventSequenceLabelProvider;

class EventNotificationPopup extends PopupDialog {

	
	public static class EventRecorded extends EventNotificationPopup {

		private final IUISemanticEvent event;

		public EventRecorded(IUISemanticEvent event) {
			this.event = event;
		}

		protected String getPopupTitle() {
			return "Event Recorded";
		}
		
		protected Image getPopupImage() {
			return ImageManager.getImage("check.gif");
		}
		
		protected void createFormContents() {
			EventSequenceLabelProvider labelProvider = new EventSequenceLabelProvider();
			Label label = toolkit.createLabel(getBody(), "foo");
			Event e = new Event(event);//NOTICE this adaptation...
			if (Platform.isRunning())
				label.setImage(labelProvider.getImage(e));
			label.setText(labelProvider.getText(e)); 
		}
		
		
	}
	
	private static final String FOOTER_TEXT = null;

	private Composite composite;
	protected final FormToolkit toolkit;
	
	private Rectangle bounds;
	
	protected Form form;
	
	
	public EventNotificationPopup() {		
		this(new Shell(SWT.ON_TOP));
	}	
		
	public EventNotificationPopup(Shell parent) {		
			super(parent, SWT.ON_TOP, false, false, true, false, null /*title is handed in form */, FOOTER_TEXT);
		this.toolkit = new FormToolkit(parent.getDisplay());
	}
	
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		initializeBounds();
		return createDialogArea(parent);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		initializeComposite(parent);
		initializeForm();
		//initializeSections();
	
		createFormContents();
		
		parent.pack();
		return composite;
	}


	protected void createFormContents() {
		toolkit.createText(getBody(), "bar!");
		
//		Section section = toolkit.createSection(form.getBody(), getSectionStyleBits());
//		section.clientVerticalSpacing = 9;
//		section.setText("Locator");
	}

	protected Composite getBody() {
		return form.getBody();
	}
	
	public int getSectionStyleBits() {
		return ExpandableComposite.TITLE_BAR |/*|Section.DESCRIPTION|*/
				  Section.TWISTIE | Section.EXPANDED;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#initializeBounds()
	 */
	protected void initializeBounds() {
		getShell().setBounds(restoreBounds());
	}
	
	private void initializeComposite(Composite parent) {
		this.composite = (Composite) super.createDialogArea(parent);
	}
	
	private void initializeForm() {
		
		form = toolkit.createForm(composite);
		
		/* $codepro.preprocessor.if version >= 3.3 $ */
		toolkit.decorateFormHeading(form);
		/* $codepro.preprocessor.endif $ */
		
		form.setText(getPopupTitle());
		
		
		Image image = getPopupImage();
		if (!Platform.isRunning())
			image = null; //workaround to address strange initialization ordering issues.
		form.setImage(image);

		//addToolbarActions();
		//addMenuActions();
		form.getToolBarManager().update(true);

		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 10;
		layout.verticalSpacing = 10;
		getBody().setLayout(layout);
	}

	
	private Rectangle restoreBounds() {
	bounds = getShell().getBounds();
	Rectangle maxBounds = null;

	IWorkbenchWindow window = getActiveWindow();
	if (window != null) {
		maxBounds = window.getShell().getMonitor().getClientArea();
	} else {
		// fallback
		Display display = Display.getCurrent();
		if (display == null)
			display = Display.getDefault();
		if (display != null && !display.isDisposed())
			maxBounds = display.getPrimaryMonitor().getClientArea();
	}

	if (bounds.width > -1 && bounds.height > -1) {
		if (maxBounds != null) {
			bounds.width = Math.min(bounds.width, maxBounds.width);
			bounds.height = Math.min(bounds.height, maxBounds.height);
		}
		// Enforce an absolute minimal size
		bounds.width = Math.max(bounds.width, 30);
		bounds.height = Math.max(bounds.height, 30);
	}

	if (bounds.x > -1 && bounds.y > -1 && maxBounds != null) {
		// bounds.x = Math.max(bounds.x, maxBounds.x);
		// bounds.y = Math.max(bounds.y, maxBounds.y);

		if (bounds.width > -1 && bounds.height > -1) {
			bounds.x = maxBounds.x + maxBounds.width - bounds.width;
			bounds.y = maxBounds.y + maxBounds.height - bounds.height;
		}
	}

	return bounds;
}


	private IWorkbenchWindow getActiveWindow() {
		try {
			return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		} catch(Throwable e) {
			return null;
		}
	}
	

	protected String getPopupTitle() {
		return "Event Recorded";
	}
	
	protected Image getPopupImage() {
		return null;
	}
	
	

}
