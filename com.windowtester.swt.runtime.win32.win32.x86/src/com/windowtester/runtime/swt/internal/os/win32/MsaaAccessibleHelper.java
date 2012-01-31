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
package com.windowtester.runtime.swt.internal.os.win32;

import org.eclipse.actf.accservice.core.AccessibleConstants;
import org.eclipse.actf.accservice.core.win32.msaa.MsaaAccessible;

import com.windowtester.runtime.swt.internal.os.IAccessibleComponent;
import com.windowtester.runtime.swt.internal.os.InvalidComponentException;

public class MsaaAccessibleHelper {

	/**
	 * Finds first match.
	 */
	public static IAccessibleComponent findIn(IAccessibleComponentMatcher matcher, IAccessibleComponent element) {
		if (element == null)
			return null;
		if (matcher.matches(element))
			return element;
		try {
			IAccessibleComponent[] children = element.getAccessibleChildren();
			for (int i = 0; i < children.length; i++) {
				IAccessibleComponent child = children[i];
				if (matcher.matches(child))
					return child;
				IAccessibleComponent descendantMatch = findInChildren(matcher, child);
				if (descendantMatch != null)
					return descendantMatch;
			}
		} catch (InvalidComponentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}


	private static IAccessibleComponent findInChildren(IAccessibleComponentMatcher matcher, IAccessibleComponent element) throws InvalidComponentException {
		IAccessibleComponent[] children = element.getAccessibleChildren();
			for (int i = 0; i < children.length; i++) {
				IAccessibleComponent child = children[i];
				if (matcher.matches(child))
					return child;
			}
		return null;
	}
	
	
	public static IAccessibleComponent getTitleBar(MsaaAccessible element) throws InvalidComponentException {
		element = element.getAccessibleParent();
		
		IAccessibleComponent[] children = element.getAccessibleChildren();
		for (int i = 0; i < children.length; i++) {
			IAccessibleComponent child = children[i];
			String role = child.getAccessibleRole();
			if (role == null)
				continue;
			if (AccessibleConstants.ROLE_TITLE_BAR.equals(role))
				return child;
		}
		return null;
	}
	
}
