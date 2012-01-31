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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


/**
 * A dialog to display an error message, 
 * product and provider information,
 * plus the details of the exception itself.
 * A details button shows or hides an error details viewer.
 * <p>
 * @author Dan Rubel
 * @version $Revision: 1.2 $
 */
public class ExceptionDetailsDialog extends AbstractDetailsDialog {

	
	/**
	 * The details to be shown
	 * ({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 */
	private final Object details;
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Creates a dialog showing the specified message and exception.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * 
	 * @param parentShell the shell under which to create this dialog
	 * @param title the title to use for this dialog,
	 *		or <code>null</code> to indicate that the default title should be used
	 * @param message the dialog message (not null)
	 * @param details the details to be displayed.
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 */
	public ExceptionDetailsDialog(Shell parentShell, String title, String message, Object details) {
		this(parentShell, title, getDialogImage(details), message, details);
	}

	/**
	 * Creates a dialog showing the specified message and exception.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * 
	 * @param parentShell the shell under which to create this dialog
	 * @param title the title to use for this dialog,
	 *		or <code>null</code> to indicate that the default title should be used
	 * @param image the image to appear to the left of the dialog
	 * 		or <code>null</code> for no image
	 * @param message the dialog message (not null)
	 * @param details the details to be displayed
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 */
	public ExceptionDetailsDialog(Shell parentShell, String title, DialogImage image, String message, Object details) {
		this(parentShell, title, image != null ? image.getImage() : null, message, details);
	}

	/**
	 * Creates a dialog showing the specified message and exception.
	 * Note that the dialog will have no visual representation (no widgets)
	 * until it is told to open.
	 * 
	 * @param parentShell the shell under which to create this dialog
	 * @param title the title to use for this dialog,
	 *		or <code>null</code> to indicate that the default title should be used
	 * @param image the image to appear to the left of the dialog
	 * 		or <code>null</code> for no image
	 * @param message the dialog message
	 * @param details the details to be displayed
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 */
	public ExceptionDetailsDialog(Shell parentShell, String title, Image image, String message, Object details) {
		super(parentShell, getTitle(title, details), image, getMessage(message, details));
		this.details = details;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// UI creation and event handling
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Create the details area with content.
	 * 
	 * @param parent the parent of the details area
	 * @return the details area
	 */
	protected Control createDetailsArea(Composite parent) {
		
		// create the details area
		Composite panel = new Composite(parent, SWT.NONE);
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		panel.setLayout(layout);
		
		// create the details content
		createProductInfoArea(panel);
		createDetailsViewer(panel);
		
		return panel;
	}

	/**
	 * Create the product information area
	 * 
	 * @param parent the parent of the product information area
	 * @return the product information area
	 */
	protected Composite createProductInfoArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayoutData(new GridData());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		composite.setLayout(layout);

		new Label(composite, SWT.NONE).setText("Provider:");
		new Label(composite, SWT.NONE).setText("Google, Inc.");
		new Label(composite, SWT.NONE).setText("Plug-in Name:");
		new Label(composite, SWT.NONE).setText("WindowTester-Pro");
//		new Label(composite, SWT.NONE).setText("Plug-in ID:");
//		new Label(composite, SWT.NONE).setText(product.getPluginId());
//		new Label(composite, SWT.NONE).setText("Version:");
//		new Label(composite, SWT.NONE).setText(product.getVersion().toString());

		return composite;
	}

