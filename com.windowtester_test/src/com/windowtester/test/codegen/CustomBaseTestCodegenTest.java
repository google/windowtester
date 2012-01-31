package com.windowtester.test.codegen;

import org.eclipse.swt.widgets.TableItem;

import com.windowtester.codegen.swt.ClassName;
import com.windowtester.codegen.swt.SWTV2TestCaseBuilder;
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
public class CustomBaseTestCodegenTest extends BaseSWTAPICodeBlockCodegenTest {

	
	/* (non-Javadoc)
	 * @see com.windowtester.test.codegen.BaseSWTAPICodeBlockCodegenTest#createTestBuilder()
	 */
	@Override
	protected SWTV2TestCaseBuilder createTestBuilder() {
		return new SWTV2TestCaseBuilder("MockTest", "mock", null, ClassName.forQualifiedName("com.acme.BaseTest"), null, getExecType());
	}
	
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
	
	
	public void testCustomExtends() throws Exception {
		String baseClass = getTestCaseBuilder().getExtends();
		assertEquals("BaseTest", baseClass);
	}
	

	public void testCustomExtendImport() throws Exception {
		assertImportsContainAsString("import com.acme.BaseTest;");
	}


	public void testCustomImportDoesNotIncludeDefault() throws Exception {
		assertImportsDoNotContainAsString("import " + UITestCaseSWT.class.getName() + ";");
	}



	
	
	
}
