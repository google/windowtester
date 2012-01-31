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
package com.windowtester.internal.swing;

import java.lang.reflect.*;
import java.awt.Component;
import java.awt.Point;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTable;

import abbot.tester.ActionFailedException;
import abbot.tester.ComponentMissingException;
import abbot.util.AWT;

import com.windowtester.internal.runtime.Platform;
import com.windowtester.internal.runtime.RuntimePlugin;
import com.windowtester.internal.runtime.preferences.PlaybackSettings;


public class DelayUIDriverSwing extends UIDriverSwing {
	
	private static boolean printMessage = false;
	
	/**
	 * Create an instance (using the default settings).
	 */
	public DelayUIDriverSwing() {
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
	//	pc = getPresentationContext();
	//	pc.setDefaultShowNoteDuration(5000);
	}

	
	/**
	 * Create an instance.
	 * @param settings
	 */
	public DelayUIDriverSwing(PlaybackSettings settings) {
		_settings = settings;
	//	pc = getPresentationContext();
	}


	public Component click(Component owner, String labelOrPath) throws ComponentMissingException, ActionFailedException {
		
		
		Component c = super.click(owner, labelOrPath);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		
		return c;
		
	}


	public Component click(int clickCount, Component w, int x, int y, int mask) {
		//first move the mouse to the point of interest:
		mouseMove(w); 
		Component c = super.click(clickCount, w, x, y, mask);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		
		return c;
	}




	public Component clickComboBox(JComboBox owner, String labelOrPath, int clickCount) throws ActionFailedException {
		//first move the mouse to the point of interest:
		mouseMove(owner); 
		Component c =  super.clickComboBox(owner, labelOrPath, clickCount);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		return c;
	}


	public Component clickListItem(int clickCount, JList owner, String labelOrPath, int mask) throws ActionFailedException {
		//first move the mouse to the point of interest:
		mouseMove(owner); 
		Component c = super.clickListItem(clickCount, owner, labelOrPath, mask);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		return c;
	}


	public Component clickMenuItem(JMenuItem owner) {
		Component c =  super.clickMenuItem(owner);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		return c;

	}


	public Component clickTable(int clickCount, JTable owner, int row, int col, int mask) {
		mouseMove(owner);
		Component c = super.clickTable(clickCount, owner, row, col, mask);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		return c;

	}


	public Component clickTreeItem(int clickCount, Component owner, String path, int mask) {
		Component c = super.clickTreeItem(clickCount, owner, path, mask);
		//pause
		if (_settings.getDelayOn())
			pause(_settings.getWidgetClickDelay());
		return c;

	}

	
	public void keyClick(char key) {
		super.keyClick(key);
		pause(_settings.getKeyClickDelay());
	}


	public void keyClick(int ctrl, char c) {
		super.keyClick(ctrl, c);
		pause(_settings.getKeyClickDelay());
	}


	public void keyClick(int key) {
		super.keyClick(key);
		pause(_settings.getKeyClickDelay());
	}


	private Point pointT;
	private Point cursorT;
	
	public void mouseMove(Component w, int x, int y) {
		int delay = _settings.getMouseMoveDelay();
		if (!_settings.getDelayOn() || delay == 0) {
			super.mouseMove(w, x, y);
			return;
		}
		
		// get location of the widget
		pointT = AWT.getLocationOnScreen(w);
		// get current location of the cursor/mouse for jre 1.5 and above
		//	PointerInfo info = MouseInfo.getPointerInfo();
		//	cursorT = info.getLocation();
		// since build system is 1.4, use reflection to make calls
		
		try {
            Class c = Class.forName("java.awt.MouseInfo");
            Method m = c.getMethod("getPointerInfo",null);
            Object o = m.invoke(null,null);
            Class cPointer = Class.forName("java.awt.PointerInfo");
            Method method = cPointer.getMethod("getLocation",null);
            cursorT = (Point)method.invoke(o,null);
            
         }
         catch (Throwable e) {
        	 if (!printMessage){
        		 System.out.println("Mouse Delay not supported, turn off Mouse Delay in Window->Preferences->WindowTester->Playback");
        		 printMessage = true;
        	 }
            return;
         }
		
		Point cursorLocation = cursorT;
		Point target = new Point(pointT.x+x,pointT.y+y);
		
		Point[] path = getPath(cursorLocation, target);
		
		for (int i = 0; i < path.length; i++) {
			super.mouseMove(path[i].x, path[i].y);
			pause(delay);
		}
	}




	public void enterText(String txt) throws ActionFailedException {
		if (!_settings.getDelayOn() || _settings.getKeyClickDelay() == 0) {
			super.enterText(txt);
			return;
		}
		
		//enter text one char at a time, pausing as we go
		for (int i=0; i <= txt.length()-1; ++i) {
			keyClick(txt.charAt(i));
		}
	}
	
	//////////////////////////////////////////////////////////////////////
	//
	//
	// Utility methods
	//
	//
	/////////////////////////////////////////////////////////////////////
	
	public static Point[] getPath(Point p1, Point p2) {
		int numSteps = getNumSteps(p1, p2);
		return getPath(p1, p2, numSteps+3);
	}


	private static int getNumSteps(Point p1, Point p2) {
		int dx = Math.abs(p1.x - p2.x);
		int dy = Math.abs(p1.y - p2.y);
		int numSteps = Math.max(dx, dy);
		return numSteps;
	}


	public static Point[] getPath(Point p1, Point p2, int numSteps) {
		int[] xsteps = getSteps(p1.x, p2.x, numSteps);
		int[] ysteps = getSteps(p1.y, p2.y, numSteps);
		
		Point[] points = new Point[xsteps.length];
		
		for (int i=0; i < xsteps.length; ++i)
			points[i] = new Point(xsteps[i], ysteps[i]);
		
		return points;
	}



	static int[] getSteps(int start, int stop, int numOfSteps) {
		float[] fsteps = new float[numOfSteps];
		int[] steps = new int[numOfSteps];
		float delta = stop - start;
		float increment = delta/numOfSteps;
		
		
		fsteps[0] = start;
		for (int i=1; i < numOfSteps; ++i) {
			fsteps[i] =  fsteps[i-1] + increment;
		}
		for (int i = 0; i < numOfSteps; ++i) {
			steps[i] = Math.round(fsteps[i]);
		}
		//sanity check last coord:
		steps[numOfSteps-1] = stop;
		
		return steps;
	}
	
}