	/**
	 * Create the details viewer with content.
	 * 
	 * @param parent the parent of the details viewer
	 * @return the details viewer or <code>null</code> if none created
	 */
	protected Control createDetailsViewer(Composite parent) {
		if (details == null)
			return null;

		Text text = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// Create the content
		StringWriter writer = new StringWriter(1000);
		if (details instanceof Throwable)
			appendException(new PrintWriter(writer), (Throwable) details);
		else if (details instanceof IStatus)
			appendStatus(new PrintWriter(writer), (IStatus) details, 0);
		text.setText(writer.toString());
		
		return text;
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Utility
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Answer the title.
	 * 
	 * @param title the title or <code>null</code>
	 * @param details the details to be displayed
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 * @return the title (not <code>null</code>)
	 */
	public static String getTitle(String title, Object details) {
		if (title != null)
			return title;
		if (details instanceof Throwable) {
			Throwable e = (Throwable) details;
			while (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();
			String name = e.getClass().getName();
			return name.substring(name.lastIndexOf('.') + 1);
		}
		return "Exception";
	}

	/**
	 * Answer the image for the specified level of detail.
	 * 
	 * @param details the details to be displayed.
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 * @return the dialog image
	 */
	public static DialogImage getDialogImage(Object details) {
		if (details instanceof IStatus) {
			switch (((IStatus) details).getSeverity()) {
				case IStatus.ERROR : 
					return DialogImage.ERROR;
				case IStatus.WARNING : 
					return DialogImage.WARNING;
				case IStatus.INFO : 
					return DialogImage.INFO;
				case IStatus.OK :
					return null;
			}
		}
		return DialogImage.ERROR;
	}

	/**
	 * Answer the message.
	 * 
	 * @param message the message or <code>null</code>
	 * @param details the details to be displayed
	 * 		({@link Exception}, {@link IStatus}, or <code>null</code> if no details)
	 * @return the message (not <code>null</code>)
	 */
	public static String getMessage(String message, Object details) {
		if (details instanceof Throwable) {
			Throwable e = (Throwable) details;
			while (e instanceof InvocationTargetException)
				e = ((InvocationTargetException) e).getTargetException();
			if (message == null)
				return e.toString();
			return MessageFormat.format(message, new Object[] {e.toString()});
		}
		if (details instanceof IStatus) {
			String statusMessage = ((IStatus) details).getMessage();
			if (message == null)
				return statusMessage;
			return MessageFormat.format(message, new Object[] {statusMessage});
		}
		if (message != null)
			return message;
		return "An Exception occurred.";
	}

	/**
	 * Append the exception information to the writer.
	 * 
	 * @param writer the writer to contain the stack trace
	 * @param ex the exception
	 */
	public static void appendException(PrintWriter writer, Throwable ex) {
		if (ex instanceof CoreException) {
			appendStatus(writer, ((CoreException) ex).getStatus(), 0);
			writer.println();
		}
		appendStackTrace(writer, ex);
		if (ex instanceof InvocationTargetException)
			appendException(writer, ((InvocationTargetException) ex).getTargetException());
	}
	
	/**
	 * Append the status information to the writer.
	 * 
	 * @param writer the writer to contain the stack trace
	 * @param status the status object
	 * @param nesting the indent level
	 */
	public static void appendStatus(PrintWriter writer, IStatus status, int nesting) {
		for (int i = 0; i < nesting; i++)
			writer.print("  "); //$NON-NLS-1$
		writer.println(status.getMessage());
		IStatus[] children = status.getChildren();
		for (int i = 0; i < children.length; i++)
			appendStatus(writer, children[i], nesting + 1);
	}
	
	/**
	 * Append the stack trace for the specified exception to the writer.
	 * 
	 * @param writer the writer to contain the stack trace
	 * @param ex the exception
	 */
	public static void appendStackTrace(PrintWriter writer, Throwable ex) {
		ex.printStackTrace(writer);
	}
	
	////////////////////////////////////////////////////////////////////////////
	//
	// Testing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Test this dialog
	 */
	public static void test() {
		new ExceptionDetailsDialog(null, "Test without exception", DialogImage.ERROR, "Test this dialog with a null exception", null).open(); //$NON-NLS-1$  //$NON-NLS-2$
		new ExceptionDetailsDialog(null, null, (Image) null, null, null).open();
		try {
			throw new RuntimeException("Test " + ExceptionDetailsDialog.class.getName()); //$NON-NLS-1$
		}
		catch (Exception e) {
			new ExceptionDetailsDialog(null, "Test with exception", DialogImage.ERROR, "Test this dialog with an exception.  The exception is {0}", e).open(); //$NON-NLS-1$  //$NON-NLS-2$
			new ExceptionDetailsDialog(null, null, DialogImage.ERROR, null, e).open();
			try {
				throw new InvocationTargetException(e);
			}
			catch (Exception e2) {
				new ExceptionDetailsDialog(null, null, DialogImage.ERROR, null, e2).open();
			}
		}
	}
	
	/**
	 * Answer an action to test this dialog.
	 * 
	 * @return the test action
	 */
	public static IAction getTestAction() {
		return new Action("Test " + ExceptionDetailsDialog.class.getName()) {
			public void run() {
				test();
			}

		};
	}
}