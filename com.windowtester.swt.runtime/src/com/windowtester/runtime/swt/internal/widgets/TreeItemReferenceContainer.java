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

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * Interface for accessing tree items regardless of whether they are top level children in
 * a {@link Tree} or nested elements in a {@link TreeItem}.
 */
public interface TreeItemReferenceContainer
{
	/**
	 * Proxy for {@link Widget#getDisplay()}.
	 */
	DisplayReference getDisplayRef();
	
	/**
	 * Expand to show the items contained by the recevier
	 */
	void expand();

	/**
	 * Proxy for {@link Tree#getItems()} and {@link TreeItem#getItems()}
	 */
	TreeItemReference[] getItems();
}