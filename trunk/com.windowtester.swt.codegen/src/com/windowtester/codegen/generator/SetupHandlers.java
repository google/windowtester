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
package com.windowtester.codegen.generator;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.WelcomePageHandler;
import com.windowtester.codegen.generator.setup.WorkbenchFocusHandler;
import com.windowtester.codegen.generator.setup.WorkbenchMaximizedHandler;

/**
 * Default handler factory.
 */
public class SetupHandlers {

	public static final ISetupHandler[] FOR_SWT   = new ISetupHandler[]{};
	public static final ISetupHandler[] FOR_SWING = new ISetupHandler[]{};
	public static final ISetupHandler[] FOR_RCP   = new ISetupHandler[]{ new WorkbenchFocusHandler().enabledByDefault(), 
																		 new WelcomePageHandler().enabledByDefault(), 
																		 new WorkbenchMaximizedHandler()};
		

	public static final SetupHandlerProvider NONE = new SetupHandlerProvider(new ISetupHandler[]{});

	
	public static SetupHandlerProvider forContext(ExecutionProfile execProfile) {
		
		ISetupHandler[] handlers = null;
		int type = execProfile.getExecType();
		switch (type) {
			case ExecutionProfile.RCP_EXEC_TYPE : 
				handlers = SetupHandlers.FOR_RCP;
				break;
			case ExecutionProfile.SWING_EXEC_TYPE :
				handlers = SetupHandlers.FOR_SWING;
				break;
			case ExecutionProfile.SWT_EXEC_TYPE :
				handlers = SetupHandlers.FOR_SWT;
				break;
			default :
				throw new IllegalArgumentException("unexpected profile type: " + type);		
		}
		return new SetupHandlerProvider(handlers);
	}
	
}
