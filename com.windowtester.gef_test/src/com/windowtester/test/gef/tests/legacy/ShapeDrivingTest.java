package com.windowtester.test.gef.tests.legacy;




import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.gef.internal.experimental.locator.ModelObjectLocator;
import com.windowtester.runtime.gef.locator.FigureCanvasLocator;
import com.windowtester.runtime.gef.locator.PaletteButtonLocator;
import com.windowtester.runtime.gef.locator.PaletteItemLocator;
import com.windowtester.runtime.internal.matcher.ClassByNameMatcher;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;


/**
 * Sample Shape Example driving test (tk 2).
 * Note that this is sketchy and relies on NON-API calls.  Interfaces subject to change...
 * 
 * <p>
 * Copyright (c) 2006, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ShapeDrivingTest extends BaseShapeDrivingTest {

	
	/*
	 * This model is not rich enough for us to disambiguate instances.
	 * 
	 * These locators just serve as a simple example of how GEF locatros might be authored.
	 * 
	 */
	
	
	/**
	 * Locates parts by Model Object Class.
	 */
	static class ByModelObjectClassNameLocator extends ModelObjectLocator {
		public ByModelObjectClassNameLocator(String className) {
			super(new ClassByNameMatcher(className));
		}
	}
	
	static class RectangularShapeLocator extends ByModelObjectClassNameLocator {
		public RectangularShapeLocator() {
			super("org.eclipse.gef.examples.shapes.model.RectangularShape");
		}
	}
	
	static class EllipticalShapeLocator extends ByModelObjectClassNameLocator {
		public EllipticalShapeLocator() {
			super("org.eclipse.gef.examples.shapes.model.EllipticalShape");
		}
	}
	

	public void testDrive() throws Exception {
		
		//setup
		createShapeDiagramExample();
		
		
		IUIContext ui = getUI();
		
		//open the shape palette
		ui.click(new PaletteButtonLocator());
		//wait for palette to open --- TODO: this should be a condition and in runtime
		ui.pause(2000);

		//select and drop ellipse
		//selectEllipseInPalette();
		ui.click(new PaletteItemLocator("Shapes/Ellipse"));
		ui.click(new XYLocator(new FigureCanvasLocator(), 100, 100));
		
		//select and drop rectangle
		ui.click(new PaletteItemLocator("Shapes/Rectangle"));		
		ui.click(new XYLocator(new FigureCanvasLocator(), 10, 10));
		
		//connect the items
		ui.click(new PaletteItemLocator("Solid connection"));
		ui.click(new EllipticalShapeLocator());
		ui.click(new RectangularShapeLocator());
		
		
		
		//pause so we can see inspect what happened...
		ui.pause(500);		
			
		//save our changes
		ui.click(new MenuItemLocator("File/Save")); 
	}
	

		
	
}
