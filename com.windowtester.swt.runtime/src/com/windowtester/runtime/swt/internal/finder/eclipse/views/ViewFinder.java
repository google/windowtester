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
package com.windowtester.runtime.swt.internal.finder.eclipse.views;

import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.WorkbenchPartReference;
import org.eclipse.ui.views.IViewDescriptor;

import com.windowtester.runtime.swt.internal.finder.FinderHelper;
import com.windowtester.runtime.swt.internal.finder.SWTHierarchyHelper;
import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.util.StringComparator;

/**
 * A service for finding {@link com.windowtester.runtime.swt.internal.finder.eclipse.views.IViewHandle}s corresponding to given
 * widgets.
 * 
 */
@SuppressWarnings("restriction")
public class ViewFinder extends WorkbenchFinder {

	
	public static IViewMatcher nameMatcher(String name) {
		return new NameMatcher(name);
	}
	
	public static IViewMatcher idMatcher(String id) {
		return new IdMatcher(id);
	}
	
	public static interface IViewMatcher {
		boolean matches(IViewReference view);
		boolean matches(IViewPart part);
		boolean matches(IViewDescriptor view);
		String getLabel();
	}
	
	private static final class NameMatcher implements IViewMatcher {
		private final String name;

		NameMatcher(String name) {
			this.name = name;
		}
		
		public boolean matches(IViewReference view) {
			return StringComparator.matches(view.getPartName(), name);
		}
		public boolean matches(IViewPart part) {
			return StringComparator.matches(part.getTitle(), name);
		}
		public boolean matches(IViewDescriptor view) {
			return StringComparator.matches(view.getLabel(), name);
		}
		public String getLabel() {
			return name;
		}
	
	};
	
	private static final class IdMatcher implements IViewMatcher {
		private final String id;

		IdMatcher(String name) {
			this.id = name;
		}
		
		public boolean matches(IViewReference view) {
			return StringComparator.matches(view.getId(), id);
		}
		public boolean matches(IViewDescriptor view) {
			return StringComparator.matches(view.getId(), id);
		}
		
		public boolean matches(IViewPart part) {
			IViewSite viewSite = part.getViewSite();
			if (viewSite == null)
				return false;
			return StringComparator.matches(viewSite.getId(), id);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher#getLabel()
		 */
		public String getLabel() {
			return id;
		}
	};
	
	public static IViewReference findNamed(String viewName) {
		return findMatch(new NameMatcher(viewName));
	}
	
	public static IViewReference findWithId(String viewId) {
		return findMatch(new IdMatcher(viewId));
	}
	
	
	//note: finds first match
	public static IViewReference findMatch(IViewMatcher matcher) {
		IWorkbenchPage page = getActivePage();
		if (page == null)
			return null;
		IViewReference[] open = page.getViewReferences();
		for (int i = 0; i < open.length; i++) {
			if (matcher.matches(open[i])) {
				return open[i];
			}
		}
		return null;
	}
	
	
	
	/**
	 * Find the view handle associated with the view that contains this widget (or
	 * <code>null</code> if there is no associated view in the {@link IViewRegistry}.
	 * @param w the widget in question
	 * @return a corresponding view handle or <code>null</code> if there is none
	 */
	public static IViewHandle find(Widget w) {

		/*
		 * Strategy: climb up chain of parents, looking for controls. 
		 * If the found control is associated with a view, return that 
		 * view, else return null.
		 */
		
		//get the parents
		List<Control> parents = FinderHelper.getParentControls(w);
		
		//get the registered views
		IViewHandle[] viewHandles = ViewRegistry.getDefault().get();
		IViewHandle handle;
		//inspect each view looking for a match
		for (int i = 0; i < viewHandles.length; i++) {
			handle = viewHandles[i];
			if (parents.contains(getViewControl(handle.getId())))
				return handle;
		}
		//fall through
		return null;
	}
	
	
	
	///////////////////////////////////////////////////////////////////////////////////
	//
	// Public utility methods.
	//
	///////////////////////////////////////////////////////////////////////////////////
	
	
	public static Control getViewControl(IViewMatcher viewMatcher) {
		final IViewReference ref = findMatch(viewMatcher);
		if (ref == null)
			return null;
		return DisplayReference.getDefault().execute(new Callable<Control>(){
			public Control call() throws Exception {
				IViewPart view = ref.getView(false);
				if (view == null)
					return null;
				return getControl(view);
			}
		});
	}
	
	/**
	 * Get the control associated with the view registered with the platform at
	 * the given id.
	 * @param viewId the platform-registered view identifier
	 * @return the associated <code>Control</code> or <code>null</code> if there is
	 * either no associated view or there is no control associated with the view.
	 */
	public static Control getViewControl(String viewId) {
		IViewPart viewPart = getViewPart(viewId);
		return getControl(viewPart);
	}

