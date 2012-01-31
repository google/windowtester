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
package com.windowtester.runtime.swt.internal.locator.forms;

import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.locator.forms.FormTextReference;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkSegmentCondition;
import com.windowtester.runtime.swt.internal.locator.forms.HyperlinkSegmentLocator;
import com.windowtester.runtime.swt.internal.locator.forms.SectionFinder;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.runtime.swt.locator.SectionLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkLocator.IHyperlinkCondition;
import com.windowtester.runtime.util.StringComparator;


public class FormTextLocator extends SWTWidgetLocator {

	public static interface IHyperlinkConditionSpecifier {
		IHyperlinkCondition withText(String text);
		IHyperlinkCondition withHRef(String href);	
	}
	
	public static interface IHyperlinkLocatorSpecifier {
		IHyperlinkLocator withText(String text);
		IHyperlinkLocator withHRef(String href);	
	}
	
//	public static interface IHyperlinkCondition extends IUICondition {
//		IHyperlinkCondition withText(String text);
//		IHyperlinkCondition withHRef(String href);	
//	}
	
	private static final long serialVersionUID = -4032446577404997676L;
	private final SectionLocator section;
	private final ViewLocator view;

	public FormTextLocator() {
		this(null); 
	}

	public FormTextLocator(SectionLocator section) {
		this(section, null);
	}

	public FormTextLocator(SectionLocator section, ViewLocator view) {
		super(FormText.class, section);
		this.section = section;
		this.view = view;
	}

	public IHyperlinkConditionSpecifier hasHyperlink() {
		return HyperlinkSegmentCondition.forFormText(this);
	}

	public IHyperlinkLocatorSpecifier hyperlink() {
		return HyperlinkSegmentLocator.forFormText(this);
	}
	
	/* (non-Javadoc)
	 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
	 */
	public IWidgetLocator[] findAll(IUIContext ui) {
		IWidgetLocator[] found = super.findAll(ui);
		FormTextReference[] adaptedRefs = new FormTextReference[found.length];
		for (int i = 0; i < found.length; i++) {
			adaptedRefs[i] = FormTextReference.forText((FormText)((IWidgetReference)found[i]).getWidget());
		}
		return adaptedRefs;
	}


	/* (non-Javadoc)
	 * @see com.windowtester.runtime.swt.locator.SWTWidgetLocator#matches(java.lang.Object)
	 */
	public boolean matches(Object widget) {
		//adaptation from new runtime expectations
		if (widget instanceof IWidgetReference)
			widget = ((IWidgetReference)widget).getWidget();
		
		if (!(widget instanceof FormText))
			return false;
		FormText text = (FormText)widget;
		return parentSectionMatches(text);
	}

	private boolean parentSectionMatches(FormText text) {
		if (parentSectionIsUnspecified())
			return true;
		final Section parentSection = SectionFinder.findParentSection(text);
		if (parentSection == null)
			return false;
		if (!parentSectionIsInViewScope(parentSection))
			return false;
		String sectionTitle = (String) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				return parentSection.getText();
			}			
		});
		return StringComparator.matches(sectionTitle, section.getNameOrLabel());
	}

	private boolean parentSectionIsInViewScope(Section parentSection) {
		if (view == null)
			return true;
		return view.matches(parentSection);
	}

	private boolean parentSectionIsUnspecified() {
		return section == null;
	}
	
	
	
}
