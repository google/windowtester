package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.eclipse.ContributedToolItemLocator;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testOpenSearchToolItem extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestOpenSearchToolItem() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Navigator"));
		ui.click(new ContributedToolItemLocator(
				"org.eclipse.search.ui.openSearchDialog"));  // ? 
				"org.eclipse.search.OpenSearchDialogPage")); // ? 3.4+
		ui.wait(new ShellShowingCondition("Search"));
		ui.click(new ButtonLocator("Cancel"));
		ui.wait(new ShellDisposedCondition("Search"));
	}

}