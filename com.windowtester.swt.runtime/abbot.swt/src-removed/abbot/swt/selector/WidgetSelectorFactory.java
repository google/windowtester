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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.selector.BasicWidgetSelector;
import com.windowtester.runtime.swt.internal.selector.CanvasSelector;
import com.windowtester.runtime.swt.internal.selector.ComboSelector;
import com.windowtester.runtime.swt.internal.selector.ListSelector;
import com.windowtester.runtime.swt.internal.selector.MenuItemSelector;
import com.windowtester.runtime.swt.internal.selector.SWTWidgetSelectorAdapter;
import com.windowtester.runtime.swt.internal.selector.StyledTextSelector;
import com.windowtester.runtime.swt.internal.selector.TextSelector;
import com.windowtester.runtime.swt.internal.selector.ToolItemSelector;
import com.windowtester.runtime.swt.internal.selector.TreeItemSelector;
import com.windowtester.internal.runtime.selector.WidgetSelectorService;

/**
 * 
 * A factory that produces the appropriate widget selection strategy based
 * on the class of the target widget.
 * 
 * @author Phil Quitslund
 *
 */
public class WidgetSelectorFactory {

//	private static final String WIDGET_SELECTOR_EXTENSION = "com.windowtester.swt.runtime.widgetSelector";

	/**
	 * Get the widget selector appropriate for this widget type.
	 * @param w - the widget in question
	 * @return an appropriate ISWTWidgetSelectorDelegate
	 */
	public static ISWTWidgetSelectorDelegate get(Widget w) {
		return get(w.getClass());
	}

	/**
	 * Get the widget selector appropriate for this widget class.
	 * @param cls- the widget class in question
	 * @return an appropriate ISWTWidgetSelectorDelegate
	 */
	private static ISWTWidgetSelectorDelegate get(Class cls) {
		
		ISWTWidgetSelectorDelegate registered = checkRegistry(cls);
		if (registered != null)
			return registered;
		
		if (cls == Canvas.class)
			return new CanvasSelector();
		if (cls == Combo.class)
			return new ComboSelector();
		if (cls == List.class)
			return new ListSelector();
		if (cls == TreeItem.class)
			return new TreeItemSelector();
		if (cls == Tree.class)
			return new TreeItemSelector();
		if (cls == ToolItem.class)
			return new ToolItemSelector();
		if (cls == Text.class)
			return new TextSelector();
		if (cls == StyledText.class)
			return new StyledTextSelector();
		if (cls == MenuItem.class)
			return new MenuItemSelector();
		if (cls == CCombo.class)
			return new CComboSelector();
		if (cls == TableItem.class) 
			return new TableItemSelector();
		//fall through
		return getBasic();
	}

//	private static ISWTWidgetSelectorDelegate checkRegistry(Class cls) {
//		java.util.List selectors = new ArrayList();
//		try {
//			selectors = getRegisteredSelectorPairs(selectors);
//		} catch (CoreException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		for (Iterator iter = selectors.iterator(); iter.hasNext();) {
//			WidgetSelectorPair pair = (WidgetSelectorPair) iter.next();
//			if (pair.widget == cls)
//				return pair.selector;
//		}
//	
//		return null;
//	}

//	private static java.util.List getRegisteredSelectorPairs(java.util.List selectors) throws CoreException {
//		IExtensionRegistry registry = Platform.getExtensionRegistry();
//		if (registry == null)
//			return selectors; //not being run in Platform...
//		IExtensionPoint point = registry.getExtensionPoint(WIDGET_SELECTOR_EXTENSION);
//		if (point == null) 
//			return selectors;
//		IExtension[] extensions = point.getExtensions();
//		for (int i = 0; i < extensions.length; i++)
//			parseExtension(extensions[i], selectors); 
//		return selectors;
//	}
//
//	private static void parseExtension(IExtension extension, java.util.List pairs) throws CoreException {
//		IConfigurationElement[] elements = extension.getConfigurationElements();
//		for (int i = 0; i < elements.length; i++) {
//			IConfigurationElement element = elements[i];
//			Class widgetClass                   = (Class)element.createExecutableExtension("widgetClass");
//			ISWTWidgetSelectorDelegate selector    = (ISWTWidgetSelectorDelegate)element.createExecutableExtension("selectorDelegate");
//			pairs.add(new WidgetSelectorPair(widgetClass, selector));
//		}
//		
//		
//	}

	private static ISWTWidgetSelectorDelegate checkRegistry(Class cls) {
		com.windowtester.runtime.IWidgetSelectorDelegate delegate = WidgetSelectorService.getInstance().get(cls);
		if (delegate != null)
			return new SWTWidgetSelectorAdapter(delegate);
		return null;
	}

	/**
	 * Get the default (basic) widget selector.
	 * @return a default widget selector.
	 */
	public static ISWTWidgetSelectorDelegate getBasic() {
		return new BasicWidgetSelector();
	}
	
//	static class WidgetSelectorPair {	
//		public WidgetSelectorPair(Class cls, ISWTWidgetSelectorDelegate s) {
//			widget   = cls;
//			selector = s;
//		}
//		Class widget;
//		ISWTWidgetSelectorDelegate selector;
//	}
//	
	
}
