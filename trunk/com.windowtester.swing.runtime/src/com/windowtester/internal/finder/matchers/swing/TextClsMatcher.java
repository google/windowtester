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
package com.windowtester.internal.finder.matchers.swing;

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextComponent;

import javax.swing.AbstractButton;
import javax.swing.JLabel;

import abbot.finder.matchers.ClassMatcher;

public class TextClsMatcher extends ClassMatcher {
	
	private final Class cls;
    private final String text;
	private String ctext = null;
	/**
     * Constructs a Matcher for the text given.
     * <p/>
     * The component must be visible.
     * @param text the text to match.
     */
    public TextClsMatcher(String text) {
        this(text, true, Component.class);
    }
    /**
     * Constructs a Matcher with the text and the visibility given.
     * <p/>
     * @param text the text to match.
     * @param mustBeShowing true if the widget must be visible.
     */
    public TextClsMatcher(String text, boolean mustBeShowing) {
    	this(text, mustBeShowing, Component.class);
    }
    /**
     * Constructs a matcher with the text and the class given.
     * <p/>
     * The component must be visible. Note that searches are considerably faster
     * when a class is provided to the matcher.
     * @param text  the text to match.
     * @param clas the Class to match.
     */
    public TextClsMatcher(String text, Class clas) {
    	this(text, true, clas);
    }
    /**
     * Constructs a Matcher with the text, visibility and class given.
     * <p/>
     * Note that searches are considerably faster when a class is provided
     * to the matcher.
     * @param text the text to match.
     * @param mustBeShowing true if the widget must be visible.
     * @param clas the class to match.
     */
    public TextClsMatcher(String text, boolean mustBeShowing, Class clas) {
    	super(clas);
    	this.cls = clas;
    	this.text = text;
    }
    
    public boolean matches(final Component w) {
    	if (this.cls != null) {
    		boolean superResult = super.matches(w);
    		if (!superResult) {
    			return false;
    		}
    	}
    	
    	// AWT Components
    	if (w instanceof Button)
    		ctext = ((Button)w).getLabel();
    	if (w instanceof Checkbox)
    		ctext = ((Checkbox)w).getLabel();
    	if (w instanceof Label)
    		ctext = ((Label)w).getText();
    	if (w instanceof TextComponent)
    		ctext = ((TextComponent)w).getText();
    	if (w instanceof Dialog)
    		ctext = ((Dialog)w).getTitle();
    	if (w instanceof Frame)
    		ctext = ((Frame)w).getTitle();
    	
    	// Swing Components
    	if (w instanceof AbstractButton) // button,menuitem,togglebutton
    		ctext = ((AbstractButton)w).getText();
    	if (w instanceof JLabel)
    		ctext = ((JLabel)w).getText();
    	// popupmenu getLabel ?
    	
    //	if (w instanceof JTextComponent)
    //		ctext = ((JTextComponent)w).getText();
    	
    	if (ctext == null) return false;
    	if (text == null)
            return ctext == null;
    	
    	return stringsMatch(text, ctext);
    	
    }
	
    /**
	 * Retrieve the text of this matcher.
	 * @return Returns the text.
	 */
	public String getText() {
		return text;
	}
	

}
