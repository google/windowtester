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
package com.windowtester.swt.util;

import org.eclipse.swt.SWT;



/**
 * A helper class that determines what index to select given sets of before and after
 * indicies.  This service is used to identify list and table items to select in
 * constructing selection events.
 */
public class SelectionDeltaParser {

	/**
	 * Find the index to select in order to match before and after index states.
	 * @param before - the indices selected before a selection event
	 * @param after - the indices selected after a selection event
	 * @param type - the type (SWT.CTRL, SWT.SHIFT) of selection
	 * @return the index to select
	 */
	public static int indexToSelect(int[] before, int[] after, int type) {
		if (type == 0)
			return stdSelect(before, after);
		if (type == SWT.CTRL)
			return ctrlSelect(before, after);
		if (type == SWT.SHIFT)
			return shiftSelect(before, after);
		
		throw new IllegalArgumentException("invalid selection type: " + type);
	}

	//////////////////////////////////////////////////////////////////////////////
	//
	// Selection implementations.
	//
	//////////////////////////////////////////////////////////////////////////////

	/**
	 * Do a standard select.
	 */
	private static int stdSelect(int[] before, int[] after) {
		int current;
		for (int i = 0; i < after.length; i++) {
			current = after[i];
			if (!isContainedIn(current, before))
				return current;
		}
		if (after.length == 0 && before.length == 1)
			return before[0];
		if (after.length == 1)
			return after[0];
		
		throw new IllegalStateException();
	}
	
	/**
	 * Do a shift select.
	 */
	private static int shiftSelect(int[] before, int[] after) {
		//self select
		if ((before.length == 1) && (after.length == 1))
			return before[0];
		//single-deselect
		if (after.length == 0 && before.length == 1)
			return before[0];
		//single-select
		if (after.length == 1)
			return after[0];
		//multi-select cases
		//-> up:
		if (first(after) == first(before))
			return last(after);
		if (first(after) == last(before))
			return last(after);
		//-> down:
		return  first(after);
	}
	
	/**
	 * Do a control select.
	 */
	private static int ctrlSelect(int[] before, int[] after) {
		//self select
		if ((before.length == 1) && (after.length == 1))
			return before[0];
		//single-deselect
		if (after.length == 0 && before.length == 1)
			return before[0];

		
		//general select
		if (after.length > before.length)
			return last(after);
		
		//general deselect
		if (after.length < before.length)
			return findFirstNotContainedIn(before, after);
		
		//if arrays are of the same size (but not == 1), we have an internal error...
		throw new IllegalStateException();
	}

	
	//////////////////////////////////////////////////////////////////////////////
	//
	// Helpers
	//
	//////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Find the first element in an array not contained in the other.
	 */
	private static int findFirstNotContainedIn(int[] items1, int[] items2) {
		int current;
		for (int i = 0; i < items1.length; i++) {
			current = items1[i];
			if (!isContainedIn(current, items2))
				return current;
		}
		throw new IllegalStateException();
	}

	/**
	 * Get the first item in this array.
	 */
	private static int first(int[] items) {
		return items[0];
	}
	/**
	 * Get the last item in this array.
	 */
	private static int last(int[] items) {
		return items[items.length-1];
	}



	public static int indexToSelect(int[] before, int[] after) {
		return indexToSelect(before, after, 0);
	}
	
	
	/**
	 * Check to see if the given item is in an array.
	 */
	private static boolean isContainedIn(int item, int[] items) {
		for (int i = 0; i < items.length; ++i) {
			if (items[i] == item)
				return true;
		}
		return false;
	}
	
}
