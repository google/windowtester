package com.windowtester.test.eclipse.locator;

import junit.framework.AssertionFailedError;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.pde.internal.ui.wizards.RenameDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.dialogs.SelectionStatusDialog;
import org.eclipse.ui.internal.MessageLine;

import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.ExceptionHandlingHelper;
import com.windowtester.runtime.swt.internal.locator.WidgetIdentifier;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.jface.DialogMessageLocator;
import com.windowtester.test.util.Serializer;

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
@SuppressWarnings("restriction")
public class DialogMessageLocatorSmokeTest extends UITestCaseSWT {

	//????: is this ambiguous?
	// status message vs. input message
	// "Enter name: " (prompt) vs. "Invalid" (status)
	// I think we want the status message...
	
//	public static void main(String[] args) {
//		Display.getDefault().syncExec(new Runnable() {
//			public void run() {
//				ErrorDialog.openError(new Shell(Display.getDefault()), "Oops...", "An error occurred", new Status(Status.ERROR, "foo", "Something unexpected happened."));
//			}
//		});
//	}
	
	
	
	//TODO: an input dialog...
	
	public void testInfoTextAssertion() throws WidgetSearchException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageDialog.openInformation(getShell(), "Info", "Something");
			}
		});
		
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("Info"));
		ui.assertThat(new DialogMessageLocator().hasText("Something"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Info"));
	}

	public void testInputDialogAssertion() throws WidgetSearchException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				InputDialog dialog = new InputDialog(getShell(), "Input", "Enter: ",  "", new IInputValidator() {
					public String isValid(String newText) {
						return "Does not compute";
					}
				});
				dialog.create();
				dialog.open();
			}
		});
		
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("Input"));
		ui.enterText("blah");
		ui.assertThat(new DialogMessageLocator().hasText("Does not compute"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Input"));
	}
	
	
//	public void testListDialogAssertion() throws WidgetSearchException {
//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				ListDialog dialog = new ListDialog(getShell());
//				dialog.setContentProvider(new ArrayContentProvider());
//				dialog.setLabelProvider(new LabelProvider());
//				dialog.setTitle("Selection Dialog");
//				dialog.setAddCancelButton(true);
//				dialog.setInput(Arrays.asList("one", "two"));
//				
////				(getShell(), "Input", "Enter: ",  "", new IInputValidator() {
////					public String isValid(String newText) {
////						return "Does not compute";
////					}
////				});
//				dialog.create();
//				dialog.open();
//			}
//		});
//		
//		IUIContext ui = getUI();
//		ui.wait(new ShellShowingCondition("Selection Dialog"));
//		ui.pause(3000);
//		ui.assertThat(new DialogMessageLocator().hasText("Does not compute"));
//		ui.click(new ButtonLocator("Cancel"));
//		ui.wait(new ShellDisposedCondition("Selection Dialog"));
//	}
	
	
	
	public void testInfoTextIdentification() throws WidgetSearchException {
		
		final Label[] label = new Label[1];
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
		        MessageDialog dialog = new MessageDialog(getShell(), "Info", null,
		        		"Something", MessageDialog.INFORMATION,
		                new String[] { IDialogConstants.OK_LABEL }, 0) {
		      
		        	public void create() {
		        		super.create();
		        		label[0] = this.messageLabel; //cache label
		        	}
		        };
		        dialog.open();
			}
		});
		

		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("Info"));
		
		assertIsMessageLocator(identify(label[0]));
		
		ui.assertThat(new DialogMessageLocator().hasText("Something"));
		ui.click(new ButtonLocator("OK"));
		ui.wait(new ShellDisposedCondition("Info"));
	}


	/**
	 * Instance of {@link SelectionStatusDialog}.
	 * @throws WidgetSearchException
	 */
	public void testRenameDialogTextAssertion() throws WidgetSearchException {
		assertTrue("This test must be run as a PDE test", org.eclipse.core.runtime.Platform.isRunning());

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				RenameDialog dialog = new RenameDialog(getShell(), "foo");
				dialog.setInputValidator(new IInputValidator() {
					public String isValid(String newText) {
						return "Does not compute";
					}
					
				});
				dialog.create();
				dialog.setTitle("MyRename");
				dialog.open();
			}
		});
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("MyRename"));
		ui.enterText("foo");
		ui.assertThat(new DialogMessageLocator().hasText("Does not compute"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("MyRename"));

	}
	
	public void testRenameDialogTextIdentification() throws WidgetSearchException {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				RenameDialog dialog = new RenameDialog(getShell(), "foo");
				dialog.setInputValidator(new IInputValidator() {
					public String isValid(String newText) {
						return "Does not compute";
					}
					
				});
				dialog.create();
				dialog.setTitle("MyRename");
				dialog.open();
			}
		});
		IUIContext ui = getUI();
		ui.wait(new ShellShowingCondition("MyRename"));
		ui.enterText("foo");
		IWidgetReference ref = (IWidgetReference)ui.find(new SWTWidgetLocator(MessageLine.class));
		
		assertIsMessageLocator(identify((Widget) ref.getWidget()));

		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("MyRename"));

	}
	
	public void testStreamOutAndIn() throws Exception {
		Serializer.serializeOutAndIn(new DialogMessageLocator());
	}
	
	
	private static void assertIsMessageLocator(IWidgetIdentifier locator) {
		try {
			assertTrue("expected DialogMessageLocator, got: " + locator, locator instanceof DialogMessageLocator);
		} catch (AssertionFailedError e) {
			//TODO: notice we need to do this! (yikes!) --- should we do this in assertThat?
			new ExceptionHandlingHelper(Display.getDefault(), false).closeOpenShells();
			throw e;
		}
	}


	private IWidgetIdentifier identify(Widget w) {
		return WidgetIdentifier.getInstance().identify(w);
	}

	Shell shell;
	
	private Shell getShell() {
		if (shell == null)
			shell = new Shell(Display.getDefault());
		return shell;
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		if (shell != null)
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					shell.dispose();
				}
			});
	}

	
}
