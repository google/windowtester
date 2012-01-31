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
package com.windowtester.runtime.internal.junit4;

import org.junit.runner.Description;

import com.windowtester.internal.runtime.junit.core.ITestIdentifier;


/**
 *
 * @author Phil Quitslund
 *
 */
public class JUnit4TestId implements ITestIdentifier {

	private final Description _description;

	public JUnit4TestId(Description description) {
		_description = description;
	}

	public Description getDescription() {
		return _description;
	}
	
	public String getName() {
		return junit3AdaptedName();
		//return getDescription().getDisplayName();
	}

	private String junit3AdaptedName() {
		String displayName = getDescription().getDisplayName();
		String[] parts = displayName.split("\\(");
		String testMethodName = parts[0];
		String testClassName  = parts[1];
		testClassName = trimTrailingParen(testClassName);
		return testClassName + "_" + testMethodName;
	}

	private String trimTrailingParen(String testClassName) {
		return testClassName.substring(0, testClassName.length()-1);
	}

}
