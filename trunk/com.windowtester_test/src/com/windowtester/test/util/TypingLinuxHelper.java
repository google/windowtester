package com.windowtester.test.util;

import com.windowtester.runtime.internal.OS;
import com.windowtester.runtime.swt.internal.text.ITextEntryStrategy;
import com.windowtester.runtime.swt.internal.text.InsertTextEntryStrategy;
import com.windowtester.runtime.swt.internal.text.TextEntryStrategy;

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
public final class TypingLinuxHelper {

	private static ITextEntryStrategy current;
	private static InsertTextEntryStrategy insertStrategy;

	private TypingLinuxHelper() {

	}

	public static void switchToInsertStrategyIfNeeded() {
//		if (OS.isLinux()) {
//			current = TextEntryStrategy.getCurrent();
//			if (insertStrategy == null) {
//				insertStrategy = new InsertTextEntryStrategy();
//			}
//			TextEntryStrategy.set(insertStrategy);
//		} else {
//			current = null;
//		}
	}

	public static void restoreOriginalStrategy() {
//		if (OS.isLinux() && current != null) {
//			TextEntryStrategy.set(current);
//			current = null;
//		}
	}
}
