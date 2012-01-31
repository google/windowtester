package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition; // ?
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;

public class testSingleShapeCreate extends UITestCaseSWT {

	/**
	 * Main test method.
	 */
	public void testtestSingleShapeCreate() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.wait(new ShellShowingCondition("")); // ?
		ui.click(new FigureCanvasXYLocator(100, 100));
	}

}