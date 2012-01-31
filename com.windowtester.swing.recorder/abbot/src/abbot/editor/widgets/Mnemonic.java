package abbot.editor.widgets;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.lang.reflect.Method;
import javax.swing.*;

import abbot.Log;
import abbot.Platform;
import abbot.tester.KeyStrokeMap;
import abbot.tester.Robot;
import abbot.util.AWT;
import abbot.i18n.Strings;

/** Provide access to mnemonics appropriate for the current platform and
 * locale.  Encapsulates displayed text, a KeyEvent.VK_ constant, and a
 * displayed mnemonic index.  All instances are obtained through the factory
 * method, {@link #getMnemonic(String)}.  
 * @see java.awt.event.KeyEvent
 * @see javax.swing.AbstractButton#setMnemonic(int)
 * @see javax.swing.AbstractButton#setDisplayedMnemonicIndex(int)
 * @see javax.swing.JLabel#setDisplayedMnemonic(int)
 * @see javax.swing.JLabel#setDisplayedMnemonicIndex(int)
 * @see javax.swing.Action#MNEMONIC_KEY
 */ 
public class Mnemonic {

    /** The unencoded text.  For example "&amp;File" results in "File". */
    public String text;
    /** The keycode to use as an argument to
        {@link AbstractButton#setMnemonic(int)}.  Returns
        KeyEvent.VK_UNDEFINED if no mnemonic was found.
    */
    public int keycode;
    /** The index to use as an argument to
        {@link AbstractButton#setDisplayedMnemonicIndex(int)}.  Returns
        -1 if the default value should be used.
    */
    public int index;

    private Mnemonic(String text, int keycode, int index) {
        this.text = text;
        this.keycode = keycode;
        this.index = index;
    }

    /** Set the displayed mnemonic index, if doing so is supported. */
    public static void setDisplayedMnemonicIndex(Component c, int index) {
        if (index == -1)
            return;
        try {
            Method m = c.getClass().getMethod("setDisplayedMnemonicIndex",
                                              new Class[] { int.class });
            m.invoke(c, new Object[] { new Integer(index) });
        }
        catch(Exception e) {
            // ignore errors
        }
    }

    public String toString() {
        return "Mnemonic text=" + text + ", keycode="
            + AWT.getKeyCode(keycode)
            + (index != -1 ? ("displayed index=" + index) : "");
    }

    /** Apply this mnemonic to the given AbstractButton. */
    public void setMnemonic(AbstractButton button) {
        button.setText(text);
        button.setMnemonic(keycode);
        setDisplayedMnemonicIndex(button, index);
    }

    /** Apply this mnemonic to the given JLabel. */
    public void setMnemonic(JLabel label) {
        label.setText(text);
        label.setDisplayedMnemonic(keycode);
        setDisplayedMnemonicIndex(label, index);
    }

    /** Apply this mnemonic to the given JLabel. */
    public void setMnemonic(JTabbedPane tabbedPane, int tabIndex) {
        tabbedPane.setTitleAt(tabIndex, text);
        // NOTE: 1.4-only
        try {
            Method m = JTabbedPane.class.getMethod("setMnemonicAt",
                                                   new Class[] {
                                                       int.class, int.class,
                                                   });
            m.invoke(tabbedPane, new Object[] { 
                new Integer(tabIndex), new Integer(keycode)});
            m = JTabbedPane.class.getMethod("setDisplayedMnemonicIndexAt",
                                            new Class[] {
                                                int.class, int.class,
                                            });
            if (index != -1)
                m.invoke(tabbedPane, new Object[] {
                    new Integer(tabIndex), new Integer(index) });
        }
        catch(Exception e) {
            // ignore errors
        }
    }

    /** Apply this mnemonic to the given Action. */
    public void setMnemonic(Action action) {
        action.putValue(Action.NAME, text);
        if (keycode != KeyEvent.VK_UNDEFINED)
            action.putValue(Action.MNEMONIC_KEY, new Integer(keycode));
        // Don't think buttons listen for mnemonic index changes anyway...
        //if (index != -1)
        //action.putValue(Action.MNEMONIC_INDEX, new Integer(index));
    }

    /** Return whether the character is disallowed as a mnemonic. */
    private static boolean isDisallowed(char ch) {
        return Character.isWhitespace(ch)
            || ch == '\''
            || ch == '"';
    }

