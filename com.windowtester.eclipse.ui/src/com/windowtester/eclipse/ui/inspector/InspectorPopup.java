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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.windowtester.eclipse.ui.dialogs.Mover;
import com.windowtester.eclipse.ui.usage.ProfiledAction;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.recorder.event.user.IWidgetDescription;
import com.windowtester.runtime.swt.internal.display.DisplayExec;

public class InspectorPopup extends PopupDialog {

	public static interface PopupClosedCallback {
		void popupClosed();
	}
		
	private static final PopupClosedCallback NULL_CALLBACK = new PopupClosedCallback() {
		public void popupClosed() {
			//no-op
		};
	};
	
	private class CloseAction extends ProfiledAction {
		public ImageDescriptor getImageDescriptor() {
			if (!Platform.isRunning()) {
				Image image = ImageManager.getImage("delete.gif");
				if (image == null)
					return null;
				return ImageDescriptor.createFromImage(image);
			}
			return PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE);
		}

		public String getToolTipText() {
			return "Close";
		}

		/* (non-Javadoc)
		 * @see com.windowtester.eclipse.ui.usage.ProfiledAction#doRun()
		 */
		public void doRun() {
			close();
		}
	}
	
	private static final String FOOTER_TEXT = null;
	
	private Composite composite;
	private final InspectorFormToolkit toolkit;
	
	private ScrolledForm form;
	private final IWidgetDescription widget;
	private final PopupClosedCallback callback;

	private Mover mover;
	
	
	@SuppressWarnings("deprecation")
	public InspectorPopup(Shell parent, IWidgetDescription widget, PopupClosedCallback callback) {	
		super(parent, SWT.ON_TOP, false, false, true, false, null /*title is handed in form */, FOOTER_TEXT);
		this.widget = widget;
		this.toolkit = new InspectorFormToolkit();
		this.callback = callback != null ? callback : NULL_CALLBACK;
	}
	
	public InspectorPopup(Shell parent, IWidgetDescription widget) {		
		this(parent, widget, NULL_CALLBACK);
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
	 * @see org.eclipse.jface.window.Window#create()
	 */
	public void create() {
		super.create();
		addListeners();
		startCloseTimer();
	}
	
	
	private void startCloseTimer() {
		DisplayExec.sync(new Runnable() {
			Runnable runnable = new Runnable() {
				public void run() {
					Display display = Display.getDefault();
					try {
						Point cursorLocation = display.getCursorLocation();
						Shell shell = InspectorPopup.this.getShell();
						if (shell == null) {
							doClose(display);
							return;
						}
						Rectangle bounds = shell.getBounds();
						if (bounds == null) {
							doClose(display);
							return;
						}
						if (bounds.contains(cursorLocation) || mover.isMoving() ) {
							startTimer(display);		
						} else {
							doClose(display);
						}
					} catch (Throwable th) {  //<--- safety: just close
						doClose(display);
					}
				}

				private void startTimer(Display display) {
					display.timerExec(2000, this);
				}

				private void doClose(Display display) {
					display.timerExec(-1, this);
					InspectorPopup.this.close();
				}
			};
			public void run() {
				Display.getDefault().timerExec(3000, runnable);
			}
		});
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createContents(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createContents(Composite parent) {
		getShell().setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		initializeBounds();
		return createDialogArea(parent);
	}
	
	private void addListeners() {
		mover = Mover.forShell(getShell());
		form.getForm().getHead().addMouseListener(mover);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent) {
		initializeComposite(parent);
		initializeForm();
		initializeSections();
	
		parent.pack();
		return composite;
	}


	private void initializeSections() {
		
		new LocatorSection().addTo(widget, form, toolkit);
		new PropertiesSection().addTo(widget, form, toolkit);
		//TODO: consider putting this in menu?
		//new DebugSection().addTo(widget, form, toolkit);		

	}


	private void initializeComposite(Composite parent) {
		this.composite = (Composite) super.createDialogArea(parent);
	}


	private void initializeForm() {
		
		form = toolkit.createScrolledForm(composite);
		
		/* $codepro.preprocessor.if version >= 3.3 $ */
		toolkit.decorateFormHeading(form.getForm());
		/* $codepro.preprocessor.endif $ */
		
		form.setText(WidgetDescriptionLabelProvider.getDescription(widget));
		
		Image image = ImageManager.getImage("spy.gif");
		if (!Platform.isRunning())
			image = null; //workaround to address strange initialization ordering issues.
		form.setImage(image);

		addToolbarActions();
		addMenuActions();
		form.getToolBarManager().update(true);

		TableWrapLayout layout = new TableWrapLayout();
		layout.leftMargin = 10;
		layout.rightMargin = 10;
		layout.topMargin = 10;
		layout.verticalSpacing = 10;
		form.getBody().setLayout(layout);
	}


	private void addMenuActions() {
		// TODO how to do this?
	}


	private void addToolbarActions() {
		form.getToolBarManager().add(new CloseAction());
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
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.PopupDialog#close()
	 */
	public boolean close() {
		System.out.println("InspectorPopup.close()");
		boolean closed = super.close();
		callback.popupClosed();
		return closed;
	}
	
}
