package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.locator.CTabItemLocator; // ?
import com.windowtester.runtime.swt.locator.SWTWidgetLocator; // ? Mac
import org.eclipse.swt.custom.CTabFolder; // ? Mac
import org.eclipse.swt.widgets.Composite; // ? Mac
import com.windowtester.runtime.locator.XYLocator; // ? Mac

public class testClickErrorLogCTabItem extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestClickErrorLogCTabItem() throws Exception {
		IUIContext ui = getUI();
		ui.click(new MenuItemLocator("Window/Show View/Error Log"));
		ui.click(new CTabItemLocator("Error Log")); // ?
		ui.click(new XYLocator(new SWTWidgetLocator(CTabFolder.class, 1, // ? Mac
				new SWTWidgetLocator(Composite.class)), 321, 11)); // ? Mac
	}

}