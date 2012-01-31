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
package com.windowtester.eclipse.ui_tool;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog with a details button.
 * <p>
 * @author Dan Rubel
 * @version $Revision: 1.2 $
 */
public abstract class AbstractDetailsDialog extends Dialog {

	/**
	 * The title of the dialog 
	 */
	private final String title;

	/** 
	 * The message to display 
	 */
	private final String message;

	/** 
	 * The image to dsplay 
	 */
	private final Image image;

	/** 
	 * The details button 
	 */
	private Button detailsButton;
	
	/** 
	 * The details area 
	 */
	private Control detailsArea;
	
	/** 
	 * The window size for the alternate state 
	 */
	private Point cachedWindowSize;

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a dialog showing the specified message.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * 
	 * @param parentShell the shell under which to create this dialog
	 * @param title the title to use for this dialog,
	 *		or <code>null</code> to indicate that the default title should be used
	 * @param image the image to appear to the left of the dialog
	 * 		or <code>null</code> for no image
	 * @param message the dialog message
	 */
	public AbstractDetailsDialog(Shell parentShell, String title, DialogImage image, String message) {
		this(parentShell, title, image.getImage(), message);
	}

	/**
	 * Creates a dialog showing the specified message.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * 
	 * @param parentShell the shell under which to create this dialog
	 * @param title the title to use for this dialog,
	 *		or <code>null</code> to indicate that the default title should be used
	 * @param image the image to appear to the left of the dialog
	 * 		or <code>null</code> for no image
	 * @param message the dialog message
	 */
	public AbstractDetailsDialog(Shell parentShell, String title, Image image, String message) {
		super(parentShell);
		
		this.title = title;
		this.image = image;
		this.message = message;
		
		setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// UI creation and event handling
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Override superclass implementation to handle details button.
	 * 
	 * @param id the id of the button that was pressed
	 */
	protected void buttonPressed(int id) {
		if (id == IDialogConstants.DETAILS_ID) { // was the details button pressed?
			toggleDetailsArea();
		}
		else {
			super.buttonPressed(id);
		}
	}

	/**
	 * Override the superclass implementation to set the dialog's title
	 * 
	 * @param shell the dialog's shell
	 */
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}

	/**
	 * Override superclass implementation to create OK and Details buttons
	 * but no cancel button.
	 * 
	 * @param parent the button bar
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
	}

	/**
	 * Creates and returns the contents of the upper part 
	 * of the dialog (above the button bar).
	 * 
	 * @param parent the parent of the dialog area
	 * @return the dialog area (above the button bar)
	 */
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		if (image != null) {
			((GridLayout) composite.getLayout()).numColumns = 2;
			Label label = new Label(composite, 0);
			image.setBackground(label.getBackground());
			label.setImage(image);
			label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.VERTICAL_ALIGN_BEGINNING));
		}

		// create message
		Label label = new Label(composite, SWT.WRAP);
		if (message != null)
			label.setText(message);
		GridData data = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		return composite;
	}

	/**
	 * Toggles the unfolding of the details area.  This is triggered by
	 * the user pressing the details button.
	 */
	protected void toggleDetailsArea() {
		Point oldWindowSize = getShell().getSize();
		Point newWindowSize = cachedWindowSize;
		cachedWindowSize = oldWindowSize;

		// show the details area
		if (detailsArea == null) {
			detailsArea = createDetailsArea((Composite) getContents());
			detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
		}
		
		// hide the details area
		else {
			detailsArea.dispose();
			detailsArea = null;
			detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
		}
		
		/*
		 * Must be sure to call getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT)
		 * before calling getShell().setSize(newWindowSize);
		 * since controls have been added or removed
		 */
			
		// compute the new window size
		Point oldSize = getContents().getSize();
		Point newSize = getContents().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		if (newWindowSize == null)
			newWindowSize = new Point(oldWindowSize.x, oldWindowSize.y + (newSize.y - oldSize.y));
		
		// crop new window size to screen
		Point windowLoc = getShell().getLocation();
		Rectangle screenArea = getContents().getDisplay().getClientArea();
		if (newWindowSize.y > screenArea.height - (windowLoc.y - screenArea.y))
			newWindowSize.y = screenArea.height - (windowLoc.y - screenArea.y);
		
		getShell().setSize(newWindowSize);
		((Composite) getContents()).layout();
	}

	/**
	 * Create the details area with content.
	 * 
	 * @param parent the parent of the details area
	 * @return the details area
	 */
	protected abstract Control createDetailsArea(Composite parent);

}
