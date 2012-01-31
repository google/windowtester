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
package com.windowtester.eclipse.ui.updater;

import java.util.Map;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

/**
 * Displays product update details.
 * 
 * @author Phil Quitslund
 *
 */
public class UpdateDialog extends FormDialog {

	private Shell shell;
	private final String shellTitle;
	private final String updateDescription;
	private final Map<String, String> hrefToHelpResourceMap;

	private Point initialSize = null;
	
	/**
	 * Create an update dialog.
	 * @param shellTitle - the title 
	 * @param updateDescription - a description of the update (NOTE: this should contain at least one hyperlink!)
	 * @param contextId - the help context id identifying the help context to open when the hyperlink is clicked
	 * @param hrefToHelpResourceMap - a mapping of hrefs to associated help resources
	 */
	public UpdateDialog(String shellTitle, String updateDescription, Map<String, String> hrefToHelpResourceMap) {
		super(getParent());
		this.shellTitle = shellTitle;
		this.updateDescription = updateDescription;
		this.hrefToHelpResourceMap = hrefToHelpResourceMap;
	}
	
	public UpdateDialog setSize(Point initialSize) {
		this.initialSize = initialSize;
		return this;
	}
	
	private static Shell getParent() {
		Shell parent = getActiveWorkbenchShell();
		if (parent != null)
			return parent;
		return new Shell(Display.getDefault());
	}
	
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public static IWorkbench getWorkbench() {
	    return PlatformUI.getWorkbench();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getWorkbench().getActiveWorkbenchWindow();
	}
	
	public static Shell getActiveWorkbenchShell() {
		 IWorkbenchWindow window= getActiveWorkbenchWindow();
		 if (window != null) {
		 	return window.getShell();
		 }
		 return null;
	}
	
	
	@Override
	protected void configureShell(Shell shell) {
		this.shell = shell;
		super.configureShell(shell);
		shell.setText(shellTitle);
//	    PlatformUI.getWorkbench().getHelpSystem().setHelp(shell,contextId);
	}
	
	@Override
	public boolean isHelpAvailable() {
		return false; //don't draw a help button
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK 
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);	
	}

	@Override
	protected void initializeBounds() {
		super.initializeBounds();
		if (initialSize != null)
			shell.setSize(initialSize);
	}
	
//	private void helpPressed() {
//    	if (getShell() != null) {
////	    	Control c = getShell().getDisplay().getFocusControl();
////	    	while (c != null) {
////	    		if (c.isListening(SWT.Help)) {
////	    			c.notifyListeners(SWT.Help, new Event());
////	    			break;
////	    		}
////	    		c = c.getParent();
////	    	}
//    		
//	    	IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
//    		//works
////    		helpSystem.displayHelp("com.windowtester.eclipse.help.api_migration");
//    		//pops up a separate info center (not desired)
//    		helpSystem.displayHelpResource("/com.windowtester.eclipse.help/html/reference/API%20Migration.html");
//    	}
//	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void createFormContent(IManagedForm mform) {
		ScrolledForm form = mform.getForm();
		Composite body = form.getBody();
			
		FormToolkit toolkit = mform.getToolkit();
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 15;
		layout.topMargin         = 10;	
		
		Image image = getImage();
		Label imageLabel = new Label(body, SWT.NULL);
		imageLabel.setImage(image);
		imageLabel.setBackground(form.getBackground());
		
		FormText formText = toolkit.createFormText(
				body, true);
		
		body.setLayout(layout);
		formText.setText(getFormText(), true, true);
		formText.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				hrefPressed(e.getHref());
//				helpPressed();
			}
		});
		formText.setColor("header", toolkit.getColors().
			     getColor(FormColors.TITLE));
		formText.setFont("header", JFaceResources.getHeaderFont());
	}
	
	protected void hrefPressed(Object href) {
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		String res = mapHrefToHelpResource(href);
		if (res == null)
			return;
		helpSystem.displayHelpResource(res);
	}

	protected String mapHrefToHelpResource(Object href) {
		return hrefToHelpResourceMap.get(href);
	}

	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.RESIZE;
	}
	
	
	public final String getFormText() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$
//header experiment		
//		buffer.append("<p><span color=\"header\" font=\"header\">"+
//		   "API Migration</span></p>");		
//		buffer.append("<p>");
		buffer.append(updateDescription);
//		buffer.append("</p>");
		buffer.append("</form>"); //$NON-NLS-1$
		return buffer.toString();
	}
	
	
	protected Image getImage() {
		return getSWTImage(SWT.ICON_INFORMATION);
	}
	
	private Image getSWTImage(final int imageID) {
		Shell shell = getShell();
		final Display display;
		if (shell == null || shell.isDisposed()) {
			shell = getParentShell();
		}
		if (shell == null || shell.isDisposed()) {
			display = Display.getCurrent();
		} else {
			display = shell.getDisplay();
		}

		final Image[] image = new Image[1];
		display.syncExec(new Runnable() {
			public void run() {
				image[0] = display.getSystemImage(imageID);
			}
		});

		return image[0];
	}
	
	
}
