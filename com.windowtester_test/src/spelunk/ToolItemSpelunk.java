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
package spelunk;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.condition.shell.ShellShowingCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.TreeItemLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;


/**
 *
 * @author Phil Quitslund
 *
 */
public class ToolItemSpelunk extends UITestCaseSWT {
	
	private static final String PROJECT_EXPLORER_VIEW_CATEGORY = "(Simple|General)";
	private static final String PROJECT_EXPLORER_VIEW_NAME = "Project Explorer";
	
	protected void setUp() throws Exception {
		closeWelcomePageIfNecessary();
	}
	
	protected void closeWelcomePageIfNecessary() throws Exception {
//		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
//		if (welcomeTab.length == 0)
//			return;
//		getUI().close(welcomeTab[0]);	
		getUI().ensureThat(view("Welcome").isClosed());
	}
	
	public void testSpelunk() throws Exception {
		openProjectExplorer();
		spelunk();
	}

	
	private void spelunk() {
		IUIContext ui = getUI();
		IWidgetLocator[] matches = ui.findAll(new SWTWidgetLocator(ToolItem.class) {
			private static final long serialVersionUID = 1L;
			
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#buildMatcher()
			 */
			@Override
			protected ISWTWidgetMatcher buildMatcher() {
				return new ToolItemMatcher();
			}		
		});
		
		for (IWidgetLocator loc : matches) {
			dump((IWidgetReference)loc);
		}
	}
		
	private void dump(final IWidgetReference ref) {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
        		System.out.println(ref.getWidget());
            }                            
        });
	}

	class ToolItemMatcher implements ISWTWidgetMatcher {

		public boolean matches(ISWTWidgetReference<?> ref) {
			Object widget = ref.getWidget();
		    if (!(widget instanceof ToolItem))
	            return false;
	        final ToolItem item = (ToolItem)widget;
	        final boolean[] result = new boolean[1];
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
	                result[0] = satisfiesCriteria(item);
	            }                            
	        });
	        return result[0];
	    }

		protected boolean satisfiesCriteria(ToolItem item) {
			//TODO: insert proper test here... for now just display the data for investigative purposes.
			System.out.println(item.getData());
			Object data = item.getData();
			//perhaps we can identify the item based on the backing action?
			if (!(data instanceof ActionContributionItem))
				return false;
			ActionContributionItem actionContrib = (ActionContributionItem)data;
			System.out.println("action: " + actionContrib.getAction());
			return true;
		}
	}
	
	
	void openProjectExplorer() throws WidgetSearchException {
		openView(PROJECT_EXPLORER_VIEW_CATEGORY + "/" + PROJECT_EXPLORER_VIEW_NAME);
        getUI().wait(ViewLocator.forName(PROJECT_EXPLORER_VIEW_NAME).isVisible());
	}
	
	void openView(String viewPath) throws WidgetSearchException {
    	IUIContext ui = getUI();
        ui.click(new MenuItemLocator("&Window/Show &View/&Other.*")); //3.* safe path
        ui.wait(new ShellShowingCondition("Show View"));
        ui.click(new TreeItemLocator(viewPath));
        ui.click(new ButtonLocator("OK"));
    }

	
}
