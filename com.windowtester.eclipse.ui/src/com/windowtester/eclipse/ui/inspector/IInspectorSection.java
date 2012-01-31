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

import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.windowtester.recorder.event.user.IWidgetDescription;

public interface IInspectorSection {

	void addTo(IWidgetDescription description, ScrolledForm form, InspectorFormToolkit toolkit);

}
