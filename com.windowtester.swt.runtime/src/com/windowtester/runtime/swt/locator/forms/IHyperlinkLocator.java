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
package com.windowtester.runtime.swt.locator.forms;

import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.windowtester.internal.runtime.locator.IUISelector;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.condition.IUICondition;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * Locates hyperlink widgets as created
 * using the {@link Hyperlink} class or embedded in {@link FormText} controls.
 */
public interface IHyperlinkLocator extends IWidgetLocator, IUISelector, IAdaptable {
	
	/**
	 * Implementers provide condition factory methods for {@link Hyperlink}s.
	 * 
	 */
	public static interface IHyperlinkCondition extends IUICondition, IAdaptable {
		IHyperlinkCondition withText(String text);
		IHyperlinkCondition withHRef(String href);
	}
	
	/**
	 * Specify this hyperlink's text.
	 */
	IHyperlinkLocator withText(String text);

	/**
	 * Specify this hyperlink's href.
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	IHyperlinkLocator withHRef(String href);

	/**
	 * Specify this hyperlink's section.
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	IHyperlinkLocator inSection(String sectionTitle);
	
	/**
	 * Specify this hyperlink's containing editor.
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	IHyperlinkLocator inEditor(String editorTitle);
	
	/**
	 * Specify this hyperlink's containing view..
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */
	IHyperlinkLocator inView(String viewId);
	
	/**
	 * Create a condition that verifies that this hyperlink is visible.
	 */
	IUICondition isVisible();

	/**
	 * Create a condition that verifies that this hyperlink has the given href.
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */	
	IHyperlinkCondition hasHRef(String href);	
	
	/**
	 * Create a condition that verifies that this hyperlink has the given text.
	 * 	(can be a regular expression as described in the {@link StringComparator} utility)
	 */	
	IHyperlinkCondition hasText(String text);



}