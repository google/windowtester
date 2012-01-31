package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.IUIContext;

public class testClickPaletteButton extends UITestCaseSWT {

		/**
		 * Main test method.
		 */
		public void testtestClickPaletteButton() throws Exception {
			IUIContext ui = getUI();
			ui.click(new PaletteButtonLocator());
		}
		
}