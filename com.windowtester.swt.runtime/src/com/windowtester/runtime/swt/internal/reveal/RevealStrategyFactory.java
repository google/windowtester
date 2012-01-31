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
package com.windowtester.runtime.swt.internal.reveal;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * The <code>RevealFactory</code> generates appropriate revealers
 * (implementers of {@link com.windowtester.runtime.swt.internal.reveal.IRevealStrategy})
 * based on widget type.  
 */
public class RevealStrategyFactory {

	/**
	 * Retrieve the appropriate reveal strategy for the given widget type.
	 * @param target the widget to reveal
	 * @return a revealer
	 */
	public static IRevealStrategy getRevealer(Widget target) {
		if (target == null)
			return defaultRevealer();
		return getRevealer(target.getClass());
	}

	public static IRevealStrategy getRevealer(Class cls) {
		if (cls.equals(TreeItem.class) || cls.equals(Tree.class))
			return new TreeRevealer();
		if (cls.equals(TableItem.class))
			return new TableItemRevealer();
		return defaultRevealer();
	}

	private static UnhandledTypeRevealer defaultRevealer() {
		return new UnhandledTypeRevealer();
	}

	
	
	
}
