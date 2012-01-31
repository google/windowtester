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
package com.windowtester.runtime.swt.locator.eclipse;

import java.io.Serializable;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WaitTimedOutException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.condition.IConditionHandler;
import com.windowtester.runtime.condition.IsVisible;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewClosedConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewCondition;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewShowingConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewZoomedConditionHandler;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewCondition.Active;
import com.windowtester.runtime.swt.internal.condition.eclipse.ViewCondition.Dirty;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.finder.RetrySupport;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewExplorer;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher;
import com.windowtester.runtime.swt.internal.locator.ICloseableLocator;
import com.windowtester.runtime.swt.internal.matchers.eclipse.ViewComponentMatcher;
import com.windowtester.runtime.swt.internal.matchers.eclipse.ViewComponentMatcher.IViewControlProvider;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;

/**
 * 
 * Locates eclipse workbench views.
 * <p>
 * Views are identified by their <code>view-id</code> which is specified
 * when the view is contributed to the workbench via the <code>org.eclipse.ui.views</code>
 * extension point.
 * 
 * For instance, given that the resource navigator view has an id of "org.eclipse.ui.views.ResourceNavigator",
 * the locator to identify the tree in the resource navigator could be written like this:
 * 
 * <pre>
 * new SWTWidgetLocator(Tree.class, 
 *    new ViewLocator("org.eclipse.ui.views.ResourceNavigator");
 * </pre>
 * 
 * @see org.eclipse.ui.IViewPart
 *
 */
public class ViewLocator extends SWTWidgetLocator implements IWorkbenchPartLocator, IsVisible {

	private static final long serialVersionUID = 8106851292164382419L;
	
	

