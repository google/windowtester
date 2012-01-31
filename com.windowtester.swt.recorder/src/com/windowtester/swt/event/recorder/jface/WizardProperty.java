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
package com.windowtester.swt.event.recorder.jface;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;

public class WizardProperty {

	public static final PropertyMapping HAS_MESSAGE       = PropertyMapping.withKey("hasMessage").withName("Has Message");
	public static final PropertyMapping HAS_DESCRIPTION   = PropertyMapping.withKey("hasDescription").withName("Has Description");
	public static final PropertyMapping HAS_ERROR_MESSAGE = PropertyMapping.withKey("hasErrorMessage").withName("Has Error Message");
	public static final PropertyMapping HAS_TITLE         = PropertyMapping.withKey("hasTitle").withName("Has Title");

	
	public static PropertySet hasMessage(String message) {
		return PropertySet.empty().withMapping(HAS_MESSAGE.withValue(message));
	}
	
	public static PropertySet hasErrorMessage(String message) {
		return PropertySet.empty().withMapping(HAS_ERROR_MESSAGE.withValue(message));
	}
	
	public static PropertySet hasDescription(String description) {
		return PropertySet.empty().withMapping(HAS_DESCRIPTION.withValue(description));
	}
	
	public static PropertySet hasTitle(String title) {
		return PropertySet.empty().withMapping(HAS_TITLE.withValue(title));
	}
	
}
