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
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

import com.windowtester.runtime.gef.internal.matchers.NamedEditPartMatcher;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;

/**
 * Locates {@link Figure} references by {@link EditPart} "name".
 * <p>
 * EditParts are named by implementing the special <code>getEditPartId()</code> method on 
 * the <code>EditPart</code> of interest.  Any EditPart that provides a <code>getEditPartId()</code> 
 * method can used to identify its associated {@link IFigure} using a <code>NamedEditPartFigureLocator</code>.  
 * Named edit parts will resolve
 * to <code>NamedEditPartFigureLocator</code>s at recording time. An example of an edit part that
 * could be identified using this scheme is as follows:
 * <p>
 * <pre>
 * class MyPart extends AbstractEditPart {
 *   private String getEditPartId() {
 *      return ((Model)getModel()).getName();
 *   }
 *   ...
 * }
 * </pre>  
 * The EditPart id in this case is derived from the backing model object (an instance of
 * a fictitious <code>Model</code> class).  Supposing a particular EditPart is backed
 * by a <code>Model</code> with the name "Homer", a recording of a click on this figure 
 * would then yield a test with a call like this:
 * <pre>
 *   ...
 *   ui.click(new NamedEditPartFigureLocator("Homer"));
 * </pre>
 * <p>
 * 
 * Notice that as this example demonstrates, the visibility of the <code>getEditPartId()</code> 
 * method is not required to be <code>public</code> (though it can be).  This means that your 
 * test-enabling method hooks do not need to be outward-facing if that is not desirable.
 * Also note that it is the programmer's responsibility to ensure that edit part IDs are 
 * unique.
 * 
 *
 * <p>  
 *   
 * @see NamedFigureLocator  
 * @see NamedWidgetLocator
 */
public class NamedEditPartFigureLocator extends FigureLocator {

	private static final long serialVersionUID = 1695107448536810569L;
	private final String partId;

	/**
	 * Create a named edit part figure locator that locates figures with the 
	 * given edit part name.
	 * @param partId - the name of the edit part behind the figure to locate
	 */
	public NamedEditPartFigureLocator(String partId) {
		super(new NamedEditPartMatcher(partId));
		this.partId = partId;
	}
	
	/**
	 * Get this named edit part's identifying name.
	 */
	public String getEditPartId() {
		return partId;
	}
	
}
