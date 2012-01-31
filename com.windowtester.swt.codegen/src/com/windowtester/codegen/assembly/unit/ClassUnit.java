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
package com.windowtester.codegen.assembly.unit;

import com.windowtester.codegen.assembly.block.CodeBlock;

public class ClassUnit extends MemberUnit {

	public static abstract class Type {}
	public static abstract class Visibility {
		abstract String asString();
	}
	
	public static Type       STATIC  = new Type(){};
	public static Type   NOT_STATIC  = new Type(){};
	
	public static Visibility PRIVATE = new Visibility(){
		String asString() {
			return "private";
		}
	};
	public static Visibility PUBLIC  = new Visibility(){
		String asString() {
			return "public";
		}
	};
	private final Visibility _visibility;
	private final Type _type;
	private String _contents;
	private String _superClass;
	
	public ClassUnit(String name) {
		this(name, PUBLIC, NOT_STATIC);
	}

	public ClassUnit(String name, Visibility v, Type t) {
		this(name, v, t, "");
	}

	public ClassUnit(String name, Visibility v, Type t, String contents) {
		super(name);
		_visibility = v;
		_type       = t;
		_contents   = contents;
	}
	
	public Visibility getVisibility() {
		return _visibility;
	}
	
	public Type getType() {
		return _type;
	}
	
	public String getContents() {
		return _contents;
	}
	
	public void setBody(CodeBlock body) {
		_contents = body.toString();
	}
	
	public CodeBlock getBody() {
		return new CodeBlock(visibility() + typeMod() +  classDecl() + extendsDecl() + open() + getContents() + close()); 
	}

	private String extendsDecl() {
		String supr = getExtends();
		if (supr == null)
			return "";
		return " extends " + supr;
	}

	private String visibility() {
		return getVisibility().asString() + " ";
	}

	private String classDecl() {
		return "class " + getName();
	}

	private String open() {
		return "{";
	}

	private String close() {
		return "}";
	}
	
	private String typeMod() {
		return getType() == STATIC ? "static " : "";
	}

	public String getExtends() {
		return _superClass;
	}
	public void setExtends(String superClass) {
		_superClass = superClass;
	}
	
	
}
