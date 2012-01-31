package com.windowtester.test.locator.swt;

import java.awt.event.KeyEvent;
import java.util.concurrent.Callable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

import abbot.tester.swt.TextTester;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.MultipleWidgetsFoundException;
import com.windowtester.runtime.WT;
import com.windowtester.runtime.WidgetNotFoundException;
import com.windowtester.runtime.WidgetSearchException;
import com.windowtester.runtime.condition.ICondition;
import com.windowtester.runtime.internal.concurrent.VoidCallable;
import com.windowtester.runtime.locator.WidgetReference;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.widgets.DisplayReference;
import com.windowtester.runtime.swt.locator.MenuItemLocator;
import com.windowtester.runtime.swt.locator.TextLocator;
import com.windowtester.test.locator.swt.shells.TextTestShell;
import com.windowtester.test.util.TypingLinuxHelper;
import com.windowtester.test.util.junit.OS;
import com.windowtester.test.util.junit.RunOn;

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
public class TextEntryTest extends AbstractLocatorTest {

	
	public static class CaretIsAtIndexCondition implements ICondition {

		private final int index;
		private final Text text;

		public CaretIsAtIndexCondition(Text text, int index) {
			this.text = text;
			this.index = index;
		}

		/* (non-Javadoc)
		 * @see com.windowtester.runtime.condition.ICondition#test()
		 */
		public boolean test() {
			return DisplayReference.getDefault().execute(new Callable<Boolean>(){
				public Boolean call() throws Exception {
					return text.getCaretPosition() == index;
				}
			});
		}
	}
	
	
	TextTestShell window;
	
	 
	@Override
	public void uiSetup() {
		window = new TextTestShell();
		window.open();
	} 
	
	@Override
	public void uiTearDown() {
		window.getShell().dispose();
	}
	
	



