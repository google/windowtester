/**
 * 
 */
package com.windowtester.test.codegen;

import org.eclipse.swt.widgets.TableItem;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.swt.locator.TableItemLocator;


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
public class SWTSetupCodegenTest extends BaseSWTAPICodeBlockCodegenTest {

	
	/* (non-Javadoc)
	 * @see com.windowtester.test.codegen.BaseSWTAPICodeBlockCodegenTest#getExecType()
	 */
	@Override
	protected int getExecType() {
		return ExecutionProfile.SWT_EXEC_TYPE;
	}

	//NOTE: the test here is to verify that it there is NO SETUP (no handlers apply)	
	public void testBasicSetupEndToEndBody() throws Exception {
		
		SemanticWidgetSelectionEvent select  = CodeGenFixture.mockSelect(TableItem.class, new TableItemLocator("item"));
	
		String src = generate(select);
		System.out.println(src);
		
		MethodUnit setup = getSetupMethod();
		assertNull(setup);	
	}

	
}