    /** Return the appropriate mnemonic for the given character. */
    private static int getMnemonicMapping(char ch) {
        if (isDisallowed(ch))
            return KeyEvent.VK_UNDEFINED;

        if (ch >= 'A' && ch <= 'Z')
            return KeyEvent.VK_A + ch - 'A';
        if (ch >= 'a' && ch <= 'z')
            return KeyEvent.VK_A + ch - 'a';
        if (ch >= '0' && ch <= '9')
            return KeyEvent.VK_0 + ch - '0';

        // See if there's been a mapping defined; usage is similar to NetBeans
        // handling, except that raw integers are not allowed (use the VK_
        // constant name instead).
        String str = Strings.get("MNEMONIC_" + ch, true);
        if (str != null) {
            try {
                return AWT.getKeyCode("VK_" + str.toUpperCase());
            }
            catch(IllegalArgumentException e) {
                Log.warn("'" + str + "' is not a valid mnemonic "
                         + "(use a VK_ constant from KeyEvent)");
            }
        }

        // Make a guess based on keymaps
        KeyStroke keystroke = KeyStrokeMap.getKeyStroke(ch);
        if (keystroke != null) {
            return keystroke.getKeyCode();
        }

        return KeyEvent.VK_UNDEFINED;
    }

    /** Create a Mnemonic instance with the mnemonic information from the
        given encoded String.  Unencoded text, the mnemonic keycode, and
        the display index are encapsulated in the returned Mnemonic object.
        Encoding consists of placing an ampersand (&) prior to the character
        designated as the mnemonic.   
        <p>
        Mnemonics may be encoded as follows:
        <table border=1>
        <tr><td><i>Original Text</i></td><td><i>Visible Text</i></td><td><i>Mnemonic</i></td></tr>
        <tr><td>&File</td><td><u>F</u>ile</td><td><b>VK_F</b></td></tr>
        <tr><td>Save &As...</td><td>Save <u>A</u>s...</td><td><b>VK_A</b> second instance</td></tr>
        <tr><td>Me&&&You</td><td>Me&<u>Y</u>ou</td><td><b>VK_Y</b> ambiguous ampersands
        must be escaped</td></tr> 
        <tr><td>Sugar & Spice</td><td>Sugar & Spice</td><td><b>None</b> ampersand is unambiguous,
        whitespace is not allowed as a mnemonic</td></tr>
        </table>
        <p>
        Swing restricts mnemonics to available KeyEvent VK_ constants, so
        there must exist a mapping between text characters and said
        constants.  If the obvious mappings (A-Z, a-z, 0-9) don't hold, lookup
        falls back to other methods.  Whitespace, quotes, and ampersand are
        disallowed as mnemonics. 
        <p>
        Mappings from arbitrary characters to mnemonic keys may be defined
        by providing a property
        MNEMONIC_{unicode char}={KeyEvent.VK_ constant name} within a bundle
        accessible by <code>abbot.i18n.Strings</code>.  If no such
        mapping is defined, a VK_ code is guessed by checking if there can be
        found a keystroke mapping for the original character.
        <p>
        @see abbot.tester.KeyStrokeMap
        @see abbot.i18n.Strings
    */
    public static Mnemonic getMnemonic(String input) {
        String text = input;
        int mnemonicIndex = -1;
        int keycode = KeyEvent.VK_UNDEFINED;
        int amp = text.indexOf("&");
        int displayIndex = -1;
        while (amp != -1 && amp < text.length()-1) {
            char ch = text.charAt(amp + 1);
            if (ch == '&') {
                text = text.substring(0, amp)
                    + text.substring(amp + 1);
                amp = text.indexOf("&", amp + 1);
            }
            else {
                int code = getMnemonicMapping(ch);
                if (code == KeyEvent.VK_UNDEFINED) {
                    amp = text.indexOf("&", amp + 2);
                }
                else {
                    // Only use the first mapping
                    if (mnemonicIndex == -1) {
                        text = text.substring(0, amp)
                            + text.substring(amp + 1);
                        displayIndex = mnemonicIndex = amp;
                        keycode = code;
                    }
                    amp = text.indexOf("&", amp + 1);
                }
            }
        } 
        // Mnemonics are not used on OSX
        if (Platform.isOSX()) {
            keycode = KeyEvent.VK_UNDEFINED;
            displayIndex = -1;
        }
        Mnemonic m = new Mnemonic(text, keycode, displayIndex);
        Log.debug(input + "->" + m);
        return m;
    }
}
