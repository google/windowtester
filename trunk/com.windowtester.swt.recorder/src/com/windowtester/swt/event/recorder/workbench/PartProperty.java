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
package com.windowtester.swt.event.recorder.workbench;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;

public class PartProperty {

	
	public static final PropertyMapping IS_DIRTY   = PropertyMapping.withKey("isDirty").withName("Is Dirty");
	public static final PropertyMapping IS_ACTIVE  = PropertyMapping.withKey("isActive").withName("Is Active");
	public static final PropertyMapping IS_VISIBLE = PropertyMapping.withKey("isVisible").withName("Is Visible");

	
	public static PropertySet isDirty(boolean dirty) {
		return PropertySet.empty().withMapping(IS_DIRTY.withValue(dirty));
	}
	
	public static PropertySet isActive(boolean active) {
		return PropertySet.empty().withMapping(IS_ACTIVE.withValue(active));
	}
	
	public static PropertySet isVisible(boolean visible) {
		return PropertySet.empty().withMapping(IS_VISIBLE.withValue(visible));
	}
	
}
