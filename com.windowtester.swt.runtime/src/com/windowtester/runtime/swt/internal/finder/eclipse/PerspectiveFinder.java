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
package com.windowtester.runtime.swt.internal.finder.eclipse;


import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbenchPage;

import com.windowtester.runtime.util.StringComparator;

public class PerspectiveFinder extends WorkbenchFinder {

	
	public static interface IPerspectiveMatcher {
		boolean matches(IPerspectiveDescriptor perspective);
	}
	
	private static final class NameMatcher implements IPerspectiveMatcher {
		private final String name;

		NameMatcher(String name) {
			this.name = name;
		}
		
		public boolean matches(IPerspectiveDescriptor perspective) {
			return StringComparator.matches(perspective.getLabel(), name);
		}
	};
	
	
	private static final class IdMatcher implements IPerspectiveMatcher {
		private final String id;

		IdMatcher(String name) {
			this.id = name;
		}
		
		public boolean matches(IPerspectiveDescriptor perspective) {
			return StringComparator.matches(perspective.getId(), id);
		}
	};
	
	
	public static IPerspectiveDescriptor findNamed(String perspectiveName) {
		return findMatch(new NameMatcher(perspectiveName));
	}
	
	public static IPerspectiveDescriptor findWithId(String perspectiveId) {
		return findMatch(new IdMatcher(perspectiveId));
	}
	
	public static IPerspectiveDescriptor findActive() {
		IWorkbenchPage page = getActivePage();
		if (page == null)
			return null;
		return page.getPerspective();
	}
	
	
	//note: finds first match
	public static IPerspectiveDescriptor findMatch(IPerspectiveMatcher matcher) {
		IWorkbenchPage page = getActivePage();
		if (page == null)
			return null;
		IPerspectiveDescriptor[] open = page.getOpenPerspectives();
		for (int i = 0; i < open.length; i++) {
			if (matcher.matches(open[i])) {
				return open[i];
			}
		}
		return null;
	}

	public static boolean isActive(IPerspectiveDescriptor perspective) {
		return perspective == findActive();
	}

	
	public static IPerspectiveDescriptor[] getPerspectives() {
		IPerspectiveRegistry perspectiveRegistry = getWorkbench().getPerspectiveRegistry();
		return perspectiveRegistry.getPerspectives();
	}


	public static IPerspectiveDescriptor findByNameInRegistry(String name) {
		IPerspectiveDescriptor[] perspectives = getPerspectives();
		for (int i = 0; i < perspectives.length; i++) {
			IPerspectiveDescriptor perspective = perspectives[i];
			if (StringComparator.matches(perspective.getLabel(), name))
				return perspective;
		}
		return null;
	}

	public static IPerspectiveDescriptor findByIdInRegistry(String name) {
		IPerspectiveDescriptor[] perspectives = getPerspectives();
		for (int i = 0; i < perspectives.length; i++) {
			IPerspectiveDescriptor perspective = perspectives[i];
			if (StringComparator.matches(perspective.getId(), name))
				return perspective;
		}
		return null;
	}

	
	
	
}
