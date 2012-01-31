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

import java.lang.reflect.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.KeyStroke;

import abbot.Log;
import abbot.Platform;

/** Provides read of local-specific mappings for virtual keycode-based
    KeyStrokes to characters and vice versa.  
    The map format is a properties file with each line containing an entry of
    the form<br>
    <code>VKNAME.MOD=VALUE</code><br>
    The VKNAME is the String suffix of the KeyEvent VK_ keycode.  MOD is the
    integer value of the current modifier mask (assumes only a single modifier
    has any effect on key output, interesting values are considered to be 0,
    1, 2, 8).  VALUE is the char value of the KEY_TYPED keyChar
    corresponding to the VK_ keycode and modifiers, as an integer value.
 */ 
public class KeyStrokeMap implements KeyStrokeMapProvider {

    /** Map of Characters to virtual keycode-based KeyStrokes. */
    private static Map keycodes = getKeyStrokeMap();
    /** Map of Characters to the default keycode-based KeyStrokes */
    private static Map defaultKeycodes = getDefaultKeyStrokeMap();
    /** Map of keycode-based KeyStrokes to Characters. */
    private static Map chars = getCharacterMap();
    /** Map of Characters to accent keys */
    private static Map accentKeys = getAccentCharMap();

    
    
    /** Return the keycode-based KeyStroke corresponding to the given
     * character, as best we can guess it, or null if we don't know how to
     * generate it.  
     */
    public static KeyStroke getKeyStroke(char ch) {
        return (KeyStroke)keycodes.get(new Character(ch));
    }

    /**
     * Return the accent key associated with the char, else return 0
     * @param ch
     * @return 1 - circumflex ^
     * 		   2 - umlaut
     *         3 - grave accent `
     *         4 - acute accent  	
     */
    public static int getAccentKey(char ch){
    	String o = (String)accentKeys.get(new Character(ch));
    	return o != null ? Integer.parseInt(o) : 0;
    }
    
    /** Given a keycode-based KeyStroke, return the equivalent character.
     * Defined properly for US keyboards only.  Please contribute your own.
     * @return KeyEvent.VK_UNDEFINED if the result is unknown.
     */
    public static char getChar(KeyStroke ks) {
        Character ch = (Character)chars.get(ks);
        if (ch == null) {
            // Try again, but strip all modifiers but shift
            int mask = ks.getModifiers() & ~KeyEvent.SHIFT_MASK;
            ks = KeyStroke.getKeyStroke(ks.getKeyCode(), mask);
            ch = (Character)chars.get(ks);
            if (ch == null)
                return KeyEvent.CHAR_UNDEFINED;
        }
        return ch.charValue();
    }

    private static KeyStrokeMapProvider generator = null;
    /** If available, provide a dedicated class to provide mappings between
     * keystrokes and generated characters.
     */
    private static KeyStrokeMapProvider getGenerator() {
        if (generator == null) {
            try {
                String gname =
                    System.getProperty("abbot.keystroke_map_generator",
                    					"com.windowtester.runtime.swt.internal.text.KeyStrokeMap");
                                      // "abbot.tester.KeyStrokeMap");
                if (gname != null) {
                    generator = (KeyStrokeMapProvider)
                        Class.forName(gname).newInstance();
                }
            }
            catch(Exception e) {
                Log.warn(e);
            }
        }
        return generator;
    }

    private static Map getCharacterMap() {
        KeyStrokeMapProvider generator = getGenerator();
        Map m = generator != null
            ? generator.loadCharacterMap() : null;
        return m != null ? m : generateCharacterMappings();
    }
    
    private static Map getAccentCharMap(){
    	KeyStrokeMapProvider generator = getGenerator();
        Map m = generator != null
            ? generator.loadAccentKeyMap() : null;
        return m != null ? m : null;
    	
    }

