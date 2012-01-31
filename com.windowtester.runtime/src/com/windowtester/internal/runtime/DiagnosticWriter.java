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
package com.windowtester.internal.runtime;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * A collector of diagnostic information via the
 * {@link IDiagnosticParticipant#diagnose(IDiagnostic)} method.
 */
public class DiagnosticWriter
	implements IDiagnostic
{
	private final StringWriter stringWriter = new StringWriter();
	private final PrintWriter printWriter = new PrintWriter(stringWriter);
	private int depth = 0;

	/**
	 * Output diagnostic information.
	 * 
	 * @param key the diagnostic key associated with this value
	 * @param value the diagnostic value to be collected. If value implements
	 *            IDiagnosticWriter, then its
	 *            {@link IDiagnosticParticipant#diagnose(IDiagnostic)} method will be
	 *            called
	 */
	public void diagnose(String key, Object value) {
		if (stringWriter.getBuffer().length() > 0) {
			printWriter.println();
			for (int i = 0; i < depth; i++)
				printWriter.print("  ");
		}
		printWriter.print(key);
		printWriter.print(": ");
		if (value == null) {
			printWriter.println("null");
		}
		else if (value instanceof IDiagnosticParticipant) {
			depth++;
			((IDiagnosticParticipant) value).diagnose(this);
			depth--;
		}
		else {
			attribute("toString", String.valueOf(value));
			attribute("class", value.getClass().getName());
		}
	}

	public void attribute(String key, String value) {
		printWriter.println();
		printWriter.print(key);
		printWriter.print("=");
		printWriter.print(value);
	}

	public void attribute(String key, int value) {
		attribute(key, String.valueOf(value));
	}

	public void attribute(String key, boolean value) {
		attribute(key, String.valueOf(value));
	}

	public String toString() {
		return stringWriter.toString();
	}
}
