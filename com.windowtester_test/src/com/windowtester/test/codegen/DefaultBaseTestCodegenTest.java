package com.windowtester.test.codegen;

import org.eclipse.swt.widgets.TableItem;

import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.swt.UITestCaseSWT;
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
public class DefaultBaseTestCodegenTest extends BaseSWTAPICodeBlockCodegenTest {

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SemanticWidgetSelectionEvent select  = CodeGenFixture.mockSelect(TableItem.class, new TableItemLocator("item"));
		
		String src = generate(select);
		System.out.println(src);
	}
	
	
	public void testDefaultExtends() throws Exception {
		String baseClass = getTestCaseBuilder().getExtends();
		assertEquals("UITestCaseSWT", baseClass);
	}
	

	public void testDefaultExtendImport() throws Exception {
		assertImportsContainAsString("import " + UITestCaseSWT.class.getName() + ";");
	}


	
	
	
	
}
