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
package com.windowtester.runtime.swt.internal.widgets;

import java.util.concurrent.Callable;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Scrollable;

/** 
 * A {@link Scrollable} reference.
 * @param <T> the scrollable type
 */
public class ScrollableReference<T extends Scrollable>  extends ControlReference<T> {

	public ScrollableReference(T scrollable) {
		super(scrollable);
	}

	/**
	 * Proxy for {@link Scrollable#getClientArea()}.
	 * 
	 * @return the client area
	 */
	public Rectangle getClientArea() {
		return displayRef.execute(new Callable<Rectangle>() {
			public Rectangle call() throws Exception {
				return widget.getClientArea();
			}
		});
	}
	
	
	/**
	 * Proxy for {@link Scrollable#getHorizontalBar()}.
	 * <p/>
	 * @return the horizontal bar.
	 */
	public ScrollBarReference getHorizontalBar() {
		return displayRef.execute(new Callable<ScrollBarReference>() {
			public ScrollBarReference call() throws Exception {
				return new ScrollBarReference(widget.getHorizontalBar());
			}
		});
	}

	/**
	 * Proxy for {@link Scrollable#getVerticalBar()}.
	 * <p/>
	 * @return the vertical bar.
	 */
	public ScrollBarReference getVerticalBar() {
		return displayRef.execute(new Callable<ScrollBarReference>() {
			public ScrollBarReference call() throws Exception {
				return new ScrollBarReference(widget.getVerticalBar());
			}
		});
	}
}
