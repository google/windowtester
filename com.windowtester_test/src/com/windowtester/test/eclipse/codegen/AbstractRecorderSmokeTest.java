package com.windowtester.test.eclipse.codegen;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.windowtester.recorder.event.ISemanticEvent;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.XYLocator;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.util.TestMonitor;
import com.windowtester.test.eclipse.EclipseUtil;
import com.windowtester.test.util.PlatformEventWatcherAndCodegenerator;
import com.windowtester.test.util.PlatformEventWatcherAndCodegenerator.API;

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
public abstract class AbstractRecorderSmokeTest extends UITestCaseSWT {
	
	private static final boolean DISPLAY_EVENTS = true;
	private static final boolean DISPLAY_CODEGEN = true;
	
	static final String ECLIPSE_VERSION_SUFFIX = "_" + EclipseUtil.getMajor() + EclipseUtil.getMinor();
	static final String EXPECTED_FILE_EXT = ".java";
	static final String TEST_PACKAGE = "expected";
	static final String OPTIONAL_TAG = "// ?";
	
	//watcher is shared
	private static PlatformEventWatcherAndCodegenerator _watcher;
	
	private API _api = API.V2;

	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Lifecycle
	//
	////////////////////////////////////////////////////////////////////////////////////////

	@Override
	protected void setUp() throws Exception {
		//we need to override this, to make the runtime THINK it is in recording mode
		//this matters because in non-recording mode exceptions cause shells to be closed, menus to be 
		//dismissed etc...  (that is, they are treated as ERRORS, instead of part of daily life
		//as they are treated in recording)
		TestMonitor.getInstance().endTestCase();

		setUpCodegenDetails();
		//start the recorder
		startWatching();
	}

	@Override
	protected void tearDown() throws Exception {
		stopWatching();
	}
	
	private void setUpCodegenDetails() {
		getWatcher().setTestName(getExpectedTestName());
		getWatcher().setTestPackageName(TEST_PACKAGE);
	}

	private String getExpectedTestName() {
		String testName = getName();
		if (testName.endsWith("Fails"))
			testName = testName.substring(0, testName.length() - 5);
		return testName;
	}
	
	private void startWatching() {
		getWatcher().watch();
	}
	
	private void stopWatching() {
		getWatcher().stop();
	}
	
