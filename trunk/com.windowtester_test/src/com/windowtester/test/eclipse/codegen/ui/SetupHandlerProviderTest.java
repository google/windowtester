package com.windowtester.test.eclipse.codegen.ui;

import java.util.Comparator;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.generator.SetupHandlerProvider;
import com.windowtester.codegen.generator.SetupHandlers;
import com.windowtester.codegen.generator.setup.WelcomePageHandler;
import com.windowtester.codegen.generator.setup.WorkbenchFocusHandler;
import com.windowtester.codegen.generator.setup.WorkbenchMaximizedHandler;
import com.windowtester.swt.codegen.wizards.SetupHandlerTableStore;

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
public class SetupHandlerProviderTest extends BaseSetupHandlerTest {

	
	@SuppressWarnings("unchecked")
	Comparator comparator = SetupHandlerTableStore.getHandlerComparator();
	
	public void testRCP() throws Exception {
		SetupHandlerProvider provider = SetupHandlers.forContext(new ExecutionProfile().setExecType(ExecutionProfile.RCP_EXEC_TYPE));
		assertContainsOnly(provider.getHandlers(), new WelcomePageHandler(), new WorkbenchFocusHandler(), new WorkbenchMaximizedHandler());
		
	}

		
	
	
	
	
}
