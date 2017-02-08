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

import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;

import com.windowtester.runtime.swt.internal.finder.eclipse.WorkbenchFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder.IViewMatcher;

/**
 * A helper for navigating and exploring views.
 */
public class ViewExplorer {

    public void spelunk() {
        IViewDescriptor[] views = getViews();
        for (int i = 0; i < views.length; i++) {
            IViewDescriptor view = views[i];
            // System.out.println(Arrays.toString(view.getCategoryPath()));
            System.out.println(view);
            System.out.println(view.getLabel());
            System.out.println(findCategoryPath(view));
            System.out.println("----");
        }

    }

    private IViewDescriptor[] getViews() {
        return getViewRegistry().getViews();
    }

    public String findCategory(String viewName) {
        return findCategoryPath(findView(viewName));

    }

    public String findCategoryPath(IViewDescriptor view) {
        if (view == null) {
            return null;
        }
        IViewCategory[] categories = getViewRegistry().getCategories();
        for (int i = 0; i < categories.length; i++) {
            IViewCategory category = categories[i];
            IViewDescriptor[] views = category.getViews();
            for (int j = 0; j < views.length; j++) {
                IViewDescriptor candidateView = views[j];
                if (view == candidateView) {
                    return category.getLabel();
                }
            }
        }
        // TODO Auto-generated method stub
        return null;
    }

    private IViewRegistry getViewRegistry() {
        return WorkbenchFinder.getWorkbench().getViewRegistry();
    }

    public IViewDescriptor findView(String name) {
        if (name == null) {
            return null;
        }
        IViewDescriptor[] views = getViews();
        for (int i = 0; i < views.length; i++) {
            IViewDescriptor view = views[i];
            String label = view.getLabel();
            if (label == null) {
                continue;
            }
            if (name.equals(view.getLabel())) {
                return view;
            }
        }
        return null;
    }

    public IViewDescriptor findMatchInRegistry(IViewMatcher matcher) {
        IViewDescriptor[] views = getViews();
        for (int i = 0; i < views.length; i++) {
            IViewDescriptor view = views[i];
            if (matcher.matches(view)) {
                return view;
            }
        }
        return null;
    }

}
