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
package com.windowtester.runtime.junit4.runners;

import junit.extensions.TestDecorator;
import junit.framework.AssertionFailedError;
import junit.framework.JUnit4TestAdapter;
import junit.framework.JUnit4TestCaseFacade;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;


/**
 * <p>The <code>JUnit38ClassRunner</code> provides a means to explicitly define
 * a JUnit3 runner for running JUnit3 tests.  This comes in handy  when you
 * are trying to run a suite of PDE tests that combines JUnit3 with JUnit4 style tests.
 * <p>
 * For example, suppose we have two tests:
 * <pre>
 * &#064;RunWith(TestRunnerSWT.class)
 * public class NewProjectJUnit4Test {
 *    &#064;Test
 *    public void verifyNewProjectCreation() throws Exception {
 *    ...
 *    }
 * }
 * 
 * &#064;(JUnit38ClassRunner.class)
 * public class NewProjectJUnit3Test extends UITestCaseSWT {
 *    public void testNewProjectCreation() throws Exception {
 *    ...
 *    }
 * } 
 * </pre>
 * The annotation on <code>NewProjectJUnit3Test<code> will allow you to run them both in a JUnit4 suite
 * like so:
 * <pre>
 * &#064;RunWith(Suite.class)
 * &#064;SuiteClasses( { NewProjectJUnit3Test.class, NewProjectJUnit4Test.class })
 * public class MixedSuite {}
 * </pre>
 * <p>
 * <b>NOTE:</b> this provision should not be required as the JUnit test suite runner
 * should handle the mixed case smartly.  That said, there are issues with the PDE test
 * runner and until they are resolved, this workaround is required.
 *  
 * 
 *
 * @author Phil Quitslund
 *
 */
public class JUnit38ClassRunner extends Runner implements /*Filterable, */ Sortable {
	private static final class OldTestClassAdaptingListener implements
			TestListener {
		private final RunNotifier fNotifier;

		private OldTestClassAdaptingListener(RunNotifier notifier) {
			fNotifier= notifier;
		}

		public void endTest(Test test) {
			fNotifier.fireTestFinished(asDescription(test));
		}

		public void startTest(Test test) {
			fNotifier.fireTestStarted(asDescription(test));
		}

		// Implement junit.framework.TestListener
		public void addError(Test test, Throwable t) {
			Failure failure= new Failure(asDescription(test), t);
			fNotifier.fireTestFailure(failure);
		}

		private Description asDescription(Test test) {
			if (test instanceof JUnit4TestCaseFacade) {
				JUnit4TestCaseFacade facade= (JUnit4TestCaseFacade) test;
				return facade.getDescription();
			}
			return Description.createTestDescription(test.getClass(), getName(test));
		}

		private String getName(Test test) {
			if (test instanceof TestCase)
				return ((TestCase) test).getName();
			else
				return test.toString();
		}

		public void addFailure(Test test, AssertionFailedError t) {
			addError(test, t);
		}
	}

	private Test fTest;
	
	public JUnit38ClassRunner(Class<?> klass) {
		this(new TestSuite(klass.asSubclass(TestCase.class)));
	}

	public JUnit38ClassRunner(Test test) {
		super();
		fTest= test;
	}

	@Override
	public void run(RunNotifier notifier) {
		TestResult result= new TestResult();
		result.addListener(createAdaptingListener(notifier));
		fTest.run(result);
	}

	public static TestListener createAdaptingListener(final RunNotifier notifier) {
		return new OldTestClassAdaptingListener(notifier);
	}
	
	@Override
	public Description getDescription() {
		return makeDescription(fTest);
	}

	private Description makeDescription(Test test) {
		if (test instanceof TestCase) {
			TestCase tc= (TestCase) test;
			return Description.createTestDescription(tc.getClass(), tc.getName());
		} else if (test instanceof TestSuite) {
			TestSuite ts= (TestSuite) test;
			String name= ts.getName() == null ? "" : ts.getName();
			Description description= Description.createSuiteDescription(name);
			int n= ts.testCount();
			for (int i= 0; i < n; i++)
				description.addChild(makeDescription(ts.testAt(i)));
			return description;
		} else if (test instanceof JUnit4TestAdapter) {
			JUnit4TestAdapter adapter= (JUnit4TestAdapter) test;
			return adapter.getDescription();
		} else if (test instanceof TestDecorator) {
			TestDecorator decorator= (TestDecorator) test;
			return makeDescription(decorator.getTest());
		} else {
			// This is the best we can do in this case
			return Description.createSuiteDescription(test.getClass());
		}
	}


	//TODO (pq): verify these impls.
	
//	public void filter(Filter filter) throws NoTestsRemainException {
//		filter.apply(this);
//	}

	public void sort(Sorter sorter) {
		sorter.apply(this);
	}
	
}
