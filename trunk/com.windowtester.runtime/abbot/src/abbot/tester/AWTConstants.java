package abbot.tester;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import abbot.Platform;
import abbot.util.AWT;

/** Provides shared UI- and action-related constants. */

public interface AWTConstants {

    int MULTI_CLICK_INTERVAL = 250; // a guess
    /** Number of pixels traversed before a drag starts. */
    // OSX 10(1.3.1), 5(1.4.1)
    // Linux/X11: delay+16
    // NOTE: could maybe install a drag gesture recognizer, but that's kinda
    // complex for what you get out of it.
    int DRAG_THRESHOLD =
        Platform.isWindows() || Platform.isMacintosh() ? 10 : 16;
    int BUTTON_MASK = (InputEvent.BUTTON1_MASK 
                                           | InputEvent.BUTTON2_MASK 
                                           | InputEvent.BUTTON3_MASK);
    int POPUP_MASK = AWT.getPopupMask();
    String POPUP_MODIFIER = AWT.getMouseModifiers(POPUP_MASK);
    boolean POPUP_ON_PRESS = AWT.getPopupOnPress();
    int TERTIARY_MASK = AWT.getTertiaryMask();
    String TERTIARY_MODIFIER =
        AWT.getMouseModifiers(TERTIARY_MASK);
    int MENU_SHORTCUT_MASK =
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    String MENU_SHORTCUT_MODIFIER = 
        AWT.getKeyModifiers(MENU_SHORTCUT_MASK);
    String MENU_SHORTCUT_STRING = 
        MENU_SHORTCUT_MASK == InputEvent.ALT_MASK ? "alt "
        : MENU_SHORTCUT_MASK == InputEvent.META_MASK ? "meta " 
        : MENU_SHORTCUT_MASK == InputEvent.SHIFT_MASK ? "shift " 
        : "control ";
    String MENU_SHORTCUT_KEYCODE =
        AWT.getKeyCode(AWT.maskToKeyCode(MENU_SHORTCUT_MASK));
}

