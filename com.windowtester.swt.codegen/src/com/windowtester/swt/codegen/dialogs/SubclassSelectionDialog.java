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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;

import com.windowtester.internal.debug.LogHandler;


/**
 * Dialog for presenting subclasses of a given type for selection.
 */
public class SubclassSelectionDialog {


	private final IType type;
	private IJavaProject project;


	public SubclassSelectionDialog(IType type) {
		this.type = type;
	}

	public static SubclassSelectionDialog forType(IType type) {
		return new SubclassSelectionDialog(type);
	}
	
	public SubclassSelectionDialog inProject(IJavaProject project) {
		this.project = project;
		return this;
	}
	

	public String getSelection(){
		
		//TODO: improve exception handling
		
		String result = "";
		
		try { 
			IType[] subtypes = SearchScopeHelper.forSubclasses(type).inProject(getJavaProject());

			SelectionDialog dialog = null;

			Object[] types;
			IJavaSearchScope scope;
			
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();	
			
			scope = getSearchScope();
			
			dialog = JavaUI.createTypeDialog(
				window.getShell(),
				new ProgressMonitorDialog(window.getShell()),
				scope,
				IJavaElementSearchConstants.CONSIDER_CLASSES,
				false, /*type.getFullyQualifiedName()*/ "", FilteredTypeSelectionExtension.forTypes(subtypes));
			dialog.setTitle("Superclass Selection");
			dialog.setMessage("Choose a type:");
			if (dialog.open() == IDialogConstants.CANCEL_ID) {
				return null;
			}
			types = dialog.getResult();
			if (types != null && types.length > 0) {
				result = ((IType)types[0]).getFullyQualifiedName();
			}
		} catch (JavaModelException e) {
			LogHandler.log(e);
		} catch (Exception e) {
			LogHandler.log(e);
		}
		
		return result;
	}


	private IJavaSearchScope getSearchScope() {
		return SearchEngine.createJavaSearchScope(new IJavaElement[]{project}, true /* referenced projects */);
	}

	private IJavaProject getJavaProject() {
		return project;
	}
	
}
