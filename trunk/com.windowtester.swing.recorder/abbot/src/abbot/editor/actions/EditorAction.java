package abbot.editor.actions;

import java.awt.event.*;
import java.net.URL;

import javax.swing.*;

import abbot.Log;
import abbot.Platform;
import abbot.editor.*;
import abbot.editor.widgets.*;
import abbot.i18n.Strings;

/** Encapsulate GUI attributes for an editor action. */

public abstract class EditorAction
    extends AbstractAction implements EditorConstants {

    /** Key to refer to the action in ActionMaps; <strong>not</strong> the
        same as the NAME property which is typically used for labels.
    */
    public static final String ACTION_KEY = "action-key";
    public static final String LARGE_ICON = "large-icon";
    public static final String MNEMONIC_INDEX = "mnemonic-index";

    public EditorAction(String actionKey) {
        super(actionKey);
        putValue(ACTION_KEY, actionKey);

        String key = ACTION_PREFIX + actionKey;

        Mnemonic mnemonic = Mnemonic.getMnemonic(Strings.get(key));
        mnemonic.setMnemonic(this);
        if (mnemonic.index != -1)
            putValue(MNEMONIC_INDEX, new Integer(mnemonic.index));
        // Check deprecated usage
        if (mnemonic.keycode == KeyEvent.VK_UNDEFINED) {
            int mn = getMnemonic(key);
            if (mn != KeyEvent.VK_UNDEFINED)
                putValue(MNEMONIC_KEY, new Integer(mn));
        }

        String desc = Strings.get(key + ".desc", true);
        if (!"".equals(desc) && desc != null) {
            putValue(SHORT_DESCRIPTION, TextFormat.tooltip(desc));
        }
        String longDesc = Strings.get(key + ".ldesc", true);
        if (!"".equals(longDesc) && longDesc != null) {
            putValue(LONG_DESCRIPTION, TextFormat.tooltip(longDesc));
        }
        else {
            putValue(LONG_DESCRIPTION, getValue(SHORT_DESCRIPTION));
        }

        String iconName = Strings.get(key + ".icon", true);
        if (!"".equals(iconName) && iconName != null) {
            putValue(SMALL_ICON, getIcon(iconName, 16));
            putValue(LARGE_ICON, getIcon(iconName, 24));
        }

        String accelerator = Strings.get(key + ".acc", true);
        if (!"".equals(accelerator) && accelerator != null) {
            accelerator =
                abbot.tester.AWTConstants.MENU_SHORTCUT_STRING + accelerator;
            try {
                // In case the accelerator given is garbage...
                putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(accelerator));
            }
            catch(Exception e) {
                Log.warn("Bad accelerator '" + accelerator + "': " + e);
            }
        }
    }

    /** @deprecated Encode the mnemonic into the localized label instead. */
    public static int getMnemonic(String key) {
        int code = KeyEvent.VK_UNDEFINED;
        // No (visible) mnemonics on OSX
        if (Platform.isOSX())
            return code;

        String mnemonic = Strings.get(key + ".mn", true);
        if (!"".equals(mnemonic) && mnemonic != null) {
            if (!mnemonic.startsWith("VK_")) {
                mnemonic = "VK_" + mnemonic;
            }
            try {
                code = abbot.util.AWT.getKeyCode(mnemonic);
            }
            catch(IllegalArgumentException e) {
                String msg = Strings.get("editor.bad_mnemonic",
                                         new Object[] {
                                             mnemonic, key + ".mn",
                                             java.util.Locale.getDefault(),
                                             e.toString()
                                         });
                // TODO: format this into an email message
                Log.warn(msg);
            }
        }
        return code;
    }

    /** 
     * Returns the Icon associated with the given name from the available
     * resources. 
     * 
     * @param name Base name of the icon file e.g., help for help16.gif
     * @param size Size in pixels of the icon from the filename, or zero if
     * none, e.g. 16 for help16.gif or 0 for help.gif.
     * @return an ImageIcon or null if no corresponding icon resource is found.
     */
    private ImageIcon getIcon(String name, int size)  {
        String ABBOT_IMAGE_DIR = "/abbot/editor/icons/";
        String base = ABBOT_IMAGE_DIR + name;
        URL url = getClass().getResource(base + ".gif");
        if (url == null) {
            url = getClass().getResource(base + size + ".gif");
        }
        ImageIcon icon = url != null ? new ImageIcon(url) : null;
        return icon;
    }
}
