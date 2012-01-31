package com.windowtester.swt.gef.internal.reflector;

import static com.instantiations.test.util.TestCollection.assertContainsOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.windowtester.internal.runtime.ClassReference;

import junit.framework.TestCase;

/**
 * The class <code>ClassReflectorTest</code> contains tests for the class
 * {@link <code>ClassReflector</code>}
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassReflectorTest extends TestCase {

	interface Foo {}
	interface Baz {}
	interface Zoom {}
	
	class Lonely{}
	
	class Bar implements Foo, Baz {}
	
	class Blah extends Bar implements Zoom {}

	public void testCollectTypes() {
		Set set = new HashSet();
		ClassReflector.collectTypes(set, Lonely.class);
		assertContainsOnly(refs(Object.class, Lonely.class), set);
	}

	private Object[] refs(Class ...classes) {
		List<Object> list = new ArrayList<Object>();
		for (Class<?> cls : classes) {
			list.add(new ClassReference(cls));
		}
		return list.toArray();
	}

}