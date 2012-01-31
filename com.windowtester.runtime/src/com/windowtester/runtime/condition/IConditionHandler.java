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

/**
 * An interface used by condition monitors to test and handle a condition.
 * 
 * @see com.windowtester.runtime.condition.ICondition
 * @see com.windowtester.runtime.condition.IHandler
 * @see com.windowtester.runtime.condition.IConditionMonitor
 * 
 */
public interface IConditionHandler extends ICondition, IHandler {

}
