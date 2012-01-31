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
package com.windowtester.runtime.swt.internal.locator.forms;


import com.windowtester.runtime.swt.internal.locator.forms.FormTextLocator.IHyperlinkConditionSpecifier;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator.IHyperlinkCondition;

public class HyperlinkSegmentCondition extends AbstractHyperlinkSegmentAccessor implements IHyperlinkConditionSpecifier {

	public static IHyperlinkConditionSpecifier forFormText(FormTextLocator formTextLocator) {
		return new HyperlinkSegmentCondition(formTextLocator);
	}
	
	public HyperlinkSegmentCondition(FormTextLocator textLocator) {
		super(textLocator);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.forms.FormTextLocator.IHyperlinkSpecifier#withText(java.lang.String)
	 */
	public IHyperlinkCondition withText(String text) {
		return new HyperlinkCondition(this).withText(text);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.test.locator.swt.forms.FormTextLocator.IHyperlinkSpecifier#withHRef(java.lang.String)
	 */
	public IHyperlinkCondition withHRef(String href) {
		return new HyperlinkCondition(this).withHRef(href);
	}

	
	
}

