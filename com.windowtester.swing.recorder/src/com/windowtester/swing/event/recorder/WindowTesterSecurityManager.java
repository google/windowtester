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
package com.windowtester.swing.event.recorder;

import java.security.Permission;

/**
 * WindowTester Security Manager used during recording to intercept System.exit() and
 * gracefully exit recording.
 */
public class WindowTesterSecurityManager extends SecurityManager
{
	/**
	 * Install our security manager.
	 */
	public static void install() {
		
		
//		if (System.getSecurityManager() == null) {
//			System.setSecurityManager(new WindowTesterSecurityManager());
//			System.out.println("Install WindowTesterSecurityManager");
//		}
//		else {
//			System.out.println("Failed to install WindowTesterSecurityManager");
//		}
	}

	/**
	 * Intercept System.exit() and gracefully exit recording.
	 */
	public void checkExit(int status) {
		System.out.println("System.exit() called... need to gracefully exit recording.");
		try {
			throw new RuntimeException("Informational Stack Trace");
		}
		catch (RuntimeException e) {
			e.printStackTrace(System.out);
		}
		super.checkExit(status);
	}

	/**
	 * Override superclass implementation to allow everything during recording
	 */
	public void checkPermission(Permission perm) {
		// super.checkPermission(perm);
	}

	/**
	 * Override superclass implementation to allow everything during recording
	 */

	public void checkPermission(Permission perm, Object context) {
		// super.checkPermission(perm, context);
	}
}
