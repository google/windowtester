package abbot.tester;

import java.awt.Component;
import java.awt.TextComponent;

import abbot.util.Bugs;

/** Provides user actions for TextComponent-derived components. */
public class TextComponentTester extends ComponentTester {

    /**
     * Type the given text into the given component, replacing all
     * text already there.
     */
    public void actionEnterText(Component c, String text) {
        actionSelectText(c, 0, ((TextComponent)c).getText().length());
        actionKeyString(c, text);
    }

    /** Set the caret position. */
    public void actionSetCaretPosition(Component c, final int index) {
        final TextComponent tc = (TextComponent)c;
        invokeLater(c, new Runnable() {
            public void run() {
                tc.setCaretPosition(index);
            }
        });
    }
    
    /** Start a selection at the given index. */
    public void actionStartSelection(Component c, final int index) {
        final TextComponent tc = (TextComponent)c;
        invokeLater(c, new Runnable() {
            public void run() {
                tc.setSelectionStart(index);
            }
        });
    }

    /** Terminate a selection on the given index. */
    public void actionEndSelection(Component c, final int index) {
        final TextComponent tc = (TextComponent)c;
        invokeLater(c, new Runnable() {
            public void run() {
                tc.setSelectionEnd(index);
            }
        });
    }

    /** Select the given text range. */
    public void actionSelectText(Component c, final int start, final int end) {
        final TextComponent tc = (TextComponent)c;
        invokeLater(c, new Runnable() {
            public void run() {
                tc.select(start, end);
            }
        });
        if (Bugs.hasTextComponentSelectionDelay()) 
            delay(100);
    }
}
