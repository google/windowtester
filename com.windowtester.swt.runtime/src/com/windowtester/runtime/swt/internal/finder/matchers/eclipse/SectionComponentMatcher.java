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
package com.windowtester.runtime.swt.internal.finder.matchers.eclipse;

import static com.windowtester.runtime.swt.internal.matchers.WidgetMatchers.withText;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.runtime.swt.internal.matchers.ByClassMatcher;
import com.windowtester.runtime.swt.internal.matchers.ComponentOfMatcher;
import com.windowtester.runtime.swt.internal.widgets.ISWTWidgetReference;
import com.windowtester.runtime.swt.locator.SectionLocator;

/**
 * Matcher that matches widgets that are components of a section.
 *
 */
public class SectionComponentMatcher extends ComponentOfMatcher {

	public SectionComponentMatcher() {
		super(classMatcher());
	}

	private static ByClassMatcher classMatcher() {
		return new ByClassMatcher(Section.class.getName());
	}

	public SectionComponentMatcher(String sectionText) {
//		super(CompoundMatcher.create(classMatcher(), TextMatcher.create(sectionText)));
		super(classMatcher().and(withText(sectionText)));
	}
	
	public static SectionComponentMatcher forLocator(SectionLocator locator) {
		return new SectionComponentMatcher(locator.getNameOrLabel());
	}

	
	@Override
	public boolean matches(ISWTWidgetReference<?> ref) {
		if (isUnbounded(ref))
			return false;
		return super.matches(ref);
	}
	
//	/**
//	 * Overriding to prune dups.
//	 * 
//	 * @see com.windowtester.runtime.swt.internal.matcher.ContainedInWidgetMatcher#findAllChildMatches(org.eclipse.swt.widgets.Widget, com.windowtester.runtime.locator.IWidgetMatcher)
//	 */
//	protected Widget[] findAllChildMatches(Widget root, IWidgetMatcher matcher) {
//		
//		Widget[] widgets = super.findAllChildMatches(root, matcher);
//		if (widgets.length  <= 1)
//			return widgets;
//		
//		return pruneBoundlessMatches(widgets);
//	}
//
//	/**
//	 * This is to handle an odd case discovered in the PDE Manifest editor 
//	 * where there are duplicate visible buttons the good news is that 
//	 * one appears to be boundless so we can reject based on a lack of bounds.
//	 * 
//	 * 	Section {}<HC|12714759>
//     *     ...       
//     *    Label {Execution Environments}<HC|24628215>
//     *       ...
//     *        LayoutComposite {}<HC|123928>
//     *         Menu {}<HC|17852216>
//     *  ->        Button {Add...}<HC|6090947>
//     *            ...         
//     *        LayoutComposite {}<HC|13488905>
//     *         Menu {}<HC|17852216>
//     *  ->        Button {Add...}<HC|25799710>
//     *             ...
//     *    
//	 */
//	private Widget[] pruneBoundlessMatches(Widget[] widgets) {
//		List<Widget> pruned = new ArrayList<Widget>();
//		for (int i = 0; i < widgets.length; i++) {
//			final Widget w = widgets[i];
//			if (isUnbounded(w))
//				continue;
//			pruned.add(w);
//		}
//		return pruned.toArray(new Widget[]{});
//	}

	private boolean isUnbounded(final ISWTWidgetReference<?> ref) {
		
		Rectangle bounds = ref.getDisplayBounds();
		
//		Rectangle bounds = (Rectangle) DisplayExec.sync(new RunnableWithResult(){
//			public Object runWithResult() {
//				return SWTWorkarounds.getBounds(w);
//			}			
//		});
		if (bounds == null)
			return true;   //shouldn't happen but being safe
		return (bounds.width == 0 && bounds.height == 0);
	}
	
//	public boolean matches(Widget widget) {
//		if (isUnbounded(widget))
//			return false;
//		return super.matches(widget);
//	}
	
}
