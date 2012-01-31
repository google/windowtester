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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

import com.windowtester.runtime.internal.concurrent.VoidCallable;

/** 
 * A @link{Control} reference.
 * @param <T> the control type
 */
public class ControlReference<T extends Control>  extends SWTWidgetReference<T> {

	/**
	 * Constructs a new instance with the given control.
	 * 
	 * @param control the control.
	 */
	public ControlReference(T control) {
		super(control);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IControlReference#getForeground()
	 */
	public Color getForeground() {
		return displayRef.execute(new Callable<Color>() {
			public Color call() throws Exception {
				return widget.getForeground();
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IControlReference#getBackground()
	 */
	public Color getBackground() {
		return displayRef.execute(new Callable<Color>() {
			public Color call() throws Exception {
				return widget.getBackground();
			}
		});
	}
	
	
	/**
	 * Proxy for {@link Control#getMenu()}.
	 */
	public MenuReference getMenu() {
		return displayRef.execute(new Callable<MenuReference>() {
			public MenuReference call() throws Exception {
				Menu menu = widget.getMenu();
				if (menu == null)
					return null;
				return asReferenceOfType(menu, MenuReference.class);
			}
		});
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.IControlReference#isVisible()
	 */
	public boolean isVisible() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.isVisible();
			}
		});
	}
	
	/**
	 * Check to see if this control has focus.
	 */
	public boolean hasFocus() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				ControlReference<?> control = displayRef.getFocusControl();
				if (control == null)
					return false;
				return control.getWidget() == widget;
			}
		});
	}
	
	/**
	 * Programmatically set focus to the control
	 */
	public boolean setFocus() {
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.setFocus();
			}
		});
	}
	
	public ShellReference getShell() {
		return displayRef.execute(new Callable<ShellReference>() {
			public ShellReference call() throws Exception {
				return ShellReference.forShell(widget.getShell());
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference#getParent()
	 */
	public CompositeReference<Composite> getParent() {
		return displayRef.execute(new Callable<CompositeReference<Composite>>() {
			public CompositeReference<Composite> call() throws Exception {
				Composite parent = widget.getParent();
				if (parent == null)
					return null;
				return new CompositeReference<Composite>(parent);
			}
		});
	}
	
//	public boolean isMatchedBy(ISWTWidgetMatcher matcher){
//		return matcher.matchesControl(this);
//	}
}
