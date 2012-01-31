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
package com.windowtester.runtime.swt.internal.effects;

import org.eclipse.swt.widgets.Tree;

import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;

public class TreeHighlighter extends AbstractControlHighlighter {


	public TreeHighlighter(Tree tree, PlaybackSettings settings) {
		super(tree, settings);
	}


	/**
	 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#getPixelBuffer()
	 */
	protected int getPixelBuffer() {
		return 3;
	}

}
