package com.windowtester.test.product.docs;

import static com.windowtester.runtime.swt.locator.eclipse.EclipseLocators.view;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.browser.Browser;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.IWidgetLocator;
import com.windowtester.runtime.locator.IWidgetReference;
import com.windowtester.runtime.swt.UITestCaseSWT;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.CTabItemLocator;
import com.windowtester.runtime.swt.locator.SWTWidgetLocator;
import com.windowtester.test.eclipse.EclipseUtil;

/**
 * Basic intro page content tests.
 *
 * @author Phil Quitslund
 *
 */
public class ValidateIntroPageContentTest extends UITestCaseSWT /* intentionally NOT subclassing BASETEST! */ {

	
	private static final String TUTORIALS = "http://org.eclipse.ui.intro/showPage?id=tutorials";
	//private static final String OVERVIEW  = "http://org.eclipse.ui.intro/showPage?id=overview";
	private static final String SAMPLES   = "http://org.eclipse.ui.intro/showPage?id=samples";
	private static final String WHATS_NEW = "http://org.eclipse.ui.intro/showPage?id=whatsnew";
	
	private static final String SHOW_HELP_CMD = "http://org.eclipse.ui.intro/showHelpTopic?id=/";
	private static final String WT_GETTING_STARTED_LINK = 
		SHOW_HELP_CMD + "com.windowtester.eclipse.help/html/gettingStarted.html";
	private static final String WT_WHATS_NEW_LINK = 	
		SHOW_HELP_CMD + "com.windowtester.eclipse.help/html/whatsnew.html";
	
	private BrowserReference browser;

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
	public static class BrowserReference implements IWidgetReference {

		
		private class HtmlContainsCondition implements ICondition {

			String expectedText;
			
			public HtmlContainsCondition(String expectedText) {
				this.expectedText = expectedText;
			}
			
			/* (non-Javadoc)
			 * @see com.windowtester.runtime.condition.ICondition#test()
			 */
			public boolean test() {
				String html = getHTML();
				if (html == null)
					return false;
				return html.contains(expectedText);
			}
						
		}

		
		private final IWidgetReference browserWidget;

		public BrowserReference(IWidgetReference browserWidget) {
			this.browserWidget = browserWidget;
		}
				
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetReference#getWidget()
		 */
		public Object getWidget() {
			return browserWidget.getWidget();
		}

		/**
		 * Text access was introduced in 3.4
		 * TODO: consider throwing an exception here if not 3.4+
         * 
		 * @since Eclipse 3.4 where Browser.getText() is introduced
		 */
		public String getHTML() {
			final String text[] = new String[1];
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					text[0] = getText(getBrowser());
				}			
			});
			return text[0];
		}
		
		/**
		 * Using reflection to access <code>Browser.getText()</code> so that this
		 * will compile pre Eclipse 3.4.
		 */
		private static String getText(Browser browser) {
			if (browser == null)
				return null;
			
			try {
				Method m = browser.getClass().getMethod("getText", (Class[]) null);
				m.setAccessible(true);
				return (String) m.invoke(browser, (Object[]) null);
			} catch (SecurityException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
			return null;
		}
		
		public void execute(final String script) {
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					getBrowser().execute(script);
				}			
			});
		}
		public void setURL(final String url) {
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					getBrowser().setUrl(url);
				}			
			});
		}
		
		public ICondition htmlContains(String expectedText) {
			return new HtmlContainsCondition(expectedText);
		}
		
		public Browser getBrowser() {
			return (Browser)getWidget();
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetLocator#findAll(com.windowtester.runtime.IUIContext)
		 */
		public IWidgetLocator[] findAll(IUIContext ui) {
			return browserWidget.findAll(ui);
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.locator.IWidgetMatcher#matches(java.lang.Object)
		 */
		public boolean matches(Object widget) {
			return browserWidget.matches(widget);
		}
		
		
	}
	
	
	public static class BrowserLocator extends SWTWidgetLocator {

		private static final long serialVersionUID = 1L;

		public BrowserLocator() {
			super(Browser.class);
		}
		
		/* (non-Javadoc)
		 * @see com.windowtester.runtime.WidgetLocator#findAll(com.windowtester.runtime.IUIContext)
		 */
		@Override
		public IWidgetLocator[] findAll(IUIContext ui) {
			IWidgetLocator[] refs = super.findAll(ui);
			BrowserReference[] browsers = new BrowserReference[refs.length];
			for (int i = 0; i < browsers.length; i++) {
				browsers[i] = new BrowserReference((IWidgetReference)refs[i]);
			}
			return browsers;
		}
	}
	
	
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		//for some reason, the browser is not getting rendered unless the page is zoomed
		getUI().ensureThat(view("Welcome").isZoomed());
		
		browser = getBrowser();
	}
	
	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// Zoom out again
		getUI().click(2, new CTabItemLocator("Welcome"));
//		String html = getHTML(); 
//		System.out.println(html);
//		System.out.println("-------------------------------------");
//		System.out.println("-------------------------------------");		
//		super.tearDown();
	}
	
	public void testTutorialsLinkExists() throws Exception {
		if (!testPrereqs())
			return; //notice we just skip here...
		openPage(TUTORIALS);
		assertThat(browser.htmlContains(WT_GETTING_STARTED_LINK));
	}

	private boolean testPrereqs() {
		return EclipseUtil.isAtLeastVersion_34();
	}

	public void testWhatsNewLinkExists() throws Exception {
		if (!testPrereqs())
			return; //notice we just skip here...
		openPage(WHATS_NEW);
		assertThat(browser.htmlContains(WT_WHATS_NEW_LINK));
	}
	
	//TODO: enable once there is a link for wt-commons
	public void XtestSamples() throws Exception {		
		if (!testPrereqs())
			return; //notice we just skip here...
		openPage(SAMPLES);
		assertThat(browser.htmlContains(/* TODO: add link for wt-commons */ null));
	}
	
	
	private void assertThat(ICondition condition) {
		getUI().assertThat(condition);
	}


	private void openPage(String url) {
		browser.setURL(url);
	}

	private BrowserReference getBrowser() throws WidgetSearchException {
		return (BrowserReference) getUI().find(new BrowserLocator());
	}
	
	
}