	public static void enterText(IUIContext ui, final String text){
		final Display display = Display.getDefault();
		//wait until UI settles down to guard against focus race condition
		new SWTIdleCondition(display).waitForIdle();
		
		//get the focus control
		final Control focusControl = getFocusControl(display);
		
		//if it's a Text, use our special insert logic
		if (focusControl instanceof Text) {
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					Text widget = (Text)focusControl;
					for (int i = 0; i < text.length(); i++) {
						widget.insert(Character.toString(text.charAt(i)));
					}
				}
			});
		} else if (focusControl instanceof StyledText) {
			DisplayReference.getDefault().execute(new VoidCallable() {
				public void call() throws Exception {
					StyledText widget = (StyledText)focusControl;
					for (int i = 0; i < text.length(); i++) {
						widget.insert(Character.toString(text.charAt(i)));
					}
				}
			});
		} else {
			try{
			TypingLinuxHelper.switchToInsertStrategyIfNeeded();
			//or else, use our default strategy
			ui.enterText(text);
			}finally{
				TypingLinuxHelper.restoreOriginalStrategy();	
			}
		}
	}
	
	

	private static Control getFocusControl(final Display display) {
		final Control focusControl[] = new Control[1];
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				focusControl[0] = display.getFocusControl();
			}
		});
		return focusControl[0];
	}
	
	
	private void idle() {
		//TODO: need IdleCondition
		DisplayReference.getDefault().execute(new VoidCallable() {
			public void call() throws Exception {
				while(Display.getDefault().readAndDispatch());
			}
		});
	}
	
	private void tryTestWithText(final String strText) throws WidgetSearchException {

		IUIContext ui = getUI();
		
		Text text = window.getText();
		
		//assert initial state
		assertEquals("", new TextTester().getText(text));
		
		ui.click(new TextLocator());
		
		enterText(ui, strText);
		idle();
		assertEquals(strText, new TextTester().getText(text));
	}

	public void testTextEntry() throws WidgetSearchException {
		tryTestWithText("�f�b�l���s�f�b�ʫȮb�b�l�[��");
	}

	@RunOn(value=OS.ALL, but={OS.OSX})
	public void testEndKeyEntry() throws Exception {
		IUIContext ui = getUI();
		
		Text text = window.getText();
		ui.click(new TextLocator());
		
		ui.enterText("foo");
		ui.keyClick(WT.ARROW_LEFT);
		ui.keyClick(WT.ARROW_LEFT);
		ui.keyClick(WT.ARROW_LEFT);
		
		// TODO[pq]: push this condition into the text locator
		ui.assertThat(new CaretIsAtIndexCondition(text, 0));		
		ui.keyClick(WT.END);
		ui.assertThat(new CaretIsAtIndexCondition(text, 3));
	}
	
	public void testComplexTextEntry() throws WidgetSearchException {
		tryTestWithText("a!\"$%&/()=?*'_:;><,.-#+|~\\}][{'\")");
	}
	
	public void testPeriodTextEntry() throws WidgetSearchException {
		tryTestWithText("periods:........,numpad periods:...........");
	}
	
	public void testMinusTextEntry() throws WidgetSearchException {
		char keypadSubtract = (char)SWT.KEYPAD_SUBTRACT;
		String keypadSubtractSequence = new String(new char[] {keypadSubtract,
				keypadSubtract, keypadSubtract, keypadSubtract, keypadSubtract,
				keypadSubtract, keypadSubtract, keypadSubtract, keypadSubtract
		});
		
		tryTestWithText("minus:--------------,numpad minus:"+keypadSubtractSequence);
	}
	
	public void testVK_MinusTextEntry2() throws WidgetSearchException {
		char keypadVKMinus = (char)KeyEvent.VK_MINUS;
		String keypadVKMinusSequence = new String(new char[] {keypadVKMinus,
				keypadVKMinus, keypadVKMinus, keypadVKMinus, keypadVKMinus,
				keypadVKMinus, keypadVKMinus, keypadVKMinus, keypadVKMinus
		});
		
		tryTestWithText("minus:--------------,vk minus:"+keypadVKMinusSequence);
	}
	
	public void testPlusTextEntry() throws WidgetSearchException {
		char keypadAdd = (char)SWT.KEYPAD_ADD;
		String keypadAddSequence = new String(new char[] {keypadAdd,
				keypadAdd, keypadAdd, keypadAdd, keypadAdd,
				keypadAdd, keypadAdd, keypadAdd, keypadAdd
		});
		
		tryTestWithText("add:++++++++,numpad add:"+keypadAddSequence);
	}
	
	public void testMultiplyTextEntry() throws WidgetSearchException {
		char keypadMultiply = (char)SWT.KEYPAD_MULTIPLY;
		String keypadMultiplySequence = new String(new char[] {keypadMultiply,
				keypadMultiply, keypadMultiply, keypadMultiply, keypadMultiply,
				keypadMultiply, keypadMultiply, keypadMultiply, keypadMultiply
		});
		
		tryTestWithText("multiply:********,numpad multiply:"+keypadMultiplySequence);
	}
	
	public void testDivideTextEntry() throws WidgetSearchException {
		char keypadDivide = (char)SWT.KEYPAD_DIVIDE;
		String keypadDivideSequence = new String(new char[] {keypadDivide,
				keypadDivide, keypadDivide, keypadDivide, keypadDivide,
				keypadDivide, keypadDivide, keypadDivide, keypadDivide
		});
		
		tryTestWithText("divide:///////////,numpad divide:"+keypadDivideSequence);
	}
	
	public void testContextMenuSelection1() throws WidgetSearchException {
		
		IUIContext ui = getUI();
		
		Text text = window.getText();
		boolean previousState = window.ALPHA;
		ui.contextClick(new WidgetReference(text), new MenuItemLocator("alpha"));
		assertFalse(previousState == window.ALPHA);
	}
	
	
	public void testContextMenuFailedSelection() {		
		IUIContext ui = getUI();

		Text text = window.getText();
		try {
			ui.contextClick(new WidgetReference(text), new MenuItemLocator("bogus"));
			fail("should have thrown a WNF exception");
		} catch (MultipleWidgetsFoundException e) {
			fail("should not have thrown " + e);
		} catch (WidgetNotFoundException e) {
			//pass
		} catch (WidgetSearchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	

	
}
