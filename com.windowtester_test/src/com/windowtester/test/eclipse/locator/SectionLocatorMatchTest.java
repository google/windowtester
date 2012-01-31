package com.windowtester.test.eclipse.locator;

import static com.windowtester.runtime.swt.internal.matcher.VisibilityMatcher.setVisibleForTesting;
import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.runtime.swt.internal.matcher.VisibilityMatcher;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.SectionLocator;


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
public class SectionLocatorMatchTest extends TestCase {

	
	private final FormToolkit toolkit = new FormToolkit(Display.getDefault());
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		VisibilityMatcher.TEST_MODE = true;
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		VisibilityMatcher.TEST_MODE = false;
	}
	
	public void testMatches() {
		Section s = createSection("text");
		assertTrue(new SectionLocator("text").matches(s));
	}

	public void testMatchesComponent() {
		Section section = createSection("Section");
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		Button button = createButton(sectionClient, "Button");
		assertTrue(new ButtonLocator("Button", new SectionLocator("Section")).matches(button));
	}
	
	//rub here is that the index resolving is hard-wired
	public void testMatchesIndexInComponent() {
		Section section = createSection("Section");
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		/* not used */   createButton(sectionClient, "Button");
		Button button2 = createButton(sectionClient, "Button");
		assertTrue(new ButtonLocator("Button", 1, new SectionLocator("Section")).matches(button2));
	}
	
	//rub here is that the index resolving is hard-wired
	public void testMatchesIndexInComponentNot() {
		Section section = createSection("Section");
		Composite sectionClient = toolkit.createComposite(section);
		sectionClient.setLayout(new GridLayout());
		/* not used */   createButton(sectionClient, "Button");
		Button button2 = createButton(sectionClient, "Button");
		assertFalse(new ButtonLocator("Button", 1, new SectionLocator("Section")).matches(button2));
	}
	
	
	private Section createSection(String text) {
		Section s = toolkit.createSection(new Shell(), SWT.NONE);
		s.setText(text);
		setVisibleForTesting(s);
		return s;
	}


	private Button createButton(Composite sectionClient, String text) {
		Button b = (Button) VisibilityMatcher.setVisibleForTesting(toolkit.createButton(sectionClient, text, SWT.RADIO));
		b.setBounds(10, 10, 20, 20); //needed because boundless buttons are special-cased.
		/*
		 * TODO: this bound set is not enough...  to get these tests working we need to remedy this.
		 */
		return b;
	}
	
	
	
}
