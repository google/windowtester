package com.windowtester.test.gef.tests.recorder;

import java.util.List;

import com.windowtester.codegen.ITestCaseBuilder;
import com.windowtester.codegen.generator.ICodegenAdvisor;
import com.windowtester.codegen.generator.LocatorJavaStringFactory;
import com.windowtester.codegen.generator.NewAPICodeBlockBuilder;
import com.windowtester.codegen.generator.PluggableCodeGenerator;
import com.windowtester.codegen.swt.SWTV2TestCaseBuilder;
import com.windowtester.codegen.util.IBuildPathUpdater;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.swt.gef.codegen.GEFCodegenerator;

class GEFTestCodeGenerator implements ICodegenAdvisor {
	
	final GEFCodegenerator gefGenerator = new GEFCodegenerator();
	final ITestCaseBuilder baseBuilder  = new SWTV2TestCaseBuilder("FakeTest", "test", null, null);
	final PluggableCodeGenerator baseGenerator = new PluggableCodeGenerator(baseBuilder, new NewAPICodeBlockBuilder(baseBuilder));
	
	String toJava(ILocator locator) {
		fakeAndHandleSelection(locator);
		return LocatorJavaStringFactory.toJavaString(locator);
	}

	//we do this because by the time codegen is initiated, the codegenerator
	//will already have had a chance to update state when it is notified of the
	//selection
	private void fakeAndHandleSelection(ILocator locator) {
		handleSelection(fakeSelection(locator), baseGenerator, new Advice());
	}
	
	private SemanticWidgetSelectionEvent fakeSelection(ILocator locator) {
		EventInfo info = new EventInfo();
		info.hierarchyInfo = new IdentifierAdapter(locator);
		return new SemanticWidgetSelectionEvent(info);
	}

	public void handleSelection(ISemanticEvent event, PluggableCodeGenerator generator, Advice advice) {
		gefGenerator.handleSelection(event, generator, advice);
	}

	public String toJavaString(ILocator locator) {
		return gefGenerator.toJavaString(locator);
	}
	
	@SuppressWarnings("unchecked")
	public void addPluginDependencies(List events, IBuildPathUpdater updater) {
		//ignored
	}
	
	public PluggableCodeGenerator getCodeGenerator() {
		return baseGenerator;
	}
	
}