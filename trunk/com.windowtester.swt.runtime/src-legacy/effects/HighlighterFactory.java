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
package com.windowtester.runtime.swt.internal.effects;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.runtime.swt.internal.selector.HighlightingDriver;
import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;

/**
 * @author pq
 */
public class HighlighterFactory {

	/**
	 * Create a highlighter instance for applying highlights to this type of widget.
	 * @param w - the widget to highlight
	 * @param settings - settings used to perform the highlighting 
	 * @return a highlighter instance
	 */
	public static IHighlighter create(Widget w, PlaybackSettings settings) {
		if (w instanceof Button)
			return new ButtonHighlighter((Button)w, settings);
		if (w instanceof Text)
			return new TextHighlighter((Text)w, settings);
		if (w instanceof StyledText)
			return new StyledTextHighlighter((StyledText)w, settings);
//		if (w instanceof Tree)
//			return new TreeHighlighter((Tree)w);		
		if (w instanceof TreeItem)
			return new TreeItemHighlighter((TreeItem)w, settings);
		
		//fall through case
		return new NoOpHighlighter(w);
	}
	
	
	
	static final class NoOpHighlighter implements IHighlighter {

		private Widget _widget;

		/**
		 * Create an instance.
		 * @param w
		 */
		public NoOpHighlighter(Widget widget) {
			_widget = widget;
		}

		public void doPaint(HighlightingDriver driver) {
			String desc = (_widget == null) ? null : _widget.getClass().toString();
			TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "<highlight doPaint called on a no-op highlighter -- typed: " + desc +">");				
		}
	}
	
	
	
}
