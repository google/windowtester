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
package com.windowtester.eclipse.ui.target;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.windowtester.eclipse.ui.UiPlugin;
import com.windowtester.internal.debug.LogHandler;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;

@SuppressWarnings({"deprecation", "unchecked"})
public class NewTargetProvisionerPage extends WizardPage implements Listener {

	private static final String DESCRIPTION = "Create a new WindowTester File System Target Provisioner.";
	private static final String TITLE = "WindowTester Target Provisioner";
	
	protected static final int COMBO_HISTORY_LENGTH = 5;
	
	private static final String SELECT_DESTINATION_MESSAGE = "Select a parent directory.";
	private static final String SELECT_DESTINATION_TITLE = "Provisioner Destination";
	
	private static final String STORE_DESTINATION_NAMES_ID = "destination.names";
    private static final String STORE_RECORDING_CONTENT    = "recording.content";
	private static final String STORE_RUNTIME_CONTENT      = "runtime.content";
	
	private static final String CREATION_ERROR_MSG = "Creation Error";
	private static final String HELP_CONTEXT_ID = "com.windowtester.eclipse.help.wt_ui_context";

	private Combo destination;
	private Button recordingButton;
	private Button runtimeButton;
	private Button browseButton;
	
	private final SettingsManager settings = new SettingsManager();
	private final ITargetProvisionerFileProvider fileProvider;
	
	private Button gefSupportButton;
	
	private class SettingsManager {

		protected void restoreWidgetValues() {
	        IDialogSettings settings = getDialogSettings();
	        if (settings == null)
	        	return;
	        
	        restoreDestinations(settings);
	        restoreContents(settings);
	    }

		private void restoreContents(IDialogSettings settings) {
            recordingButton.setSelection(settings
                    .getBoolean(STORE_RECORDING_CONTENT));
            runtimeButton.setSelection(settings
                    .getBoolean(STORE_RUNTIME_CONTENT));
		}

		private void restoreDestinations(IDialogSettings settings) {
			String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
	        if (directoryNames == null || directoryNames.length == 0) {
				return;
			}
	        for (int i = 0; i < directoryNames.length; i++) {
				destination.add(directoryNames[i], i);
			}
	        destination.select(0);
		}
		
	    protected void saveWidgetValues() {
	    	
	    	IDialogSettings settings = getDialogSettings();
	        if (settings == null)
	        	return;
	        
	        saveDestinationHistory(settings);
	        saveContentTypes(settings);
	   }

		private void saveContentTypes(IDialogSettings settings) {
            // options
            settings.put(STORE_RECORDING_CONTENT,
                    recordingButton.getSelection());

            settings.put(STORE_RUNTIME_CONTENT,
                    runtimeButton.getSelection());
		}

		private void saveDestinationHistory(IDialogSettings settings) {
			String[] directoryNames = settings.getArray(STORE_DESTINATION_NAMES_ID);
	        if (directoryNames == null) {
				directoryNames = new String[0];
			}

	        directoryNames = addToHistory(directoryNames, getDestinationValue());
	        settings.put(STORE_DESTINATION_NAMES_ID, directoryNames);
		}
	    
	    /**
	     * Adds an entry to a history, while taking care of duplicate history items
	     * and excessively long histories.  The assumption is made that all histories
	     * should be of length <code>WizardDataTransferPage.COMBO_HISTORY_LENGTH</code>.
	     *
	     * @param history the current history
	     * @param newEntry the entry to add to the history
	     */
		protected String[] addToHistory(String[] history, String newEntry) {
	        java.util.ArrayList l = new java.util.ArrayList(Arrays.asList(history));
	        addToHistory(l, newEntry);
	        String[] r = new String[l.size()];
	        l.toArray(r);
	        return r;
	    }

