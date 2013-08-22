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
package com.windowtester.swt.event.recorder.workbench;

import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewReference;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.internal.runtime.reflect.Reflector;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.finder.eclipse.editors.EditorFinder;
import com.windowtester.runtime.swt.internal.finder.eclipse.views.ViewFinder;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.locator.eclipse.EditorLocator;
import com.windowtester.runtime.swt.locator.eclipse.ViewLocator;

/**
 * Inspector for (suspected) part controls.
 */
@SuppressWarnings("restriction")
public class PartInspector {

	
	private static abstract class PartControlDescription extends SemanticWidgetInspectionEvent {

		private static final long serialVersionUID = -2190690350993741795L;

		public PartControlDescription(ILocator partLocator) {
			super(getPartInfo(partLocator));
		}
		
		static EventInfo getPartInfo(ILocator partLocator) {
			EventInfo info = new EventInfo();
			info.hierarchyInfo = new IdentifierAdapter(partLocator);
			return info;
		}
			
		public String toString() {
			return "Part element [" + getProperties() + "]";
		}
	}
	
	private static class EditorControlDescription extends PartControlDescription {

		private static final long serialVersionUID = -3448937080102493781L;
		private final String editorName;

		public EditorControlDescription(String editorName) {
			super(new EditorLocator(editorName));
			this.editorName = editorName;
		}
		public String getDescriptionLabel() {
			return "Editor ("+ editorName + ")";
		}
		
	}

	
	private static class ViewControlDescription extends PartControlDescription {

		private static final long serialVersionUID = -5806035542817006946L;

		private String viewName;
		public ViewControlDescription(String viewId) {
			super(new ViewLocator(viewId));
		}
		
		public ViewControlDescription withName(String viewName) {
			this.viewName = viewName;
			return this;
		}
		public String getDescriptionLabel() {
			return "View ("+ viewName + ")";
		}
		
	}

	public SemanticWidgetInspectionEvent getDescription(Widget w) {
		Object data = UIProxy.getData(w);
//		if (!(data instanceof AbstractTabItem))
//			return null;
//		AbstractTabItem tab = (AbstractTabItem)data;
//		data = tab.getData();
		Object ref = Reflector.forObject(Reflector.forObject(data).invoke("getPane")).invoke("getPartReference");
		if (ref instanceof IEditorReference)
			return getEditorDescription((IEditorReference)ref);
		if (ref instanceof IViewReference)
			return getViewDescription((IViewReference)ref);
		return null;
	}
	
	private static SemanticWidgetInspectionEvent getViewDescription(IViewReference ref) {
		return new ViewControlDescription(ref.getId()).withName(ref.getPartName()).withProperties(viewProperties(ref));
	}

	private static PropertySet viewProperties(IViewReference ref) {
		String id = ref.getId();
		return PartProperty.isActive(ViewFinder.isViewWithIdActive(id))
				.withMapping(PartProperty.IS_DIRTY.withValue(ViewFinder.isViewWithIdDirty(id)))
				.withMapping(PartProperty.IS_VISIBLE.withValue(ViewFinder.isViewWithIdVisible(id)));
	}

	private static SemanticWidgetInspectionEvent getEditorDescription(IEditorReference ref) {
		return new EditorControlDescription(ref.getPartName()).withProperties(editorProperties(ref));
	}

	private static PropertySet editorProperties(IEditorReference ref) {
		return PartProperty.isActive(EditorFinder.isEditorActiveNoRetries(ref))
				.withMapping(PartProperty.IS_DIRTY.withValue(ref.isDirty()))
				.withMapping(PartProperty.IS_VISIBLE.withValue(EditorFinder.isEditorControlVisibleNoRetries(ref)));
	}

	private static final PartInspector INSTANCE = new PartInspector();
	
	public static PartInspector forPartControl() {
		return INSTANCE;
	}
}
