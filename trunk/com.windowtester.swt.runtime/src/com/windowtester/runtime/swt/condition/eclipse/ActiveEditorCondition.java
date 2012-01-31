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
package com.windowtester.runtime.swt.condition.eclipse;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.util.StringComparator;

/**
 * Tests the active editor to see if it contains a given file.
 * <p>
 * Example use:
 * <pre>
 * //wait for "MyFile.java" to be in the active editor
 * ui.wait(ActiveEditorCondition.forName("MyFile.java"));
 * </pre>
 *
 */
public abstract class ActiveEditorCondition implements ICondition, IDiagnosticParticipant {

	protected final boolean isActive;
	
	private static class ByPath extends ActiveEditorCondition {
		private final IPath expectedPath;
		private IPath actualPath;

		public ByPath(IPath path, boolean isActive) {
			super(isActive);
			if (path == null)
				throw new IllegalArgumentException("file path must not be null");
			this.expectedPath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.swt.condition.ICondition#test()
		 */
		public boolean test() {
			IFile file = getActiveFile();
			if (file == null)
				return false;
			actualPath = file.getLocation();
			return actualPath.equals(actualPath) == isActive;
		}
		
		private IFile getFile(IEditorInput input) {
			/*
			 * We'd like to cast to an IFileEditorInput but that would introduce an
			 * unwanted dependency on eclipse.ui.ide.
			 */

			if (input == null)
				return null;

			Class<? extends IEditorInput> cls = input.getClass();
			try {
				Method m = cls.getMethod("getFile", (Class[]) null);
				IFile file = (IFile) m.invoke(input, (Object[]) null);
				return file;
			} catch (Exception e) {
				//really, do nothing
			}
			return null;
		}
		
		/**
		 * Return the IFile for the active editor 
		 */
		IFile getActiveFile() {
			// get the file from the active workbench editor
			IEditorPart activeEditor = getActiveEditor();
			if (activeEditor == null)
				return null;
			IEditorInput input = activeEditor.getEditorInput();
			return getFile(input);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return " for path [" + expectedPath + "] to be in the active editor: "
				+ isActive;
		}
		
//		public ICondition not() {
//			return new ByPath(expectedPath, false);
//		}
		
		////////////////////////////////////////////////////////////////////////////
		//
		// IDiagnosticParticipant
		//
		////////////////////////////////////////////////////////////////////////////

		public void diagnose(IDiagnostic diagnostic) {
			diagnostic.attribute("class", ActiveEditorCondition.class.getName());
			diagnostic.attribute("expected", safeToString(expectedPath));
			diagnostic.attribute("actual", safeToString(actualPath));
		}

		
		protected String safeToString(Object object) {
			return object == null ? "null" : object.toString();
		}

			
	}
	
	private static class ByName extends ActiveEditorCondition {
		private final String expectedTitle;
		private String actualTitle;
		ByName(String name, boolean isActive) {
			super(isActive);
			this.expectedTitle = name;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.swt.condition.ICondition#test()
		 */
		public boolean test() {
			IEditorPart activeEditor = getActiveEditor();
			if (activeEditor == null)
				return false;
			actualTitle = activeEditor.getTitle();
			return StringComparator.matches(actualTitle, expectedTitle);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return " for name [" + expectedTitle + "] to be in the active editor: " + isActive;
		}
		
//		public ICondition not() {
//			return new ByName(expectedTitle, false);
//		}
		
		////////////////////////////////////////////////////////////////////////////
		//
		// IDiagnosticParticipant
		//
		////////////////////////////////////////////////////////////////////////////

		public void diagnose(IDiagnostic diagnostic) {
			diagnostic.attribute("class", ActiveEditorCondition.class.getName());
			diagnostic.attribute("expected", expectedTitle);
			diagnostic.attribute("actual", actualTitle);
		}
		
	}
	
	/**
	 * Create a condition that matches by path.
	 */
	public static ActiveEditorCondition forPath(IPath path) {
		return new ByPath(path, true);
	}
	
	/**
	 * Create a condition that matches by name.
	 */
	public static ActiveEditorCondition forName(String name) {
		return new ByName(name, true);
	}

	
	public ActiveEditorCondition(boolean isActive) {
		this.isActive = isActive;
	}

	

	IEditorPart getActiveEditor() {
		final IWorkbenchPage page = getActivePage();
		final IEditorPart[] activeEditor = new IEditorPart[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				activeEditor[0] = page.getActiveEditor();
			}
		});
		return activeEditor[0];
	}

	IWorkbenchPage getActivePage() {
		final IWorkbenchWindow window = getActiveWorkbenchWindow();
		final IWorkbenchPage[] page = new IWorkbenchPage[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				page[0] = window.getActivePage();
			}
		});
		TestCase.assertNotNull(page[0]);
		return page[0];
	}

	IWorkbenchWindow getActiveWorkbenchWindow() {
		final IWorkbench workbench = getWorkbench();
		final IWorkbenchWindow[] window = new IWorkbenchWindow[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				window[0] = workbench.getActiveWorkbenchWindow();
			}
		});
		TestCase.assertNotNull(window[0]);
		return window[0];
	}

	IWorkbench getWorkbench() {
		final IWorkbench[] workbench = new IWorkbench[1];
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				workbench[0] = PlatformUI.getWorkbench();
			}
		});
		TestCase.assertNotNull(workbench[0]);
		return workbench[0];
	}
	

}
