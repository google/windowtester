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
package com.windowtester.runtime.gef.locator;

import org.eclipse.draw2d.Figure;
import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.internal.matchers.NamedFigureMatcher;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;

/**
 * Locates {@link Figure} references by "name".
 * <p>
 * Figures are named by implementing the special <code>getFigureId()</code> method on 
 * the <code>Figure</code> of interest.  Any figure that provides a <code>getFigureId()</code> method can
 * be identified using a <code>NamedFigureLocator</code>.  Named figures will resolve
 * to <code>NamedFigureLocator</code>s at recording time. An example of a figure that
 * could be identified using this scheme is as follows:
 * <p>
 * <pre>
 * class MyFigure extends Figure {
 *   private static final String FIGURE_NAME = "my.figure";
 *   public String getFigureId() {
 *      return FIGURE_NAME;
 *   }
 * }
 * </pre>  
 * A recording of a click on this figure would then yield a test with a call like this:
 * <pre>
 *   ...
 *   ui.click(new NamedFigureLocator("my.figure"));
 * </pre>
 * <p>
 * 
 * Do note that the visibility of the <code>getFigureId()</code> method is not required 
 * to be <code>public</code>.  This means that your 
 * test-enabling method hooks do not need to be outward-facing if that is not desirable.
 * Also note that the onus is on you, the programmer, to ensure that figure IDs are unique.
 * The above example is an especially BAD naming scheme since every instance of this figure
 * will bear the same name.  To this point, it should be noted that, while supported, 
 * figure naming is often not desirable since <code>Figure</code>s
 * rarely themselves know enough about the domain objects they represent in order
 * to uniquely tag them.  A better alternative is generally to put this logic in the 
 * {@link EditPart}, using the special naming scheme described in {@link NamedEditPartFigureLocator}.
 *
 * <p>  
 *   
 * @see NamedEditPartFigureLocator
 * @see NamedWidgetLocator
 *
 */
public class NamedFigureLocator extends FigureLocator {

	private static final long serialVersionUID = 1695107448536810569L;
	private final String figureId;

	/**
	 * Create a named figure locator that locates figures with the given name.
	 * @param figureId - the name of the figure to locate
	 */
	public NamedFigureLocator(String figureId) {
		super(new NamedFigureMatcher(figureId));
		this.figureId = figureId;
	}
	
	/**
	 * Get this named figure's identifying name.
	 */
	public String getFigureId() {
		return figureId;
	}
	
}
