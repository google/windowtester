package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.forms.HyperlinkLocator;
import com.windowtester.runtime.IUIContext;

public class testHyperlinkClicks extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestHyperlinkClicks() throws Exception {
		IUIContext ui = getUI();
		ui.click(new HyperlinkLocator("http://www.instantiations.com")
				.inSection("Section"));
	}

}