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
package com.windowtester.swt.event.spy;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.recorder.event.user.SemanticWidgetInspectionEvent;
import com.windowtester.runtime.swt.internal.locator.jface.DialogFinder;
import com.windowtester.swt.event.recorder.jface.WizardInspector;
import com.windowtester.swt.event.recorder.workbench.PartInspector;
import com.windowtester.swt.event.recorder.workbench.PerspectiveInspector;

/**
 * Helper for creating inspection events that correspond to a widget's semantic
 * container (e.g., wizard page, workbench view, editor, etc.).
 *
 */
public class SemanticContainerDescription {

	
	public static SemanticWidgetInspectionEvent forWidgetInShell(Widget w, Shell shell) {
		SemanticWidgetInspectionEvent d = getWizardDialogElementDescription(w, shell);
		if (d != null)
			return d;
		d = getPartDescription(w);
		if (d != null)
			return d;
		d = getPerspectiveDescription(w);
		if (d != null)
			return d;
		
		return null;
	}
	
	
	
	
	private static SemanticWidgetInspectionEvent getPerspectiveDescription(Widget w) {
		SemanticWidgetInspectionEvent description = PerspectiveInspector.forPerspectiveControl().getDescription(w);
		if (description == null)
			return null;
		return description.withWidgetHash(w.hashCode()).atHoverPoint(getCursorPosition());
	}




	private static SemanticWidgetInspectionEvent getPartDescription(Widget w) {
		SemanticWidgetInspectionEvent description = PartInspector.forPartControl().getDescription(w);
		if (description == null)
			return null;
		return description.withWidgetHash(w.hashCode()).atHoverPoint(getCursorPosition());
	}


	private static SemanticWidgetInspectionEvent getWizardDialogElementDescription(Widget w, Shell currentShell) {
		Dialog dialog = DialogFinder.toDialog(currentShell);
		if (dialog == null)
			return null;
		SemanticWidgetInspectionEvent description = WizardInspector.forDialog(dialog).getDescription(w);
		if (description == null)
			return null;
		return description.withWidgetHash(w.hashCode()).atHoverPoint(getCursorPosition());
	}
	
	
	public static Point getCursorPosition() {
		return Display.getDefault().getCursorLocation();
	}

}
