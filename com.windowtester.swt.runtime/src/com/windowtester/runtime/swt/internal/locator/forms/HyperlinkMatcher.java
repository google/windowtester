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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.internal.matchers.eclipse.EditorComponentMatcher;
import com.windowtester.runtime.swt.internal.matchers.eclipse.ViewComponentMatcher;
import com.windowtester.runtime.swt.internal.widgets.SWTWidgetReference;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;
import com.windowtester.runtime.util.StringComparator;

/**
 * Hyperlink matcher factory.
 *
 */
public class HyperlinkMatcher {

	
	public static IHyperlinkMatcher forText(String text) {
		return new HyperlinkTextMatcher(text);
	}
	
	public static IHyperlinkMatcher forHref(String href) {
		return new HyperlinkHRefMatcher(href);
	}
	
	public static IHyperlinkMatcher forSection(String sectionTitle) {
		return new HyperlinkInSectionMatcher(sectionTitle);
	}

	public static IHyperlinkMatcher forEditor(String editorTitle) {
		return new HyperlinkInEditorMatcher(editorTitle);
	}

	public static IHyperlinkMatcher forView(String viewId) {
		return new HyperlinkInViewMatcher(viewId);
	}
	
	
	public static class HyperlinkHRefMatcher implements IHyperlinkMatcher, Serializable {
		
		private static final long serialVersionUID = 1945493566060584495L;
		private final String href;

		public HyperlinkHRefMatcher(String href) {
			this.href = href;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.test.locator.swt.forms.IHyperlinkMatcher#matches(com.windowtester.test.locator.swt.forms.HyperlinkReference)
		 */
		public boolean matches(IHyperlinkReference link) {
			return link.hasHRef(href);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "has HRef: " + href;
		}
	}
	
	public static class HyperlinkTextMatcher implements IHyperlinkMatcher, Serializable {
		
		private static final long serialVersionUID = -5924134672286135698L;
		
		private final String text;

		public HyperlinkTextMatcher(String text) {
			this.text = text;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.test.locator.swt.forms.IHyperlinkMatcher#matches(com.windowtester.test.locator.swt.forms.HyperlinkReference)
		 */
		public boolean matches(IHyperlinkReference link) {
			return link.hasText(text);
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "has text: " + text;
		}
		
		public String getText() {
			return text;
		}
		
	}

	
	public static class HyperlinkInSectionMatcher implements IHyperlinkMatcher, Serializable {
		
		private static final long serialVersionUID = 5254059108369797417L;

		private final String sectionTitle;
		
		public HyperlinkInSectionMatcher(String sectionTitle) {
			this.sectionTitle = sectionTitle;
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.test.locator.swt.forms.IHyperlinkMatcher#matches(com.windowtester.test.locator.swt.forms.HyperlinkReference)
		 */
		public boolean matches(IHyperlinkReference link) {
			return isInSection(link.getControl());
		}

		private boolean isInSection(Widget w) {
			Section section = SectionFinder.findParentSection(w);
			if (section == null)
				return false;
			return StringComparator.matches(SectionFinder.getText(section), sectionTitle);
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "in section: " + sectionTitle;
		}
	}
	
	public static class HyperlinkInViewMatcher implements IHyperlinkMatcher, Serializable {
		
		private static final long serialVersionUID = 3146398992575265477L;

		private final String viewId;
		private final transient ViewComponentMatcher matcher;
		
		public HyperlinkInViewMatcher(String viewId) {
			this.viewId = viewId;
			this.matcher = new ViewComponentMatcher(viewId);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.test.locator.swt.forms.IHyperlinkMatcher#matches(com.windowtester.test.locator.swt.forms.HyperlinkReference)
		 */
		public boolean matches(IHyperlinkReference link) {
			return matcher.matches(SWTWidgetReference.forWidget(link.getControl()));
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "in view: " + viewId;
		}
	}
	
	public static class HyperlinkInEditorMatcher implements IHyperlinkMatcher, Serializable {
		
		private static final long serialVersionUID = -5383698262970737551L;

		private final String editorName;
		private final EditorComponentMatcher matcher;
		
		public HyperlinkInEditorMatcher(String editorName) {
			this.editorName = editorName;
			this.matcher = new EditorComponentMatcher(editorName);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.test.locator.swt.forms.IHyperlinkMatcher#matches(com.windowtester.test.locator.swt.forms.HyperlinkReference)
		 */
		public boolean matches(IHyperlinkReference link) {
			
			return matcher.matches(SWTWidgetReference.forWidget(link.getControl()));
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "in editor: " + editorName;
		}
	}


	public static final PropertyMapping HAS_HREF   = PropertyMapping.withKey("hasHRef").withName("Has Href");
	
	
	
	public static String toCriteriaString(IHyperlinkMatcher matcher) {
		if (matcher instanceof HyperlinkInSectionMatcher) {
			return ".inSection" + stringParam(((HyperlinkInSectionMatcher)matcher).sectionTitle);
		}
		if (matcher instanceof HyperlinkInEditorMatcher) {
			return ".inEditor" + stringParam(((HyperlinkInEditorMatcher)matcher).editorName);
		}
		if (matcher instanceof HyperlinkInViewMatcher) {
			return ".inView" + stringParam(((HyperlinkInViewMatcher)matcher).viewId);
		}
		if (matcher instanceof HyperlinkHRefMatcher) {
			return ".hasHRef" + stringParam(((HyperlinkHRefMatcher)matcher).href);
		}
		if (matcher instanceof HyperlinkTextMatcher) {
			return ".hasText" + stringParam(((HyperlinkTextMatcher)matcher).text);
		}		
		return "";
	}


	public static List<PropertyMapping> getPropertyMappingsForContext(IHyperlinkMatcher matcher, IUIContext ui) {
		List<PropertyMapping> mappings = new ArrayList<PropertyMapping>();
		if (matcher instanceof HyperlinkHRefMatcher) {
			mappings.add(HAS_HREF.withValue(((HyperlinkHRefMatcher)matcher).href));
		}
		return mappings;
	}
	
	
	private static String stringParam(String arg) {
		return "(\"" + arg + "\")";
	}
	

}
