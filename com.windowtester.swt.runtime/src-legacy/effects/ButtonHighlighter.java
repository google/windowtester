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

import org.eclipse.swt.widgets.Button;

import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;

/**
 * @author Phil Quitslund
 */
public class ButtonHighlighter extends AbstractControlHighlighter {
	
	/**
	 * Create an instance.
	 * @param button
	 * @param settings 
	 */
	public ButtonHighlighter(Button button, PlaybackSettings settings) {
		super(button, settings);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#getPixelBuffer()
	 */
	protected int getPixelBuffer() {
		return 1;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.effects.AbstractControlHighlighter#postOutline()
	 */
	public void preOutline() {
		//System.out.println("pre-outline");
		//color the button momentarily
//		Button button = (Button)_widget;
//		button.setBackground(_display.getSystemColor(SELECT_COLOR));
//		button.se
//		button.update();
	}
	
}
