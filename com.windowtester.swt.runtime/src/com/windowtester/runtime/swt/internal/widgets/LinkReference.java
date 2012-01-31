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

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Link;

import com.windowtester.runtime.internal.concurrent.SafeCallable;

/**
 * A {@link Link} reference.
 */
public class LinkReference extends ControlReference<Link>{

	public LinkReference(Link control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.ControlReference#isVisible()
	 */
	@Override
	public boolean isVisible() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				// for some reason Links return false when asked if "isVisible"...
				// so use getVisible() instead
				return widget.getVisible();
			}
		});
	}
	
	/**
	 * Get link text offset index.
	 */
	public Rectangle getOffset(final int index) {
		return displayRef.execute(new SafeCallable<Rectangle>() {
			public Rectangle call() throws Exception {
				Class<? extends Link> linkClass = widget.getClass();
				Field field = linkClass.getDeclaredField("offsets");
				field.setAccessible(true);
				Point[] offsets = (Point[]) field.get(widget);
				Field layoutField = linkClass.getDeclaredField("layout");
				layoutField.setAccessible(true);
				TextLayout layout = (TextLayout) layoutField.get(widget);
				Point offset = offsets[index];
				boolean synthesized = false;
				if (layout == null) {
					/*
					 * in win32, the layout is coming back null. The remedy
					 * is to synthesize our own.
					 */
					synthesized = true;
					layout = synthesizeLayout();
				}

				Rectangle bounds = layout.getBounds(offset.x, offset.y);
				if (synthesized)
					layout.dispose();
				return bounds;
			}
			public Rectangle handleException(Throwable e) throws Throwable {
				throw new RuntimeException("unable to get Link offset", e);
			}
		});
	}
	
	protected TextLayout synthesizeLayout() {
		TextLayout layout = new TextLayout(widget.getDisplay());
		layout.setOrientation(SWT.LEFT_TO_RIGHT);
		layout.setText(widget.getText());
		layout.setFont(widget.getFont());
		return layout;
	}
	
}
