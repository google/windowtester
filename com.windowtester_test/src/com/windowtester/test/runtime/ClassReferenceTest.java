package com.windowtester.test.runtime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.runtime.WidgetLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

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
public class ClassReferenceTest extends TestCase {

	
	public void testRefersToByString() {
		ClassReference ref = new ClassReference("java.lang.String");
		assertTrue(ref.refersTo(java.lang.String.class));
	}

	public void testEquals() {
		assertEquals(new ClassReference(String.class), new ClassReference(String.class));
		assertEquals(new ClassReference(String.class), new ClassReference("java.lang.String"));
	}
	
	public void testRefersToByClass() {
		ClassReference ref = new ClassReference(java.lang.String.class);
		assertTrue(ref.refersTo(java.lang.String.class));
	}

	
	public void testSerialization() throws IOException, ClassNotFoundException{
		ClassReference classRef = new ClassReference("java.lang.String");
		
		ClassReference copy = (ClassReference)streamOutAndBackIn(classRef);
		assertEquals(classRef.getName(), copy.getName());

		classRef = new ClassReference(Button.class);
		copy = (ClassReference)streamOutAndBackIn(classRef);
		assertEquals(classRef.getName(), copy.getName());
	}

	public void testWidgetLocatorSerialization() throws IOException, ClassNotFoundException{
		WidgetLocator locator = new WidgetLocator(Tree.class,"root/child/grandchild",1,new SWTWidgetLocator(Shell.class));
		WidgetLocator copy = (WidgetLocator)streamOutAndBackIn(locator);
		
		assertEquals(locator.getTargetClass(),copy.getTargetClass());
		assertEquals(locator.getNameOrLabel(),copy.getNameOrLabel());
		assertEquals(locator.getIndex(),copy.getIndex());
		
		WidgetLocator parent = locator.getParentInfo();
		WidgetLocator copyP = copy.getParentInfo();
		assertEquals(parent.getTargetClass(),copyP.getTargetClass());
		assertEquals(parent.getNameOrLabel(),copyP.getNameOrLabel());
		assertEquals(parent.getIndex(),copyP.getIndex());
		
		
	}
	/**
	 * @param event
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private Object streamOutAndBackIn(Object classRef) throws IOException, ClassNotFoundException {
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream ();
	    ObjectOutputStream out     = new ObjectOutputStream (bout);

	    out.writeObject(classRef);
	    out.flush();

	    ByteArrayInputStream bin = new ByteArrayInputStream (bout.toByteArray ());
	    ObjectInputStream in     = new ObjectInputStream (bin);

	    return in.readObject();
	}
}
