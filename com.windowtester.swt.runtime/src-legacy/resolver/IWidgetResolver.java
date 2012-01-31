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
package com.windowtester.swt.resolver;

import org.eclipse.swt.widgets.Widget;

/**
 * An interface that defines classes that can be used to resolve widgets.
 * <p>
 * Widget resolvers are associated with name handles (like locators) and are registered 
 * with the runtime via the Widget Resolver Service which
 * is obtained from the UI context using the {@link com.windowtester.swt.resolver.IWidgetResolverService} adapter:
 * <pre>
 * IWidgetResolverService wrs = (IWidgetResolverService)ui.getAdapter(IWidgetResolverService.class);
 * wrs.add("widget.label", new WidgetResolver() {
 *     public boolean matches(Widget w) { ... }
 *     public Widget resolve() { ... }
 * });
 * </pre>
 * <p>
 * Widget resolvers can be managed by widget resolver factories.  Resolver factories
 * implement the {@link com.windowtester.swt.resolver.IWidgetResolverFactory} interface.
 * 
 * 
 * @see com.windowtester.swt.resolver.IWidgetResolverService
 * @see com.windowtester.swt.resolver.IWidgetResolverFactory 
 *
 */
public interface IWidgetResolver {

	/**
	 * Widget resolver factories 
	 * are also the means by which resolvers are made available to the recorder. 
	 * Widget resolver factories are made available to the recorder via the --TBD-- extension point.
	 * (TODO: flesh out recorder story)
	 */
	
	
	/**
	 * Resolve the widget associated with this widget resolver.
	 * @return the associated Widget instance
	 */
	Widget resolve();
	
	/**
	 * Check to see if the given widget matches this resolver's matching criteria.
	 * <p>
	 * Note that this method might be called many times during recording so it's implementation
	 * should be efficient.
	 * @param widget the widget to check
	 * @return true if this widget matches and false otherwise
	 */
	boolean matches(Widget widget);
	
}
