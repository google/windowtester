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
package com.windowtester.runtime.swt.internal.text;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;

import com.windowtester.runtime.IUIContext;
import com.windowtester.runtime.swt.condition.SWTIdleCondition;
import com.windowtester.runtime.swt.internal.UIContextSWT;

/**
 * Uses incremental set text strategy for Text controls; else defaults to
 * {@link UIDriverTextEntryStrategy}.
 */
public class InsertTextEntryStrategy implements ITextEntryStrategy {

	/**
	 * @author kor
	 */
	public abstract static class CharTyper {

		/**
		 * @param c
		 * @param chr
		 */
		protected abstract void typeChar(Control c, char chr);

		/**
		 * Not accurate typer for combo
		 */
		public static class ComboCharTyper extends CharTyper {

			/**
			 * @see com.chartyper.CharTyper#typeChar(org.eclipse.swt.widgets.Control,
			 *      char)
			 */
			protected void typeChar(Control c, char chr) {
				Combo cmb = (Combo) c;
				String text = cmb.getText();
				StringBuffer buf = new StringBuffer();
				buf.append(text);
				buf.append(chr);
				cmb.setText(buf.toString());
			}

		}

		/**
		 * Not accurate typer for ccombo
		 */
		public static class CComboCharTyper extends CharTyper {

			/**
	         *
	         */
			public CComboCharTyper() {

			}

			/**
			 * @see com.chartyper.CharTyper#typeChar(org.eclipse.swt.widgets.Control,
			 *      char)
			 */
			protected void typeChar(Control c, char chr) {
				CCombo cmb = (CCombo) c;
				String text = cmb.getText();
				StringBuffer buf = new StringBuffer();
				buf.append(text);
				buf.append(chr);
				cmb.setText(buf.toString());
			}
		}

		/**
		 * typer for widgets who has key down handler method
		 */
		public static class ReflectionBasedTyper extends CharTyper {

			/**
			 * method that handles key events
			 */
			protected final Method typeMethod;

			/**
			 * @param clazz
			 * @param methodName
			 */
			public ReflectionBasedTyper(Class<?> clazz, String methodName) {
				try {
					typeMethod = clazz.getDeclaredMethod(methodName,
							new Class[] { Event.class });
					typeMethod.setAccessible(true);
				} catch (SecurityException e) {
					throw new LinkageError();
				} catch (NoSuchMethodException e) {
					throw new LinkageError();

				}
			}

			/**
			 * @see com.chartyper.CharTyper#typeChar(org.eclipse.swt.widgets.Control,
			 *      char)
			 */
			public void typeChar(Control tm, char c) {
				Event event = new Event();
				event.widget = tm;
				event.detail = SWT.KeyDown;
				event.display = tm.getDisplay();
				event.character = c;
				event.keyCode = 0;
				try {
					typeMethod.invoke(tm, new Object[] { event });
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InvocationTargetException e) {
					throw new RuntimeException(e.getCause());
				}
			}

		}

		/**
	     *
	     *
	     */
		public static class StyledTextTyper extends ReflectionBasedTyper {
			/**
	         *
	         */
			public StyledTextTyper() {
				super(StyledText.class, "handleKeyDown"); //$NON-NLS-1$
			}

			/**
			 * @return
			 */
			protected String getMethodName() {
				return "handleKeyDown"; //$NON-NLS-1$
			}
		}

		/**
	     *
	     *
	     */
		public static class TextCharTyper extends CharTyper {

			/**
			 * @see com.chartyper.CharTyper#typeChar(org.eclipse.swt.widgets.Control,
			 *      char)
			 */
			protected void typeChar(Control c, char chr) {
				Text tm = (Text) c;
				tm.insert(Character.toString(chr));
			}

		}

		private static HashMap<Class<?>, CharTyper> typers = new HashMap<Class<?>, CharTyper>();

		/**
		 * @param clz
		 * @param typer
		 */
		public static void register(Class<?> clz, CharTyper typer) {
			typers.put(clz, typer);
		}

		public static boolean hasTyper(Control c) {
			return typers.containsKey(c.getClass());
		}

		static {
			register(Combo.class, new ComboCharTyper());
			register(CCombo.class, new CComboCharTyper());
			register(StyledText.class, new StyledTextTyper());
			register(Text.class, new TextCharTyper());
		}

		/**
		 * @param c
		 * @param chr
		 * @return true if there is a registered typer for a given control
		 */
		public static boolean type(Control c, char chr) {
			CharTyper object = typers.get(c.getClass());
			if (object != null) {
				object.typeChar(c, chr);
				return true;
			}
			return false;
		}

		/**
		 * @param c
		 * @param text
		 * @return true if typing succeed
		 */
		public static boolean type(Control c, String text) {
			if (typers.containsKey(c.getClass())) {
				for (int a = 0; a < text.length(); a++) {
					type(c, text.charAt(a));
				}
				return true;
			}
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.windowtester.event.swt.text.ITextEntryStrategy#enterText(com.windowtester
	 * .swt.UIContext, java.lang.String)
	 */
	public void enterText(final IUIContext ui, final String txt) {
		final Display display = ((UIContextSWT) ui).getDisplay();
		// wait until UI settles down to guard against focus race condition
		new SWTIdleCondition(display).waitForIdle();

		// get the focus control
		final Control focusControl = getFocusControl(display);

		// if it's a Text, use our special insert logic
		if (CharTyper.hasTyper(focusControl)) {
			display.syncExec(new Runnable() {
				public void run() {
					CharTyper.type(focusControl, txt);
				}
			});
		} else {
			// or else, use our default strategy
			getDefaultStrategy().enterText(ui, txt);
		}
	}

	private ITextEntryStrategy getDefaultStrategy() {
		return TextEntryStrategy.getDefault();
	}

	private Control getFocusControl(final Display display) {
		final Control focusControl[] = new Control[1];
		display.syncExec(new Runnable() {
			public void run() {
				focusControl[0] = display.getFocusControl();
			}
		});
		return focusControl[0];
	}

	public void keyClick(IUIContext ui, int key) {
		// TODO Auto-generated method stub

	}

	public void keyClick(IUIContext ui, char key) {
		// TODO Auto-generated method stub

	}

	public void keyClick(IUIContext ui, int ctrl, char c) {
		// TODO Auto-generated method stub

	}

	public void keyDown(IUIContext ui, char key) {
		// TODO Auto-generated method stub

	}

	public void keyDown(IUIContext ui, int key) {
		// TODO Auto-generated method stub

	}

	public void keyUp(IUIContext ui, char key) {
		// TODO Auto-generated method stub

	}

	public void keyUp(IUIContext ui, int key) {
		// TODO Auto-generated method stub

	}


}
