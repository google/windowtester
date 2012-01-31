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
package com.windowtester.eclipse.ui.assertions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.windowtester.eclipse.ui.inspector.InspectorFormToolkit;
import com.windowtester.internal.runtime.PropertySet.PropertyMapping;
import com.windowtester.recorder.event.user.IWidgetDescription;


/**
 * Properties Section.
 */
public class AssertablesSection /* extends AbstractSection */ {

	public static interface IListener {
		void stateChanged(Button[] buttons);
	}
	
	
	private Section section;
	private Composite composite;

	private final List assertables = new ArrayList();
	private final List listeners   = new ArrayList();

	
	private IWidgetDescription widget;
	
	public AssertablesSection addTo(IWidgetDescription widget, ScrolledForm form, InspectorFormToolkit toolkit) {
		createSection(form, toolkit);
		createAssertablesComposite(toolkit);
		createAssertables(widget, toolkit);
		return this;
	}

	
	private void createAssertables(IWidgetDescription widget, InspectorFormToolkit toolkit) {
		this.widget = widget;
		
		PropertyMapping[] properties = widget.getProperties().toArray();
		for (int i = 0; i < properties.length; i++) {
			PropertyMapping property = properties[i];
			addAssertable(property, toolkit);
		}		
	}


	private void addAssertable(PropertyMapping property, InspectorFormToolkit toolkit) {
		assertables.add(buildButton(property, toolkit));
	}


	private Button buildButton(PropertyMapping property, InspectorFormToolkit toolkit) {
		Button button = toolkit.createButton(composite, getPropertyDescription(property), SWT.CHECK);
		button.setData(property);
		button.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				fireStateChanged();
			}
			public void widgetSelected(SelectionEvent e) {
				fireStateChanged();
			}
		});
		return button;
	}

	protected void fireStateChanged() {
		for (Iterator iterator = listeners.iterator(); iterator.hasNext();) {
			IListener listener = (IListener) iterator.next();
			listener.stateChanged((Button[]) assertables.toArray(new Button[]{}));
		}
	}


	private String getPropertyDescription(PropertyMapping property) {
		String value = property.getValue();
		String detail = "";
		if (property.isBoolean()) {
			if ("false".equals(value))
				detail = " (false)";
		} else {
			detail = " \"" + value +'"';
		}
		
		return property.getKey() + detail;
	}
	
	private void createAssertablesComposite(InspectorFormToolkit toolkit) {
		composite = toolkit.createComposite(section, SWT.NONE);
		composite.setLayout(new GridLayout());
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
	}

	private void createSection(ScrolledForm form,
			InspectorFormToolkit toolkit) {
		section = toolkit.createSection(form.getBody(), Section.TITLE_BAR | Section.DESCRIPTION /*| Section.EXPANDED | Section.TWISTIE*/);
		section.setText("Properties");
		section.setDescription("Select properties to assert.");
		section.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
	}


	public void apply() {
		for (Iterator iterator = assertables.iterator(); iterator.hasNext();) {
			Button button = (Button) iterator.next();
			if (!button.getSelection())
				continue;
			PropertyMapping property = (PropertyMapping) button.getData();
			//System.out.println("flagging: " + property);
			widget.getProperties().flag(property);
		}
	}


	public void addListener(IListener listener) {
		listeners.add(listener);
	}

	
	
}
