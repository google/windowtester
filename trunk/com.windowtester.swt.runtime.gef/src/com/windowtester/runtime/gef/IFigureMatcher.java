/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.gef;

import org.eclipse.draw2d.IFigure;

import com.windowtester.runtime.gef.locator.FigureLocator;


/**
 * A matcher for identifying {@link IFigure} instances.
 * <p>
 * Matchers are used to specify figures of interest. For instance, to select
 * figures whose class is <code>MyFigure.class</code> we could write a 
 * matcher like this:
 * <pre>
 *   class MyFigureMatcher implements IFigureMatcher {
 *      public boolean matches(IFigureReference figureRef) {
 *         return figureRef.getFigure().getClass().equals(MyFigure.class);
 *      }
 *   } 
 </pre>
 * 
 * This matcher could then be used to specify a {@link FigureLocator} which
 * could be used to drive the UI. For example, if we wanted to click an instance
 * of <code>MyFigure</code> we might write:
 * <pre>
 *    ui.click(new FigureLocator(new MyFigureMatcher());
 * </pre>
 * For convenience, we might go further and provide a subclass of
 * {@link FigureLocator} like so:
 * <pre>
 *    class MyFigureLocator extends FigureLocator {
 *       public MyFigureLocator() {
 *       	super(new MyFigureMatcher());
 *       }
 *    }
 * </pre>
 * 
 * Having done this, <code>MyFigure</code>s could be located like this:
 * <pre>
 *    ui.click(new MyFigureLocator());
 * </pre>
 */ 
public interface IFigureMatcher /* extends IGEFMatcher */ {

	/**
	 * Check whether the given figure satisfies the specified criteria.
	 * 
	 * @param figureRef the figure reference to test
	 * @return <code>true</code> if the figure matches,
	 * 		<code>false</code> otherwise
	 */
	boolean matches(IFigureReference figureRef);

}
