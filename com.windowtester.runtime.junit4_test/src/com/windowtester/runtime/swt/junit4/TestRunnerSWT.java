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
package com.windowtester.runtime.swt.junit4;



import org.eclipse.swt.widgets.Display;

import com.windowtester.internal.runtime.junit.core.IExecutionContext;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.internal.junit4.mirror.runners.InitializationError;
import com.windowtester.runtime.junit4.UITestRunner;
import com.windowtester.runtime.swt.internal.junit.SWTExecutionContext;
import com.windowtester.swt.UIContextFactory;

/**
 * The custom runner <code>TestRunnerSWT</code> provides runner support for SWT tests.
 * tests. To use it, annotate a test class
 * with <code>@RunWith(TestRunnerSWT.class)</code> and optionally provide launch
 * details using the &#064;{@link UITestRunner.Launch} annotation. Some simple examples follow.
 * <p>
 * To drive an RCP or Eclipse test, simply specify the runner like so:
 * <pre>
 * &#064;RunWith(TestRunnerSWT.class)
 * public class RCPTest {
 *    &#064;Test
 *    public void verifySomething() throws Exception {
 *       ...
 *    }
 * }
 * </pre>
 * 
 * To drive an SWT test for an SWT application whose main entry-point is
 * defined in the <code>SimpleShell</code> class, you would do something like this:
 * <pre>
 * &#064;RunWith(TestRunnerSWT.class)
 * &#064;Launch(SimpleShell.class)
 * public class SimpleShellTest {
 *    &#064;Test
 *    public void verifySomething() throws Exception {
 *       ...
 *    }
 * }
 * </pre>
 * 
 *
 * @author Phil Quitslund
 * 
 * @see UITestRunner
 * @see UITestRunner.Launch
 * 
 *
 */
public class TestRunnerSWT extends UITestRunner {

	private static final IExecutionContext GLOBAL_CONTEXT = new SWTExecutionContext(){
		
		private IUIContext _ui;
		
		@Override
		public IUIContext getUI() {
			if (_ui == null) {
				_ui = fetchUI();
			}
			return _ui;
		}
		private IUIContext fetchUI() {
			return (IUIContext) UIContextFactory.createContext(Display.getDefault());
		}
		
	};
	
	public TestRunnerSWT(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected IExecutionContext getGlobalContext() {
		return GLOBAL_CONTEXT;
	}

	/* (non-Javadoc)
	 * @see org.junit.extensions.UITestClassRunner#createExecContext()
	 */
	@Override
	public IExecutionContext createExecContext() {	
		System.out.println("runner creating new exec context context");
		return new SWTExecutionContext();
	}
	
	
}
