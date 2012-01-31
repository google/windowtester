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

import org.eclipse.draw2d.FigureCanvas;

import com.windowtester.runtime.gef.internal.locator.DelegatingLocator;
import com.windowtester.runtime.gef.internal.locator.FigureCanvasLocatorDelegate;
import com.windowtester.runtime.gef.internal.locator.ScopeFactory;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.IWorkbenchPartLocator;

/**
 * Locates the {@link FigureCanvas} in a GEF Graphical Editor.
 * For convenience, you may use {@link FigureCanvasXYLocator} to specify
 * a particular location on the canvas rather than this locator.
 * By default, search is scoped by the active editor.  If the active editor 
 * is not desired, another editor or part can be specified.
 */
public class FigureCanvasLocator extends DelegatingLocator {

	private static final long serialVersionUID = 5467204369110634362L;

	/**
	 * Create an instance that locates the canvas in the active editor.
	 */
	public FigureCanvasLocator() {
		this(ScopeFactory.unspecifedEditorLocator());
	}

	/**
	 * Create an instance that locates the canvas in the editor with the given name.
	 * 
	 * @param editorName the name of the target editor
	 * @see EditorLocator#EditorLocator(String)
	 */
	public FigureCanvasLocator(String editorName) {
		this(ScopeFactory.editorLocator(editorName));
	}

	/**
	 * Create an instance that locates the canvas in the given part.
	 * 
	 * @param partLocator the target part
	 */
	public FigureCanvasLocator(IWorkbenchPartLocator partLocator) {
		super(new FigureCanvasLocatorDelegate(partLocator));
	}

}
