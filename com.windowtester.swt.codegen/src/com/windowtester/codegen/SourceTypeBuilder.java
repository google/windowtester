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
package com.windowtester.codegen;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.windowtester.codegen.assembly.unit.ClassUnit;
import com.windowtester.codegen.assembly.unit.FieldUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;

/**
 * A base class for source builders.
 */
public abstract class SourceTypeBuilder implements ISourceTypeBuilder {

    /** This type's defined methods */
    private Collection /*MethodUnit*/ _methods;
    /** This type's defined fields */
    private Collection /*FieldUnit*/ _fields;
    /** This type's defined imports */
    private Collection /*ImportUnit*/ _imports;
    /** This type's defined classes */
    private Collection /*ClassUnit*/ _classes;
    /** This type's base class */
    private String _extends;
    /** This type's name */
    private String _name;
    /** This type's package */
    private String _pkg;
    /**The cuurrent active shell title */
    private String _shellTitle;
    

    /**
     * Create an instance.
     * @param name - the name of the test case
     * @param pkg  - the package of the test case
     */
    public SourceTypeBuilder(String name, String pkg) {
        _name = name;
        _pkg = pkg;
    }
    
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#hasMethod(com.windowtester.codegen.assembly.unit.MethodUnit)
     */
    public boolean hasMethod(MethodUnit method) {
        return getMethods().contains(method);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#addMethod(com.windowtester.codegen.assembly.unit.MethodUnit)
     */
    public boolean addMethod(MethodUnit method) {
    	return (method == null) ? false : getMethods().add(method);
    }
    

	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ISourceTypeBuilder#addClass(com.windowtester.codegen.assembly.unit.ClassUnit)
	 */
	public boolean addClass(ClassUnit cls) {
		return (cls == null || hasClass(cls)) ? false : getClasses().add(cls);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.codegen.ISourceTypeBuilder#hasClass(com.windowtester.codegen.assembly.unit.ClassUnit)
	 */
	public boolean hasClass(ClassUnit cls) {
		//TODO: push into a ClassSet abstraction
		for (Iterator iter = getClasses().iterator(); iter.hasNext();) {
			ClassUnit unit = (ClassUnit) iter.next();
			if (unit.getName().equals(cls.getName()))
				return true;
		}
		return false;
	}
	
   


	/* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#hasField(com.windowtester.codegen.assembly.unit.FieldUnit)
     */
    public boolean hasField(FieldUnit field) {
        return getFields().contains(field);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#addField(com.windowtester.codegen.assembly.unit.FieldUnit)
     */
    public boolean addField(FieldUnit field) {
        return getFields().add(field);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#hasImport(com.windowtester.codegen.assembly.unit.ImportUnit)
     */
    public boolean hasImport(ImportUnit imprt) {
        return getImports().contains(imprt);
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#addImport(com.windowtester.codegen.assembly.unit.ImportUnit)
     */
    public boolean addImport(ImportUnit imprt) {
        return getImports().add(imprt);
    }
    
    /**
     * Set this type's base class.
     * @param ext
     */
    public void setExtends(String ext) {
        _extends = ext;
    }
    
    /**
     * @return this type's base class
     */
    public String getExtends() {
        return _extends;
    }
    
    /**
     * @return the currently defined methods
     */
    public Collection getMethods() {
        if (_methods == null)
            _methods = newSet(); 
        return _methods;
    }
    
    /**
     * Return a new Set-based collection.
     * @return a new set
     */
    private Set newSet() {
        //using a LinkedHashSet for its predictable (insertion-order) ordering 
        return new LinkedHashSet();
    }

    /**
     * @return the currently defined fields
     */
    public Collection getFields() {
        if (_fields == null)
            _fields = newSet(); 
        return _fields;
    }
    
    /**
     * @return the currently defined imports
     */
    public Collection getImports() {
        if (_imports == null)
            _imports = newSet(); 
        return _imports;
    }

    /**
     * @return the currently defined inner classes
     */
    public Collection getClasses() {
        if (_classes == null)
        	_classes = newSet(); 
        return _classes;
	}
    
    /**
     * @return Returns the name of the source type being built.
     */
    public String getName() {
        return _name;
    }
    /**
     * @param name - the name to set.
     */
    public void setName(String name) {
        _name = name;
    }
    /**
     * @return Returns the type's package.
     */
    public String getPackage() {
        return _pkg;
    }
    /**
     * @param pkg The pkg name to set.
     */
    public void setPackage(String pkg) {
        _pkg = pkg;
    }
    
    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#getCurrentShellTitle()
     */
    public String getCurrentShellTitle() {
        return _shellTitle;
    }
   

    /* (non-Javadoc)
     * @see com.windowtester.codegen.ISourceTypeBuilder#updateCurrentShellTitle(java.lang.String)
     */
    public boolean updateCurrentShellTitle(String shellTitle) {
        boolean hasChanged = (_shellTitle == null) ? (shellTitle == null)
                : !_shellTitle.equals(shellTitle);
        _shellTitle = shellTitle;
        return hasChanged;
    }
    
    
}
