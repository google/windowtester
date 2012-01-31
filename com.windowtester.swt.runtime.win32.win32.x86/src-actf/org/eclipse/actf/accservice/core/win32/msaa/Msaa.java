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
package org.eclipse.actf.accservice.core.win32.msaa;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.actf.accservice.core.AccessibleConstants;


/**
 * utility constants and methods for MSAA
 * 
 * @author Mike Smith, Kavitha Tegala, Mike Squillace
 *
 */
public final class Msaa
{

	// These values were obtained from org.eclipse.swt.accessibility.ACC
	public static final int MSAA_STATE_NORMAL = 0x00000000;
	public static final int MSAA_STATE_UNAVAILABLE = 0x00000001;
	public static final int MSAA_STATE_SELECTED = 0x00000002;
	public static final int MSAA_STATE_SELECTABLE = 0x00200000;
	public static final int MSAA_STATE_MULTI_SELECTABLE = 0x1000000;
	public static final int MSAA_STATE_FOCUSED = 0x00000004;
	public static final int MSAA_STATE_FOCUSABLE = 0x00100000;
	public static final int MSAA_STATE_PRESSED = 0x8;
	public static final int MSAA_STATE_CHECKED = 0x10;
	public static final int MSAA_STATE_EXPANDED = 0x200;
	public static final int MSAA_STATE_COLLAPSED = 0x400;
	public static final int MSAA_STATE_HOTTRACKED = 0x80;
	public static final int MSAA_STATE_BUSY = 0x800;
	public static final int MSAA_STATE_READ_ONLY = 0x40;
	public static final int MSAA_STATE_INVISIBLE = 0x8000;
	public static final int MSAA_STATE_OFF_SCREEN = 0x10000;
	public static final int MSAA_STATE_SIZEABLE = 0x20000;
	public static final int MSAA_STATE_LINKED = 0x400000;
    public static final int MSAA_STATE_MOVEABLE = 0x40000;
    public static final int MSAA_STATE_HASPOPUP = 0x40000000;
	
