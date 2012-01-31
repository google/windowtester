package abbot.editor.widgets;

import abbot.i18n.Strings;

/** Provides text formatting utilities. */

public class TextFormat {
    public static final int TOOLTIP_WRAP = 50;
    public static final int DIALOG_WRAP = 60;

    /** Turns "SomeRunTogetherWords" into "Some Run Together Words". */
    public static String wordBreak(String phrase) {
        StringBuffer words = new StringBuffer(phrase);
        for (int i=0;i < words.length()-1;i++) {
            char up = words.charAt(i+1);
            if (Character.isUpperCase(up)
                && (i == words.length()-2
                    || Character.isLowerCase(words.charAt(i+2)))) {
                words.insert(++i, ' ');
            }
        }
        return words.toString();
    }

    /** Wrap the given text at the given number of characters per line.
        Whitespace may be compressed.
     */
    public static String wordWrap(String msg, int wrapAt, String lineSep) {
        if (msg == null)
            return null;
        
        int len = msg.length();
        StringBuffer sb = new StringBuffer(len * 3 / 2);
        int pos = 0;
        while (pos < len) {
            // Trim leading whitespace
            char ch;
            while (pos < len
                   && Character.isWhitespace(ch = msg.charAt(pos))) {
                if (ch == '\n') {
                    sb.append(lineSep);
                }
                ++pos;
            }
                   
            // Find the last whitespace prior to the wrap column
            int lastWhite = -1;
            boolean nonwhite = false;
            int col = 0;
            while (pos + col < len && col <= wrapAt) {
                if (Character.isWhitespace(ch = msg.charAt(pos + col))) {
                    if (lastWhite == -1 || nonwhite) {
                        lastWhite = pos + col;
                    }
                    if (ch == '\n') {
                        break;
                    }
                }
                else {
                    nonwhite = true;
                }
                ++col;
            }
            if (pos + col == len) {
                // end of input
                while (pos < len) {
                    sb.append(msg.charAt(pos));
                    ++pos;
                }
                break;
            }
            else if (lastWhite != -1) {
                // found whitespace, wrap there
                while (pos < lastWhite) {
                    sb.append(msg.charAt(pos));
                    ++pos;
                }
            }
            else {
                // no whitespace on the line; wrap at next whitespace
                // or end of input
                while (pos < len) {
                    ch = msg.charAt(pos);
                    if (Character.isWhitespace(ch))
                        break;
                    sb.append(ch);                    
                    ++pos;
                }
                if (pos == len)
                    break;
            }
            sb.append(lineSep);
            ++pos;
        }

        return sb.toString();
    }

    /** Emit html, suitably line-wrapped and formatted for a tool tip. */
    public static String tooltip(String tip) {
        if (tip.startsWith("<html>")) {
            tip = tip.substring(6, tip.length() - 7);
        }
        else {
            tip = wordWrap(tip, TOOLTIP_WRAP, "<br>");
        }
        return Strings.get("TooltipFormat", new Object[] { tip });
    }

    /** Emit html, suitably line-wrapped and formatted for a dialog. */
    public static String dialog(String msg) {
        if (msg.startsWith("<html>")) {
            msg = msg.substring(6, msg.length() - 7);
        }
        else {
            msg = wordWrap(msg, DIALOG_WRAP, "<br>");
        }
        return Strings.get("DialogFormat", new Object[] { msg });
    }
}
