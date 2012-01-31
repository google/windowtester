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
package com.windowtester.runtime.gef.internal.locator;

import com.windowtester.runtime.draw2d.internal.finder.IFigureSearchScope;
import com.windowtester.runtime.gef.internal.finder.scope.EditorScope;
import com.windowtester.runtime.gef.internal.finder.scope.UnscopedSearch;
import com.windowtester.runtime.gef.internal.finder.scope.ViewScope;
import com.windowtester.runtime.internal.finder.scope.IWidgetSearchScope;
import com.windowtester.runtime.swt.locator.eclipse.ActiveEditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.IEditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.IWorkbenchPartLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * Scope related factory.
 */
public class ScopeFactory {

	public static IEditorLocator unspecifedEditorLocator() {
		return new ActiveEditorLocator();
	}
	
	public static IEditorLocator editorLocator(String name) {
		return new EditorLocator(name);
	}
	
	public static EditorScope editor(String name) {
		return editor(editorLocator(name));
	}
	
	public static EditorScope editor(IEditorLocator locator) {
		return new EditorScope(locator);
	}

	public static IFigureSearchScope unscoped() {
		return UnscopedSearch.getInstance();
	}

	public static IFigureSearchScope figureScopeForPart(IWorkbenchPartLocator part) {
		if (part instanceof IEditorLocator)
			return editor((IEditorLocator)part);
		if (part instanceof ViewLocator)
			return view((ViewLocator)part);
		throw new IllegalArgumentException(part == null ? "<null>" : part.toString());
	}

	private static ViewScope view(ViewLocator part) {
		return new ViewScope(part);
	}

	public static IWidgetSearchScope widgetScopeForPart(IWorkbenchPartLocator part) {
		if (part instanceof IEditorLocator)
			return editor((IEditorLocator)part);
		if (part instanceof ViewLocator)
			return view((ViewLocator)part);
		throw new IllegalArgumentException(part == null ? "<null>" : part.toString());
	}
	
	
}
