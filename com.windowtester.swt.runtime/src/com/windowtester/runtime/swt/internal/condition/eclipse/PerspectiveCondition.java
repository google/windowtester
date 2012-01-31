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

import org.eclipse.ui.IPerspectiveDescriptor;

import com.windowtester.internal.runtime.condition.NotCondition;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.finder.eclipse.PerspectiveFinder;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;

public abstract class PerspectiveCondition implements ICondition {
	
	
	public static ICondition isOpen(IPerspectiveDescriptor perspective) {
		return new Open(perspective);
	}
	
	public static ICondition isActive(IPerspectiveDescriptor perspective) {
		return new Active(perspective);
	}
	
	protected final IPerspectiveDescriptor perspective;

	PerspectiveCondition(IPerspectiveDescriptor perspective) {
		this.perspective = perspective;
	}
	
	
	static class Open extends PerspectiveCondition {
		
		Open(IPerspectiveDescriptor perspective) {
			super(perspective);
		}

		public boolean test() {
			return perspective != null;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "OpenPerspectiveCondition(" + perspective.getId() +")";
		}
	}
	
	static class Active extends PerspectiveCondition {
		
		Active(IPerspectiveDescriptor perspective) {
			super(perspective);
		}

		public boolean test() {
			if (perspective == null)
				return false;
			return PerspectiveFinder.findActive() == perspective;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "ActivePerspectiveCondition(" + perspective.getId() +")";
		}
	}

	public static PerspectiveCondition isActive(PerspectiveLocator perspectiveLocator) {
		return new Active(PerspectiveFinder.findWithId(perspectiveLocator.getPerspectiveId()));
	}
	
	public ICondition not() {
		return new NotCondition(this);
	}
	
	
}