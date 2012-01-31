package com.windowtester.test.scenarios;


import junit.framework.Test;
import junit.framework.TestSuite;

import com.windowtester.runtime.gef.locator.FigureLocatorSmokeTest;
import com.windowtester.runtime.internal.scope.ViewScopeSmokeTest;
import com.windowtester.test.SaveAllTest;
import com.windowtester.test.gef.tests.codegen.GEFCodeBlockBuilderTest;
import com.windowtester.test.gef.tests.codegen.GEFCodegenClassPathAdvisorTest;
import com.windowtester.test.gef.tests.codegen.GEFDNDCodegenTest;
import com.windowtester.test.gef.tests.codegen.GEFLocatorJavaStringFactoryTest;
import com.windowtester.test.gef.tests.customer.cat.T1CatShapeDrivingTest;
import com.windowtester.test.gef.tests.customer.cat.T2CatShapeDrivingTest;
import com.windowtester.test.gef.tests.recorder.GEFLocatorSerializationSmokeTest;
import com.windowtester.test.gef.tests.recorder.GEFLocatorStreamedCodegenTest;
import com.windowtester.test.gef.tests.recorder.GEFRecorderSmokeTests;
import com.windowtester.test.gef.tests.runtime.ClickTranslationTest;
import com.windowtester.test.gef.tests.runtime.CommandStackTransactionTest;
import com.windowtester.test.gef.tests.runtime.DragToFigureTargetTest;
import com.windowtester.test.gef.tests.runtime.finder.NamedFigureIdentificationTest;
import com.windowtester.test.gef.tests.runtime.finder.NamedFigureMatcherTest;
import com.windowtester.test.gef.tests.runtime.finder.NamedPartMatcherTest;
import com.windowtester.test.gef.tests.runtime.reflect.IdIntrospectorTest;
import com.windowtester.test.gef.tests.smoke.FlowContextSmokeTest;
import com.windowtester.test.gef.tests.smoke.LogicComboMagnifyTest;
import com.windowtester.test.gef.tests.smoke.LogicContextSmokeTest;
import com.windowtester.test.gef.tests.smoke.ShapeContextSmokeTest;
import com.windowtester.test.gef.tests.smoke.TextContextSmokeTest;
import com.windowtester.test.gef.tests.smoke.locators.AnchorLocatorSmokeTest;
import com.windowtester.test.gef.tests.smoke.locators.ResizeHandleLocatorSmokeTest;
import com.windowtester.test.gef.tests.smoke.scenarios.CanvasContextClickTest;
import com.windowtester.test.gef.tests.smoke.scenarios.FigureContextClickTest;
import com.windowtester.test.gef.tests.smoke.scenarios.FlowDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.LogicDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.LogicDrivingSmokeTest2;
import com.windowtester.test.gef.tests.smoke.scenarios.ShapeCreationStressTest;
import com.windowtester.test.gef.tests.smoke.scenarios.ShapeDrivingSmokeTest1;
import com.windowtester.test.gef.tests.smoke.scenarios.ShapeDrivingSmokeTest2;
import com.windowtester.test.gef.tests.smoke.scenarios.TextDrivingSmokeTest1;
import com.windowtester.test.gef.tests.ui.EventSequenceLabelProviderGEFTest;
import com.windowtester.test.gef.tests.ui.FigureLabelProviderTest;

/**
 * GEF test scenario 1.
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class WTGEFScenario1 {
	public static Test suite() {
		TestSuite suite = new TestSuite("WTGEFScenario1");
		
		suite.addTestSuite(ClickTranslationTest.class);
		
		// all tests in com.windowtester.test.gef.test:
		suite.addTestSuite(FlowContextSmokeTest.class);
		suite.addTestSuite(LogicContextSmokeTest.class);
		suite.addTestSuite(ShapeContextSmokeTest.class);
		suite.addTestSuite(TextContextSmokeTest.class);
		
		suite.addTestSuite(ShapeDrivingSmokeTest1.class);
		suite.addTestSuite(ShapeDrivingSmokeTest2.class);
		
		suite.addTestSuite(LogicDrivingSmokeTest1.class);
		suite.addTestSuite(LogicDrivingSmokeTest2.class);
		
		suite.addTestSuite(FlowDrivingSmokeTest1.class);
		
		suite.addTestSuite(TextDrivingSmokeTest1.class);
		
		// recorder tests
		suite.addTestSuite(GEFRecorderSmokeTests.class);
		suite.addTestSuite(GEFLocatorStreamedCodegenTest.class);
		suite.addTestSuite(GEFLocatorSerializationSmokeTest.class);
		
		//codegen
		suite.addTestSuite(GEFCodegenClassPathAdvisorTest.class);
		suite.addTestSuite(GEFCodeBlockBuilderTest.class);
		suite.addTestSuite(GEFLocatorJavaStringFactoryTest.class);
		suite.addTestSuite(GEFDNDCodegenTest.class);
		
		//context click tests
		suite.addTestSuite(FigureContextClickTest.class);
		suite.addTestSuite(CanvasContextClickTest.class);
		
	
		// all Caterpillar- Shapes GEF tests:
		suite.addTestSuite(T1CatShapeDrivingTest.class);
		suite.addTestSuite(T2CatShapeDrivingTest.class);
		
		// other tests:
		suite.addTestSuite(CommandStackTransactionTest.class);
	
		suite.addTestSuite(ViewScopeSmokeTest.class);
		suite.addTestSuite(FigureLocatorSmokeTest.class);
		suite.addTestSuite(LogicComboMagnifyTest.class);
		suite.addTestSuite(ResizeHandleLocatorSmokeTest.class);
		suite.addTestSuite(AnchorLocatorSmokeTest.class);
		
		suite.addTestSuite(FigureLabelProviderTest.class);
		suite.addTestSuite(EventSequenceLabelProviderGEFTest.class);
		suite.addTestSuite(IdIntrospectorTest.class);
		suite.addTestSuite(NamedFigureMatcherTest.class);
		suite.addTestSuite(NamedPartMatcherTest.class);
		suite.addTestSuite(NamedFigureIdentificationTest.class);
		
		suite.addTestSuite(ShapeCreationStressTest.class);
		
		suite.addTestSuite(DragToFigureTargetTest.class);
		
		// ensure editors saved so scenario does not hang on shutdown
		suite.addTestSuite(SaveAllTest.class);
		
		return suite;
	}
}
