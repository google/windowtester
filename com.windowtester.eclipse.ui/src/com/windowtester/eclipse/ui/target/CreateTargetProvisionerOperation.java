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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;

public class CreateTargetProvisionerOperation extends AbstractOperation {

	
	private static final int TOTAL_WORK_UNITS = 2000;

	private static final String LABEL = "Create Target Provisioner";
	
	private File[] targetFiles;
	private File targetDestination;
	
	public CreateTargetProvisionerOperation(File[] targetFiles, File targetDestination) {
		super(LABEL);
		this.targetFiles = targetFiles;
		this.targetDestination = targetDestination;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#canUndo()
	 */
	public boolean canUndo() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#execute(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus execute(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {

		monitor.beginTask("", TOTAL_WORK_UNITS); //$NON-NLS-1$
		monitor.setTaskName("Creating Target Provisioner");

		int numFiles = targetFiles.length;
		try {
			for (int i = 0; i < numFiles; i++) {
				if (monitor.isCanceled())
					throw new OperationCanceledException();
				File file = targetFiles[i];
				monitor.subTask("Copying " + file.getName());
				FileHelper.copy(file, targetDestination);
				monitor.worked(TOTAL_WORK_UNITS/numFiles);
			}
		} catch (IOException e) {
			throw new ExecutionException(e.getMessage());
		} finally {
			monitor.done();
		}
		
		return Status.OK_STATUS;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.operations.AbstractOperation#redo(org.eclipse.core.runtime.IProgressMonitor, org.eclipse.core.runtime.IAdaptable)
	 */
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		return null; //wont be called
	}

	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		throw new ExecutionException("Undo not supported");
	}
	

}