	/**
	 * Extend superclass behavior to compare generated code to expected code
	 * after each test has been run. This should not be done in tearDown() method
	 * because throwing an exception in tearDown() will suppress an exception
	 * that has already occurred during test execution.
	 * @see junit.framework.TestCase#runTest()
	 */
	protected void runTest() throws Throwable {
		super.runTest();
		optionallyDisplayEvents(getWatcher());
		String generatedTestCase = getWatcher().codegen();
		optionallyDisplayGeneratedTest(generatedTestCase);
		assertSameAsFileContents(generatedTestCase, getExpectedUrl());
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Accessors
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	protected URL getExpectedUrl() {
		try {
			Bundle bundle = Platform.getBundle(getBundleName());
			String entryPrefix = "src-test-resources/" + TEST_PACKAGE + "/" + getExpectedTestName();
			URL url = bundle.getEntry(entryPrefix + ECLIPSE_VERSION_SUFFIX + EXPECTED_FILE_EXT);
			if (url == null)
				url = bundle.getEntry(entryPrefix + EXPECTED_FILE_EXT);
			if (url == null)
				throw new RuntimeException("Failed to find " + url);
			
			/* $codepro.preprocessor.if version < 3.2 $ 
			return Platform.asLocalURL(url);
			
			$codepro.preprocessor.elseif version >= 3.2 $ */
			return org.eclipse.core.runtime.FileLocator.toFileURL(url);
			
			/* $codepro.preprocessor.endif $ */
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getBundleName() {
		return "com.windowtester.test";
	}

	/**
	 * Default is set to {@link API#V2}.  Call {@link #setAPIVersion(API)} to override.
	 * @return the current API version
	 */
	protected API getAPIVersion() {
		return _api;
	}
	
	/**
	 * Default is set to {@link API#V2}; call this to override.
	 */
	protected void setAPIVersion(API version) {
		_api = version;
	}	
	
	private PlatformEventWatcherAndCodegenerator getWatcher() {
		if (_watcher == null)
			_watcher = new PlatformEventWatcherAndCodegenerator(getAPIVersion());
		return _watcher;
	}


	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Setup helpers
	//
	////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * Recent versions of Eclipse do not close the welcome page when view
	 * is opened. Make sure it gets closed.
	 * @throws WidgetSearchException
	 */
	protected void closeWelcomePageIfNecessary() throws WidgetSearchException {
		IWidgetLocator[] welcomeTab = getUI().findAll(new CTabItemLocator("Welcome"));
		if (welcomeTab.length == 0)
			return;
		// TODO: compute x based on tab width to avoid font dependencies
		int x = 88;
		if (abbot.Platform.isOSX())
			x = 95;
		else if (abbot.Platform.isLinux())
			x = 100;
		getUI().click(new XYLocator(welcomeTab[0], x, 12));
	}
	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Assertion helpers
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	private void assertSameAsFileContents(String result, URL url) throws FileNotFoundException, IOException {
				
		println("------------------------");
		
		String[] lines = result.split("\n");
		LineNumberReader in = null;
		try {
			in = new LineNumberReader(new InputStreamReader(new BufferedInputStream(url.openStream())));
			String str;
			String line;
			int i = 0;
			while ((str = in.readLine()) != null) {
				
				// RemoveEclipse version specific class name suffix (e.g. "_32")
				int offset = str.indexOf(ECLIPSE_VERSION_SUFFIX + " extends");
				if (offset > -1)
					str = str.substring(0, offset) + str.substring(offset + ECLIPSE_VERSION_SUFFIX.length());
				
				if (isOptional(str)) {
					str = trimOptional(str);
					//only advance index in case of match
					if (str.trim().equals(lines[i].trim()))
						++i;
				} else {
					assertTrue("codegen missing line [" + i +"]"+ lines[i], i < lines.length);
					str = str.trim();
					line = lines[i++].trim();
					//notice we don't decrement i since lines start at 1 and not 0
					assertEquals("expected: " + str + " but got: " + line + " at [" + i + "]", 
							str, line);
				}
			}		
			//notice we need to decrement i to back up one line since we've eagerly incremented above
			int lastLine = i-1;
			assertTrue("unexpected line [" + lastLine + "] in codegen:\n\t" + lines[lastLine], lastLine == lines.length-1);
				
		} finally {
			if (in != null)
				in.close();
		}
	}

	static String trimOptional(String str) {
		str = str.trim();
		int index = str.indexOf(OPTIONAL_TAG);
		if (index == -1)
			return str;
		return str.substring(0, index);
	}

	static boolean isOptional(String str) {
		return str.indexOf(OPTIONAL_TAG) > -1;	
	}

	
	////////////////////////////////////////////////////////////////////////////////////////
	//
	// Debugging
	//
	////////////////////////////////////////////////////////////////////////////////////////
	
	private void optionallyDisplayGeneratedTest(String generatedTestCase) {
		if (DISPLAY_CODEGEN) {
			println("---- generated test: -----");
			println(generatedTestCase);
			println("--------------------------");
		}
	}
	
	private void optionallyDisplayEvents(PlatformEventWatcherAndCodegenerator watcher) {
		if (DISPLAY_EVENTS)	{
			println("---- recorded events: ----");
			displayEvents(watcher.getEvents());	
			println("--------------------------");
		}
	}

	private void displayEvents(List<ISemanticEvent> events) {
		for (ISemanticEvent event : events) {
			println(event);
		}
	}
	
	private void println(Object obj) {
		System.out.println(obj);
	}
	
}
