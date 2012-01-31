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

import java.lang.reflect.Field;

import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.internal.forms.widgets.FormTextModel;
import org.eclipse.ui.internal.forms.widgets.IHyperlinkSegment;

import com.windowtester.runtime.swt.internal.display.DisplayExec;
import com.windowtester.runtime.swt.internal.display.RunnableWithResult;
import com.windowtester.runtime.swt.internal.locator.SWTWidgetReference2;
import com.windowtester.runtime.swt.locator.forms.IHyperlinkReference;

@SuppressWarnings("restriction")
public class FormTextReference extends SWTWidgetReference2 {

	public static FormTextReference forText(FormText formText) {
		return new FormTextReference(formText);
	}
	
	public FormTextReference(FormText widget) {
		super(widget);
	}

	public HyperlinkSegmentReference[] getHyperlinks() {
		final FormText text = (FormText) getWidget();
		return (HyperlinkSegmentReference[]) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				try {
					FormTextModel model = getModel(text);
					int count = model.getHyperlinkCount();
					HyperlinkSegmentReference[] refs = new HyperlinkSegmentReference[count];
					for (int i=0; i < count; ++i) {
						refs[i] = HyperlinkSegmentReference.forSegmentInText(model.getHyperlink(i), text);
					}
					return refs;
					
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new HyperlinkSegmentReference[0];
			}			
		});

		
	}


	public IHyperlinkReference findHyperlinkAt(final int x, final int y) {
		final FormText text = (FormText) getWidget();
		return (HyperlinkSegmentReference) DisplayExec.sync(new RunnableWithResult() {
			public Object runWithResult() {
				try {
					FormTextModel model = getModel(text);
					IHyperlinkSegment link = model.findHyperlinkAt(x, y);
					return HyperlinkSegmentReference.forSegmentInText(link, text);
					
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new HyperlinkSegmentReference[0];
			}			
		});
	}

	private FormTextModel getModel(final FormText text)
			throws NoSuchFieldException, IllegalAccessException {
		Field field = FormText.class.getDeclaredField("model");
		field.setAccessible(true);
		FormTextModel model = (FormTextModel) field.get(text);
		return model;
	}
	
	
	
	

}