	// Added based on oleacc.h
	public static final int MSAA_ROLE_TITLEBAR = 0x1;
	public static final int MSAA_ROLE_GRIP = 0x4;
	public static final int MSAA_ROLE_STATUS_BAR = 0x17;
	public static final int MSAA_ROLE_INDICATOR = 0x27;
	public static final int MSAA_ROLE_PANE = 0x10;
	public static final int MSAA_ROLE_GRAPHIC = 0x28;
	public static final int MSAA_ROLE_SPLIT_BUTTON = 0x3e;
	public static final int MSAA_ROLE_SOUND = 0x5;
	public static final int MSAA_ROLE_CURSOR = 0x6;
	public static final int MSAA_ROLE_CARET = 0x7;
	public static final int MSAA_ROLE_ALERT = 0x8;
	public static final int MSAA_ROLE_APPLICATION = 0xe;
	public static final int MSAA_ROLE_DOCUMENT = 0xf;
	public static final int MSAA_ROLE_CHART = 0x11;
	public static final int MSAA_ROLE_BORDER = 0x13;
	public static final int MSAA_ROLE_GROUPING = 0x14;
	public static final int MSAA_ROLE_COLUMN = 0x1b;
	public static final int MSAA_ROLE_ROW = 0x1c;
	public static final int MSAA_ROLE_HELP_BALLOON = 0x1f;
	public static final int MSAA_ROLE_CHARACTER = 0x20;
	public static final int MSAA_ROLE_PROPERTY_PAGE = 0x26;
	public static final int MSAA_ROLE_DROP_LIST = 0x2f;
	public static final int MSAA_ROLE_DIAL = 0x31;
	public static final int MSAA_ROLE_HOTKEY_FIELD = 0x32;
	public static final int MSAA_ROLE_SPIN_BUTTON = 0x34;
	public static final int MSAA_ROLE_DIAGRAM = 0x35;
	public static final int MSAA_ROLE_ANIMATION = 0x36;
	public static final int MSAA_ROLE_EQUATION = 0x37;
	public static final int MSAA_ROLE_BUTTON_DROPDOWN = 0x38;
	public static final int MSAA_ROLE_BUTTON_MENU = 0x39;
	public static final int MSAA_ROLE_BUTTON_DROPDOWN_GRID = 0x3a;
	public static final int MSAA_ROLE_WHITE_SPACE = 0x3b;
	public static final int MSAA_ROLE_CLOCK = 0x3d;
	public static final int MSAA_ROLE_IP_ADDRESS = 0x3f;
	public static final int MSAA_ROLE_OUTLINE_BUTTON = 0x40;
	public static final int MSAA_ROLE_CLIENT_AREA = 0xa;
	public static final int MSAA_ROLE_WINDOW = 0x9;
	public static final int MSAA_ROLE_MENU_BAR = 0x2;
	public static final int MSAA_ROLE_MENU = 0xb;
	public static final int MSAA_ROLE_MENU_ITEM = 0xc;
	public static final int MSAA_ROLE_SEPARATOR = 0x15;
	public static final int MSAA_ROLE_TOOL_TIP = 0xd;
	public static final int MSAA_ROLE_SCROLL_BAR = 0x3;
	public static final int MSAA_ROLE_DIALOG = 0x12;
	public static final int MSAA_ROLE_LABEL = 0x29;
	public static final int MSAA_ROLE_PUSH_BUTTON = 0x2b;
	public static final int MSAA_ROLE_CHECK_BUTTON = 0x2c;
	public static final int MSAA_ROLE_RADIO_BUTTON = 0x2d;
	public static final int MSAA_ROLE_COMBO_BOX = 0x2e;
	public static final int MSAA_ROLE_TEXT = 0x2a;
	public static final int MSAA_ROLE_TOOL_BAR = 0x16;
	public static final int MSAA_ROLE_LIST = 0x21;
	public static final int MSAA_ROLE_LIST_ITEM = 0x22;
	public static final int MSAA_ROLE_TABLE = 0x18;
	public static final int MSAA_ROLE_TABLE_CELL = 0x1d;
	public static final int MSAA_ROLE_TABLE_COLUMN_HEADER = 0x19;
	public static final int MSAA_ROLE_TABLE_ROW_HEADER = 0x1a;
	public static final int MSAA_ROLE_TREE = 0x23;
	public static final int MSAA_ROLE_TREE_ITEM = 0x24;
	public static final int MSAA_ROLE_TAB_FOLDER = 0x3c;
	public static final int MSAA_ROLE_TAB_ITEM = 0x25;
	public static final int MSAA_ROLE_PROGRESS_BAR = 0x30;
	public static final int MSAA_ROLE_SLIDER = 0x33;
	public static final int MSAA_ROLE_LINK = 0x1e;
	
	public static final String ACTF_MSAA_BUNDLE = "org.eclipse.actf.accservice.win32.msaa";

