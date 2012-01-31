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
package com.windowtester.finder.matchers.swt;

import abbot.finder.swt.Matcher;

/**
 * A matcher that matches widgets by way of their index in their parent's 
 * list of children.  
 * <p>
 * Note that indexes only matter in the case where there is at least one sibling
 * that matches the target widget exactly (by class and name/label).
 * 
 * @see com.windowtester.swt.WidgetLocatorService
 * <p>
 * <b>Note:</b> This is Legacy API.
 * <p>
 * @author Phil Quitslund
 * @deprecated - functionality folded into HierarchyMatcher
 *
 */
public class ParentIndexMatcher extends com.windowtester.runtime.swt.internal.abbot.matcher.ParentIndexMatcher {

	public ParentIndexMatcher(Matcher matcher, int index, Matcher parentMatcher) {
		super(matcher, index, parentMatcher);
	}

}

