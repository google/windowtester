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
package com.windowtester.codegen.debug;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;


import com.swtdesigner.ResourceManager;
import com.windowtester.codegen.CodeGenPlugin;

/**
 * Displays debugging information to the user after a (failed) recording session.
 */
public class DebugRecordingDialog extends Dialog
{

	class DebugRecordingClasspathLabelProvider extends LabelProvider
	{
		public String getText(Object element) {
			return super.getText(element);
		}

		public Image getImage(Object element) {
			return null;
		}
	}

	private static final int COPY_TO_CLIPBOARD_ID = 10;

	private TreeViewer classpathTreeViewer;
	private Tree classpathTree;
	private Text recLogText;
	private Text devLogText;

	/**
	 * Create the dialog
	 * 
	 * @param parentShell
	 */
	public DebugRecordingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog
	 * 
	 * @param parent
	 */
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout());

		final Label ifTheRecordingLabel = new Label(container, SWT.NONE);
		final GridData gd_ifTheRecordingLabel = new GridData();
		gd_ifTheRecordingLabel.horizontalIndent = 10;
		ifTheRecordingLabel.setLayoutData(gd_ifTheRecordingLabel);
		ifTheRecordingLabel
			.setText("\nIf the recording did not start properly or there was an exception during the recording process,\nuse the following information to diagnose the problem...\n ");

		final CTabFolder tabFolder = new CTabFolder(container, SWT.BORDER);
		tabFolder.setSimple(false);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final CTabItem classpathTabItem = new CTabItem(tabFolder, SWT.NONE);
		classpathTabItem.setImage(ResourceManager.getPluginImage(CodeGenPlugin.getDefault(),
			"icons/full/obj16/classpath.gif"));
		classpathTabItem.setText("Classpath");

		final Composite classpathComposite = new Composite(tabFolder, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		classpathComposite.setLayout(gridLayout);
		classpathTabItem.setControl(classpathComposite);

		classpathTreeViewer = new TreeViewer(classpathComposite, SWT.BORDER);
		classpathTreeViewer.setLabelProvider(new DebugRecordingClasspathLabelProvider());
		classpathTreeViewer.setContentProvider(new DebugRecordingClasspathContentProvider());
		classpathTree = classpathTreeViewer.getTree();
		final GridData gd_classpathTree = new GridData(SWT.FILL, SWT.FILL, true, true);
		classpathTree.setLayoutData(gd_classpathTree);

		final CTabItem recLogTabItem = new CTabItem(tabFolder, SWT.NONE);
		recLogTabItem.setImage(ResourceManager.getPluginImage(CodeGenPlugin.getDefault(), "icons/full/obj16/log.gif"));
		recLogTabItem.setText("Recording Log");

		final Composite recLogComposite = new Composite(tabFolder, SWT.NONE);
		recLogComposite.setLayout(new GridLayout());
		recLogTabItem.setControl(recLogComposite);

		recLogText = new Text(recLogComposite, SWT.V_SCROLL | SWT.BORDER);
		recLogText.setText("Reading recording log file...");
		recLogText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		final CTabItem devLogTabItem = new CTabItem(tabFolder, SWT.NONE);
		devLogTabItem.setImage(ResourceManager.getPluginImage(CodeGenPlugin.getDefault(), "icons/full/obj16/log.gif"));
		devLogTabItem.setText("Development Log");

		final Composite devLogComposite = new Composite(tabFolder, SWT.NONE);
		devLogComposite.setLayout(new GridLayout());
		devLogTabItem.setControl(devLogComposite);

		devLogText = new Text(devLogComposite, SWT.V_SCROLL | SWT.BORDER);
		devLogText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		devLogText.setText("Reading development log file...");

		initContent();

		return container;
	}

	/**
	 * Start a background process to read and display the log text
	 */
	private void initContent() {
		classpathTreeViewer.setInput(DebugRecordingInfo.getInfo());
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				classpathTreeViewer.expandAll();
			}
		});
		new Job("Read Recording Log") {
			protected IStatus run(IProgressMonitor monitor) {
				final String text = DebugRecordingInfo.getInfo().getRecorderLogContent();
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						recLogText.setText(text);
					}
				});
				return Status.OK_STATUS;
			}
		}.schedule();
		new Job("Read Development Log") {
			protected IStatus run(IProgressMonitor monitor) {
				final String text = DebugRecordingInfo.getInfo().getDevelopmentLogContent();
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						devLogText.setText(text);
					}
				});
				return Status.OK_STATUS;
			}
		}.schedule();
	}

	/**
	 * Create contents of the button bar
	 * 
	 * @param parent
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, COPY_TO_CLIPBOARD_ID, "Copy to Clipboard", false);
		createButton(parent, IDialogConstants.OK_ID, "Close", true);
	}

	/**
	 * Return the initial size of the dialog
	 */
	protected Point getInitialSize() {
		return new Point(825, 562);
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Recording Information");
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == COPY_TO_CLIPBOARD_ID) {
			DebugRecordingInfo.getInfo().copyToClipboard();
			return;
		}
		super.buttonPressed(buttonId);
	}
}
