package com.windowtester.swt.gef.internal.reflector;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.windowtester.internal.runtime.ClassReference;
import com.windowtester.internal.runtime.ClassReference.UnresolvableClass;

/**
 * <p>
 * Copyright (c) 2007, Instantiations, Inc.<br>
 * All Rights Reserved
 *
 * @author Phil Quitslund
 *
 */
public class ClassReflector implements Serializable {

	private static final long serialVersionUID = -2064118437541630317L;
	private static final ClassReference[] EMPTY_ARRAY = new ClassReference[0];
	private final ClassReference _classRef;

	private Set _types = null;
	
	public ClassReflector(ClassReference classRef) {
		_classRef = classRef;
	}

	public ClassReference getClassRef() {
		return _classRef;
	}
	
	/**
     * Determines if the class or interface represented by this
     * <code>ClassReflector's</code> {@link ClassReference} object is either the same as, 
     * or is a superclass or superinterface of, the class or interface represented by the specified
     * <code>Class</code> parameter. 
	 */
	public boolean isAssignableFrom(Class cls) {
		Class myClass = getClassRef().getClassForName();
		if (myClass == UnresolvableClass.class)
			return isAssignableFrom(myClass, cls);
		return myClass.isAssignableFrom(cls);
	}

	public /* public for testing */ boolean isAssignableFrom(Class myClass, Class cls) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/**
	 * Returns all superclasses and interfaces implemented by this class.
	 */
	public ClassReference[] getTypes() {
		if (_types == null)
			populateTypes();
		return (ClassReference[]) _types.toArray(EMPTY_ARRAY);
		
	}

	private void populateTypes() {
		_types = new HashSet();
		Class cls = getClassRef().getClassForName();
		collectTypes(_types, cls);
	}

	/**
	 * Collect all of this classes types (interfaces and super classes) -- including itself.
	 */
	public static void collectTypes(Set types, Class cls) {
		add(types, cls);		
		add(types, cls.getSuperclass());
	}

	private static void add(Set types, Class cls) {
		types.add(new ClassReference(cls));
	}
	
	
	
	
}
