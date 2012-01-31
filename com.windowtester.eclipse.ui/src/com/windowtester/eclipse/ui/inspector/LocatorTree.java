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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.windowtester.internal.runtime.locator.LocatorIterator;
import com.windowtester.runtime.locator.ILocator;

public class LocatorTree {


	public static Tree forLocatorInComposite(ILocator locator, Composite parent) {
		Tree tree = new Tree(parent, SWT.NONE);
		//section.setClient(tree);
		
		final ToolTipHandler tooltip = new ToolTipHandler(parent.getShell());
		
		
		LocatorIterator locators = LocatorIterator.forLocator(locator);
		
		//first is root tree node
		if (!locators.hasNext())
			return tree;
		
		TreeItem item = new TreeItem(tree, SWT.NONE);
		
		ILocator next = locators.next();
		setTextAndImage(item, next);
		
		
		for ( ; locators.hasNext(); ) {
			item.setExpanded(true); //expand last
			item = new TreeItem(item, SWT.NONE);
			next = locators.next();
			setTextAndImage(item, next);
		}
		
		tooltip.activateHoverHelp(tree);
		
		return tree;
	}

	private static void setTextAndImage(TreeItem item, ILocator locator) {
		String shortString = toShortString(locator);
		String fullString  = toString(locator);
		if (!fullString.equals(shortString))
			item.setData (ToolTipHandler.TIP_TEXT_DATA_KEY, fullString);
		item.setText(shortString);
		item.setImage(toImage(locator));
	}

	private static String toShortString(ILocator locator) {
		return LocatorString.forDisplayShort(locator);
	}

	private static Image toImage(ILocator locator) {
		return getClassIcon();
	}

	private static String toString(ILocator locator) {
		return LocatorString.forDisplay(locator);
	}
	
	private static Image getClassIcon() {
		return ImageManager.getImage("class.gif");
	}
	
}
