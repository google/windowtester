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
package com.windowtester.eclipse.ui.viewers;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.Modifier;
import com.windowtester.codegen.assembly.unit.Parameter;
import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.codegen.swt.SWTV2TestCaseBuilder;
import com.windowtester.ui.core.model.ISemanticEvent;

public class EventSequenceSnippetBuilder {

	private static final String SNIPPET_TEST_NAME = "Snippet";
	

	static class CodeBlockBuilder {
		

		SWTV2TestCaseBuilder builder = new SWTV2TestCaseBuilder(SNIPPET_TEST_NAME, "mock", "Mock", null);
		PluggableCodeGenerator generator = new PluggableCodeGenerator(builder, new NewAPICodeBlockBuilder(builder));
		
		String generate(IEventStream events) {
			
			generator.generate(events);
			MethodUnit mainMethod = getMainMethod();
			if (mainMethod == null)
				return "";
			mainMethod.setComment(null); //remove generic comment
			return mainMethod.getBody().toString();
		}

		public String generateMainMethod(ISemanticEvent[] selection) {
			return generate(new EventStream(Arrays.asList(selection)));
		}
		
		ImportUnit[] getImports() {
			Collection imports = generator.getTestBuilder().getImports();
			return (ImportUnit[]) imports.toArray(new ImportUnit[]{});
		}
		
		MethodUnit getMainMethod() {
			Collection methods = generator.getTestBuilder().getMethods();
			for (Iterator iterator = methods.iterator(); iterator.hasNext();) {
				MethodUnit method = (MethodUnit) iterator.next();
				if (!method.isConstructor() && containsTestName(method)) {
					return method;
				}
			}
			return null;
		}

		private boolean containsTestName(MethodUnit method) {
			return contains(method.getName(), SNIPPET_TEST_NAME);
		}

		private boolean contains(String str, String seq) {
	        return str.indexOf(seq) > -1;
		}
		
		
	}
	
	
	private CodeBlockBuilder builder;
	private final ISemanticEvent[] events;
 
	public EventSequenceSnippetBuilder(ISemanticEvent[] events) {
		this.events = events;
		this.builder = new CodeBlockBuilder();
	}
	
	public String getMainMethodString() {
		return builder.generateMainMethod(events);
	}
	
	
	
	public String getMethodSnippet() {
		MethodUnit method = builder.getMainMethod();
		if (method == null)
			return "";
		CodeBlock body = method.getMethodBodyContents();
		if (body == null)
			return "";
		nullGetter(body);
		return body.toString();
	}

	private void nullGetter(CodeBlock body) {
		body.setBody(null); //this just nulls the getter
	}

	public ImportUnit[] getImports() {
		return builder.getImports();
	}

	public String getParameterizedMethod() {
		MethodUnit method = builder.getMainMethod();
		if (method == null)
			return "";
		CodeBlock body = method.getMethodBodyContents();
		if (body == null)
			return "";
		nullGetter(body);
		method.setName("helperMethod");
		method.addModifier(Modifier.STATIC);
		method.addParameter(new Parameter("IUIContext", "ui"));
		return method.getBody().toString();
	}



	
	
}
