/* InvokeMenuITem.java
 * Created on May 16, 2005
 * 
 * This utility class can be used to invoke an arbitrary menu item from the menu bar.
 */
package abbot.swt.eclipse.utils;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import abbot.tester.swt.MenuItemTester;
import abbot.tester.swt.WidgetTester;

/**
 * @author Chris Jaun
 */

public class InvokeMenuItem {
	
	final static MenuItemTester menuItemTester = (MenuItemTester) WidgetTester.getTester(MenuItem.class);

	/**
	 * Will invoke an arbitrary menu item from the menu bar.
	 * 
	 * @param menuPath - The path to the menu item
	 * @param parentShell - The parent window shell
	 */
	
	public static void invoke(String menuPath, Shell parentShell) {
		menuItemTester.actionSelectMenuItem(menuPath, null, parentShell, 100);
	}
}