    /** Generate a map from characters to virtual keycode-based KeyStrokes. */
    private static Map generateCharacterMappings() {
        Log.debug("Generating default character mappings");
        Map map = new HashMap();
        Iterator iter = keycodes.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            map.put(keycodes.get(key), key);
        }
        return map;
    }

    private static Map getKeyStrokeMap() {
        KeyStrokeMapProvider generator = getGenerator();
        Map m = generator != null
            ? generator.loadKeyStrokeMap() : null;
        return m != null ? m : generateKeyStrokeMappings(false);
    }

    /**
     * Generate the mapping between characters and key codes.   This is
     * invoked exactly once per VM invocation.  
     * We don't have complete coverage, so if you use this fallback map in AWT
     * mode some events may be missing that would otherwise be generated in
     * robot mode.
     */
    private static Map generateKeyStrokeMappings(boolean english) {
        Log.debug("Generating default keystroke mappings");
        // character, keycode, modifiers
        int shift = InputEvent.SHIFT_MASK;
        //int alt = InputEvent.ALT_MASK;
        //int altg = InputEvent.ALT_GRAPH_MASK;
        int ctrl = InputEvent.CTRL_MASK;
        //int meta = InputEvent.META_MASK;
        // These are assumed to be standard across all keyboards (?)
        int[][] universalMappings = {
            { '', KeyEvent.VK_ESCAPE, 0 }, // No escape sequence exists
            { '\b', KeyEvent.VK_BACK_SPACE, 0 },
            { '', KeyEvent.VK_DELETE, 0 }, // None for this one either
            { '\n', KeyEvent.VK_ENTER, 0 },
            { '\r', KeyEvent.VK_ENTER, 0 },
        };
        // Add to these as needed; note that this is based on a US keyboard
        // mapping, and will likely fail for others.
        int[][] mappings = {
            { ' ', KeyEvent.VK_SPACE, 0, },
            { '\t', KeyEvent.VK_TAB, 0, },
            { '~', KeyEvent.VK_BACK_QUOTE, shift, },
            { '`', KeyEvent.VK_BACK_QUOTE, 0, },
            { '!', KeyEvent.VK_1, shift, },
            { '@', KeyEvent.VK_2, shift, },
            { '#', KeyEvent.VK_3, shift, },
            { '$', KeyEvent.VK_4, shift, },
            { '%', KeyEvent.VK_5, shift, },
            { '^', KeyEvent.VK_6, shift, },
            { '&', KeyEvent.VK_7, shift, },
            { '*', KeyEvent.VK_8, shift, },
            { '(', KeyEvent.VK_9, shift, },
            { ')', KeyEvent.VK_0, shift, },
            { '-', KeyEvent.VK_MINUS, 0, },
            { '_', KeyEvent.VK_MINUS, shift, },
            { '=', KeyEvent.VK_EQUALS, 0, },
            { '+', KeyEvent.VK_EQUALS, shift, },
            { '[', KeyEvent.VK_OPEN_BRACKET, 0, },
            { '{', KeyEvent.VK_OPEN_BRACKET, shift, },
            // NOTE: The following does NOT produce a left brace
            //{ '{', KeyEvent.VK_BRACELEFT, 0, },
            { ']', KeyEvent.VK_CLOSE_BRACKET, 0, },
            { '}', KeyEvent.VK_CLOSE_BRACKET, shift, },
            { '|', KeyEvent.VK_BACK_SLASH, shift, },
            { ';', KeyEvent.VK_SEMICOLON, 0, },
            { ':', KeyEvent.VK_SEMICOLON, shift, },
            { ',', KeyEvent.VK_COMMA, 0, },
            { '<', KeyEvent.VK_COMMA, shift, },
            { '.', KeyEvent.VK_PERIOD, 0, },
            { '>', KeyEvent.VK_PERIOD, shift, },
            { '/', KeyEvent.VK_SLASH, 0, },
            { '?', KeyEvent.VK_SLASH, shift, },
            { '\\', KeyEvent.VK_BACK_SLASH, 0, },
            { '|', KeyEvent.VK_BACK_SLASH, shift, },
            { '\'', KeyEvent.VK_QUOTE, 0, },
            { '"', KeyEvent.VK_QUOTE, shift, },
        };
        HashMap map = new HashMap();
        // Universal mappings
        for (int i=0;i < universalMappings.length;i++) {
            int[] entry = universalMappings[i];
            KeyStroke stroke = KeyStroke.getKeyStroke(entry[1], entry[2]);
            map.put(new Character((char)entry[0]), stroke);
        }

        // If the locale is not en_US/GB, provide only a very basic map and
        // rely on key_typed events instead
        if (!english){
	        Locale locale = Locale.getDefault();
	        if (!Locale.US.equals(locale) && !Locale.UK.equals(locale)) {
	            Log.debug("Not US: " + locale);
	            return map;
	        }
        }
        // Basic symbol/punctuation mappings
        for (int i=0;i < mappings.length;i++) {
            int[] entry = mappings[i];
            KeyStroke stroke = KeyStroke.getKeyStroke(entry[1], entry[2]);
            map.put(new Character((char)entry[0]), stroke);
        }
        // Lowercase
        for (int i='a';i <= 'z';i++) {
            KeyStroke stroke = 
                KeyStroke.getKeyStroke(KeyEvent.VK_A + i - 'a', 0);
            map.put(new Character((char)i), stroke);
            // control characters
            stroke = KeyStroke.getKeyStroke(KeyEvent.VK_A + i - 'a', ctrl);
            Character key = new Character((char)(i - 'a' + 1));
            // Make sure we don't overwrite something already there
            if (map.get(key) == null) {
                map.put(key, stroke);
            }
        }
        // Capitals
        for (int i='A';i <= 'Z';i++) {
            KeyStroke stroke = 
                KeyStroke.getKeyStroke(KeyEvent.VK_A + i - 'A', shift);
            map.put(new Character((char)i), stroke);
        }
        // digits
        for (int i='0';i <= '9';i++) {
            KeyStroke stroke = 
                KeyStroke.getKeyStroke(KeyEvent.VK_0 + i - '0', 0);
            map.put(new Character((char)i), stroke);
        }
        return map;
    }

    private static Map characterMap = null;
    private static Map keyStrokeMap = null;
    private static Map accentMap = null;
    private static boolean loaded = false;

    private static InputStream findMap() {
        String[] names = getMapNames();
        for (int i=0;i < names.length;i++) {
            Log.debug("Trying " + names[i]);
            String name = getFilename(names[i]);
            InputStream is = KeyStrokeMapProvider.class.
                getResourceAsStream("keymaps/" + name);
            if (is != null)
                return is;
        }
        // do we want to return a default map?
        InputStream is = KeyStrokeMapProvider.class.
            getResourceAsStream("keymaps/default.map");
        return is;
    }

    private synchronized void loadMaps() {
        if (loaded)
            return;
        Properties props = new Properties();
        Map cmap = null;
        Map kmap = null;
        Map amap = null;
        try {
            InputStream is = findMap();
            if (is == null) {
                Log.debug("No appropriate map file found");
                loaded = true;
                return;
            }
            props.load(is);
            Iterator iter = props.keySet().iterator();
            cmap = new HashMap();
            kmap = new HashMap();
            amap = new HashMap();
            while (iter.hasNext()) {
                String key = (String)iter.next();
                Log.debug("Property " + key + "=" + props.getProperty(key));
                try {
                    String codeName = key.substring(0, key.indexOf("."));
                    String modString = key.substring(key.indexOf(".")+1);
            		int mask;
            		String accentCode = null;
            		if (modString.length() == 1)
            			mask = Integer.parseInt(modString, 16);
            		else{
            			mask = Integer.parseInt(modString.substring(1), 16);
            			accentCode = modString.substring(0, 1);
            		}
                    int value = Integer.parseInt(props.getProperty(key), 16);
                    Character ch = new Character((char)value);
                    Field field = KeyEvent.class.getField("VK_" + codeName);
                    int code = field.getInt(null);
                    KeyStroke ks = KeyStroke.getKeyStroke(code, mask);
                    // May be more than one KeyStroke mapping to a given key
                    // character; prefer no mask or shift mask over any other
                    // masks. 
                    KeyStroke existing = (KeyStroke)kmap.get(ch);
                    if (existing == null
                        || ((existing.getModifiers() != 0
                             && existing.getModifiers() != KeyEvent.SHIFT_MASK)
                            || (mask == 0
                                && (existing.getModifiers() != 0
                                    || ks.toString().length() 
                                    < existing.toString().length())))) {
                        Log.debug("Installing " + ks + " for '" + ch + "'");
                        kmap.put(ch, ks);
                    }
                    // check if it is an accent char, if so add to map
                    if (accentCode != null){
                    	amap.put(ch,accentCode);
                    }
                    cmap.put(ks, ch);
                }
                catch(NumberFormatException e) {
                    // ignore invalid entries
                }
                catch(Exception e) {
                    Log.warn(e);
                }
            }
        }
        catch(IOException io) {
        }
        Log.debug("Successfully loaded character/keystroke map");
        characterMap = cmap;
        keyStrokeMap = kmap;
        accentMap = amap;
        loaded = true;
    }

    /** Load a map for the current locale to translate a character into a
        corresponding virtual keycode-based KeyStroke. */
    public Map loadCharacterMap() {
        loadMaps();
        return characterMap;
    }

    /** Load a map for the current locale to translate a virtual keycode into
        a character-based KeyStroke. */
    public Map loadKeyStrokeMap() {
        loadMaps();
        return keyStrokeMap;
    }
    
    public Map loadAccentKeyMap(){
    	loadMaps();
    	return accentMap;
    }

    
