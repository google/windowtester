package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;

public class testMultipleShapeCreateAndClick extends UITestCaseSWT {

	private static class EllipseLocator extends FigureClassLocator {
		private static final long serialVersionUID = 1L;
		
		public EllipseLocator() {
			super("org.eclipse.draw2d.Ellipse");
		}
	}

	/**
	 * Main test method.
	 */
	public void testtestMultipleShapeCreateAndClick() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(50, 50));
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(150, 150));
		ui.click(new LRLocator(0, new EllipseLocator()));
		ui.click(new LRLocator(1, new EllipseLocator()));
	}

}