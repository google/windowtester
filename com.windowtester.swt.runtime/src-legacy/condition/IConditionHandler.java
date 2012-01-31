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
 * to test and handle a condition.
 * <p>
 * @see com.windowtester.swt.condition.ICondition
 * @see com.windowtester.swt.condition.IHandler
 * 
 * @author Phil Quitslund
 * @author Dan Rubel
 * @deprecated Use {@link com.windowtester.runtime.condition.IConditionHandler} instead
 */
public interface IConditionHandler extends ICondition, IHandler {

	
	
}
