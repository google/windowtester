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
package com.windowtester.runtime.internal.junit4.mirror.runners;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.windowtester.runtime.internal.junit4.mirror.runners.TestIntrospector;


public abstract class BeforeAndAfterRunner {
	private static class FailedBefore extends Exception {
		private static final long serialVersionUID= 1L;
	}

	private final Class<? extends Annotation> fBeforeAnnotation;

	private final Class<? extends Annotation> fAfterAnnotation;

	private TestIntrospector fTestIntrospector;

	protected Object fTest; //!pq: made protected

	public BeforeAndAfterRunner(Class<?> testClass,
			Class<? extends Annotation> beforeAnnotation,
			Class<? extends Annotation> afterAnnotation, 
			Object test) {
		fBeforeAnnotation= beforeAnnotation;
		fAfterAnnotation= afterAnnotation;
		fTestIntrospector= new TestIntrospector(testClass);
		fTest= test;
	}

	public void runProtected() {
		try {
			runBefores();
			runUnprotected();
		} catch (FailedBefore e) {
		} finally {
			runAfters();
		}
	}

	protected abstract void runUnprotected();

	protected abstract void addFailure(Throwable targetException);

	// Stop after first failed @Before
	private void runBefores() throws FailedBefore {
		try {
			List<Method> befores= fTestIntrospector.getTestMethods(fBeforeAnnotation);
			for (Method before : befores)
				invokeMethod(before);
		} catch (InvocationTargetException e) {
			addFailure(e.getTargetException());
			throw new FailedBefore();
		} catch (Throwable e) {
			addFailure(e);
			throw new FailedBefore();
		}
	}

	// Try to run all @Afters regardless
	private void runAfters() {
		List<Method> afters= fTestIntrospector.getTestMethods(fAfterAnnotation);
		for (Method after : afters)
			try {
				invokeMethod(after);
			} catch (InvocationTargetException e) {
				addFailure(e.getTargetException());
			} catch (Throwable e) {
				addFailure(e); // Untested, but seems impossible
			}
	}
	
	//!pq: made protected
	protected void invokeMethod(Method method) throws Exception {
		method.invoke(fTest);
	}
}
