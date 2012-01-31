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
package com.windowtester.runtime.swt.internal.identifier;

import com.windowtester.runtime.locator.ILocator;

/**
 * Interface for widget identifier strategies contributed via an extension point.
 * <p>
 * This interface should be implemented by clients who need to contribute identification
 * logic to the WindowTester Recorder.
 */
public interface IWidgetIdentifierDelegate {

	ILocator identify(Object widget);
	
}
