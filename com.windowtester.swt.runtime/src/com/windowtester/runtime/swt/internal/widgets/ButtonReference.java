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

import org.eclipse.swt.widgets.Button;

import com.windowtester.runtime.condition.ICondition;

/**
 * A  {@link Button} reference.
 */
public class ButtonReference extends ControlReference<Button>{

	public class SelectedCondition implements ICondition { 

		private final boolean selected;

		public SelectedCondition(boolean selected) {
			this.selected = selected;
		}
		
		public boolean test() {
			return getSelection() == selected;
		}
		
		@Override
		public String toString() {
			return ButtonReference.this.toString() + " to be selected (" + selected +")";
		}
	}
	
	public ButtonReference(Button control) {
		super(control);
	}

	/**
	 * Proxy for {@link Button#getSelection()}.
	 */
	public boolean getSelection(){
		return displayRef.execute(new Callable<Boolean>() {
			public Boolean call() throws Exception {
				return widget.getSelection();
			}
		});
	}
	
	public ICondition isSelected(){
		return isSelected(true);
	}

	public ICondition isSelected(boolean selected) {
		return new SelectedCondition(selected);
	}
	
}
