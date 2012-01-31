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
package com.windowtester.eclipse.ui.jdt.analysis.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

import com.windowtester.ui.util.Logger;

/**
 * Instances of the class <code>TypeHierarchyCache</code> cache one or more
 * type hierarchies associated with either types or projects.
 */
public class TypeHierarchyCache
{
	/**
	 * The maximum number of type hierarchies that should be kept in the cache.
	 */
	private int maxCacheSize;

	/**
	 * A table mapping types and projects to the type hierarchy associated with
	 * them.
	 *
	 * @type HashMap<IJavaElement, ITypeHierarchy>
	 */
	private HashMap cache;

	/**
	 * A list of the keys in the cache ordered by the time at which they were
	 * last accessed. The most recently accessed are at the beginning of the
	 * list.
	 *
	 * @type ArrayList<IJavaElement>
	 */
	private ArrayList mruList;

	////////////////////////////////////////////////////////////////////////////
	//
	// Constructors
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Initialize a newly created type hierarchy cache to be empty and to have
	 * the default maximum size.
	 */
	public TypeHierarchyCache()
	{
		this(100);
	}

	/**
	 * Initialize a newly created type hierarchy cache to be empty.
	 *
	 * @param maxCacheSize the maximum number of type hierarchies that should
	 *        be kept in the cache
	 */
	public TypeHierarchyCache(int maxCacheSize)
	{
		this.maxCacheSize = maxCacheSize;
		cache = new HashMap(maxCacheSize);
		mruList = new ArrayList(maxCacheSize);
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessing
	//
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Return a type hierarchy for the class <code>java.lang.Object</code> from
	 * the given project.
	 *
	 * @param project the project used to find the class <code>Object</code>
	 *
	 * @return a type hierarchy for the given project
	 */
	public ITypeHierarchy getTypeHierarchy(IJavaProject project)
	{
		ITypeHierarchy typeHierarchy;
		IType objectType;

		typeHierarchy = getHierarchy(project);
		if (typeHierarchy == null) {
			try {
				objectType = project.findType("java.lang.Object");
				typeHierarchy = getTypeHierarchy(objectType, project);
				if (typeHierarchy != null) {
					addHierarchy(project, typeHierarchy);
				}
			} catch (JavaModelException exception) {
				Logger.log("Could not create type hierarchy for project " + project.getElementName(), exception);
			}
		}
		return typeHierarchy;
	}

	/**
	 * Return a type hierarchy containing the given type, all supertypes, and
	 * all subtypes of the given type.
	 *
	 * @param type the type around which the type hierarchy is centered
	 * @param project the project used to identify which classes should be
	 *        included in the hierarchy
	 *
	 * @return a type hierarchy for the given type
	 */
	public ITypeHierarchy getTypeHierarchy(IType type, IJavaProject project)
	{
		ITypeHierarchy typeHierarchy;
		IType[] subtypes;

		typeHierarchy = getHierarchy(type);
		if (typeHierarchy == null) {
			try {
//long startTime = System.currentTimeMillis();
//System.out.println("   " + startTime + " start creating type hierarchy for " + type.getElementName());
				typeHierarchy = type.newTypeHierarchy(project, null);
//long endTime = System.currentTimeMillis();
//System.out.println("   " + endTime + " finished (" + (endTime - startTime) + "ms)");
				addHierarchy(type, typeHierarchy);
				subtypes = typeHierarchy.getAllSubtypes(type);
				for (int i = 0; i < subtypes.length; i++) {
					cache.put(subtypes[i], typeHierarchy);
				}
			} catch (JavaModelException exception) {
				Logger.log("Could not create type hierarchy for the type " + type.getElementName(), exception);
			}
		}
		return typeHierarchy;
	}

	////////////////////////////////////////////////////////////////////////////
	//
	// Accessing
	//
	////////////////////////////////////////////////////////////////////////////

	private ITypeHierarchy getHierarchy(IJavaElement key)
	{
		ITypeHierarchy typeHierarchy;

		typeHierarchy = (ITypeHierarchy) cache.get(key);
		if (typeHierarchy != null) {
			mruList.remove(key);
			mruList.add(0, key);
		}
		return typeHierarchy;
	}

	private void addHierarchy(IJavaElement key, ITypeHierarchy typeHierarchy)
	{
		int cacheSize;
		Object removedKey;

		cacheSize = mruList.size();
		if (cacheSize >= maxCacheSize) {
			removedKey = mruList.remove(cacheSize - 1);
			cache.remove(removedKey);
		}
		mruList.add(0, key);
		cache.put(key, typeHierarchy);
	}
}