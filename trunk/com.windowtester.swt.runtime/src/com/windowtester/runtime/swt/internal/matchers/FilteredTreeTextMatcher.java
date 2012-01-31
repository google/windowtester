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
package com.windowtester.runtime.swt.internal.matchers;

import java.util.concurrent.Callable;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;

import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;

/**
 * A matcher that matches the Text field in a {@link FilteredTree}.
 */
public class FilteredTreeTextMatcher implements ISWTWidgetMatcher {

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.widgets.ISWTWidgetMatcher#matches(com.windowtester.runtime.swt.widgets.ISWTWidgetReference)
	 */
	public boolean matches(ISWTWidgetReference<?> ref) {
		Object widget = ref.getWidget();
		//notice not exact class match here (should it be?)
		if (!(widget instanceof Text))
			return false;
		return containedInFilteredTree((Text)widget);
	}

	
	private boolean containedInFilteredTree(final Text text) {
		return DisplayReference.getDefault().execute(new Callable<Boolean>(){
			public Boolean call() throws Exception {
				Composite parent = text.getParent();
				while (parent != null) {
					if (parent instanceof FilteredTree)
						return Boolean.TRUE;
					parent = parent.getParent();
				}
				return Boolean.FALSE;
			}
		});
//		/* $codepro.preprocessor.if version >= 3.2.0 $ */ 
//		//batch this in one UI thread access
//		return ((Boolean)UIProxy.syncExec(text.getDisplay(), new RunnableWithResult() {
//			public Object runWithResult() {
//				Composite parent = text.getParent();
//				while (parent != null) {
//					if (parent instanceof FilteredTree)
//						return Boolean.TRUE;
//					parent = parent.getParent();
//				}
//				return Boolean.FALSE;
//			}
//		})).booleanValue();		
//		/* $codepro.preprocessor.endif$ */
//		
//		/* $codepro.preprocessor.if version < 3.2.0 $
//		return false;
//		$codepro.preprocessor.endif$ */
	}
	
}
