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
package com.windowtester.runtime.swt.internal.selector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import abbot.tester.swt.WidgetLocator;

import com.windowtester.internal.debug.IRuntimePluginTraceOptions;
import com.windowtester.internal.debug.TraceHandler;
import com.windowtester.internal.runtime.Platform;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.swt.internal.effects.HighlighterFactory;
import com.windowtester.runtime.swt.internal.effects.IHighlighter;
import com.windowtester.runtime.swt.internal.effects.LineIteratorUtil;
import com.windowtester.runtime.swt.internal.effects.MenuItemHighlightingSelector;
import com.windowtester.runtime.swt.internal.preferences.PlaybackSettings;
import com.windowtester.swt.RuntimePlugin;

/**
 * A decorator that wraps calls to drive UI events in highlighting actions.
 * 
 * @author Phil Quitslund
 *
 */
public class HighlightingDriver extends UIDriver {

	
	//pq: removing presentation bits	
//	private PresentationContext pc;
//	
	/**
	 * Create an instance (using the default settings).
	 */
	public HighlightingDriver() {
		//this(RuntimePlugin.getDefault().getPlaybackSettings());
		try {
			if (Platform.isRunning())
				_settings = RuntimePlugin.getDefault().getPlaybackSettings();
		} catch (Throwable t) {
			//ignore: if an exception occurs we will properly setup settings below
			//TODO: this is NOT a clean way to do this! wee _should_ clean it up!
		}
		if (_settings == null)
			_settings = PlaybackSettings.loadFromFile();
//		pc = getPresentationContext();
//		pc.setDefaultShowNoteDuration(5000);
	}
	
	/**
	 * Create an instance.
	 * @param settings
	 */
	public HighlightingDriver(PlaybackSettings settings) {
		_settings = settings;
//		pc = getPresentationContext();
	}

	
	///////////////////////////////////////////////////////////////////////////////////
	//
	// Decorated selection actions.
	//
	///////////////////////////////////////////////////////////////////////////////////	

	
	
	/**
	 * @see com.windowtester.event.swt.UIDriver#click(org.eclipse.swt.widgets.Widget, int, int, int)
	 */
	public Widget click(Widget w, int x, int y, int mask) {
	
		//first move the mouse to the point of interest:
		mouseMove(w); 
		
		//do highlighting
		highlight(w);
		
		//click
		super.click(w, x, y, mask);
		
		//pause
		postClickPause();
		
		return w;
	}

	public void postClickPause() {
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
	}
	

	
	
	private Point pointT;
	private Point cursorT;
	public void mouseMove(final Widget w, int x, int y) {
		
		int delay = _settings.getMouseMoveDelay();
		
		if (!_settings.getDelayOn() || delay == 0) {
			TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "delay off or 0 so not slowing down mouse move");
			super.mouseMove(w, x, y);
			return;
		}	
		
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "calling overriden mouseMove with delay: " + delay);

		w.getDisplay().syncExec(new Runnable(){
			public void run(){
				pointT = WidgetLocator.getLocation(w);		
			}
		});

		w.getDisplay().syncExec(new Runnable(){
			public void run(){
				cursorT = w.getDisplay().getCursorLocation();		
			}
		});
		
		Point cursorLocation = cursorT;
		Point target = new Point(pointT.x+x,pointT.y+y);
		
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "source=" + cursorLocation);
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "target=" + target);
		
		
		Point[] path = LineIteratorUtil.getPath(cursorLocation, target);
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "...starting move");
		for (int i = 0; i < path.length; i++) {
			super.mouseMove(path[i].x, path[i].y);
			pause(delay);
		}
	
	}
	
	
	
	/**
	 * @see com.windowtester.event.swt.UIDriver#enterText(java.lang.String)
	 */
	public void enterText(String str) {
		
		Display d = Display.getDefault();
		Control control = UIProxy.getFocusControl(d);
		showDefaultNote(control, str);
		
		//if it's disabled or set to zero, don't bother decorating
		if (!_settings.getDelayOn() || _settings.getKeyClickDelay() == 0) {
			super.enterText(str);
			return;
		}
		
		TraceHandler.trace(IRuntimePluginTraceOptions.WIDGET_SELECTION, "entering text with delay: " + _settings.getKeyClickDelay());
		
		//enter text one char at a time, pausing as we go
		for (int i=0; i <= str.length()-1; ++i) {
			keyClick(str.charAt(i));
		}
	}
	
