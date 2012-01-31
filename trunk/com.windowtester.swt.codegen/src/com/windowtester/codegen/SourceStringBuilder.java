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
package com.windowtester.codegen;

import java.util.Iterator;

import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import com.windowtester.codegen.assembly.unit.ClassUnit;
import com.windowtester.codegen.assembly.unit.FieldUnit;
import com.windowtester.codegen.assembly.unit.ImportUnit;
import com.windowtester.codegen.assembly.unit.MethodUnit;
import com.windowtester.codegen.assembly.unit.ModifierList;
import com.windowtester.internal.debug.Logger;
import com.windowtester.runtime.swt.internal.preferences.ICodeGenConstants;

/**
 * A builder that produces a source string, given a test case builder.
 */
public class SourceStringBuilder {

	//TODO[author=pq] newlines should be properly handled universally
    protected String NEW_LINE = ICodeGenConstants.NEW_LINE;

    /** The test case under construction */
    private final ITestCaseBuilder testcase;

    /**
     * Create an instance.
     * @param testcase - the testcase under construction
     */
    public SourceStringBuilder(ITestCaseBuilder testcase) {
        this.testcase = testcase;
    }

    /**
     * Build a String representation of the test case under construction.
     * @return a String representation of the test case
     */
    public String build() {
    	StringBuffer sb = new StringBuffer();
        addPackage(sb);
        addImports(sb);
        addClassDecl(sb);
        addFields(sb);
        addClasses(sb);
        addMethods(sb);
        close(sb);
 
        String formatted = sb.toString();    
        formatted = format(sb.toString());

        return formatted;
    }
    


	/**
     * Format the given source String
     * @param source - the source to format
     * @return the source formatted
     */
    public String format(String source) {
        
    	TextEdit textEdit = null;
    	try {
    		textEdit = ToolFactory.createCodeFormatter(null).format(
                CodeFormatter.K_COMPILATION_UNIT, source, 0,
                source.length(), 0, System.getProperty("line.separator"));
    	} catch(Throwable e) {
    		Logger.log("An error occured in formatting a source string.", e);
    		//this happens when run outside the platform (during testing)
    		//in this case we just return the unformatted original String
    		return source;
    	}
        
        String formattedContent;
        if (textEdit != null) {
            Document document = new Document(source);
            try {
                textEdit.apply(document);
            } catch (BadLocationException e) {
				Logger.log("An error occured in formatting a source string.", e);
            }
            formattedContent = document.get();
        } else {
            formattedContent = source;
        }
        return formattedContent;
    }
    
    /**
     * Add the test case's methods to the given buffer
     * @param sb - the buffer
     */
    private void addMethods(StringBuffer sb) {
        for (Iterator iter = testcase.getMethods().iterator(); iter.hasNext();) {
            MethodUnit method = (MethodUnit) iter.next();
            sb.append(method.getBody()).append(NEW_LINE);
        }
    }

    private void addClasses(StringBuffer sb) {
        for (Iterator iter = testcase.getClasses().iterator(); iter.hasNext();) {
        	ClassUnit cls = (ClassUnit) iter.next();
            sb.append(cls.getBody()).append(NEW_LINE);
        }
	}
    
    /**
     * Add the test case's fields to the given buffer
     * @param sb - the buffer
     */
    private void addFields(StringBuffer sb) {
        for (Iterator iter = testcase.getFields().iterator(); iter.hasNext();) {
            FieldUnit field = (FieldUnit) iter.next();
            sb.append(field.getBody()).append(NEW_LINE);
        }
    }

    /**
     * Add the test case's class decl to the given buffer
     * @param sb - the buffer
     */
    private void addClassDecl(StringBuffer sb) {
        sb.append("public class ").append(testcase.getName());
        String sc = testcase.getExtends();
        if (sc != null && !sc.equals(""))
            sb.append(" extends ").append(sc).append(' ');
        sb.append("{").append(NEW_LINE).append(NEW_LINE);
    }

    /**
     * Add the test case's pacage declaration to the given buffer
     * @param sb - the buffer
     */
    private void addPackage(StringBuffer sb) {
    	String pkg = testcase.getPackage();
    	if (pkg != null && !pkg.trim().equals(""))
    		sb.append("package ").append(pkg).append(';')
                .append(NEW_LINE).append(NEW_LINE);
    }

    /**
     * Add the test case's imports to the given buffer
     * @param sb - the buffer
     */
    private void addImports(StringBuffer sb) {
        for (Iterator iter = testcase.getImports().iterator(); iter.hasNext();) {
            ImportUnit element = (ImportUnit) iter.next();
            addImport(sb, element);
            sb.append(
                    NEW_LINE);
        }
        sb.append(NEW_LINE).append(NEW_LINE);
    }

	public void addImport(StringBuffer sb, ImportUnit element) {
		sb.append("import ");
		ModifierList modifiers = element.getModifiers();
		sb.append(modifiers.toString());
		if (!modifiers.isEmpty())
			sb.append(' '); //mods to have a trailing space
		sb.append(element.getName()).append(';');
	}

    /**
     * Close the given test case.
     * @param sb - the buffer
     */
    private void close(StringBuffer sb) {
        sb.append('}');
    }
}