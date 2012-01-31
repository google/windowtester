package com.windowtester.test.eclipse.codegen.ui;

import java.util.Comparator;

import junit.framework.TestCase;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceStore;

import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.codegen.generator.setup.WelcomePageHandler;
import com.windowtester.swt.codegen.wizards.SetupHandlerTableStore;
import com.windowtester.test.util.TestCollection;

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
public class BaseSetupHandlerTest extends TestCase {

	protected ISetupHandler[] handlers = new ISetupHandler[]{new WelcomePageHandler()};
	IPreferenceStore prefStore = new PreferenceStore();
	protected SetupHandlerTableStore handlerStore = new SetupHandlerTableStore(prefStore);
	@SuppressWarnings("unchecked")
	Comparator comparator = SetupHandlerTableStore.getHandlerComparator();

	protected void assertContainsOnly(ISetupHandler[] expected, SetupHandlerSet actual) {
		this.assertContainsOnly(expected, actual.toArray());
	}

	protected void assertContainsOnly(ISetupHandler[] expected, ISetupHandler ... actual) {
		TestCollection.assertContainsOnly(expected, actual, comparator);
	}
	
	
}
