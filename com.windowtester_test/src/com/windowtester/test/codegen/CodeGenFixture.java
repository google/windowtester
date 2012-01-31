package com.windowtester.test.codegen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Widget;

import com.windowtester.codegen.SourceStringBuilder;
import com.windowtester.codegen.TestCaseBuilder;
import com.windowtester.codegen.eventstream.EventStream;
import com.windowtester.codegen.eventstream.IEventStream;
import com.windowtester.internal.runtime.IWidgetIdentifier;
import com.windowtester.internal.runtime.PropertySet;
import com.windowtester.internal.runtime.locator.IdentifierAdapter;
import com.windowtester.recorder.event.user.SemanticKeyDownEvent;
import com.windowtester.recorder.event.user.SemanticWidgetClosedEvent;
import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.recorder.event.user.SemanticWidgetSelectionEvent;
import com.windowtester.recorder.event.user.UISemanticEvent.EventInfo;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.locator.ILocator;
import com.windowtester.ui.internal.corel.model.Event;

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
public class CodeGenFixture {

	

	public static final String TEST_PKG = "test";
	public static final String TEST_NAME = "Test";
	
	public static TestCaseBuilder builder() {
		return new TestCaseBuilder(TEST_NAME, TEST_PKG) {
			@Override
			protected SourceStringBuilder getStringBuilder() {
				return new SourceStringBuilder(this) {
					@Override
					public String format(String source) {
						return source; //don't format (to simplify string compare)
					}
				};
			}
		};
	}

	
	public static EventInfo mockEvent(IWidgetIdentifier wl) {
		return mockEvent(Widget.class /* likely ignored */, wl);
	}
	
	public static EventInfo mockEvent(Class<?> cls, IWidgetIdentifier wl) {
		EventInfo info     = new EventInfo();
		info.cls           = cls.getName();
		info.button        = WT.BUTTON1;
		info.hierarchyInfo = wl;
		return info;
	}

	public static SemanticWidgetSelectionEvent mockSelect(Class<?> cls, IWidgetIdentifier wl) {
		return new SemanticWidgetSelectionEvent(mockEvent(cls, wl));
	}

	public static SemanticWidgetClosedEvent mockClose(Class<?> cls, IWidgetIdentifier wl) {
		return new SemanticWidgetClosedEvent(mockEvent(cls, wl));
	}
	
	public static SemanticWidgetInspectionEvent mockAssert(Class<?> cls, IWidgetIdentifier wl) {
		return new SemanticWidgetInspectionEvent(mockEvent(cls, wl));
	}
	
	public static Event fakeSelectEvent(Class<?> cls, IWidgetIdentifier locator) {
		return new Event(mockSelect(cls, locator));
	}

	public static Event fakeCloseEvent(Class<?> cls, IWidgetIdentifier locator) {
		return new Event(mockClose(cls, locator));
	}
	
	public static Event fakeAssertEvent(Class<?> cls, IWidgetIdentifier locator, PropertySet properties) {
		return new Event(mockAssert(cls, locator).withProperties(properties));
	}
	
	public static Event fakeKeyEntry(char key) {
		SemanticKeyDownEvent keyDown = new SemanticKeyDownEvent(new EventInfo());
		keyDown.setKey(key);
		return new Event(keyDown);
	}
		
	public static EventInfo mockEvent(ILocator locator) {
		return mockEvent(adaptToIdentifier(locator));
		
	}
	
	public static IWidgetIdentifier adaptToIdentifier(ILocator locator) {
		IWidgetIdentifier identifier = null;
		if (locator instanceof IWidgetIdentifier){
			identifier = (IWidgetIdentifier)locator;
		} else  {
			identifier = new IdentifierAdapter(locator);
		}
		return identifier;
	}
	
	public static IEventStream stream(Event ... events) {
		List<Event> es = new ArrayList<Event>();
		for (Event event : events) {
			es.add(event);
		}
		return new EventStream(es);
	}
	
}