	private final class ViewHelper implements
			IViewControlProvider, IViewMatcher {

		private final IViewMatcher viewMatcher;

		private ViewHelper(IViewMatcher viewMatcher) {
			this.viewMatcher = viewMatcher;
		}

		public Control getViewControl() {
			return ViewFinder.getViewControl(viewMatcher);
		}

		public String getViewLabel() {
			return viewMatcher.getLabel();
		}
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher#matches(org.eclipse.ui.IViewReference)
		 */
		public boolean matches(IViewReference view) {
			return viewMatcher.matches(view);
		}
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher#matches(org.eclipse.ui.IViewPart)
		 */
		public boolean matches(IViewPart part) {
			return viewMatcher.matches(part);
		}
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher#getLabel()
		 */
		public String getLabel() {
			return viewMatcher.getLabel();
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher#matches(org.eclipse.ui.views.IViewDescriptor)
		 */
		public boolean matches(IViewDescriptor view) {
			// TODO Auto-generated method stub
			return viewMatcher.matches(view);
		}
	}

	private static class Closer implements ICloseableLocator, Serializable {

		private static final long serialVersionUID = 3502312171322974878L;
		private final transient ViewLocator view;

		public Closer(ViewLocator view) {
			this.view = view;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.swt.internal.locator.ICloseableLocator#doClose(com.windowtester.runtime.IUIContext, com.windowtester.runtime.locator.IWidgetReference, com.windowtester.runtime.IClickDescription)
		 */
		public void doClose(IUIContext ui) throws WidgetSearchException {
			view.doClose(ui);
		}
		
		
	}
	
	/**
	 * Closes the given view.
	 * @throws WidgetSearchException 
	 */
	private void doClose(IUIContext ui) throws WaitTimedOutException, WidgetSearchException {
		waitForIdle(ui);
		//There is a race condition here -- the view may be in the process of closing...
		//ensureViewIsVisible(ui);
		hideView();		
		ensureViewIsClosed(ui);
	}

	private void ensureViewIsClosed(IUIContext ui) {
		ui.wait(isVisible(false));
	}

	private void hideView() throws WidgetNotFoundException, WidgetSearchException {
		final IViewReference reference = ViewFinder.findMatch(viewHelper);
		if (reference == null)
			throw new WidgetNotFoundException("View: " + viewHelper.getLabel() + " not found");

		final Display display = getDisplay();
		final IWorkbenchPage page = getActiveWorkbenchPage(display);
		if (page == null)
			throw new WidgetSearchException("unable to find active workbench page");
		
		// notice that this is done as an async in case the view is dirty
		// and forces a prompt
		display.asyncExec(new Runnable() {
			public void run() {
				page.hideView(reference);
			}
		});
	}


//	private void ensureViewIsVisible(final IUIContext ui) throws WidgetSearchException {
//		RetrySupport.retryUntilArrayResultIsNonEmpty(new RunnableWithResult(){
//			public Object runWithResult() {
//				return ui.findAll(ViewLocator.this);
//			}
//		});
//	}

	private void waitForIdle(IUIContext ui) throws WaitTimedOutException {
		ui.wait(new SWTIdleCondition());
	}

	private Display getDisplay() {
		return PlatformUI.getWorkbench().getDisplay();
	}
	
	/**
	 * @param display
	 * @return
	 */
	private IWorkbenchPage getActiveWorkbenchPage(final Display display) {
		return (IWorkbenchPage) RetrySupport.retryUntilResultIsNonNull(new RunnableWithResult() {
			public Object runWithResult() {
				final IWorkbenchPage[] page = new IWorkbenchPage[1];
				display.syncExec(new Runnable() {
					public void run() {
						IWorkbench workbench = PlatformUI.getWorkbench();
						if (workbench == null)
							return;
						IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
						if (window == null)
							return;
						page[0] = window.getActivePage();
					}
				});
				return page[0];
			}
		});
	}

	/** The id/name of the target view */
	//private final String _viewId;
	
	private final transient ViewHelper viewHelper;
	
	//view id is cached for serialization purposes
	private final String viewId;
	
	
	
	
	/**
	 * Create an instance that locates the given view.
	 * @param viewId the id of the view to locate
	 */
	public ViewLocator(String viewId) {
		this(ViewFinder.idMatcher(viewId)); //the legacy default
	}
	
	private ViewLocator(final IViewMatcher viewMatcher) {
		super(Control.class);
		this.viewHelper = new ViewHelper(viewMatcher);
		this.viewId = viewMatcher.getLabel();
	}
	
	
	/**
	 * Create a new View Locator that identifies views by view name.
	 * @param viewName the view id of the target view
	 * @return a new name-matching View Locator
	 */
	public static ViewLocator forName(String viewName) {
		return new ViewLocator(ViewFinder.nameMatcher(viewName));
	}

	/**
	 * Create a new View Locator that identifies views by view id.
	 * @param viewId the view id of the target view
	 * @return a new id-matching View Locator
	 */
	public static ViewLocator forId(String viewId) {
		return new ViewLocator(ViewFinder.idMatcher(viewId));
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "ViewLocator ["+ getViewId() +"]";
	}
	
	/**
	 * Get this view locator's view id.
	 */
	public String getViewId() {
		return viewId;
	}
	

	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
	 */
	protected ISWTWidgetMatcher buildMatcher() {		
		return new ViewComponentMatcher(new IViewControlProvider() {
			public Control getViewControl() {
				return ViewFinder.getViewControl(viewHelper);
			}
			public String getViewLabel() {
				return getViewId();
			}
		});
		
//		//note we need to adapt the old matcher to the new API
//		return new AdapterFactory().adapt(new ViewComponentMatcher(new IViewControlProvider() {
//			public Control getViewControl() {
//				return ViewFinder.getViewControl(viewHelper);
//			}
//
//			public String getViewLabel() {
//				return getViewId();
//			}
//		}));
		
		
	}
	
	
	/*
	 * Name is just the view id...
	 */
	public String getNameOrLabel() {
		return getViewId();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#isVisible(com.windowtester.runtime.IUIContext)
	 */
	public boolean isVisible(IUIContext ui) throws WidgetSearchException {
		return ViewCondition.isVisible(viewHelper).test();
	}
	
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#getAdapter(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		if (adapter == ICloseableLocator.class)
			return new Closer(this);
		return super.getAdapter(adapter);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Condition factories
	//
	//////////////////////////////////////////////////////////////////////////////
	
	
	public ICondition isActive() {
		return ViewCondition.isActive(viewHelper);
	}
	
	public ICondition isActive(boolean expected) {
		Active active = ViewCondition.isActive(viewHelper);
		if (!expected)
			return active.not();
		return active;
	}
	
	public ICondition isDirty() {
		return ViewCondition.isDirty(viewHelper);
	}
	
	public ICondition isDirty(boolean expected) {
		Dirty dirty = ViewCondition.isDirty(viewHelper);
		if (!expected)
			return dirty.not();
		return dirty;
	}

	/**
	 * Create a condition handler that ensures that this view is closed.
	 * 
	 * @since 3.7.1
	 */
	public IConditionHandler isClosed() {
		return ViewClosedConditionHandler.forView(this);
	}

	
	/**
	 * Create a condition handler that ensures that this view is showing.
	 * 
	 * @since 3.8.1
	 */
	public IConditionHandler isShowing() {
		return ViewShowingConditionHandler.forView(this);
	}

	/**
	 * Create a condition handler that ensures that this view is zoomed.
	 * 
	 * @since 4.0.0
	 */
	public IConditionHandler isZoomed() {
		return ViewZoomedConditionHandler.forView(this, viewHelper);
	}
	
	
	/**
	 * Get the associated view reference.
	 * @return the associated view reference (or null) if there is none
	 * @since 3.8.1
	 */
	public IViewDescriptor getDescriptor() {
		return new ViewExplorer().findMatchInRegistry(viewHelper);
	}

	
}
