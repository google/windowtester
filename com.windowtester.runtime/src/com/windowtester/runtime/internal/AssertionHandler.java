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
package com.windowtester.runtime.internal;

import com.windowtester.internal.runtime.condition.ConditionMonitor;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;

/**
 * A handler for invariants.
 */
public class AssertionHandler implements IAssertionHandler {
	
	protected static final int ASSERTION_WAIT_TIMEOUT = 3000;

	private final IUIContext ui;
	
	public AssertionHandler(IUIContext ui) {
		this.ui = ui;
	}
	
	protected IUIContext getUI() {
		return ui;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.spike.invariants.IInvariantHandler#assertTrue(com.windowtester.runtime.condition.ICondition)
	 */
	public void assertThat(ICondition condition) {
		waitFor(condition);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.spike.invariants.IInvariantHandler#assertThat(java.lang.String, com.windowtester.runtime.condition.ICondition)
	 */
	public void assertThat(String message, ICondition condition) throws WaitTimedOutException {
		try {
			waitFor(condition);
		} catch (WaitTimedOutException e) {
			throw new WaitTimedOutException(message);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.spike.invariants.IInvariantHandler#ensureThat(com.windowtester.runtime.condition.IConditionHandler)
	 */
	public void ensureThat(IConditionHandler conditionHandler) throws Exception {
		if (isTrue(conditionHandler))
			return;
//		if (becomesTrue(conditionHandler))
//			return;
		conditionHandler.handle(getUI());
		assertThat(conditionHandler);
	}


	private boolean isTrue(ICondition condition) {
		return ConditionMonitor.test(getUI(), condition);
	}

	private void waitFor(ICondition condition) throws WaitTimedOutException {
		getUI().wait(condition, ASSERTION_WAIT_TIMEOUT);
	}
	
	
	
//	//test with wait
//	private boolean becomesTrue(IConditionHandler condition) {
//		boolean isTrue = false;
//		try {
//			waitFor(condition);
//			isTrue = true;
//		} catch (WaitTimedOutException e) {
//			//exception means isTrue does not get set
//			System.out.println("AssertionHandler.isTrue()");
//		}
//		return isTrue;
//	}




}
