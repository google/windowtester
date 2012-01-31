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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * Error presentation helper.
 */
public class ErrorHelper {

	
    public static void openError(Shell parent, String title, String message, Exception e) {
        if (!(e instanceof CoreException)) {
            MessageDialog.openError(parent, title, message);
            return;
        }
        
    	CoreException coreEx = (CoreException)e;
    	// Check for a nested CoreException
        CoreException nestedException = null;
        IStatus status = coreEx.getStatus();
        if (status != null && status.getException() instanceof CoreException) {
			nestedException = (CoreException) status.getException();
		}

        if (nestedException != null) {
            // Open an error dialog and include the extra
            // status information from the nested CoreException
            ErrorDialog.openError(parent, title, message, nestedException
                    .getStatus());
        } else {
            // Open a regular error dialog since there is no
            // extra information to display
            MessageDialog.openError(parent, title, message);
        }
    }
}
