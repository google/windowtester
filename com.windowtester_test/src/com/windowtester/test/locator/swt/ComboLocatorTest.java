package com.windowtester.test.locator.swt;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;

import abbot.tester.swt.ComboTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.swt.condition.shell.IShellConditionHandler;
import com.windowtester.runtime.swt.condition.shell.IShellMonitor;
import com.windowtester.runtime.swt.condition.shell.ShellCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.ButtonLocator;
import com.windowtester.runtime.swt.locator.ComboItemLocator;
import com.windowtester.runtime.swt.locator.ComboLocator;
import com.windowtester.runtime.swt.locator.NamedWidgetLocator;
import com.windowtester.test.locator.swt.shells.ComboTestShell;

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
public class ComboLocatorTest extends AbstractLocatorTest {

	
	ComboTestShell _window;
	
	
	private static final String DIALOG_TITLE = "Combo Event Responder";
	
	private boolean handled;
	private boolean selected;

	
	class DialogHandler extends ShellCondition implements IShellConditionHandler {
		public DialogHandler() {
			super(DIALOG_TITLE, true);
		}
		public void handle(IUIContext ui) throws WidgetSearchException {
			ui.click(new ButtonLocator("OK"));
			handled = true;
		}
	}
	
	 
	@Override
	public void uiSetup() {
		_window = new ComboTestShell();
		_window.open();
	} 
	
	@Override
	public void uiTearDown() {
		_window.getShell().dispose();
	}
	
	//https://fogbugz.instantiations.com/fogbugz/default.asp?45883
	public void testNamedComboLocator_basicSelections() throws WidgetSearchException  {

		IUIContext ui = getUI();		
		final Combo combo = _window.getCombo();
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				combo.setData("name", "named.combo");
			}
		});
		
		assertComboItemSelected(combo, null);
		ui.click(new ComboItemLocator("two", new NamedWidgetLocator("named.combo")));
		assertComboItemSelected(combo, "two");

	}
	
	
	public void testComboLocator_basicSelections() throws WidgetSearchException  {

		IUIContext ui = getUI();		
		Combo combo = _window.getCombo();
		
		assertComboItemSelected(combo, null);
		ui.click(new ComboItemLocator("two"));
		assertComboItemSelected(combo, "two");
		
		ui.click(new ComboItemLocator("three"));
		assertComboItemSelected(combo, "three");

		ui.click(new ComboItemLocator("one"));
		assertComboItemSelected(combo, "one");
		
		ui.click(new ComboItemLocator("five 5"));
		assertComboItemSelected(combo, "five 5");
	}
	
	// [author = Jaime] couldn't get this to fail, always seems to find the
	// combo items, user couldn't provide more information for us to produce code
	// leaving this test in here as a good regression test just the same.
	public void testComboLocator_advancedSelections1() throws WidgetSearchException  {

		IUIContext ui = getUI();		
		Combo combo = _window.getCombo();
		
		assertComboItemSelected(combo, null);
		
		ui.click(new ComboItemLocator("five 5"));
		assertComboItemSelected("See Case 39540", combo, "five 5");
		
		ui.click(new ComboItemLocator("many many many many words"));
		assertComboItemSelected("See Case 39540", combo, "many many many many words");
		
		ui.click(new ComboItemLocator("Subtree OF"));
		assertComboItemSelected("See Case 39540", combo, "Subtree OF");
		
		ui.click(new ComboItemLocator("tab\tconfusion"));
		assertComboItemSelected("See Case 39540", combo, "tab\tconfusion");
		
		ui.click(new ComboItemLocator("tab	confusion 2"));
		assertComboItemSelected("See Case 39540", combo, "tab	confusion 2");
	}
	
	// See Case 41110, attempted reproduction of this case
	public void testComboLocator_advancedSelections2() throws WidgetSearchException  {

		IUIContext ui = getUI();
		Combo combo = _window.getCombo();
		
		assertComboItemSelected(combo, null);
		
		ui.click(new ComboItemLocator("!="));
		assertComboItemSelected("See Case 41110", combo, "!=");
		
		ui.click(new ComboItemLocator("="));
		assertComboItemSelected("See Case 41110", combo, "=");
	}
	
	public void testComboLocator_assertionsTest() throws WidgetSearchException  {
		
		IUIContext ui = getUI();
		Combo combo = _window.getCombo();
		
		assertComboItemSelected(combo, null);
		
		for (int i = 0; i < ComboTestShell.COMBO_TEST_SHELL_ITEMS.length; i++) {
			ui.assertThat(new ComboItemLocator(ComboTestShell.COMBO_TEST_SHELL_ITEMS[i]).isVisible());
			ui.assertThat(new ComboItemLocator(ComboTestShell.COMBO_TEST_SHELL_ITEMS[i]).isVisible(true));
			ui.assertThat(new ComboItemLocator(ComboTestShell.COMBO_TEST_SHELL_ITEMS[i]+"_").isVisible(false));
		}
	}
	
	//https://fogbugz.instantiations.com/default.php?43648
	public void testBlockingOnListenerExec() throws Exception {
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				_window.getCombo().addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						System.out.println("Item selected");
						MessageDialog.openInformation(_window.getShell(), DIALOG_TITLE, "Item selected");
					};
				});
			}
		});
		
		IUIContext ui = getUI();
		IShellMonitor sm = (IShellMonitor) ui.getAdapter(IShellMonitor.class);
		sm.add(new DialogHandler());
		ui.click(new ComboItemLocator("two"));
		
		ui.assertThat(new ICondition() {
			public boolean test() {
				return handled;
			}
		});
		
		
	}
	
	//https://fogbugz.instantiations.com/default.php?43648
	public void testSelectionEventGenerated() throws Exception {
		IUIContext ui = getUI();
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				_window.getCombo().addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						System.out.println("Item selected");
						selected = true;
					};
				});
			}
		});
		ui.click(new ComboItemLocator("two"));
		ui.assertThat(new ICondition() {
			public boolean test() {
				return selected;
			}
		});
		
	}
	
	
	public void assertComboItemSelected(Combo combo, String item) {
		assertComboItemSelected(null, combo, item);
	}
	
	public void assertComboItemSelected(String message, Combo combo, String item) {
		
		ComboTester comboTester = new ComboTester();
		int index = comboTester.getSelectionIndex(combo);
		if (index == -1) {
			assertNull(item);
			return;
		}
		assertEquals(message, comboTester.getItem(combo, index), item);
		
		getUI().assertThat(new ComboLocator().hasText(item));
	}
	
}
