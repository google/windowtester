package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.FilteredTreeItemLocator;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testSimpleJavaProjectCreation extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestSimpleJavaProjectCreation() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("File/New/Project..."));
		ui.wait(new ShellShowingCondition("New Project"));
		ui.click(new FilteredTreeItemLocator("Java Project")); // ? https://fogbugz.instantiations.com/default.php?44689
		ui.click(new FilteredTreeItemLocator("Java/Java Project"));
		ui.click(new ButtonLocator("&Next >"));
		ui.enterText("JP2");
		ui.click(new ButtonLocator("&Finish"));
		ui.wait(new ShellDisposedCondition("New Java Project"));
	}

}