	/**
	 * map the given MSAA role constant to a ACTF role constant
	 * 
	 * @param role - MSAA role constant
	 * @return ACTF role constant
	 * @see org.eclipse.actf.accservice.core.AccessibleConstants
	 */
	public static String getMsaaActfRoleName (long role) {
		String res = null;
		if (role == Msaa.MSAA_ROLE_CLIENT_AREA) {
			res = AccessibleConstants.ROLE_CLIENT_AREA;
		}else if (role == Msaa.MSAA_ROLE_WINDOW) {
			res = AccessibleConstants.ROLE_WINDOW;
		}else if (role == Msaa.MSAA_ROLE_MENU_BAR) {
			res = AccessibleConstants.ROLE_MENU_BAR;
		}else if (role == Msaa.MSAA_ROLE_MENU) {
			res = AccessibleConstants.ROLE_MENU;
		}else if (role == Msaa.MSAA_ROLE_MENU_ITEM) {
			res = AccessibleConstants.ROLE_MENU_ITEM;
		}else if (role == Msaa.MSAA_ROLE_SEPARATOR) {
			res = AccessibleConstants.ROLE_SEPARATOR;
		}else if (role == Msaa.MSAA_ROLE_TOOL_TIP) {
			res = AccessibleConstants.ROLE_TOOL_TIP;
		}else if (role == Msaa.MSAA_ROLE_SCROLL_BAR) {
			res = AccessibleConstants.ROLE_SCROLL_BAR;
		}else if (role == Msaa.MSAA_ROLE_DIALOG) {
			res = AccessibleConstants.ROLE_DIALOG;
		}else if (role == Msaa.MSAA_ROLE_LABEL) {
			res = AccessibleConstants.ROLE_LABEL;
		}else if (role == Msaa.MSAA_ROLE_PUSH_BUTTON) {
			res = AccessibleConstants.ROLE_PUSH_BUTTON;
		}else if (role == Msaa.MSAA_ROLE_CHECK_BUTTON) {
			res = AccessibleConstants.ROLE_CHECK_BOX;
		}else if (role == Msaa.MSAA_ROLE_RADIO_BUTTON) {
			res = AccessibleConstants.ROLE_RADIO_BUTTON;
		}else if (role == Msaa.MSAA_ROLE_COMBO_BOX) {
			res = AccessibleConstants.ROLE_COMBO_BOX;
		}else if (role == Msaa.MSAA_ROLE_TEXT) {
			res = AccessibleConstants.ROLE_TEXT;
		}else if (role == Msaa.MSAA_ROLE_TOOL_BAR) {
			res = AccessibleConstants.ROLE_TOOL_BAR;
		}else if (role == Msaa.MSAA_ROLE_LIST) {
			res = AccessibleConstants.ROLE_LIST;
		}else if (role == Msaa.MSAA_ROLE_LIST_ITEM) {
			res = AccessibleConstants.ROLE_LIST_ITEM;
		}else if (role == Msaa.MSAA_ROLE_TABLE) {
			res = AccessibleConstants.ROLE_TABLE;
		}else if (role == Msaa.MSAA_ROLE_TABLE_CELL) {
			res = AccessibleConstants.ROLE_TABLECELL;
		}else if (role == Msaa.MSAA_ROLE_TABLE_COLUMN_HEADER) {
			res = AccessibleConstants.ROLE_TABLECOLUMNHEADER;
		}else if (role == Msaa.MSAA_ROLE_TABLE_ROW_HEADER) {
			res = AccessibleConstants.ROLE_TABLEROWHEADER;
		}else if (role == Msaa.MSAA_ROLE_TREE) {
			res = AccessibleConstants.ROLE_TREE;
		}else if (role == Msaa.MSAA_ROLE_TREE_ITEM) {
			res = AccessibleConstants.ROLE_TREEITEM;
		}else if (role == Msaa.MSAA_ROLE_TAB_FOLDER) {
			res = AccessibleConstants.ROLE_TABFOLDER;
		}else if (role == Msaa.MSAA_ROLE_TAB_ITEM) {
			res = AccessibleConstants.ROLE_TABITEM;
		}else if (role == Msaa.MSAA_ROLE_PROGRESS_BAR) {
			res = AccessibleConstants.ROLE_PROGRESS_BAR;
		}else if (role == Msaa.MSAA_ROLE_SLIDER) {
			res = AccessibleConstants.ROLE_SLIDER;
		}else if (role == Msaa.MSAA_ROLE_LINK) {
			res = AccessibleConstants.ROLE_LINK;
		}else if (role == Msaa.MSAA_ROLE_TITLEBAR) {
			res = AccessibleConstants.ROLE_TITLE_BAR;
		}else if (role == Msaa.MSAA_ROLE_GRIP) {
			res = AccessibleConstants.ROLE_GRIP;
		}else if (role == Msaa.MSAA_ROLE_STATUS_BAR) {
			res = AccessibleConstants.ROLE_STATUS_BAR;
		}else if (role == Msaa.MSAA_ROLE_INDICATOR) {
			res = AccessibleConstants.ROLE_INDICATOR;
		}else if (role == Msaa.MSAA_ROLE_PANE) {
			res = AccessibleConstants.ROLE_PANE;
		}else if (role == Msaa.MSAA_ROLE_GRAPHIC) {
			res = AccessibleConstants.ROLE_GRAPHIC;
		}else if (role == Msaa.MSAA_ROLE_SPLIT_BUTTON) {
			res = AccessibleConstants.ROLE_SPLIT_BUTTON;
		}else if (role == Msaa.MSAA_ROLE_SOUND) {
			res = AccessibleConstants.ROLE_SOUND;
		}else if (role == Msaa.MSAA_ROLE_CURSOR) {
			res = AccessibleConstants.ROLE_CURSOR;
		}else if (role == Msaa.MSAA_ROLE_CARET) {
			res = AccessibleConstants.ROLE_CARET;
		}else if (role == Msaa.MSAA_ROLE_ALERT) {
			res = AccessibleConstants.ROLE_ALERT;
		}else if (role == Msaa.MSAA_ROLE_APPLICATION) {
			res = AccessibleConstants.ROLE_APPLICATION;
		}else if (role == Msaa.MSAA_ROLE_DOCUMENT) {
			res = AccessibleConstants.ROLE_DOCUMENT;
		}else if (role == Msaa.MSAA_ROLE_CHART) {
			res = AccessibleConstants.ROLE_CHART;
		}else if (role == Msaa.MSAA_ROLE_BORDER) {
			res = AccessibleConstants.ROLE_BORDER;
		}else if (role == Msaa.MSAA_ROLE_GROUPING) {
			res = AccessibleConstants.ROLE_GROUPING;
		}else if (role == Msaa.MSAA_ROLE_COLUMN) {
			res = AccessibleConstants.ROLE_COLUMN;
		}else if (role == Msaa.MSAA_ROLE_ROW) {
			res = AccessibleConstants.ROLE_ROW;
		}else if (role == Msaa.MSAA_ROLE_HELP_BALLOON) {
			res = AccessibleConstants.ROLE_HELP_BALLOON;
		}else if (role == Msaa.MSAA_ROLE_CHARACTER) {
			res = AccessibleConstants.ROLE_CHARACTER;
		}else if (role == Msaa.MSAA_ROLE_PROPERTY_PAGE) {
			res = AccessibleConstants.ROLE_PROPERTY_PAGE;
		}else if (role == Msaa.MSAA_ROLE_DROP_LIST) {
			res = AccessibleConstants.ROLE_DROP_LIST;
		}else if (role == Msaa.MSAA_ROLE_DIAL) {
			res = AccessibleConstants.ROLE_DIAL;
		}else if (role == Msaa.MSAA_ROLE_HOTKEY_FIELD) {
			res = AccessibleConstants.ROLE_HOTKEY_FIELD;
		}else if (role == Msaa.MSAA_ROLE_SPIN_BUTTON) {
			res = AccessibleConstants.ROLE_SPIN_BUTTON;
		}else if (role == Msaa.MSAA_ROLE_DIAGRAM) {
			res = AccessibleConstants.ROLE_DIAGRAM;
		}else if (role == Msaa.MSAA_ROLE_ANIMATION) {
			res = AccessibleConstants.ROLE_ANIMATION;
		}else if (role == Msaa.MSAA_ROLE_EQUATION) {
			res = AccessibleConstants.ROLE_EQUATION;
		}else if (role == Msaa.MSAA_ROLE_BUTTON_DROPDOWN) {
			res = AccessibleConstants.ROLE_BUTTON_DROPDOWN;
		}else if (role == Msaa.MSAA_ROLE_BUTTON_MENU) {
			res = AccessibleConstants.ROLE_BUTTON_MENU;
		}else if (role == Msaa.MSAA_ROLE_BUTTON_DROPDOWN_GRID) {
			res = AccessibleConstants.ROLE_BUTTON_DROPDOWN_GRID;
		}else if (role == Msaa.MSAA_ROLE_WHITE_SPACE) {
			res = AccessibleConstants.ROLE_WHITE_SPACE;
		}else if (role == Msaa.MSAA_ROLE_CLOCK) {
			res = AccessibleConstants.ROLE_CLOCK;
		}else if (role == Msaa.MSAA_ROLE_IP_ADDRESS) {
			res = AccessibleConstants.ROLE_IP_ADDRESS;
		}else if (role == Msaa.MSAA_ROLE_OUTLINE_BUTTON) {
			res = AccessibleConstants.ROLE_OUTLINE_BUTTON;
		}
		return res;
	}

