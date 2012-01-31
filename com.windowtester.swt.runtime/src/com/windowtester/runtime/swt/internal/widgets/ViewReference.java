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
package com.windowtester.runtime.swt.internal.widgets;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.swt.internal.operation.SWTShowViewMenuOperation;

/**
 * A reference to a view. This class is initially implemented for its
 * {@link #showPulldownMenu(IClickDescription)} method and needs work to make it a first
 * class SWT reference
 */
public class ViewReference
	implements ISWTWidgetReference<Widget> // TODO Should it be <Widget> or what ?
	// Or should ViewReference subclass Composite or Canvas instead?
{
	// TODO move atomic view operations and accessors from ViewLocator into this class

	private final String viewId;

	public ViewReference(String viewId) {
		this.viewId = viewId;
	}

	public Widget getWidget() {
		// TODO what should be returned here?
		throw new RuntimeException("Not implemented yet.");
	}

	public IWidgetLocator[] findAll(IUIContext ui) {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean matches(Object widget) {
		throw new RuntimeException("Not implemented yet.");
	}

	public MenuReference showPulldownMenu(IClickDescription click) {
		SWTShowViewMenuOperation op = new SWTShowViewMenuOperation().openViewMenu(viewId);
		op.execute();
		return op.getMenu();
	}

	public ISWTWidgetReference<Widget>[] getChildren() {
		throw new RuntimeException("Not implemented yet.");
	}

	public Object getData(String key) {
		throw new RuntimeException("Not implemented yet.");
	}

	public Object getData() {
		throw new RuntimeException("Not implemented yet.");
	}

	public Rectangle getDisplayBounds() {
		throw new RuntimeException("Not implemented yet.");
	}

	public String getName() {
		throw new RuntimeException("Not implemented yet.");
	}

	public ISWTWidgetReference<Widget> getParent() {
		throw new RuntimeException("Not implemented yet.");
	}

	public int getStyle() {
		throw new RuntimeException("Not implemented yet.");
	}

	public String getText() {
		throw new RuntimeException("Not implemented yet.");
	}

	public String getTextForMatching() {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean hasStyle(int style) {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean hasText() {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean isDisposed() {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean isEnabled() {
		throw new RuntimeException("Not implemented yet.");
	}

	public boolean isVisible() {
		throw new RuntimeException("Not implemented yet.");
	}
}