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

import org.eclipse.swt.custom.StyledText;

import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;

/**
 * @author Phil Quitslund
 */
public class StyledTextHighlighter extends AbstractControlHighlighter {

	/**
	 * Create an instance.
	 * @param text
	 * @param settings 
	 */
	public StyledTextHighlighter(StyledText text, PlaybackSettings settings) {
		super(text, settings);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#getPixelBuffer()
	 */
	protected int getPixelBuffer() {
		return 5;
	}
	
	
}
