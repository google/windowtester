package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.eclipse.PullDownMenuItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testViewMenu extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestViewMenu() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Package Explorer"));
		ui.click(new PullDownMenuItemLocator("Filters...", new ViewLocator(
				"org.eclipse.jdt.ui.PackageExplorer")));
		ui.wait(new ShellShowingCondition("Java Element Filters"));
		ui.click(new ButtonLocator(
				"&Name filter patterns (matching names will be hidden):"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Java Element Filters"));
	}

}