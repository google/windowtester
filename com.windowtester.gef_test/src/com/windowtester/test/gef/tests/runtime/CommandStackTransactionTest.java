package com.windowtester.test.gef.tests.runtime;

import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.gef.internal.commandstack.CommandStackTransaction;
import com.windowtester.runtime.gef.internal.commandstack.UIRunnable;
import com.windowtester.runtime.swt.UITestCaseSWT;

/**
 * 
 * https://fogbugz.instantiations.com/default.php?40923
 * 
 * <p/>
 * Copyright (c) 2009, Instantiations, Inc.<br/>
 * All Rights Reserved
 * <p/>
 * @author Phil Quitslund
 *
 * @since 3.8.1
 */
@SuppressWarnings("restriction")
public class CommandStackTransactionTest extends UITestCaseSWT {


	private CommandStackTransaction transaction = CommandStackTransaction.UNCHECKED_TRANSACTION;

	public void testNullTransactionStart() throws Exception {
		transaction.start();
	}
	
	public void testNullTransactionStop() throws Exception {
		transaction.start();
	}

	public void testNullTransactionIsComplete() throws Exception {
		transaction.isComplete();
	}
	
	public void testNullTransactionIsStarted() throws Exception {
		transaction.isStarted();
	}
	
	public void testNullRunInUI() throws Exception {
		transaction.runInUI(new UIRunnable(){
			public Object runWithResult() throws WidgetSearchException {
				return null;
			}
		}, getUI());
	}
	
	
	
}
