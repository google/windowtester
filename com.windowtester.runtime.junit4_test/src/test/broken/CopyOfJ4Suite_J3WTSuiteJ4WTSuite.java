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
package test.broken;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.JUnit3WTSuite;
import test.JUnit4J3WTSuite;


/**
 * A suite that mixes JUnit3 and JUnit4 suites.
 * 
 * @author Phil Quitslund
 * 
 */
@RunWith(Suite.class)
@SuiteClasses( {
	/*
	 * One or the other works but not both!
	 */
	CopyOfJ4Suite_J3WTSuiteJ4WTSuite.JUnit4WrapperSuite.class,
	JUnit3WTSuite.class

})
public class CopyOfJ4Suite_J3WTSuiteJ4WTSuite {

	public static class JUnit4WrapperSuite {
		  public static Test suite() {
			    return new JUnit4TestAdapter(JUnit4J3WTSuite.class);
		}
	}
	
	

}
