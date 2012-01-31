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
package com.windowtester.runtime.swt.locator;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.runtime.condition.HasText;
import com.windowtester.runtime.condition.HasTextCondition;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates eclipse form sections and their components.
 * <p>
 * Example uses:
 * <pre>
 *  //assert that the &quot;Execution Environments&quot; Section is visible
 *  ui.assertThat(new SectionLocator(&quot;Execution Environments&quot;).isVisible());
 *  
 *  //click the &quot;Add...&quot; button in the &quot;Execution Environments&quot; Section
 *  ui.click(new ButtonLocator(&quot;Add...&quot;, new SectionLocator(&quot;Execution Environments&quot;)));
 * </pre>
 * 
 */
public class SectionLocator extends SWTWidgetLocator implements HasText {

	private static final long serialVersionUID = 621335057837701982L;

	/**
	 * Create a locator instance.
	 * @param text the text of the section 
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public SectionLocator(String text) {
		super(Section.class, text);
	}

	/**
	 * Create a locator instance.
	 * @param text the text of the section (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param parent the section's parent locator
	 */
	public SectionLocator(String text, SWTWidgetLocator parent) {
		super(Section.class, text, parent);
	}

	/**
	 * Create a locator instance.
	 * @param text the text of the section (can be a regular expression as described in the {@link StringComparator} utility)
	 * @param index the section's index relative to its parent
	 * @param parent the section's parent locator
	 */
	public SectionLocator(String text, int index, SWTWidgetLocator parent) {
		super(Section.class, text, index, parent);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#getWidgetText(org.eclipse.swt.widgets.Control)
	 */
	protected String getWidgetText(Control widget) {
		return ((Section) widget).getText();
	}
		
	///////////////////////////////////////////////////////////////////////////
	//
	// Condition Factories
	//
	///////////////////////////////////////////////////////////////////////////
	
	/**
	 * Create a condition that tests if the given widget has the expected text.
	 * @param expected the expected text
	 *  (can be a regular expression as described in the {@link StringComparator} utility)
	 */
	public IUICondition hasText(String expected) {
		return new HasTextCondition(this, expected);
	}
}
