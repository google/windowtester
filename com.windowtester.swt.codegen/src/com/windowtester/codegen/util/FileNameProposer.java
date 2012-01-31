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
package com.windowtester.codegen.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/**
 * A utility class that proposes fresh file names given a directory.
 */
public class FileNameProposer {

    /** A default (or suffix) filename */
    private final String _defaultFileName;
    /** The target directory */
    private final File _dir;
    /** The extension of stream files */
    private static final String STREAM_EXTENSION = ".xml"; //TODO: move constant

    /**
     * Create an instance.
     * @param dir - the root directory
     * @param defaultFileName - a default file name 
     */
    public FileNameProposer(String defaultFileName, File dir) {
        _defaultFileName = defaultFileName;
        _dir = dir;
    }

    /**
     * Propose a fresh filename which is based on the most recently modified
     * file with an int index (e.g., "newFile3.xml").
     * @return a fresh String file name 
     */
    public String propose() {  
        File recent = getMostRecentltyModifiedFile(_dir);
        String name = recent == null ? _defaultFileName : recent.getName();       
        return findAvailableNextIncrement(name, _dir);
    }

    
    /**
     * Given a name and a directory, find the next available increment of
     * it's index (e.g., if "newFile1.xml" and "newFile2.xml" exist,
     * "newFile3.xml" would be proposed). 
     * @param name - the file name
     * @param dir - the target directory
     * @return a String representing the next available name
     */
    private static String findAvailableNextIncrement(String name, File dir) {

        name = name.split("\\.")[0];
        ParsedName parsedName = parseName(name);
        int index = parsedName.index;
        name = parsedName.name;
        File[] files = dir.listFiles();
        String proposal;
        boolean matched;
        for ( ; ; ) {
            matched = false;
            proposal = name + index++ + STREAM_EXTENSION;
            if (files == null)
                return proposal;
            for (int i = 0; !matched && i < files.length; i++) {
                if (files[i].getName().equals(proposal))
                    matched = true;
            }
            if (!matched)
                return proposal;
        }        
    }
    

    /**
     * Parse this name into a name piece and an index
     * @param name - the name to parse
     * @return a ParsedName
     */
    private static ParsedName parseName(String name) {
        boolean done = false;
        StringBuffer sb = new StringBuffer();
        int i;
        for (i=name.length()-1; !done && i >= 0; --i) {
            char ch = name.charAt(i);
            if (Character.isDigit(ch))
                sb.append(ch);
            else
                   done = true;
        }
        ParsedName parsedName = new ParsedName();
        parsedName.index = sb.length() == 0 ? -1   : Integer.parseInt(sb.reverse().toString());
        parsedName.name  = sb.length() == 0 ? name : name.substring(0,i+2);
        return parsedName;
    }

    /**
     * Get the index of the given name (e.g., "newFile12.xml" -> 12)
     * @param name - the name to parse
     * @return an int index, or -1 if none is present
     */
    private static int getIndex(String name) {
        boolean done = false;
        StringBuffer sb = new StringBuffer();
        for (int i=name.length()-1; !done && i >= 0; --i) {
            char ch = name.charAt(i);
            if (Character.isDigit(ch))
                sb.append(ch);
            else
                   done = true;
        }
        return sb.length() == 0 ? -1 : Integer.parseInt(sb.reverse().toString());
    }

    /**
     * Get the most recently edited file (irrespective of extension).
     * @param dir - the directory in which to perform the search
     * @return the most recently edited file
     */
    public static File getMostRecentltyModifiedFile(File dir) {
        return getMostRecentltyModifiedFile(dir, null);
    }
    
    /**
     * Get the most recently edited file with a given extension.
     * @param dir - the directory in which to perform the search
     * @param extension - extension by which to filter file selections (null if extension is ignored)
     * @return the most recently edited file
     */
    public static File getMostRecentltyModifiedFile(File dir, String extension) {
        File[] files = dir.listFiles();
        if (files == null || files.length == 0)
            return null;
        //handle extension-based filtering
        if (extension != null) {
            Collection filtered = new ArrayList();
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().endsWith(extension))
                    filtered.add(files[i]);
            }
            files = (File[]) filtered.toArray(new File[]{});
        }    
        Arrays.sort(files, new Comparator(){
            public int compare(Object f1, Object f2) {
//not supported in JDK1.3
//                Long l1 = Long.valueOf(((File)f1).lastModified());
//                Long l2 = Long.valueOf(((File)f2).lastModified());
                String l1 = Long.toString(((File)f1).lastModified());
                String l2 = Long.toString(((File)f2).lastModified());
                return l2.compareTo(l1);
            }
        });
        return files[0];
    }
      
    public static void main(String[] args) {
        System.out.println(getIndex("file13"));
    }
    
    /**
     * A data holder class for parsed names. 
     */
    static class ParsedName {
        /** The name component */
        public String name;
        /** The integer index */
        public int index;
    }    
}
