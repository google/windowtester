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
package com.windowtester.eclipse.ui.assertions;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.windowtester.eclipse.ui.assertions.AssertablesSection.IListener;
import com.windowtester.eclipse.ui.dialogs.Mover;
import com.windowtester.eclipse.ui.inspector.ImageManager;
import com.windowtester.eclipse.ui.inspector.InspectorFormToolkit;
import com.windowtester.eclipse.ui.inspector.WidgetDescriptionLabelProvider;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.recorder.event.user.IWidgetDescription;

public class AssertionExpertPopup extends PopupDialog {

	public static interface PopupClosedCallback {
		void popupClosed();
		void assertionMade();
		void popupDismissed();
	}
		
	private static final PopupClosedCallback NULL_CALLBACK = new PopupClosedCallback() {
		public void popupClosed() {
			//no-op
		}
		public void assertionMade() {
			//no-op			
		}

		public void popupDismissed() {
			//no-op
		};
	};
	
//	private class CloseAction extends Action {
//		public ImageDescriptor getImageDescriptor() {
//			if (!Platform.isRunning()) {
//				Image image = ImageManager.getImage("delete.gif");
//				if (image == null)
//					return null;
//				return ImageDescriptor.createFromImage(image);
//			}
//			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
//		}
//
//		public String getToolTipText() {
//			return "Close";
//		}
//
//		public void run() {
//			close();
//		}
//	}
	
	private static final String FOOTER_TEXT = "Foo";
	
	private Composite composite;
	private final InspectorFormToolkit toolkit;
	
	private ScrolledForm form;
	private final IWidgetDescription widget;
	private final PopupClosedCallback callback;

	private Mover mover;

	private AssertablesSection assertables;

	private Button assertButton;

	private Button cancelButton;
	
	
	public AssertionExpertPopup(Shell parent, IWidgetDescription widget, PopupClosedCallback callback) {	
		super(parent, SWT.ON_TOP, false, false, true, false, null /*title is handed in form */, FOOTER_TEXT);
		this.widget = widget;
		this.toolkit = new InspectorFormToolkit();
		this.callback = callback != null ? callback : NULL_CALLBACK;
	}
	
	public AssertionExpertPopup(Shell parent, IWidgetDescription widget) {		
		this(parent, widget, NULL_CALLBACK);
	}
	
	private void initializeComposite(Composite parent) {
		composite = (Composite) super.createDialogArea(parent);
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#getInitialLocation(org.eclipse.swt.graphics.Point)
	 */
	protected Point getInitialLocation(Point initialSize) {
		Point widgetLocation = widget.getHoverPoint();
		if (widgetLocation == null)
			widgetLocation = Display.getDefault().getCursorLocation();
		return new Point(widgetLocation.x + 10, widgetLocation.y + 10);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		//TODO: if we want footer text, we'll need to do something like this:
		//composite = (Composite) super.createContents(parent);
		initializeComposite(parent);
		initializeForm();
		createFormContents();
		return composite;
	}
	
	
	private void createFormContents() {
		//form.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
		//form.setImage(ResourceManager.getPluginImage(Activator.getDefault(), "icons/spy.gif"));
		//form.setText("CLabel");
		Composite body = form.getBody();
		setFormLayout(body);
		createPropertiesSection(body);
		createActionButtons(body);
		setupListeners();
	}

	private void setupListeners() {
		assertables.addListener(new IListener() {
			public void stateChanged(Button[] buttons) {
				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].getSelection()) {
						assertButton.setEnabled(true);
						return;
					}
				}
				assertButton.setEnabled(false);
			}
		});
		cancelButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				dimissAndClose();
			}
			public void widgetSelected(SelectionEvent e) {
				dimissAndClose();
			}
		});
		assertButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				assertAndClose();
			}
			public void widgetSelected(SelectionEvent e) {
				assertAndClose();
			}
		});
		mover = Mover.forShell(getShell());
		form.getForm().getHead().addMouseListener(mover);
	}


	protected void dimissAndClose() {
		callback.popupDismissed();
		close();
	}

	private void doAssert() {
		assertables.apply();
		callback.popupClosed();
		callback.assertionMade();
	}

	
	private void setFormLayout(Composite body) {
		body.setLayout(new GridLayout());
		toolkit.paintBordersFor(body);
	}

	private void createPropertiesSection(Composite body) {
		assertables = new AssertablesSection().addTo(widget, form, toolkit);
	}

	private void createActionButtons(Composite body) {
		final Label separator = toolkit.createSeparator(body, SWT.HORIZONTAL);
		final GridData gd_separator = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_separator.verticalIndent = 12;
		separator.setLayoutData(gd_separator);
		final Composite buttons = toolkit.createComposite(body);
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		buttons.setLayout(gridLayout);
		toolkit.paintBordersFor(buttons);

		assertButton = toolkit.createButton(buttons, "Assert", SWT.NONE);
		assertButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		assertButton.setEnabled(false); //until assertion selected
		getShell().setDefaultButton(assertButton);
		
		cancelButton = toolkit.createButton(buttons, "Dismiss", SWT.NONE);
		cancelButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
	}

	private void initializeForm() {
		
		form = toolkit.createScrolledForm(composite);
		
		/* $codepro.preprocessor.if version >= 3.3 $ */
		toolkit.decorateFormHeading(form.getForm());
		/* $codepro.preprocessor.endif $ */
		
		form.setText(WidgetDescriptionLabelProvider.getDescription(widget));
		
		//form.setMessage("Foo", IMessageProvider.NONE);
		
		Image image = ImageManager.getImage("spy.gif");
		if (!Platform.isRunning())
			image = null; //workaround to address strange initialization ordering issues.
		form.setImage(image);

		//addToolbarActions();
		//addMenuActions();
		form.getToolBarManager().update(true);
		form.getBody().setLayout(new GridLayout());
	}

	private void assertAndClose() {
		doAssert();
		close();
	}

	public boolean contains(Point point) {
		Shell shell = getShell();
		if (shell == null)
			return false;
		Rectangle bounds = shell.getBounds();
		if (bounds == null)
			return false;
		return bounds.contains(point);
	}

}
