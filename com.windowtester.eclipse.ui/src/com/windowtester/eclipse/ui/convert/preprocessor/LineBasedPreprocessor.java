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
package com.windowtester.eclipse.ui.convert.preprocessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A preprocessor that processes files line by line as opposed to an XML based
 * preprocessor that would follow the XML based structure.
 */
public abstract class LineBasedPreprocessor
{
	protected final List<LineProcessor> lineProcessors = new ArrayList<LineProcessor>(7);
	private File currentFile;
	
	/**
	 * Preprocess the specified file if it exists. If no changes are necessary, then the
	 * file is not rewritten.
	 * 
	 * @param file the file to be processed
	 * @return <code>true</code> if the file exists and was modified
	 */
	public boolean processIfExists(File file) throws IOException {
		return file.exists() && process(file);
	}

	/**
	 * Preprocess all files in the specified directory recursively. If no changes are necessary, then the file is not
	 * modified.
	 * 
	 * @param dir the directory to be recursively processed
	 * @return <code>true</code> if one or more files were modified
	 */
	public boolean processAll(File dir) throws IOException {
		boolean modified = false;
		String[] names = dir.list();
		for (int i = 0; i < names.length; i++) {
			File child = new File(dir, names[i]);
			if (child.isDirectory()) {
				if (processAll(child))
					modified = true;
			}
			else {
				if (process(child))
					modified = true;
			}
		}
		return modified;
	}
	
	/**
	 * Preprocess the specified file. If no changes were necessary, the the file is not modified.
	 * 
	 * @param file the file to be processed
	 * @return <code>true</code> if the file was modified
	 */
	public boolean process(File file) throws IOException {
		currentFile = file;
		String content = process0(file);
		currentFile = null;
		if (content == null)
			return false;
		FileWriter writer = new FileWriter(file);
		try {
			writer.write(content);
		}
		finally {
			writer.close();
		}
		return true;
	}
	
	/**
	 * Answer the file being currently processed or <code>null</code> if none
	 */
	public File getCurrentFile() {
		return currentFile;
	}

	/**
	 * For testing purposes, preprocess the specified file and return the preprocessed
	 * content.
	 * 
	 * @param file the file to be processed
	 * @return the preprocessed content or <code>null</code> if no change.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public final String process0(File file) throws IOException {
		FileReader fileReader = new FileReader(file);
		try {
			return process(fileReader);
		}
		finally {
			fileReader.close();
		}
	}

	/**
	 * For testing purposes, preprocess the specified reader content and return the
	 * preprocessed content.
	 * 
	 * @param file the file to be processed
	 * @return the preprocessed content or <code>null</code> if no change.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public final String process(Reader reader) throws IOException {
		for (LineProcessor proc : lineProcessors)
			proc.reset();
		boolean modified = false;
		StringWriter stringWriter = new StringWriter(4096);
		PrintWriter writer = new PrintWriter(stringWriter);
		LineNumberReader lineReader = new LineNumberReader(new BufferedReader(reader));
		startProcess(lineReader, writer);
		while (true) {
			String oldLine = lineReader.readLine();
			if (oldLine == null)
				break;
			String newLine = processLine(oldLine);
			modified = modified || !oldLine.equals(newLine);
			if (newLine != null)
				writer.println(newLine);
		}
		endProcess(writer);
		String content = stringWriter.toString();
		if (!modified)
			content = null;
		return content;
	}

	/**
	 * Called before the processing begins. Subclasses may override
	 * @param lineReader the reader containing the content to be processed
	 * @param writer the writer to hold the processed content.
	 */
	protected void startProcess(LineNumberReader lineReader, PrintWriter writer) {
	}

	/**
	 * Called after the processing has finished. Subclasses may override
	 * @param writer the writer holding the processed content.
	 */
	protected void endProcess(PrintWriter writer) {
	}

	/**
	 * Preprocess the specified line.
	 * 
	 * @param oldLine the original line
	 * @return the new line or <code>null</code> if the line should be removed
	 */
	protected final String processLine(String line) {
		for (LineProcessor proc : lineProcessors) {
			line = proc.process(line);
			if (line == null)
				return null;
		}
		return line;
	}

	//============================================================================

	protected interface LineProcessor
	{
		public void reset();

		public String process(String line);
	}

	/**
	 * Replace a source pattern with the replacement text
	 */
	protected class LineReplacement
		implements LineProcessor
	{
		private final Pattern pattern;
		private final String replacement;

		public LineReplacement(Pattern pattern, String replacement) {
			this.pattern = pattern;
			this.replacement = replacement;
		}

		public void reset() {
		}

		public String process(String line) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find())
				line = matcher.replaceAll(replacement);
			return line;
		}
	}

	/**
	 * Remove duplicate lines
	 */
	protected class RemoveLines
		implements LineProcessor
	{
		private final Pattern pattern;

		public RemoveLines(Pattern pattern) {
			this.pattern = pattern;
		}

		public void reset() {
		}

		public String process(String line) {
			if (pattern.matcher(line).find())
				return null;
			return line;
		}
	}

	/**
	 * Remove duplicate lines
	 */
	protected class RemoveDuplicateLines
		implements LineProcessor
	{
		private final Pattern pattern;
		private boolean found;

		public RemoveDuplicateLines(Pattern pattern) {
			this.pattern = pattern;
		}

		public void reset() {
			found = false;
		}

		public String process(String line) {
			if (pattern.matcher(line).find()) {
				if (found)
					return null;
				found = true;
			}
			return line;
		}
	}
}
