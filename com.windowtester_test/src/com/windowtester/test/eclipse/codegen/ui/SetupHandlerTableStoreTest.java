package com.windowtester.test.eclipse.codegen.ui;



import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.codegen.generator.setup.WelcomePageHandler;

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
public class SetupHandlerTableStoreTest extends BaseSetupHandlerTest {


	public void testInactiveStoreState() throws Exception {
		assertFalse(handlerStore.isActivated());
	}
	
	public void testActiveStoreState() throws Exception {
		selectHandlersInStore(handlers);
		assertTrue(handlerStore.isActivated());
	}
	

	public void testSetupHandlerSelectionSticks() {
		selectHandlersInStore(handlers);
		assertSelectedInStore(handlers);
	}

	private void selectHandlersInStore(ISetupHandler... handlers) {
		handlerStore.setSelectedHandlers(handlers);
	}
	
	private void assertSelectedInStore(ISetupHandler... handlers) {
		SetupHandlerSet defaultHandlers = handlerStore.getSelectedHandlers();
		assertContainsOnly(handlers, defaultHandlers);
	}
	
	
	
	public void testHandlerStringParsing() throws Exception {
		String str = handlerStore.toString(handlers);
		SetupHandlerSet parsedHandlers = handlerStore.parseHandlersFromString(str);
		assertContainsOnly(handlers, parsedHandlers);
	}
	
	public void testHandlerStringCreationEmpty() throws Exception {
		String str = handlerStore.toString(new ISetupHandler[]{});
		assertEquals("", str);
	}
	
	public void testHandlerStringCreationSingleton() throws Exception {
		String str = handlerStore.toString(new ISetupHandler[]{new WelcomePageHandler()});
		assertEquals(WelcomePageHandler.class.getName()+ ";", str);
	}
	
	public void testHandlerStringCreationDuo() throws Exception {
		WelcomePageHandler handler = new WelcomePageHandler();
		String str = handlerStore.toString(new ISetupHandler[]{handler,handler});
		assertEquals(handler.getClass().getName() + ";" + handler.getClass().getName() + ";", str);
	}


	public void testEmptyHandlerStore() throws Exception {
		SetupHandlerSet handlers = handlerStore.parseHandlersFromString("");
		assertEquals(0, handlers.toArray().length);
	}
	
	

	public void testFindInStore() throws Exception {
		selectHandlersInStore(handlers);
		SetupHandlerSet matches = handlerStore.findMatches(handlers);
		assertContainsOnly(handlers, matches);
		
	}

	
	
}
