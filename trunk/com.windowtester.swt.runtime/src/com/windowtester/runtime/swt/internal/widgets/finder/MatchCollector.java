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
package com.windowtester.runtime.swt.internal.widgets.finder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.internal.SWTUtils;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.IVisitable;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor;

public class MatchCollector implements Visitor {

	List<SWTWidgetReference<?>> matches = new ArrayList<SWTWidgetReference<?>>();
	private final ISWTWidgetMatcher matcher; 
	
	public MatchCollector(ISWTWidgetMatcher matcher) {
		this.matcher = matcher;
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor#visit(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference)
	 */
	public <W extends Widget> void visit(final SWTWidgetReference<W> widget) {
		SWTUtils.safeExec(new VoidCallable() {
			@Override
			public void call() throws Exception {
				if (matcher.matches(widget)) {
					//System.out.println("matches");
					matches.add(widget);
				}
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor#visitEnter(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference)
	 */
	public <T extends SWTWidgetReference<?>> void visitEnter(T composite) {
		//no-op
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference.Visitor#visitLeave(com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference)
	 */
	public <T extends SWTWidgetReference<?>> void visitLeave(T composite) {
		//no-op
	}

	public List<SWTWidgetReference<?>> findMatchesIn(IVisitable root) {
//		System.out.println("MatchCollector.findMatchesIn()");
		root.accept(this);
		return matches;
	}

}
