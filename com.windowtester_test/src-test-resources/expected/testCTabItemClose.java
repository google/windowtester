package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.CTabItemLocator;

public class testCTabItemClose extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestCTabItemClose() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Error Log"));
		ui.ensureThat(new CTabItemLocator("Error Log").isClosed());
	}

}