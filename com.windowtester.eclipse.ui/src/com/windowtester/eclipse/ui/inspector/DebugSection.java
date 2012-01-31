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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import com.windowtester.recorder.event.user.IWidgetDescription;

public class DebugSection extends AbstractSection {

	
	public int getSectionStyleBits() {
		return ExpandableComposite.TITLE_BAR | Section.TWISTIE; //not expanded
	}
	
	public void addTo(IWidgetDescription description, ScrolledForm form,
			InspectorFormToolkit toolkit) {
		Section section = createSection(form, toolkit);
		section.setText("Debug");
		
		FormText text = createText(toolkit, section);
		section.setClient(text);
		
		TableWrapData td = setLayout(section);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("<form>"); //$NON-NLS-1$
		
		
		buffer.append("<li bindent=\"3\" style=\"image\" value=\"debug\">"); //$NON-NLS-1$
		buffer.append("<a href=").append(quoted("debug")).append(">Copy debug info to clipboard.</a>");
		buffer.append("</li>"); //$NON-NLS-1$
		buffer.append("<li bindent=\"3\" style=\"image\" value=\"screen\">"); //$NON-NLS-1$
		buffer.append("<a href=").append(quoted("screen")).append(">Take a screenshot.</a>");
		buffer.append("</li>"); //$NON-NLS-1$
		text.setImage("debug", getDebugIcon()); //$NON-NLS-1$
		text.setImage("screen", getScreenIcon()); //$NON-NLS-1$
		
		buffer.append("</form>"); //$NON-NLS-1$
		text.setText(buffer.toString(), true, false);
		
		text.setLayoutData(td);
		
		text.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				System.out.println("Link clicked: " + e.getHref());
			}
		});
		text.layout();	
	}
	
	private Image getDebugIcon() {
		ImageData data = new ImageData(getClass().getResourceAsStream("debug.gif"));
		return new Image(Display.getDefault(), data);
	}
	private Image getScreenIcon() {
		ImageData data = new ImageData(getClass().getResourceAsStream("screen.gif"));
		return new Image(Display.getDefault(), data);
	}

}