//	/**
//	 * @see com.windowtester.event.swt.UIDriver#clickExpand(org.eclipse.swt.widgets.Widget)
//	 */
//	public Widget clickExpand(Widget w) {
//		//do highlighting
//		highlight(w);
//		//click
//		return super.clickExpand(w);
//	}
	
	/**
	 * @see com.windowtester.event.swt.UIDriver#click(org.eclipse.swt.widgets.Widget, java.lang.String)
	 */
	public Widget click(Widget w, String itemLabelOrPath) throws WidgetNotFoundException, MultipleWidgetsFoundException {
		
		/**
		 * This is a hack and a kludge but menu highlighting is more invasive and requires
		 * input at more points than before and after...
		 * 
		 * In the future this should be broken out and handled more uniformly.
		 * 
		 */
		if (w instanceof MenuItem) {
			return new MenuItemHighlightingSelector(_settings).click(w, itemLabelOrPath);
		}
		
		//highlight root
		highlight(w);
		
		//show automatic bubble note
		showDefaultNote(w, itemLabelOrPath);
		
		//click
		Widget clicked = super.click(w, itemLabelOrPath);
		//highlight node
		highlight(clicked);
		return clicked;
	}

	/**
	 * @throws MultipleWidgetsFoundException 
	 * @throws WidgetNotFoundException 
	 * @see com.windowtester.event.swt.UIDriver#click(org.eclipse.swt.widgets.Widget, java.lang.String, int)
	 */
	public Widget click(Widget w, String itemLabelOrPath, int mask) throws WidgetNotFoundException, MultipleWidgetsFoundException {		
		// show automatic bubble note
		showDefaultNote(w, itemLabelOrPath);
		
		//highlight root
		highlight(w);
		//click
		Widget clicked = super.click(w, itemLabelOrPath, mask);
		//highlight node
		highlight(clicked);
		return clicked;
	}
	
	/**
	 * Click and pause.
	 * @see com.windowtester.event.swt.UIDriver#keyClick(char)
	 */
	public void keyClick(char key) {
		super.keyClick(key);
		pause(_settings.getKeyClickDelay());
	}
	
	/**
 	 * Click and pause.
	 * @see com.windowtester.event.swt.UIDriver#keyClick(int, char)
	 */
	public void keyClick(int ctrl, char c) {
		super.keyClick(ctrl, c);
		pause(_settings.getKeyClickDelay());
	}
	
	/**
	 * Click and pause.
	 * @see com.windowtester.event.swt.UIDriver#keyClick(int)
	 */
	public void keyClick(int key) {
		super.keyClick(key);
		pause(_settings.getKeyClickDelay());
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////
	//
	// Utilities.
	//
	///////////////////////////////////////////////////////////////////////////////////	

	/**
	 * Do the highlighting (if enabled).
	 * @param w - the widget to highlight
	 */
	public void highlight(Widget w) {
		if (_settings.getHighlightingOn()) {
			IHighlighter highlighter = HighlighterFactory.create(w, _settings);
			highlighter.doPaint(this);
		}
	}

	/**
	 * Pause for highlighting.
	 */
	public void highlightPause() {
		pause(_settings.getHighlightDuration());
	}
	
//	protected PresentationContext getPresentationContext(){
//		PresentationContext pc = new SwtPresentationContext(null, Display.getDefault());
//		return pc;
//	}
	
//	private String wrapForNote(String str) {
//		String adjustText = "Enter text \'";
//		if (str.length()>20)
//			adjustText += "\'" + str.substring(0, 20) + "...\'";
//		else
//			adjustText += "\'" + str + "\'";
//		return adjustText;
//	}

	/* (non-Javadoc)
	 * @see com.windowtester.event.swt.UIDriver#click(org.eclipse.swt.widgets.Widget)
	 */
	public synchronized Widget click(Widget w) {
		showDefaultNote(w, null);
		return super.click(w);
	}
	
	private void showDefaultNote(Widget w, String originalText){
		//pq: removing presentation bits
		
//		if(!pc.isShowNotesAutomatically())
//			return;
//		
//		String text = null;
//		if(w instanceof Text){
//			String label = getSiblingLabel((Text)w);
//			if(label!=null)
//				text = wrapForNote(originalText) + " into '"+label+"' field";
//			else
//				text = wrapForNote(originalText);
//		}
//		if(w instanceof Button){
//			text = "Click '"+UIProxy.getText((Button)w)+"' button";
//		}
//		if(w instanceof TreeItem){
//			text = "Select '"+originalText+"'";
//		}
//		if(w instanceof Tree){
//			text = "Select '"+originalText+"' in the tree";
//		}
//		if(w instanceof List){
//			text = "Select '"+originalText+"' in the list";
//		}
//		if(w instanceof Combo){
//			String label = getSiblingLabel((Combo)w);
//			if(label!=null)
//				text = "Select '"+originalText+"' in '"+label+"'.";
//			else
//				text = "Select '"+originalText;
//		}
//		
//		if(text!=null)
//			pc.showNote(text, UIProxy.getBounds(w), IPresentationContext.STYLE_NORMAL, pc.getDefaultShowNoteDuration());
	}

//	private String getSiblingLabel(final Control c) {		
//		final Display d = Display.getDefault();
//		RunnableWithResult r = new RunnableWithResult() {
//			public Object runWithResult() {
//				Control[] children = c.getParent().getChildren();
//				if(children.length<=1)
//					return null;
//				Control tmp = children[0];
//				for (int i = 1; i < children.length; i++) {
//					Control control = children[i];
//					if((tmp instanceof Label)&&(control==c)){
//						return ((Label)tmp).getText();
//					}
//					tmp = control;
//				}
//				return null;
//			}
//		};
//		d.syncExec(r);
//		return (String)r.getResult();
//	}

}
