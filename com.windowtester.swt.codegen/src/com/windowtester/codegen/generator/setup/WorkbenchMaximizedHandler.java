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
package com.windowtester.codegen.generator.setup;

import com.windowtester.runtime.swt.locator.eclipse.EclipseLocators;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;

/**
 * A handler to ensure Workbench is maximized.
 */
public class WorkbenchMaximizedHandler extends SetupHandler {


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getBody()
	 */
	public String getBody() {
		return "ui.ensureThat(new WorkbenchLocator().isMaximized());";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getImport()
	 */
	public String getImport() {
		return WorkbenchLocator.class.getName();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getBody()
	 */
	public String getStaticBody() {
		return "ui.ensureThat(workbench().isMaximized());";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getImport()
	 */
	public String getStaticImport() {
		return EclipseLocators.class.getName() + ".workbench";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getDescription()
	 */
	public String getDescription() {
		return "Workbench is maximized";
	}
	
	
	
}
