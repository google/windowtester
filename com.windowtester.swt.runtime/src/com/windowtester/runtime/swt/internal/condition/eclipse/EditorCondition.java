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
package com.windowtester.runtime.swt.internal.condition.eclipse;

import com.windowtester.internal.runtime.condition.NotCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.EditorNotFoundException;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder.MultipleEditorsFoundException;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;

/**
 * A factory for common editor conditions.
 *
 */
public class EditorCondition {

	
	public static class Active implements ICondition {
		private final String partName;
		public Active(String viewId) {
			this.partName = viewId;
		}
		public boolean test() {
			return EditorFinder.isEditorActiveNoRetries(partName);
		}	
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	public static class Dirty implements ICondition {
		private final String partName;
		public Dirty(String partName) {
			this.partName = partName;
		}
		public boolean test() {
			return EditorFinder.isEditorDirtyNoRetries(partName);
		}		
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	public static class Visible implements ICondition {
		private final String partName;
		public Visible(String partName) {
			this.partName = partName;
		}
		public boolean test() {
			try {
				return EditorFinder.isEditorControlVisibleNoRetries(partName);
			} catch (EditorNotFoundException e) {
				// TODO: show we handle this?	
			} catch (MultipleEditorsFoundException e) {
				// TODO: show we handle this?
			}
			return false;
		}		
		public ICondition not() {
			return new NotCondition(this);
		}
	}
	
	
	
	public static Active isActive(EditorLocator locator) {
		return new Active(locator.getPartName());
	}
	
	public static Dirty isDirty(EditorLocator locator) {
		return new Dirty(locator.getPartName());
	}
	
	public static Visible isVisible(EditorLocator locator) {
		return new Visible(locator.getPartName());
	}
	
	
}
