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
package com.windowtester.runtime.swt.internal.finder.eclipse.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;

import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.finder.FinderHelper;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.selector.UIDriver;
import com.windowtester.runtime.swt.internal.widgets.finder.SWTWidgetFinder;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * A service for finding {@link com.windowtester.runtime.swt.internal.finder.eclipse.editors.IEditorHandle}s corresponding to given
 * widgets.
 *
 */
@SuppressWarnings("restriction")
public class EditorFinder {

	
	public static class EditorNotFoundException extends WidgetNotFoundException {

		private static final long serialVersionUID = 8399073666506395530L;

		public EditorNotFoundException(String msg) {
			super(msg);
		}
	}

	public static class MultipleEditorsFoundException extends
			MultipleWidgetsFoundException {

		private static final long serialVersionUID = 102368605294368219L;

		public MultipleEditorsFoundException(String msg) {
			super(msg);
		}
	}

	/**
	 * Get the current active editor. NOTE: this uses the same retry scheme as widget finds.
	 * @return the currently active editor part or <code>null</code> if none can be found
	 */
	public static IEditorPart getActiveEditor() {
		
		int maxRetries = SWTWidgetFinder.getMaxFinderRetries();
		int retryInterval = SWTWidgetFinder.getFinderRetryInterval();

		for (int retries = 0; retries < maxRetries; ++retries) {
			IEditorPart part = getActiveEditorPartNoRetries();
			if (part != null)
				return part;
			UIDriver.pause(retryInterval);
		}
		return null;
	}
	
	/**
	 * Get the current active editor without retrying.
	 */
	public static IEditorPart getActiveEditorPartNoRetries() {
		if (!Platform.isRunning())
			return null;
		final IEditorPart[] part = new IEditorPart[1];
		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					part[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				}
			});
		} catch(Exception e) {
			/*
			 * trap and ignore --- this might mean that the workbench is not up yet and we want to wait and try again
			 */
		}
		return part[0];
	}


	public static Control getEditorControl(String partName)
			throws EditorNotFoundException, MultipleEditorsFoundException {
		IEditorReference[] editors = findEditors(partName);
		if (editors.length == 0)
			throw new EditorNotFoundException(partName);
		if (editors.length > 1)
			throw new MultipleEditorsFoundException(partName);
		return getEditorControl(editors[0]);
	}

	private static Control getEditorControl(IEditorReference reference) {
		IWorkbenchPart part = reference.getPart(false /* don't restore */);
		if (part == null)
			return null;
		IWorkbenchPartSite site = part.getSite();
		if (site instanceof PartSite) {
			return ((WorkbenchPartReference) ((PartSite) site).getPartReference()).getPane().getControl();
		}
		return null;
	}

	public static IEditorReference[] findEditors(final String partNameOrPattern) {
		final List<IEditorReference> found = new ArrayList<IEditorReference>();
		final Display display = Display.getDefault();
		display.syncExec(new Runnable() {
			public void run() {
				// be safe here since the workbench might be disposed (or not
				// active)
				if (!Platform.isRunning())
					return;
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
				if (window == null) {
					return;
				}

				IWorkbenchPage[] pages = window.getPages();
				if (pages == null)
					return;
				
				for (int i = 0; i < pages.length; i++) {
					IEditorReference[] editorReferences = pages[i]
							.getEditorReferences();
					if (editorReferences != null)
						for (int j = 0; j < editorReferences.length; j++) {
							if (matches(editorReferences[j].getPartName(), partNameOrPattern))
								if (isControlVisible(editorReferences[j]))
									found.add(editorReferences[j]);
						}
				}
			}

		});
		return found.toArray(new IEditorReference[] {});
	}

	public static boolean isControlVisible(IEditorReference ref) {
		Control control = getEditorControl(ref);
		if (control == null)
			return false;
		return SWTHierarchyHelper.isVisible(control);
	}
	
	
	//returns the FIRST editor (if any) to match the given name of pattern
	public static IEditorPart getEditorPart(final String partNameOrPattern) {
		IEditorReference[] references = findEditors(partNameOrPattern);
		if (references.length == 0)
			return null;
		return references[0].getEditor(false /* don't restore */);
	}
	

	public static IEditorPart[] getEditorParts(final String partNameOrPattern) {
		IEditorReference[] references = findEditors(partNameOrPattern);
		IEditorPart[] parts = new IEditorPart[references == null ? 0 : references.length];
		for (int i = 0; i < parts.length; i++) {
			parts[i] = references[i].getEditor(false /* don't restore */);
		}
		return parts;
	}
	
	public static IEditorReference findContainingEditor(Control control) throws EditorNotFoundException, MultipleEditorsFoundException {
		IEditorReference[] references = findEditors(".*");
		for (int i = 0; i < references.length; i++) {
			IEditorReference ref = references[i];
			Control editorControl = getEditorControl(ref.getPartName());
			if (editorControl == null)
				continue;
			if (isContainedIn(control, editorControl))
				return ref;				
		}
		return null;
	}
	
	
	public static EditorLocator findContainingEditorLocator(Control control) throws EditorNotFoundException, MultipleEditorsFoundException {
		final IEditorReference reference = findContainingEditor(control);
		if (reference == null)
			return null;
		return new EditorLocator(reference.getPartName());
//		return new EditorLocator("<unspecified part name>") {
//		
//			private static final long serialVersionUID = 1L;
//
//			protected IWidgetMatcher buildMatcher() {
//				return new EditorComponentMatcher("<unspecified part name>") {
//					protected Control getEditorControl() throws EditorNotFoundException, MultipleEditorsFoundException {
//						return EditorFinder.getEditorControl(reference);
//					}
//				};
//			}
//		};
	}
	
	
	private static boolean isContainedIn(Control control, Control editorControl) {
		if (control == editorControl)
			return true;
		return FinderHelper.isParentTo(editorControl, control);
	}

	

	public static boolean isEditorActiveNoRetries(String partName) {
		IEditorPart part = getEditorPart(partName);
		IEditorPart activePart = getActiveEditorPartNoRetries();
		return part == activePart;
	}
	
	public static boolean isEditorActiveNoRetries(IEditorReference ref) {
		if (ref == null)
			return false; 
		IWorkbenchPart part = ref.getPart(false);
		IEditorPart activeEditor = getActiveEditorPartNoRetries();
		if (activeEditor == null)
			return false;
		return activeEditor == part;
	}

	
	public static boolean isEditorControlVisibleNoRetries(IEditorReference ref) {
		Control control = getEditorControl(ref);
		if (control == null)
			return false;
		return SWTHierarchyHelper.isVisible(control);
	}
	
	
	public static boolean matches(String name, String nameOrPattern) {
		//System.out.println("Testing " + name + " against " + nameOrPattern);
		return StringComparator.matches(name, nameOrPattern);
	}

	public static boolean isEditorDirtyNoRetries(String partName) {
		IEditorPart part = getEditorPart(partName);
		if (part == null)
			return false;
		return part.isDirty();
	}

	public static boolean isEditorControlVisibleNoRetries(String partName) throws EditorNotFoundException, MultipleEditorsFoundException {
		Control control = getEditorControl(partName);
		if (control == null)
			return false;
		return SWTHierarchyHelper.isVisible(control);
	}

	
	
}
