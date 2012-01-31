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
package com.windowtester.eclipse.ui.inspector;

import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

public abstract class AbstractSection implements IInspectorSection {

	protected Section createSection(ScrolledForm form, InspectorFormToolkit toolkit) {
		return createSection(form, toolkit, getSectionStyleBits());
	}

	protected Section createSection(ScrolledForm form, InspectorFormToolkit toolkit, int style) {
		Section section = toolkit.createSection(form.getBody(), style | getSectionStyleBits());
		section.clientVerticalSpacing = 9;
		return section;
	}
	
	public int getSectionStyleBits() {
		return ExpandableComposite.TITLE_BAR |/*|Section.DESCRIPTION|*/
				  Section.TWISTIE | Section.EXPANDED;
	}

	protected FormText createText(InspectorFormToolkit toolkit, Section section) {
		FormText text = toolkit.createFormText(section, true);
		text.setWhitespaceNormalized(true);
		return text;
	}

	protected TableWrapData setLayout(Section section) {
		TableWrapData td = new TableWrapData();
		td.align = TableWrapData.FILL;
		td.grabHorizontal = true;
		section.setLayoutData(td);
		return td;
	}

	protected String quoted(String str) {
		return "\"" + str + "\"";
	}
}
