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
package com.windowtester.runtime.swt.internal.locator.forms;

import org.eclipse.swt.widgets.Display;

import com.windowtester.runtime.IClickDescription;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.widgets.AbstractSWTDisplayable;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

/**
 * Base class for hyperlink references.
 *
 */
public abstract class AbstractHyperlinkReference extends AbstractSWTDisplayable implements IHyperlinkReference {

		
	public AbstractHyperlinkReference(Display display) {
		super(display);
	}

	/* (non-Javadoc)
	 * @see com.windowtester.internal.runtime.locator.IUISelector#click(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
	 */
	public IWidgetLocator click(IUIContext ui, IWidgetReference widget,
			IClickDescription click) throws WidgetSearchException {
		IWidgetReference ref = widget;
		if (widget == null)
			ref = (IWidgetReference)ui.find(this); //only do this lookup if necessary
	
		IHyperlinkReference link = (IHyperlinkReference)ref;
		
		return doClick(ui, click, link);
		
	}


	protected abstract IWidgetLocator doClick(IUIContext ui, IClickDescription click,
			IHyperlinkReference link) throws WidgetSearchException;

}
