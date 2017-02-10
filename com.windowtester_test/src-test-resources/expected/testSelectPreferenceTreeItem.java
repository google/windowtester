package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator; // ? not on the Mac
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testSelectPreferenceTreeItem extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestSelectPreferenceTreeItem() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Preferences")); // ? 3.4M7+
		ui.click(new MenuItemLocator("Window/Preferences...")); // ? 3.2+
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.click(new FilteredTreeItemLocator("Help"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

}