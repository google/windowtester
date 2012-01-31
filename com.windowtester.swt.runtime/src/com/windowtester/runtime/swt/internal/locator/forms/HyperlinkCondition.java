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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.windowtester.internal.runtime.ICodeGenerator;
import com.windowtester.internal.runtime.ICodegenParticipant;
import com.windowtester.runtime.IAdaptable;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkMatcher.HyperlinkTextMatcher;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator.IHyperlinkCondition;

public class HyperlinkCondition implements IHyperlinkCondition, ICodegenParticipant /*, IDiagnosticParticipant */{
		
	protected final IHyperlinkFinder hyperlinkProvider;

	public HyperlinkCondition(IHyperlinkFinder hyperlinkProvider) {
		this.hyperlinkProvider = hyperlinkProvider;
	}

	private final List criteria = new ArrayList();
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		throw new RuntimeException("unsupported method - should call testUI(IUIContext) instead");
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.IUICondition#testUI(com.windowtester.runtime.IUIContext)
	 */
	public boolean testUI(IUIContext ui) {
		IHyperlinkReference[] hyperlinks = getHyperlinks(ui);
		for (int i = 0; i < hyperlinks.length; i++) {
			if (meetsCriteria(hyperlinks[i]))
				return true;
		}
		return false;
	}

	protected IHyperlinkReference[] getHyperlinks(IUIContext ui) {
		return hyperlinkProvider.findAllHyperlinks(ui);
	}

	private boolean meetsCriteria(IHyperlinkReference link) {
		for (Iterator iter = criteria.iterator(); iter.hasNext(); ) {
			IHyperlinkMatcher matcher = (IHyperlinkMatcher) iter.next();
			if (!matcher.matches(link))
				return false;
		}
		return true;
	}
	
	public IHyperlinkCondition withHRef(String href) {
		criteria.add(HyperlinkMatcher.forHref(href));
		return this;
	}

	public IHyperlinkCondition withText(String text) {
		criteria.add(HyperlinkMatcher.forText(text));
		return this;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Hyperlink Condition [");
		for (Iterator iter = criteria.iterator(); iter.hasNext();) {
			IHyperlinkMatcher matcher = (IHyperlinkMatcher) iter.next();
			sb.append(matcher);
			if (iter.hasNext())
				sb.append(", ");	
		}
		sb.append("]");
		return sb.toString();
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == ICodegenParticipant.class)
			return this;
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.ICodegenParticipant#describeTo(com.windowtester.internal.runtime.ICodeGenerator)
	 */
	public void describeTo(ICodeGenerator generator) {
		describeProvider(generator);
		describeCriteria(generator);
	}


	private void describeProvider(ICodeGenerator generator) {
		ICodegenParticipant cp = adaptToParticipant(hyperlinkProvider);
		if (cp == null)
			return;
		cp.describeTo(generator);
	}

	private ICodegenParticipant adaptToParticipant(Object o) {
		if (o instanceof ICodegenParticipant)
			return (ICodegenParticipant)o;
		if (o instanceof IAdaptable)
			return (ICodegenParticipant) ((IAdaptable)o).getAdapter(ICodegenParticipant.class);
		return null;
	}

	private void describeCriteria(ICodeGenerator generator) {
		for (Iterator iter = criteria.iterator(); iter.hasNext();) {
			IHyperlinkMatcher matcher = (IHyperlinkMatcher)iter.next();
			if (matcher instanceof HyperlinkTextMatcher) //handled in constructor
				continue;
			generator.append(HyperlinkMatcher.toCriteriaString(matcher));
		}

	}
}