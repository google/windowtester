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
package com.windowtester.eclipse.ui.launcher;

import org.eclipse.core.runtime.IStatus;

import com.windowtester.eclipse.ui.UiPlugin;

public class LaunchConfigurationStatusReporter {


	public static void forStatus(IStatus status) {
		//TODO: this will get improved.
		UiPlugin.getDefault().showErrorDialog("Launch Configuarion Error", status.getMessage());
	}

}
