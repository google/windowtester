package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.locator.ShellLocator;
import com.windowtester.runtime.swt.condition.shell.ShellDisposedCondition;

public class testClosePreferenceWindow extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestClosePreferenceWindow() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Preferences")); // ? 3.4M7+
		ui.click(new MenuItemLocator("Window/Preferences...")); // ? Not generated on Mac
		ui.wait(new ShellShowingCondition("Preferences"));
		ui.ensureThat(new ShellLocator("Preferences").isClosed());
		ui.wait(new ShellDisposedCondition("Preferences"));
	}

}