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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Item;

/**
 * An {@link Item} reference.
 */
public class ItemReference<T extends Item> extends SWTWidgetReference<T> {
	
	public ItemReference(T item) {
		super(item);
	}

	public String getText() {
		return displayRef.execute(new Callable<String>() {
			public String call() throws Exception {
				return widget.getText();
			}
		});
	}
	
	public Image getImage() {
		return displayRef.execute(new Callable<Image>() {
			public Image call() throws Exception {
				return widget.getImage();
			}
		});
	}
}