	private static Control getControl(IViewPart viewPart) {
		if (viewPart == null)
			return null;
		IViewSite viewSite = viewPart.getViewSite();
		if (viewSite instanceof PartSite) {
			PartSite partSite = (PartSite)viewSite;
			return ((WorkbenchPartReference) partSite.getPartReference()).getPane().getControl();
		}
		return null;
	}
	
	
	public static Control getViewControlForName(String viewName) {
		IViewReference ref = findNamed(viewName);
		if (ref == null)
			return null;
		IViewPart part = (IViewPart) ref.getPart(false);
		return getControl(part);
	}
	
	/**
	 * Get the viewpart registered to the platform with the given id.
	 * @param id the id of the viewpart
	 * @return the associated viewpart or null if there is none
	 */
	public static IViewPart getViewPart(final String id) {
		final IViewPart[] part = new IViewPart[1];
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				//be safe here since the workbench might be disposed (or not active)
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow(); 
				if (window == null) {
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null) {
					return;
				}
				
				IViewReference[] viewReferences = page.getViewReferences();
				for (int i = 0; i < viewReferences.length; i++) {
					IViewReference ref = viewReferences[i];
					if (ref.getId().equals(id)) {
						part[0] = ref.getView(false); //don't attempt to restore -- OR?
						return;
					}
				}
			}
		});
		return part[0];
	}

	public static IViewReference getViewRef(final IViewDescriptor descriptor) {
		final IViewReference[] refs = new IViewReference[1];
		final Display display = PlatformUI.getWorkbench().getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				//be safe here since the workbench might be disposed (or not active)
				IWorkbench workbench = PlatformUI.getWorkbench();
				if (workbench == null) {
					return;
				}
				IWorkbenchWindow window = workbench.getActiveWorkbenchWindow(); 
				if (window == null) {
					return;
				}
				IWorkbenchPage page = window.getActivePage();
				if (page == null) {
					return;
				}
				
				IViewReference[] viewReferences = page.getViewReferences();
				for (int i = 0; i < viewReferences.length; i++) {
					IViewReference ref = viewReferences[i];
					if (ref.getId().equals(descriptor.getId())) {
						refs[0] = ref;
						return;
					}
				}
			}
		});
		return refs[0];
	}
	
	
	
	/**
	 * Get the current active viewpart without retrying.
	 */
	public static IViewPart getActiveViewPartNoRetries() {
		if (!Platform.isRunning())
			return null;
		final IWorkbenchPart[] part = new IViewPart[1];
		try {
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.syncExec(new Runnable() {
				public void run() {
					part[0] = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
				}
			});
		} catch(Exception e) {
			/*
			 * trap and ignore --- this might mean that the workbench is not up yet and we want to wait and try again
			 */
		}
		if (part[0] instanceof IViewPart)
			return (IViewPart)part[0];
		return null;
	}
	
	
	public static boolean isViewWithIdVisible(String viewId) {
		Control viewControl = getViewControl(viewId);
		if (viewControl == null)
			return false;
		return SWTHierarchyHelper.isVisible(viewControl);
	}
	
	public static boolean isViewWithIdActive(String viewId) {
		IViewPart part = getActiveViewPartNoRetries();
		if (part == null)
			return false;
		IViewSite viewSite = part.getViewSite();
		if (viewSite == null)
			return false;
		String id = viewSite.getId();
		//System.out.println("active view: " + id);
		if (id == null)
			return false;
		return id.equals(viewId);
	}

	public static boolean isViewWithIdDirty(String viewId) {
		IViewReference view = findWithId(viewId);
		if (view == null)
			return false;
		return view.isDirty();
	}


	public static String findIdForContainingView(Widget w) {
		IViewHandle handle = find(w);
		if (handle == null)
			return null;
		return handle.getId();
	}

	public static ViewLocator findInViewStack(Widget w) {
		SWTHierarchyHelper hierarchy = new SWTHierarchyHelper();
		while (w != null) {
			Object data = w.getData();
			System.out.println("ViewFinder.findInViewStack() data.getClass(): " + data.getClass());
			
//			if (data instanceof WorkbenchPage){
//			if (data instanceof WorkbenchPart){
//				WorkbenchPart part = (WorkbenchPart) data;
//				part.getSel
				
//			if (data instanceof PartStack) {
//				ViewStack stack = (ViewStack)data;
//				PartPane selection = stack.getSelection();
//				if (selection instanceof ViewPane) {
//					IViewReference ref = ((ViewPane)selection).getViewReference();
//					return ViewLocator.forId(ref.getId());
//				}
//			}
			w = hierarchy.getParent(w);
		}
		return null;
		
	}


	
	
}