//    private static Map defaultCharacterMap = null;
    private static Map defaultKeyStrokeMap = null;
    private static boolean defaultMapLoaded = false;
    
    
    /**
     * get keystroke from default map - english
     */
    public static KeyStroke getDefaultKeyStroke(char ch) {
    	return (KeyStroke)defaultKeycodes.get(new Character(ch));
    	
    }
    
    
    private static Map getDefaultKeyStrokeMap() {
        KeyStrokeMapProvider generator = getGenerator();
        Map m = generator != null
            ? generator.loadDefaultKeyStrokeMap() : null;
        return m != null ? m : generateKeyStrokeMappings(true);
    }
    
    
    /** Load a map for the default locale to translate a virtual keycode into
    a character-based KeyStroke. */
    public Map loadDefaultKeyStrokeMap() {
    loadDefaultMaps();
    return defaultKeyStrokeMap;
}
    
    
    
    private synchronized void loadDefaultMaps() {
        if (defaultMapLoaded)
            return;
        Properties props = new Properties();
        Map cmap = null;
        Map kmap = null;
        try {
        	InputStream is = KeyStrokeMapProvider.class.
            getResourceAsStream("keymaps/default.map");
            if (is == null) {
                Log.debug("No appropriate map file found");
                loaded = true;
                return;
            }
            props.load(is);
            Iterator iter = props.keySet().iterator();
            cmap = new HashMap();
            kmap = new HashMap();
            while (iter.hasNext()) {
                String key = (String)iter.next();
                Log.debug("Property " + key + "=" + props.getProperty(key));
                try {
                    String codeName = key.substring(0, key.indexOf("."));
                    int mask = Integer.
                        parseInt(key.substring(key.indexOf(".")+1), 16);
                    int value = Integer.parseInt(props.getProperty(key), 16);
                    Character ch = new Character((char)value);
                    Field field = KeyEvent.class.getField("VK_" + codeName);
                    int code = field.getInt(null);
                    KeyStroke ks = KeyStroke.getKeyStroke(code, mask);
                    // May be more than one KeyStroke mapping to a given key
                    // character; prefer no mask or shift mask over any other
                    // masks. 
                    KeyStroke existing = (KeyStroke)kmap.get(ch);
                    if (existing == null
                        || ((existing.getModifiers() != 0
                             && existing.getModifiers() != KeyEvent.SHIFT_MASK)
                            || (mask == 0
                                && (existing.getModifiers() != 0
                                    || ks.toString().length() 
                                    < existing.toString().length())))) {
                        Log.debug("Installing " + ks + " for '" + ch + "'");
                        kmap.put(ch, ks);
                    }
                    cmap.put(ks, ch);
                }
                catch(NumberFormatException e) {
                    // ignore invalid entries
                }
                catch(Exception e) {
                    Log.warn(e);
                }
            }
        }
        catch(IOException io) {
        }
        Log.debug("Successfully loaded character/keystroke map");
//        defaultCharacterMap = cmap;
        defaultKeyStrokeMap = kmap;
        defaultMapLoaded = true;
    }
    
    
    
    /** Convert a String containing a unique identifier for the map into a
     * unique filename.
     */
    protected static String getFilename(String base) {
        //return Integer.toHexString(base.hashCode()) + ".map";
        return base + ".map";
    }

    protected static String[] getMapNames() { 
        return getMapStrings(false);
    }

    protected static String[] getMapDescriptions() { 
        return getMapStrings(true);
    }

    /** Return the keystroke map filenames that should be available for this
     * locale/OS/VM version/architecture.  Assume most changes across locale,
     * then OS, then VM version, then os version/architecture.
     */
    private static String[] getMapStrings(boolean desc) {
        ArrayList list = new ArrayList();
        // for testing, set locale to german
       // Locale locale = new Locale("de","DE");
       // Locale locale = new Locale("nl","BE");
       // Locale locale = new Locale("en","US");
       //   Locale locale = new Locale("fr","FR");
        //Locale locale = new Locale("sv","SE");
        Locale locale = Locale.getDefault();
        
        String name = locale.toString();
        if (desc)
            name = "locale=" + name;
        list.add(0, name);

        String os = "-" + getOSType();
        if (desc)
            os = " (os=" + System.getProperty("os.name")
                + ", " + System.getProperty("os.version") + ")";
        name += os;
        list.add(0, name);
        /*
        String vm = System.getProperty("java.version");
        name += " vm=" + vm;
        list.add(0, name);
        String version = System.getProperty("os.version");
        name += " version=" + version;
        list.add(0, name);
        String arch = System.getProperty("os.arch");
        name += " arch=" + arch;
        list.add(0, name);
        */
        return (String[])list.toArray(new String[list.size()]);
    }

    private static String getOSType() {
        return Platform.isMacintosh() ? "mac"
            : (Platform.isWindows() ? "w32" : "x11");
    }

    
    
    /** Return currently available locales. */
    public static void main(String[] args) {
 /*       Locale[] available = Locale.getAvailableLocales();
        System.out.println("Available Locales");
        for (int i=0;i < available.length;i++) {
            System.out.print(available[i].toString());
            System.out.print(" ");
        }
        
  */    Locale locale = Locale.getDefault();
    	System.out.println(locale.getDisplayLanguage().toString());
        findMap();
        System.exit(1);
    }
}
