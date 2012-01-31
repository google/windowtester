package com.windowtester.test.gef.factories;


import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;

/**
 * Factory for common locators.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class LocatorFactory {

	public static ILocator paletteItem(String path) {
		return new PaletteItemLocator(path);
	}
	
	public static ILocator button(String label) {
		return new ButtonLocator(label);
	}
	
	public static ILocator treeItem(String path) {
		return new TreeItemLocator(path);
	}
	
	public static ILocator menuItem(String path) {
		return new MenuItemLocator(path);
	}
	
	public static ILocator canvas(String diagramName) {
		return new FigureCanvasLocator(diagramName);
	}
	
	public static ILocator xy(ILocator loc, int x, int y) {
		return new XYLocator(loc, x, y);
	}

	public static ICondition shellShowing(String title) {
		return new ShellShowingCondition(title);
	}
	
	public static ICondition shellDisposed(String title) {
		return new ShellDisposedCondition(title);
	}
	
}
