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
package com.windowtester.runtime.swt.locator;

/**
 * A factory for creating common locators.
 * <p>
 * Using this factory promotes (more) readable tests and writing tests
 * by hand.  For maximum effect, it is best imported statically.
 * <p>
 * Tests written using factories read more fluently than their explicit 
 * constructor counterparts.  For example:
 * 
 * <pre>
 *   ui.click(treeCell("foo/bar").at(column(3)).in(view("MyView"));
 * </pre>
 * 
 * reads much like a specification.
 * <p>
 * Since this approach is still very much under review and investigation, your feedback is invaluable.
 * Until the API is hardened, it will remain provisional.
 * 
 * <p>
 * <strong>PROVISIONAL</strong>. This class has been added as
 * part of a work in progress. There is no guarantee that this API will
 * work or that it will remain the same. Please do not use this API for more than
 * experimental purpose without consulting with the WindowTester team.
 * </p> 
 */
public class SWTLocators {

	public static TreeCellLocator treeCell(String fullPath) {
		return new TreeCellLocator(fullPath);
	}

	public static TreeCellLocator.Column column(int columnIndex) {
		return new TreeCellLocator.Column(columnIndex);
	}
	
	public static MenuItemLocator menuItem(String menuPath) {
		return new MenuItemLocator(menuPath);
	}
	
	public static ButtonLocator button(String buttonText) {
		return new ButtonLocator(buttonText);
	}
	
	public static TreeItemLocator treeItem(String treeItemPath) {
		return new TreeItemLocator(treeItemPath);
	}
	
	public static ShellLocator shell(String shellTitle) {
		return new ShellLocator(shellTitle);
	}
	
	public static CComboItemLocator ccomboItem(String itemText) {
		return new CComboItemLocator(itemText);
	}	
	
}
