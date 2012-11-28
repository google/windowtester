/*******************************************************************************
 *  Copyright (c) 2012 Phillip Jensen
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Phillip Jensen - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Slider;

/**
 * A {@link Slider} reference.
 */
public class SliderReference extends ControlReference<Slider> {

	public SliderReference(Slider control) {
		super(control);
	}

	/**
	 * Gets the current selection in the slider.
	 * 
	 * @return the current selection in the slider.
	 */
	public int getSelection() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getSelection();
			}
		});
	}

	/**
	 * Gets the minimum from the slider.
	 * 
	 * @return the minimum the slider.
	 */
	public int getMinimum() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getMinimum();
			}
		});
	}

	/**
	 * Gets the maximum from the slider.
	 * 
	 * @return the maximum the slider.
	 */
	public int getMaximum() {
		return displayRef.execute(new Callable<Integer>() {
			public Integer call() throws Exception {
				return widget.getMaximum();
			}
		});
	}
}
