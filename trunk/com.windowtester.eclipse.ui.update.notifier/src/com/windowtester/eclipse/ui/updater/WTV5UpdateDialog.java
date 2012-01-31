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
package com.windowtester.eclipse.ui.updater;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.graphics.Point;

/**
 * Displays WT version 5.0 product update details.
 * 
 * @author Phil Quitslund
 *
 */
public class WTV5UpdateDialog extends UpdateDialog {

	private static final String SHELL_TITLE = "WindowTester v 5.0 Update";
//	private static final String CONTEXT_ID = "com.windowtester.eclipse.help.api_migration";
//	
	private static final String API_MIGRATION_HREF = "api_migration";
	private static final String WHATS_NEW_HREF = "whats_new";

	private static final String UPDATE_DESCRIPTION = 
		"<p><b>Welcome to WindowTester 5.0!</b></p>" +
		"<p>We've added some new APIs in this release.  To help you migrate existing tests we've added some tooling to automate conversion.</p>" +
			"<li>See the <a href=\"" + API_MIGRATION_HREF + "\" nowrap=\"true\">API Migration</a> documentation for more details.</li>" +
			"<li>To learn about what else is new in this release, see the <a href=\"" + WHATS_NEW_HREF + "\" nowrap=\"true\">What's New</a> overview.</li>";
		
		
	private static final Map<String,String> hrefToResourceMap = new HashMap<String,String>();
	static {
		hrefToResourceMap.put(API_MIGRATION_HREF, "/com.windowtester.eclipse.help/html/reference/API%20Migration.html");
		hrefToResourceMap.put(WHATS_NEW_HREF, "/com.windowtester.eclipse.help/html/whatsnew.html");
		
	}
	
	private static final Point SIZE = new Point(360, 240);
	
	public WTV5UpdateDialog() {
		super(SHELL_TITLE, UPDATE_DESCRIPTION, hrefToResourceMap);
		setSize(SIZE);
	}	
	
	
}
