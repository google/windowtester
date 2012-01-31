package com.windowtester.test.gef.tests.runtime;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.draw2d.internal.condition.FigureXYCondition;
import com.windowtester.runtime.gef.IFigureReference;
import com.windowtester.runtime.gef.locator.FigureLocator;
import com.windowtester.runtime.gef.locator.LRLocator;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.test.gef.tests.common.BaseShapeDrivingTest;

/**
 * Repros for:
 * http://fogbugz.instantiations.com/default.php?36678
 * http://fogbugz.instantiations.com/default.php?36709
 * https://fogbugz.instantiations.com/default.php?36741
 * <p>
 * Copyright (c) 2008, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class DragToFigureTargetTest extends BaseShapeDrivingTest {

	
	public void testDragFigureToFigure() throws Exception {
		createEllipseAt(50, 50);
		createEllipseAt(100, 100);
		IUIContext ui = getUI();
		
		//ui.dragTo(new XYLocator(400, 400));
			
		dragToFigureXY(ui, 0, 0);
		ui.pause(2000);
		
		dragToFigure(ui);
		ui.pause(2000);
	}


	//http://fogbugz.instantiations.com/default.php?36709
	private void dragToFigureXY(IUIContext ui, int x, int y) throws WidgetSearchException {
		final IFigureReference dragSource = (IFigureReference) ui.find(lr(0, ellipseLocator()));
		final IFigureReference dragTarget = (IFigureReference) ui.find(lr(1, ellipseLocator()));
		
		
		ui.click(lr(0, ellipseLocator()));
		ui.dragTo(xy(lr(1, ellipseLocator()), x, y));
						
		ui.assertThat(new ICondition() {
			private org.eclipse.draw2d.geometry.Point center;
			private org.eclipse.draw2d.geometry.Point topLeft;
			public boolean test() {
				center = dragSource.getFigure().getBounds().getCenter();
				topLeft = dragTarget.getFigure().getBounds().getTopLeft();
				
				return center.equals(topLeft);
			}
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			public String toString() {
				return "center (" + center +") expected to be: " + topLeft;
			}
		});

		
	}


	//http://fogbugz.instantiations.com/default.php?36678
	private void dragToFigure(IUIContext ui) throws WidgetSearchException {
		IFigureReference dragSource = (IFigureReference) ui.find(lr(0, ellipseLocator()));
		IFigureReference dragTarget = (IFigureReference) ui.find(lr(1, ellipseLocator()));
		
		ui.click(dragSource);
		ui.dragTo(dragTarget);
		
		//should really have identical bounds
		org.eclipse.draw2d.geometry.Point targetTopLeft = dragTarget.getFigure().getBounds().getTopLeft();
		ui.assertThat(new FigureXYCondition(dragSource.getFigure(), targetTopLeft));
		
	}
	
	
	
	private XYLocator xy(ILocator loc, int x, int y) {
		return new XYLocator(loc, x, y);
	}

	private LRLocator lr(int index, FigureLocator locator) {
		return new LRLocator(index, locator);
	}
	
	private FigureLocator ellipseLocator() {
		return new FigureLocator(ellipseMatcher);
	}
	
}
