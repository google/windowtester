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
package com.windowtester.swt.condition;

/**
 * An interface used by {@link com.windowtester.swt.condition.ConditionMonitor} to test
 * whether a condition has been satisfied.
 * <p>
 * Note that conditions should be designed to <em>test</em> and not to <em>modify</em>
 * the User Interface.
 * 
 * @author Dan Rubel
 * @author Phil Quitslund
 * @deprecated Use {@link com.windowtester.runtime.condition.ICondition} instead
 */
public interface ICondition extends com.windowtester.runtime.condition.ICondition
{

}
