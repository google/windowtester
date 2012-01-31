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
package com.windowtester.codegen.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.windowtester.codegen.CodeGenPlugin;

/**
 * Convenience class for error exceptions thrown inside CodeGen plugin.
 */
public class CodeGenStatus extends Status {

	private CodeGenStatus(int severity, int code, String message, Throwable throwable) {
		super(severity, CodeGenPlugin.getPluginId(), code, message, throwable);
	}
	
	public static IStatus createError(int code, Throwable throwable) {
		String message= throwable.getMessage();
		if (message == null) {
			message= throwable.getClass().getName();
		}
		return new CodeGenStatus(IStatus.ERROR, code, message, throwable);
	}

	public static IStatus createError(int code, String message, Throwable throwable) {
		return new CodeGenStatus(IStatus.ERROR, code, message, throwable);
	}
	
	public static IStatus createWarning(int code, String message, Throwable throwable) {
		return new CodeGenStatus(IStatus.WARNING, code, message, throwable);
	}

	public static IStatus createInfo(int code, String message, Throwable throwable) {
		return new CodeGenStatus(IStatus.INFO, code, message, throwable);
	}
}