	    /**
	     * Adds an entry to a history, while taking care of duplicate history items
	     * and excessively long histories.  The assumption is made that all histories
	     * should be of length <code>WizardDataTransferPage.COMBO_HISTORY_LENGTH</code>.
	     *
	     * @param history the current history
	     * @param newEntry the entry to add to the history
	     */
	    protected void addToHistory(List history, String newEntry) {
	        history.remove(newEntry);
	        history.add(0, newEntry);

	        // since only one new item was added, we can be over the limit
	        // by at most one item
	        if (history.size() > COMBO_HISTORY_LENGTH) {
				history.remove(COMBO_HISTORY_LENGTH);
			}
	    }
	}
	
	
	/**
	 * Create the wizard
	 * @param structuredSelection 
	 * @param string 
	 */
	public NewTargetProvisionerPage(String string, IStructuredSelection structuredSelection, ITargetProvisionerFileProvider fileProvider) {
		super("wizardPage");
		this.fileProvider = fileProvider;
		setTitle(TITLE);
		setDescription(DESCRIPTION);
		setImageDescriptor(getPageImageDescriptor());
	}

	private ImageDescriptor getPageImageDescriptor() {
		return UiPlugin.imageDescriptor("icons/full/wizban/newTargetProvisioner.png");
	}

