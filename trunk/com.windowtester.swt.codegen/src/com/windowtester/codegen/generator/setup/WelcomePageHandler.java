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
package com.windowtester.codegen.generator.setup;

import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.IUISemanticEvent;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.EclipseLocators;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * A handler for the Welcome Page.
 */
public class WelcomePageHandler extends SetupHandler {


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getBody()
	 */
	public String getBody() {
		return "ui.ensureThat(ViewLocator.forName(\"Welcome\").isClosed());";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getImport()
	 */
	public String getImport() {
		return ViewLocator.class.getName();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getBody()
	 */
	public String getStaticBody() {
		return "ui.ensureThat(view(\"Welcome\").isClosed());";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getImport()
	 */
	public String getStaticImport() {
		return EclipseLocators.class.getName() + ".view";
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#appliesTo(com.windowtester.codegen.eventstream.EventStream)
	 */
	public boolean appliesTo(IEventStream stream) {
//functionality tentatively removed -- the current thought is to always handle the welcome close
//and in the case where the welcome is interacted with, prune that interaction
		
//		stream = stream.copy();
//	    while (stream.hasNext()) {
//	    	ISemanticEvent event = stream.nextEvent();
//	    	if (isWelcomePageInteraction(event))
//	    		return false;
//	    }
		return true;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#fullyHandles(com.windowtester.recorder.event.ISemanticEvent)
	 */
	public boolean fullyHandles(ISemanticEvent event) {
		return  isWelcomePageInteraction(event);
	}

	protected boolean isWelcomePageInteraction(ISemanticEvent event) {
		if (!(event instanceof IUISemanticEvent))
			return false;
		IUISemanticEvent semantic = (IUISemanticEvent)event;
		IWidgetIdentifier widget = semantic.getHierarchyInfo();
		if (widget instanceof CTabItemLocator) {
			CTabItemLocator ctab = (CTabItemLocator)widget;
			String text = ctab.getNameOrLabel();
			if (text != null && text.equals("Welcome"))
				return true;
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.generator.setup.ISetupHandler#getDescription()
	 */
	public String getDescription() {
		return "\"Welcome\" page is closed";
	}
	
	
	
}
