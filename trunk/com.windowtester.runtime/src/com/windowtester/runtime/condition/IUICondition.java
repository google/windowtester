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
package com.windowtester.runtime.condition;

import com.windowtester.runtime.condition.IConditionMonitor;
import com.windowtester.runtime.IUIContext;

/**
 * An interface used by {@link IConditionMonitor} to test whether a condition has been
 * satisfied. If {@link IConditionMonitor} detects that an {@link ICondition} implements
 * {@link IUICondition}, then {@link IConditionMonitor} calls {@link #testUI(IUIContext)}
 * rather than {@link ICondition#test()}.
 * <p>
 * Note that conditions should be designed to <em>test</em> and not to <em>modify</em>
 * the User Interface.
 * <p>
 */
public interface IUICondition
	extends ICondition
{
	boolean testUI(IUIContext ui);
}
