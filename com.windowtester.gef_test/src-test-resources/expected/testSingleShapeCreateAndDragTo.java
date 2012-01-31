package expected;

import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.locator.FigureCanvasXYLocator;
import com.windowtester.runtime.gef.locator.FigureClassLocator;

public class testSingleShapeCreateAndDragTo extends UITestCaseSWT {

	private static class EllipseLocator extends FigureClassLocator {
		private static final long serialVersionUID = 1L;
		
		public EllipseLocator() {
			super("org.eclipse.draw2d.Ellipse");
		}
	}

	/**
	 * Main test method.
	 */
	public void testtestSingleShapeCreateAndDragTo() throws Exception {
		IUIContext ui = getUI();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new FigureCanvasXYLocator(100, 100));
		ui.click(new EllipseLocator());
		ui.dragTo(new FigureCanvasXYLocator(170, 150));
	}

}