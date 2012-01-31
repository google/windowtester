package com.windowtester.test.codegen;


import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.TableItem;

import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.codegen.generator.setup.WelcomePageHandler;
import com.windowtester.codegen.generator.setup.WorkbenchFocusHandler;
import com.windowtester.codegen.generator.setup.WorkbenchMaximizedHandler;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.TableItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EclipseLocators;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.eclipse.WorkbenchLocator;


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
public class RCPSetupCodegenTest extends BaseSWTAPICodeBlockCodegenTest {

	
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.codegen.BaseSWTAPICodeBlockCodegenTest#getHandlers()
	 */
	@Override
	protected SetupHandlerSet getHandlers() {
		return SetupHandlerSet.forHandler(new WelcomePageHandler());
	}
	
	
	public void testWelcomePageHandler() throws Exception {
		ISetupHandler wp = new WelcomePageHandler();
		assertEquals("ui.ensureThat(ViewLocator.forName(\"Welcome\").isClosed());", wp.getBody());
		assertEquals(ViewLocator.class.getName(), wp.getImport());
	}
	
	
	public void testWorkbenchFocusHandlerBodyContents() throws Exception {
		ISetupHandler wp = new WorkbenchFocusHandler();
		assertEquals("ui.ensureThat(new WorkbenchLocator().hasFocus());", wp.getBody());
		assertEquals(WorkbenchLocator.class.getName(), wp.getImport());		
	}
	
	public void testWorkbenchFocusHandlerStaticBodyContents() throws Exception {
		ISetupHandler wp = new WorkbenchFocusHandler();
		assertEquals("ui.ensureThat(workbench().hasFocus());", wp.getStaticBody());
		assertEquals(EclipseLocators.class.getName() + ".workbench", wp.getStaticImport());		
	}

	public void testWorkbenchMaximizedHandlerBodyContents() throws Exception {
		ISetupHandler wp = new WorkbenchMaximizedHandler();
		assertEquals("ui.ensureThat(new WorkbenchLocator().isMaximized());", wp.getBody());
		assertEquals(WorkbenchLocator.class.getName(), wp.getImport());		
	}
	
	public void testWorkbenchMaximizedHandlerStaticBodyContents() throws Exception {
		ISetupHandler wp = new WorkbenchMaximizedHandler();
		assertEquals("ui.ensureThat(workbench().isMaximized());", wp.getStaticBody());
		assertEquals(EclipseLocators.class.getName() + ".workbench", wp.getStaticImport());		
	}
	
	public void testWelcomePageHandlerAppliesTo() throws Exception {
		
		WelcomePageHandler wp = new WelcomePageHandler();
		
		SemanticWidgetSelectionEvent select  = CodeGenFixture.mockSelect(CTabItem.class, new CTabItemLocator("Some View"));
		assertTrue(wp.appliesTo(stream(select)));
		
	}
	
	
	public void testBasicSetupEndToEndBody() throws Exception {
		
		SemanticWidgetSelectionEvent select  = CodeGenFixture.mockSelect(TableItem.class, new TableItemLocator("item"));
	
		String src = generate(select);
		System.out.println(src);
		
		assertSetupHandlesWelcome();
	}



	private void assertSetupHandlesWelcome() {
		MethodUnit setup = getSetupMethod();
		assertNotNull(setup);	
		assertEquals("super.setUp();" + NEW_LINE + "IUIContext ui = getUI();" + NEW_LINE + "ui.ensureThat(view(\"Welcome\").isClosed());", setup.getMethodBodyContents());
	}


	public void testBasicSetupEndToEndImport() throws Exception {
		
		SemanticWidgetSelectionEvent select  = CodeGenFixture.mockSelect(TableItem.class, new TableItemLocator("item"));
	
		String src = generate(select);
		System.out.println(src);
		
		assertImportsContainAsString("import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;");	
	}



	public void testExplicitWelcomeCloseIsConsumed() throws Exception {
		
		SemanticWidgetClosedEvent select  = CodeGenFixture.mockClose(CTabItem.class, new CTabItemLocator("Welcome"));
		
		String src = generate(select);
		System.out.println(src);
		
		assertSetupHandlesWelcome();
		assertTestDoesNotHandleWelcome();
		
	}



	private void assertTestDoesNotHandleWelcome() {
		MethodUnit test = getTestMethod();
		assertNull(test);	
	}




	
	
}
