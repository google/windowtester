package com.windowtester.test.recorder.ui;

import static com.windowtester.test.codegen.CodeGenFixture.fakeKeyEntry;
import static com.windowtester.test.codegen.CodeGenFixture.fakeSelectEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;

import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.ui.internal.corel.model.Event;

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
public class FakeEvents {

	
	public static final Event keyA = fakeKeyEntry('a');
	public static final Event keyB = fakeKeyEntry('b');
	public static final Event keyC = fakeKeyEntry('c');
	public static final Event keyBackSpace = fakeKeyEntry(SWT.BS);
	public static final Event keyTAB = fakeKeyEntry(SWT.TAB);
	public static final Event keyCR = fakeKeyEntry(SWT.CR);
	
	
	public static final Event buttonSelect1 = fakeSelectEvent(Button.class, new ButtonLocator("1"));
	public static final Event buttonSelect2 = fakeSelectEvent(Button.class, new ButtonLocator("2"));
	public static final Event buttonSelect3 = fakeSelectEvent(Button.class, new ButtonLocator("3"));
	
}
