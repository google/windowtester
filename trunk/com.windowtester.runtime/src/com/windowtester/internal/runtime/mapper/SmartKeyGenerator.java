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
package com.windowtester.internal.runtime.mapper;

import java.util.HashMap;
import java.util.Map;

import com.windowtester.runtime.WidgetLocator;

/**
 * A (somewhat) smart key generator that generates keys based on widget type and label.  
 * These generated keys are intended to simulate the kinds of keys users might
 * themselves choose to identify widgets.
 * For example, suppose we have a Button labeled "on".  The first generated key for such a button would
 * be "on.button".  The second generated key for such a button would be "on.button1" and so on.
 * <br><br>
 * Each instance of this generator will create unique keys for each call to generate.
 */
public class SmartKeyGenerator implements IKeyGenerator {

	/** A map of witnessed keys to their counts */
	private Map/*<StringBuffer,Integer>*/ _seen = new HashMap();
	
	/**
	 * @see com.windowtester.swt.mapper.IKeyGenerator#generate(com.windowtester.swt.WidgetLocator)
	 */
	public String generate(WidgetLocator info) {
		StringBuffer base = getBase(info);
		int index         = getIndex(base.toString());
		if (index != 0)
			base.append(index);
		return base.toString();
	}

	/**
	 * Get the index that numbers this base string.
	 * @param base - the String key
	 * @return an index that describes how many such keys have been encountered
	 */
	private int getIndex(String base) {
		Integer index = (Integer)_seen.get(base);
		if (index == null) {
			index = new Integer(0);
		} else {
			index = new Integer(index.intValue()+1);
		}
		_seen.put(base, index);
		return index.intValue();
	}

	/**
	 * Extract a basic identifying String to label this info object.
	 * @param info - the info to describe
	 * @return a base StringBuffer for use in key generation
	 */
	private StringBuffer getBase(WidgetLocator info) {
		StringBuffer sb = new StringBuffer();
		String label = info.getNameOrLabel();
		if (label != null && !label.equals("")) {
			sb.append(label).append('.');
		}
		
		// get the simple name of the class 
		String className = info.getTargetClass().getName();
		int lastPeriod = className.lastIndexOf('.');
		String simpleName = (lastPeriod>=0)? className.substring(lastPeriod+1) : className;	

		sb.append(simpleName.toLowerCase());
		return sb;
	}

}
