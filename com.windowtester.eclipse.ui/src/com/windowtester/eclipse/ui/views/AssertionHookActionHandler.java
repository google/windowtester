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
package com.windowtester.eclipse.ui.views;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import com.windowtester.codegen.ui.controller.TaggedInputDialog;
import com.windowtester.recorder.event.meta.RecorderAssertionHookAddedEvent;
import com.windowtester.recorder.ui.IEventSequenceModel;
import com.windowtester.ui.internal.corel.model.Event;

public class AssertionHookActionHandler {

	private final List _usedHookNames = new ArrayList(); //a list to keep track of used assertion names
	private TaggedInputDialog _assertionDialog;
	private final IEventSequenceModel _sequenceModel;

	public AssertionHookActionHandler(IEventSequenceModel sequenceModel) {
		_sequenceModel = sequenceModel;
	}

	void addAssertion() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				TaggedInputDialog d = getAssertionDialog();
				int result = d.open();
				if (result == Window.OK) {
					String hookName = d.getValue();
					_usedHookNames.add(hookName);
					doAddHook(hookName);
				}
			}
		});

	}

	protected void doAddHook(String hookName) {
		getSequenceModel().add(new Event(new RecorderAssertionHookAddedEvent(hookName)));
	}

	private TaggedInputDialog getAssertionDialog() {
		if (_assertionDialog == null) {
			_assertionDialog = new TaggedInputDialog(Display.getDefault()
					.getActiveShell(), "Insert Assertion Hook",
					"Input the name of an assertion method hook.",
					getHookNameSuggestion(), new IInputValidator() {
						public String isValid(String newText) {
							// TODO do we want to validate more (e.g., hook exists, etc?)
							return (newText == null || newText.length() == 0) ? " " : null; //$NON-NLS-1$
						}
					});
		}
		_assertionDialog.setValue(getHookNameSuggestion());
		return _assertionDialog;
	}

	private String getHookNameSuggestion() {
		String name = "assert_1";
		for (;;) {
			if (!_usedHookNames.contains(name))
				return name;
			name = increment(name);
		}
	}

	//TODO: this should be moved into a utility class...
	public String increment(String methodName) {
		ParsedName name = parseName(methodName);
		if (name.index == -1)
			name.index = 1; //skip zero
		else
			name.index++;
		return name.toString();
	}

	/**
	 * Parse this name into a name piece and an index
	 * @param name - the name to parse
	 * @return a ParsedName
	 */
	public ParsedName parseName(String name) {
		boolean done = false;
		StringBuffer sb = new StringBuffer();
		int i;
		for (i = name.length() - 1; !done && i >= 0; --i) {
			char ch = name.charAt(i);
			if (Character.isDigit(ch))
				sb.append(ch);
			else
				done = true;
		}
		ParsedName parsedName = new ParsedName();
		parsedName.index = sb.length() == 0 ? -1 : Integer.parseInt(sb
				.reverse().toString());
		parsedName.name = sb.length() == 0 ? name : name.substring(0, i + 2);
		return parsedName;
	}

	private IEventSequenceModel getSequenceModel() {
		return _sequenceModel;
	}

	/**
	 * A data holder class for parsed names. 
	 */
	static class ParsedName {
		/** The name component */
		public String name;
		/** The integer index */
		public int index;

		public String toString() {
			return name + index;
		}
	}

}
