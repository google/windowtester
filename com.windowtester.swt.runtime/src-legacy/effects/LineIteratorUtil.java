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

import org.eclipse.swt.graphics.Point;


/**
 * A utility class for caclulating poitns along a line for the purposes of
 * slowed down mouse movement playback.
 * 
 * @author Phil Quitslund
 *
 */
public class LineIteratorUtil {

	
	public static void main(String[] args) {
		
		Point p1 = new Point(56,0);
		Point p2 = new Point(11,3);
				
		Point[] path = getPath(p1, p2, new Double(1.75));
		for (int i = 0; i < path.length; i++) {
			System.out.println(path[i]);
		}
		
	}
	
	
	
	
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


	public static Point[] getPath(Point p1, Point p2, Double multiplier) {
		int numSteps = getNumSteps(p1, p2);
		int num = new Double(Math.floor(multiplier.doubleValue() * numSteps)).intValue();
		return getPath(p1, p2, num);
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