	public static Set getState(int state) {
		HashSet res = new HashSet();
		// We must always set the visible/invisible state so that there
		// is a consistent property for the client to test against
		if ((state & Msaa.MSAA_STATE_INVISIBLE) != 0) {
			res.add(AccessibleConstants.STATE_INVISIBLE);
		}
		if ((state & Msaa.MSAA_STATE_UNAVAILABLE) != 0) {
			res.add(AccessibleConstants.STATE_UNAVAILABLE);
		}
		if ((state & Msaa.MSAA_STATE_BUSY) != 0) {
			res.add(AccessibleConstants.STATE_BUSY);
		}
		if ((state & Msaa.MSAA_STATE_CHECKED) != 0) {
			res.add(AccessibleConstants.STATE_CHECKED);
		}
		if ((state & Msaa.MSAA_STATE_COLLAPSED) != 0) {
			res.add(AccessibleConstants.STATE_COLLAPSED);
		}
		if ((state & Msaa.MSAA_STATE_EXPANDED) != 0) {
			res.add(AccessibleConstants.STATE_EXPANDED);
		}
		if ((state & Msaa.MSAA_STATE_FOCUSABLE) != 0) {
			res.add(AccessibleConstants.STATE_FOCUSABLE);
		}
		if ((state & Msaa.MSAA_STATE_FOCUSED) != 0) {
			res.add(AccessibleConstants.STATE_FOCUSED);
		}
		if ((state & Msaa.MSAA_STATE_HOTTRACKED) != 0) {
			res.add(AccessibleConstants.STATE_HOT_TRACKED);
		}
		 if ((state & Msaa.MSAA_STATE_LINKED) != 0) {
		res.add(AccessibleConstants.STATE_LINKED);
		 }
		if ((state & Msaa.MSAA_STATE_MULTI_SELECTABLE) != 0) {
			res.add(AccessibleConstants.STATE_MULTI_SELECTABLE);
		}
		if ((state & Msaa.MSAA_STATE_MOVEABLE) != 0) {
			res.add(AccessibleConstants.STATE_MOVEABLE);
		}
		if (state == Msaa.MSAA_STATE_NORMAL) {
			res.add(AccessibleConstants.STATE_NORMAL);
		}
		if ((state & Msaa.MSAA_STATE_OFF_SCREEN) != 0) {
			res.add(AccessibleConstants.STATE_OFF_SCREEN);
		}
		if ((state & Msaa.MSAA_STATE_PRESSED) != 0) {
			res.add(AccessibleConstants.STATE_PRESSED);
		}
		if ((state & Msaa.MSAA_STATE_READ_ONLY) != 0) {
			res.add(AccessibleConstants.STATE_READ_ONLY);
		}
		if ((state & Msaa.MSAA_STATE_SELECTABLE) != 0) {
			res.add(AccessibleConstants.STATE_SELECTABLE);
		}
		if ((state & Msaa.MSAA_STATE_SELECTED) != 0) {
			res.add(AccessibleConstants.STATE_SELECTED);
		}
		if ((state & Msaa.MSAA_STATE_SIZEABLE) != 0) {
			res.add(AccessibleConstants.STATE_SIZEABLE);
		}
		
		if ((state & Msaa.MSAA_STATE_HASPOPUP) != 0) {
			res.add(AccessibleConstants.STATE_HASPOPUP);
		}
		return res;
		
	}
	
}
