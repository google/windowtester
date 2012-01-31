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
package com.windowtester.runtime.gef.internal.commandstack;

import org.eclipse.core.runtime.Assert;

import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.swt.internal.condition.ModalDialogShowingCondition;

/**
 * A condition that tests for the completion of all commands
 * pushed onto the command stack for a given transaction.
 */
public class TransactionCompleteCondition implements ICondition {

	private final CommandStackTransaction transaction;
	private final ModalDialogShowingCondition blockingShellCondition = new ModalDialogShowingCondition();
	
	public static ICondition forTransaction(CommandStackTransaction transaction) {
		return new TransactionCompleteCondition(transaction);
	}

	public TransactionCompleteCondition(CommandStackTransaction transaction) {
		this.transaction = transaction;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.condition.ICondition#test()
	 */
	public boolean test() {
		//bail if a modal dialog is up
		
		if (blockingShellCondition.test())
			return true;
		//System.out.println("testing transaction complete: " + transaction.isComplete());
		Assert.isTrue(transaction.isStarted(), "transaction must be started before being tested");
		return transaction.isComplete();
	}
	
}
