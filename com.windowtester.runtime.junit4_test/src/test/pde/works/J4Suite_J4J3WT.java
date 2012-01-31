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
package test.pde.works;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.NewProjectJUnit3Test;
import test.NonUIJUnit4Test;

/**
 * 
 * @author Phil Quitslund
 *
 */
@RunWith(Suite.class)
@SuiteClasses( { 
	NewProjectJUnit3Test.class, 
	NonUIJUnit4Test.class
})
public class J4Suite_J4J3WT {

	
}
