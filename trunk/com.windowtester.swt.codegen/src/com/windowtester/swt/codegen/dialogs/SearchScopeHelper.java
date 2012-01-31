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
package com.windowtester.swt.codegen.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

/**
 * Search helper for identifying subclasses of a given type.
 */
public class SearchScopeHelper {

	private IProgressMonitor monitor;
	protected List results = new ArrayList();
	private final IType type;

	
	public SearchScopeHelper(IType type) {
		this.type = type;
	}

	public static SearchScopeHelper forSubclasses(IType type) {
		return new SearchScopeHelper(type);
	}
	
	public IType[] inProject(IJavaProject project) throws CoreException {

		int includeMask = IJavaSearchScope.SOURCES
				| IJavaSearchScope.APPLICATION_LIBRARIES |
				// IJavaSearchScope.SYSTEM_LIBRARIES |
				IJavaSearchScope.REFERENCED_PROJECTS;

		IJavaSearchScope searchScope = SearchEngine.createJavaSearchScope(
				new IJavaElement[] { project }, includeMask);

		// // DEBUG: what does our scope encompass?
		// for (IPath path : searchScope.enclosingProjectsAndJars())
		// System.out.println("will search " + path);
		// jar containing classes we want to match is seen,
		// but results within that jar are not being returned.

		SearchEngine se = new SearchEngine();
		SearchPattern pattern = SearchPattern.createPattern(type.getFullyQualifiedName(),
				IJavaSearchConstants.CLASS, IJavaSearchConstants.IMPLEMENTORS,
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE);

		SearchRequestor requestor = new SearchRequestor() {

			public void acceptSearchMatch(SearchMatch match)
					throws CoreException {
//				System.err.println("found match "
//						+ match.getElement().getClass() + ":"
//						+ match.getElement());

				results.add((IType) match.getElement());
			}
		};		
		
//		long start = System.currentTimeMillis();
		se.search(pattern, new SearchParticipant[] { SearchEngine
				.getDefaultSearchParticipant() }, searchScope, requestor,
				monitor);
		
		//add "self" type
		results.add(type);
		
//		System.err.println("done in " + (System.currentTimeMillis() - start)
//				+ " ms");
//		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
//			IType type = (IType) iterator.next();
//			System.out.println(type);
//		}
		
		return (IType[]) results.toArray(new IType[]{});
	}
}
