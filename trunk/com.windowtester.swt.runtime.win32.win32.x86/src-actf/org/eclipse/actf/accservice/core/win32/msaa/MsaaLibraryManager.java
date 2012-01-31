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
package org.eclipse.actf.accservice.core.win32.msaa;


/**
 * A number of the ACTF classes expect a DLL to be loaded.  This
 * class manages the loading of that DLL.
 */
public class MsaaLibraryManager {
	
	public static final String ACTF_MSAA_LIBRARY_NAME = "actf-msaa";

	private static boolean initialized;

	private static InitializationException INIT_EXCEPTION;
	
	public static void load() throws InitializationException {
		try {
			checkForCachedException();
			initialize();
		} catch (Throwable e) {
			INIT_EXCEPTION = new InitializationException("Unable to load library " + ACTF_MSAA_LIBRARY_NAME, e);
			throw INIT_EXCEPTION;
		} finally {
			initialized = true;
		}
	}

	private static void initialize() {
		if (!initialized) {
			System.loadLibrary(ACTF_MSAA_LIBRARY_NAME);
		}
	}

	private static void checkForCachedException() throws InitializationException {
		if (INIT_EXCEPTION != null)
			throw INIT_EXCEPTION;
	}
	
}
