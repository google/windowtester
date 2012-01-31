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
package com.windowtester.runtime.swt.internal.finder.legacy;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import abbot.finder.swt.Matcher;
import abbot.finder.swt.SWTHierarchy;

import com.windowtester.runtime.swt.internal.abbot.matcher.HierarchyMatcher;
import com.windowtester.runtime.swt.internal.abbot.matcher.ShellComponentMatcher;
import com.windowtester.runtime.swt.internal.finder.ShellFinder;
import com.windowtester.runtime.swt.internal.finder.legacy.WidgetFinder.MatchResult;
import com.windowtester.runtime.swt.internal.selector.UIDriver;


/**
 * Used for scoping widget searches by active shell.
 * 
 */
public class SearchScopeHelper {

    private final SWTHierarchy _hierarchy;
    private final WidgetFinder _finder = new WidgetFinder();
    //used for caching current active shell
    //private Shell _activeShell;
    
    
    private static int MAX_SHELL_FIND_RETRIES = 6;
    private static int SHELL_FIND_RETRY_INTERVAL = 500;
    
    
	/**
	 * Create an instance based on the given hierarchy.
	 */
	public SearchScopeHelper(SWTHierarchy hierarchy) {
		_hierarchy = hierarchy;
	}

	/**
     * Find the Shell by which to scope this search.
     */
    public Shell getShellSearchScope(Matcher m) {

    	//if it is explicitly scoped by a shell matcher, use that shell for scope
    	if (m instanceof HierarchyMatcher) {
    		Matcher topMatcher = getTopMatcher((HierarchyMatcher)m);
    		if (topMatcher instanceof ShellComponentMatcher) {
    			final ShellComponentMatcher shellMatcher = (ShellComponentMatcher)topMatcher;
    			MatchResult result = _finder.find(_hierarchy, new Matcher() {
					public boolean matches(Widget w) {
						return shellMatcher.isShellMatch(w);
					}
    			});
    			if (result.getType() == WidgetFinder.MATCH)
    				return (Shell) result.getWidget();
    			//TODO: handle exceptions here
    			return null;
    		}
    	}
    	
    	//if no top level shell matcher is specified, use the active shell
    	return getActiveShell();
  
	}

	private Shell getActiveShell() {
		//rolling back to address regressions on win32
//    	fetchAndSetActiveShell();
//    	validateActiveShell();
//    	return _activeShell;
		
		/*
		 * Rather than return null right away, we will wait a bit for 
		 * the shell to be non-null.  This eases debugging.
		 * 
		 * 
		 * TODO: we should really be delegating to a UIContext but there's no instance handy...
		 */
				
		Shell shell = ShellFinder.getActiveShell(getDisplay());
		
		for(int i =0; shell == null && i < MAX_SHELL_FIND_RETRIES; ++i) {
			UIDriver.pause(SHELL_FIND_RETRY_INTERVAL);
			shell = ShellFinder.getActiveShell(getDisplay());
		}

		return shell;
	}

//	/**
//	 * Get the active shell from the display.
//	 */
//	private void fetchAndSetActiveShell() {
//		final Display display = getDisplay();
//    	display.syncExec(new Runnable() {
//			public void run() {
//				_activeShell = display.getActiveShell();
//			}
//    	});
//    	if (_activeShell == null)
//    		LogHandler.log("fetch of active shell yielded <null> in SearchScopeHelper");
//	}

	
	/**
	 * Get the display.
	 */
	private Display getDisplay() {
		return _hierarchy.getDisplay();
	}
	
//	/**
//	 * Confirm that the active shell is valid.
//	 * Invalid case:
//	 *    - active shell is BEHIND a modal shell that is inactive
//	 *         (despite having event focus)
//	 */
//	private void validateActiveShell() {
//		Shell modalShell = getModalShell();
//		if (modalShell != null && modalShell != _activeShell) {
//			LogHandler.log("(SearchScopeHelper) - active shell: " + toString(_activeShell) + " invalid, updating to current modal shell: " + toString(modalShell));
//			_activeShell = modalShell;
//		}
//	}
	
//	/** 
//	 * Get the string representation of this shell.
//	 */
//	private String toString(Shell shell) {
//		return UIProxy.getToString(shell);
//	}

//	/**
//	 * Get the current modal shell or <code>null</code> if there is none.
//	 */
//	private Shell getModalShell() {
//		return ShellFinder.getModalShell(getDisplay());
//	}
	
	private Matcher getTopMatcher(HierarchyMatcher matcher) {
		Matcher parent = null;
		do {
			parent  = matcher.getParentMatcher(); 
			if (parent instanceof HierarchyMatcher)
				matcher = (HierarchyMatcher) parent;
			//TODO: clean up this logic
		} while (parent != null && parent instanceof HierarchyMatcher);
		return parent;
	}
}
