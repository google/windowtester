package com.windowtester.test.codegen;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import com.windowtester.codegen.ExecutionProfile;
import com.windowtester.codegen.SourceStringBuilder;
import com.windowtester.codegen.TestCaseGenerator;
import com.windowtester.codegen.assembly.block.CodeBlock;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.generator.CodegenSettings;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.codegen.generator.ICodegenAdvisor.Advice;
import com.windowtester.codegen.generator.setup.SetupHandlerSet;
import com.windowtester.codegen.swt.SWTV2TestCaseBuilder;
import com.windowtester.internal.runtime.util.StringUtils;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent;

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
public abstract class BaseSWTAPICodeBlockCodegenTest extends TestCase {

	public static EventStream stream(UISemanticEvent ... events) {
		return new EventStream(Arrays.asList(events));
	}

	private NewAPICodeBlockBuilder builder;
	private SWTV2TestCaseBuilder testBuilder;
	private final TestCaseGenerator generator = new PluggableCodeGenerator(getTestCaseBuilder(), getBuilder(), getCodegenSettings()) {
			/* (non-Javadoc)
			 * @see com.windowtester.codegen.generator.PluggableCodeGenerator#getAdvice(com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent)
			 */
			@Override
			protected Advice getAdvice(SemanticWidgetSelectionEvent event) {
				return new Advice() {
					/* (non-Javadoc)
					 * @see com.windowtester.codegen.generator.ICodegenAdvisor.Advice#isOverriden()
					 */
					@Override
					public boolean isOverriden() {
						return false;
					}
				};
			}
		};
	public static final String NEW_LINE = StringUtils.NEW_LINE;

	protected NewAPICodeBlockBuilder getBuilder() {
		if (builder == null) {
			builder = new NewAPICodeBlockBuilder(getTestCaseBuilder());
		}
		return builder;
	}

	protected SWTV2TestCaseBuilder getTestCaseBuilder() {
		if (testBuilder == null)
			testBuilder = createTestBuilder();
		return testBuilder;
	}

	protected SWTV2TestCaseBuilder createTestBuilder() {
		return new SWTV2TestCaseBuilder("MockTest", "mock", null, null, getExecType()).withSettings(getCodegenSettings());
	}

	protected int getExecType() {
		return ExecutionProfile.RCP_EXEC_TYPE;
	}

	protected void assertImportsContain(Class<?> cls) {
		assertTrue(getTestCaseBuilder().getImports().contains(new ImportUnit(cls.getName())));
	}

	protected void assertImportsDoNotContain(Class<?> cls) {
		assertFalse(getTestCaseBuilder().getImports().contains(new ImportUnit(cls.getName())));
	}

	protected void assertEquals(String expected, CodeBlock block) {
		assertEquals(expected, block.toString().trim());
	}

	public MethodUnit getSetupMethod() {
		return getMethod("setUp");
	}
	
	public MethodUnit getTestMethod() {
		return getMethod("testMock");
	}
	
	@SuppressWarnings("unchecked")
	public MethodUnit getMethod(String name) {
		Collection<MethodUnit> methods = getTestCaseBuilder().getMethods();
		for (MethodUnit m : methods) {
			System.out.println(m.getName());
			if (name.equals(m.getName()))
				return m;
		}
		return null;
	}
	

	CodegenSettings getCodegenSettings() {
		return CodegenSettings.forStatics(usingStatics()).withHandlers(getHandlers());
	}


	protected boolean usingStatics() {
		return true;
	}

	protected SetupHandlerSet getHandlers() {
		return new SetupHandlerSet();
	}

	protected String generate(UISemanticEvent ... events) {
		return generator.generate(stream(events));
	}

	@SuppressWarnings("unchecked")
	protected void assertImportsContainAsString(String importString) {
		Collection<ImportUnit> imports = getTestCaseBuilder().getImports();
		for (ImportUnit imprt : imports) {
			SourceStringBuilder strBuilder = new SourceStringBuilder(getTestCaseBuilder());
			StringBuffer sb = new StringBuffer();
			strBuilder.addImport(sb, imprt);
			if (importString.equals(sb.toString()))
					return;
		}
		fail("Import String: <" + importString + "> not found in: " + Arrays.toString(imports.toArray()));
	}

	@SuppressWarnings("unchecked")
	protected void assertImportsDoNotContainAsString(String importString) {
		Collection<ImportUnit> imports = getTestCaseBuilder().getImports();
		for (ImportUnit imprt : imports) {
			SourceStringBuilder strBuilder = new SourceStringBuilder(getTestCaseBuilder());
			StringBuffer sb = new StringBuffer();
			strBuilder.addImport(sb, imprt);
			if (importString.equals(sb.toString()))
					fail("Import String <" + importString +"> should not be found but was");
		}

	}	
	
	
	
}
