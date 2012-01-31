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
package com.windowtester.codegen.generator;

import java.util.ArrayList;
import java.util.List;

import com.windowtester.codegen.ITestCaseBuilder;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.codegen.generator.setup.ISetupHandler;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;


public class SetupBlockBuilder implements ICodeGenConstants {
	
	private final ITestCaseBuilder testBuilder;
	private final CodegenSettings codegenSettings;
	
	
	public SetupBlockBuilder(ITestCaseBuilder testGenerator, CodegenSettings codegenSettings) {
		this.testBuilder = testGenerator;
		this.codegenSettings = codegenSettings;	
	}
	
	public ISetupHandler[] getHandlersFor(IEventStream stream) {
		stream = stream.copy();
		List handlers = new ArrayList();
		ISetupHandler[] allHandlers = testBuilder.getSetupHandlers();
		for (int i=0; i < allHandlers.length; ++i) {
			ISetupHandler handler = allHandlers[i];
			if (handler.appliesTo(stream)) {
				handlers.add(handler);
			}
		}
		return (ISetupHandler[]) handlers.toArray(new ISetupHandler[]{});		
	}
	

	
	
	public MethodUnit buildSetup(IEventStream stream) {
		
		MethodUnit m = createSetupMethod();
    	String body = buildSetupBody(stream);   
    	if (body == null)
    		return null;
        m.setBody(new CodeBlock(body));
		return m;
		
	}

	private String buildSetupBody(IEventStream stream) {
		
		ISetupHandler[] handlers = getHandlersFor(stream);
    	if (handlers.length == 0)
    		return null;
    	
		StringBuffer sb = new StringBuffer();
    	appendPreamble(sb);
        appendHandlers(handlers, sb);
		return sb.toString();
	}

	private void appendHandlers(ISetupHandler[] handlers, StringBuffer sb) {
		for (int i = 0; i < handlers.length; i++) {
        	ISetupHandler handler = handlers[i];
        	ImportUnit imprt = null;
        	String body      = null;
        	if (usingStatics()) {
        		body = handler.getStaticBody();
        		imprt = new ImportUnit(handler.getStaticImport());
        		imprt.addModifier(Modifier.STATIC);
        	} else {
        		body = handler.getBody();
        		imprt = new ImportUnit(handler.getImport()); 				
        	}
    		sb.append(body).append(NEW_LINE);
        	testBuilder.addImport(imprt);
		}
	}

	

	private void appendPreamble(StringBuffer sb) {
		sb.append("super.setUp();").append(NEW_LINE).
       	   append("IUIContext ui = getUI();").append(NEW_LINE);
	}
	
	private boolean usingStatics() {
		return codegenSettings.usingStatics();
	}

	private MethodUnit createSetupMethod() {
		MethodUnit m = new MethodUnit("setUp");
		String comment = "/* @see junit.framework.TestCase#setUp()" + NEW_LINE + "*/";
		m.setComment(comment);
		m.addModifier(Modifier.PROTECTED);
		m.addThrows("Exception");
		return m;
	}

	/**
	 * Returns true if this event was handled in setup (and can safely be ignored in
	 * the test main).
	 */
	public boolean handled(ISemanticEvent event) {
		ISetupHandler[] allHandlers = testBuilder.getSetupHandlers();
		for (int i=0; i < allHandlers.length; ++i) {
			ISetupHandler handler = allHandlers[i];
			if (handler.fullyHandles(event)) {
				return true;
			}
		}
		return false;
	}
	


}