	/**
	 * Create contents of the wizard
	 * @param parent
	 */
	public void createControl(Composite parent) {
		doCreateComposite(parent);
		addListeners();
		setInitialWidgetValues();
		validatePage(); //does validation
    }

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */
	protected void configureShell(Shell newShell) {
		PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell, HELP_CONTEXT_ID);
	}
    
    
	private void doCreateComposite(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout());
		//
		setControl(container);

		final Composite contentsComposite = new Composite(container, SWT.NONE);
		contentsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		contentsComposite.setLayout(new GridLayout());

		final Group targetContentGroup = new Group(contentsComposite, SWT.NONE);
		final GridData gd_targetContentGroup = new GridData(SWT.FILL, SWT.CENTER, true, false);
		targetContentGroup.setLayoutData(gd_targetContentGroup);
		targetContentGroup.setText("Target Content");
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		targetContentGroup.setLayout(gridLayout_1);

		final Label targetContentDescriptionLabel = new Label(targetContentGroup, SWT.NONE);
		final GridData gd_targetContentDescriptionLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_targetContentDescriptionLabel.verticalIndent = 5;
		targetContentDescriptionLabel.setLayoutData(gd_targetContentDescriptionLabel);
		targetContentDescriptionLabel.setText("This target provisioner provides support for");

		final Label spacer = new Label(targetContentGroup, SWT.NONE);
		final GridData gd_spacer = new GridData(10, SWT.DEFAULT);
		spacer.setLayoutData(gd_spacer);

		recordingButton = new Button(targetContentGroup, SWT.RADIO);
		final GridData gd_recordingButton = new GridData();
		gd_recordingButton.verticalIndent = 5;
		recordingButton.setLayoutData(gd_recordingButton);
		recordingButton.setText("&Recording and Execution of WindowTester Tests");

		final Label spacer2 = new Label(targetContentGroup, SWT.NONE);
		final GridData gd_spacer2 = new GridData(10, SWT.DEFAULT);
		spacer2.setLayoutData(gd_spacer2);

		runtimeButton = new Button(targetContentGroup, SWT.RADIO);
		final GridData gd_runtimeButton = new GridData();
		runtimeButton.setLayoutData(gd_runtimeButton);
		runtimeButton.setText("E&xecution of WindowTester Tests");

		
		final Label optionalSupportLabel = new Label(targetContentGroup, SWT.NONE);
		final GridData gd_optionalSupportLabel = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gd_optionalSupportLabel.verticalIndent = 5;
		optionalSupportLabel.setLayoutData(gd_optionalSupportLabel);
		optionalSupportLabel.setText("Include support for");

		
		final Label spacer3 = new Label(targetContentGroup, SWT.NONE);
		final GridData gd_spacer3 = new GridData(10, SWT.DEFAULT);
		spacer3.setLayoutData(gd_spacer3);
			
		gefSupportButton = new Button(targetContentGroup, SWT.CHECK);
		final GridData gd_gefButton = new GridData();
		gd_gefButton.verticalIndent = 5;
		gefSupportButton.setLayoutData(gd_gefButton);
		gefSupportButton.setText("Graphical Editing Framework (GEF) Components");

		
		final Composite desinationComposite = new Composite(container, SWT.NONE);
		final GridData gd_desinationComposite = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_desinationComposite.widthHint = 461;
		desinationComposite.setLayoutData(gd_desinationComposite);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		desinationComposite.setLayout(gridLayout);

		final Label destinationLabel = new Label(desinationComposite, SWT.NONE);
		destinationLabel.setText("Provisioner &directory:");

		destination = new Combo(desinationComposite, SWT.NONE);
		final GridData gd_combo = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd_combo.widthHint = 156;
		destination.setLayoutData(gd_combo);

		browseButton = new Button(desinationComposite, SWT.NONE);
		browseButton.setText("B&rowse...");
		
		final Label label = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_label = new GridData(SWT.FILL, SWT.TOP, true, false);
		gd_label.verticalIndent = 8;
		label.setLayoutData(gd_label);
		
	}

	private void addListeners() {
		browseButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				browseForProvisionerDestination();
			}

			public void widgetSelected(SelectionEvent e) {
				browseForProvisionerDestination();
			}			
		});
		recordingButton.addListener(SWT.Selection, this);
		runtimeButton.addListener(SWT.Selection, this);
		destination.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				//System.out.println("combo modified");
				validatePage();
			}			
		});
	}

	private void setInitialWidgetValues() {
		settings.restoreWidgetValues();
		/*
		 * In case this is the first time, ensure there is a default set
		 */
		if (!isRecordingContentSelected() && !runtimeButton.getSelection())
			recordingButton.setSelection(true);
	}

	private void browseForProvisionerDestination() {
		
        DirectoryDialog dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE);
        dialog.setMessage(SELECT_DESTINATION_MESSAGE);
        dialog.setText(SELECT_DESTINATION_TITLE);
        //dialog.setFilterPath(getDestinationValue());
        String selectedDirectoryName = dialog.open();

        if (selectedDirectoryName != null) {
            setErrorMessage(null);
            setDestination(selectedDirectoryName);
        }
	}

	public void handleEvent(Event event) {
		validatePage();
	}

	private void validatePage() {
		setPageComplete(isPageValid());
	}
	

	private boolean isPageValid() {
		//System.out.println("validating page");
		if (!isRecordingContentSelected() && !runtimeButton.getSelection()) {
			setError("Please select a content type.");
			return false;
		}
		
		String text = destination.getText();
		
		if (text == null || text.trim().length() == 0) {
			setError("Please enter a destination directory.");
			return false;	
		}
		
		setError(null);
		return true;
	}

	private void setError(String msg) {
		setErrorMessage(msg);
		setMessage(msg);
	}

	private void setDestination(String browsedDestination) {
		destination.add(browsedDestination, 0);
		destination.select(0);
	}
	
	//there is no next page!
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage() {
		return false;
	}

	
    /**
     * Returns a boolean indicating whether the directory portion of the
     * passed pathname is valid and available for use.
     */
    protected boolean ensureTargetDirectoryIsValid(String fullPathname) {
        int separatorIndex = fullPathname.lastIndexOf(File.separator);

        if (separatorIndex == -1) {
			return true;
		}

        return ensureTargetIsValid(new File(fullPathname.substring(0,
                separatorIndex)));
    }
    
    protected boolean ensureTargetIsValid(File targetDirectory) {
        if (targetDirectory.exists() && !targetDirectory.isDirectory()) {
            displayErrorDialog("The destination directory must exist.");
            giveFocusToDestination();
            return false;
        }

        return ensureDirectoryExists(targetDirectory);
    }
    
    protected boolean ensureDirectoryExists(File directory) {
        if (!directory.exists()) {
            if (!queryYesNoQuestion("The destination directory does not exist.  Would you like to create it?")) {
				return false;
			}

            if (!directory.mkdirs()) {
                displayErrorDialog("An error was encountered creating the provisioner directory.");
                giveFocusToDestination();
                return false;
            }
        }

        return true;
    }
    
    protected boolean queryYesNoQuestion(String message) {
        MessageDialog dialog = new MessageDialog(getContainer().getShell(),
                "Question",
                (Image) null, message, MessageDialog.NONE,
                new String[] { IDialogConstants.YES_LABEL,
                        IDialogConstants.NO_LABEL }, 0);
        // ensure yes is the default

        return dialog.open() == 0;
    }
    
    protected void displayErrorDialog(String message) {
        MessageDialog.openError(getContainer().getShell(),
                "Provisioner Creation Error", message);
    }
    
    protected void giveFocusToDestination() {
        destination.setFocus();
    }

	public boolean finish() {
		if (!ensureTargetIsValid(getTargetDestination()))
			return false;
		boolean created = createProvisioner();
		if (!created)
			return false;
				
		settings.saveWidgetValues();
        return true;
	}

	
	public static IAdaptable getUIInfoAdapter(final Shell shell) {
		return new IAdaptable() {
			public Object getAdapter(Class clazz) {
				if (clazz == Shell.class) {
					return shell;
				}
				return null;
			}
		};
	}
	
	private boolean createProvisioner() {
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) {
				try {
					CreateTargetProvisionerOperation op = new CreateTargetProvisionerOperation(getTargetFiles(), getTargetDestination());
					PlatformUI.getWorkbench().getOperationSupport().getOperationHistory().execute(
									op, monitor, getUIInfoAdapter(getShell()));
				} catch (final Exception e) {
					getContainer().getShell().getDisplay().syncExec(
							new Runnable() {
								public void run() {
									if (e.getCause() instanceof CoreException) {
										ErrorDialog.openError(getContainer().getShell(), 
														CREATION_ERROR_MSG, null, ((CoreException) e.getCause()).getStatus());
									} else {
										LogHandler.log(e);
										MessageDialog.openError(getContainer().getShell(),
														CREATION_ERROR_MSG, e.getCause().getMessage());
									}
								}
							});
				}
			}
		};
		try {
			getContainer().run(true, true, op);
			return true;
		} catch (InterruptedException e) {
			//do nothing
		} catch (InvocationTargetException e) {
			LogHandler.log(e);
			MessageDialog.openError(getContainer().getShell(),
					CREATION_ERROR_MSG, e.getCause().getMessage());
			
		}
		return false;
	}

	protected File getTargetDestination() {
		String dest = (String) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return getDestinationValue();
			}
			
		});
		return new File(dest);
	}

	protected File[] getTargetFiles() throws IOException {
		return fileProvider.getTargetFiles(getTargetSpecification());
	}

	
	private TargetSpecification getTargetSpecification() {
		TargetSpecification spec = getBaseSpec();
		if (isGEFSupportSelected())
			return spec.withBundles(RequiredPlugins.GEF_SUPPORT);
		return spec;
	}

	/**
	 * @since 3.8.1
	 */
	private boolean isGEFSupportSelected() {
		return ((Boolean)DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(gefSupportButton.getSelection());
			}
		})).booleanValue();
	}

	private TargetSpecification getBaseSpec() {
		if (isRecordingContentSelected())
			return TargetSpecification.RECORDING.withBundles(RequiredPlugins.RUNTIME); //notice this includes both
		return TargetSpecification.EXECUTION;
	}

	private boolean isRecordingContentSelected() {
		return ((Boolean)DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return Boolean.valueOf(recordingButton.getSelection());
			}
		})).booleanValue();
	}

	private String getDestinationValue() {
		return destination.getText().trim();
	}
	
    

    
}
