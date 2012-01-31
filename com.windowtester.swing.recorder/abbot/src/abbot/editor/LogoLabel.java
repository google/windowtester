package abbot.editor;

/** Provides the abbot/costello logo. */

import javax.swing.*;
import javax.swing.border.*;
import abbot.i18n.Strings;

class LogoLabel extends JLabel {
    private static final String PATH = "icons/abbot.gif";
    public LogoLabel() {
        super(Strings.get("Splash"), JLabel.CENTER);
        java.net.URL url = getClass().getResource(PATH);
        if (url == null)
            System.err.println("Logo not found at " + PATH);
        else 
            setIcon(new ImageIcon(url));
        setVerticalTextPosition(JLabel.BOTTOM);
        setHorizontalTextPosition(JLabel.CENTER);
        setBorder(new EmptyBorder(4,4,4,4));
    }
}
