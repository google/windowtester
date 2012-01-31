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
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.internal.PerspectiveBarContributionItem;

import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.internal.runtime.reflect.Reflector;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.runtime.swt.internal.finder.eclipse.PerspectiveFinder;
import com.windowtester.runtime.swt.internal.selector.UIProxy;
import com.windowtester.runtime.swt.locator.eclipse.PerspectiveLocator;


@SuppressWarnings("restriction")
public class PerspectiveInspector {

	
	
	private static class PerspectiveControlDescription extends SemanticWidgetInspectionEvent {

		private static final long serialVersionUID = -2190690350993741795L;
		private String perspectiveName;

		public PerspectiveControlDescription(ILocator partLocator) {
			super(getControlInfo(partLocator));
		}
		
		static EventInfo getControlInfo(ILocator partLocator) {
			EventInfo info = new EventInfo();
			info.hierarchyInfo = new IdentifierAdapter(partLocator);
			return info;
		}
			
		public String toString() {
			return "Perpsective [" + getProperties() + "]";
		}

		public SemanticWidgetInspectionEvent withName(String name) {
			this.perspectiveName = name;
			return this;
		}
		
		public String getDescriptionLabel() {
			if (perspectiveName == null)
				return null; //use default
			return "Perspective (" + perspectiveName +")";
		}
	}
	private static final PerspectiveInspector INSTANCE = new PerspectiveInspector();
		
	public static PerspectiveInspector forPerspectiveControl() {
		return INSTANCE;
	}

	public SemanticWidgetInspectionEvent getDescription(Widget w) {
		Object data = UIProxy.getData(w);
		if (!(data instanceof PerspectiveBarContributionItem))
			return null;
		Object ref = Reflector.forObject(data).invoke("getPerspective");
		if (!(ref instanceof IPerspectiveDescriptor))
			return null;
		IPerspectiveDescriptor perspective = (IPerspectiveDescriptor)ref;
		return new PerspectiveControlDescription(new PerspectiveLocator(perspective.getId())).withName(perspective.getLabel()).withProperties(perspectiveProperties(perspective));
	}

	private PropertySet perspectiveProperties(IPerspectiveDescriptor perspective) {
		return PartProperty.isActive(PerspectiveFinder.isActive(perspective));
	}
	
}
