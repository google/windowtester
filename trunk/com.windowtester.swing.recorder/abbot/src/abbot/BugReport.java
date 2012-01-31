package abbot;

import javax.swing.UIManager;
import javax.swing.LookAndFeel;
import java.io.*;
import abbot.tester.Robot;
import abbot.i18n.Strings;

/** Exception for reporting unexpected situations in the program.
 * Automatically generates a message suitable for posting in a bug report.
 */
public class BugReport extends Error implements Version {

    private static final String LS = System.getProperty("line.separator");

    private static final String BUGREPORT_URL = Strings.get("bugreport.url");

    private static String getReportingInfo() {
        return Strings.get("bugreport.info",
                           new Object[] { LS + BUGREPORT_URL + LS });
    }

    public static String getSystemInfo() {
        String desc = "none";
        LookAndFeel laf = UIManager.getLookAndFeel();
        if (laf != null) {
            desc = laf.getName() + " (" + laf.getDescription() + ")";
        }
        return ""
       //     + "abbot version: " + VERSION + LS
            + "         mode: " + Robot.getEventModeDescription() + LS
            + "           OS: " + System.getProperty("os.name")
            + " " + System.getProperty("os.version") 
            + " (" + System.getProperty("os.arch") + ") " + LS
            + " Java version: " + System.getProperty("java.version") 
            + " (vm " + System.getProperty("java.vm.version") + ")" + LS
            + "    Classpath: " + System.getProperty("java.class.path") + LS
            + "Look and Feel: " + UIManager.getLookAndFeel();
    }

    private String errorMessage;
    private Throwable throwable;

    public BugReport(String error) {
        this(error, null);
    }

    public BugReport(String error, Throwable thr) {
        super(error);
        this.errorMessage = error;
        this.throwable = thr;
    }

    public String toString() {
        String exc = "";
        if (throwable != null) {
            StringWriter writer = new StringWriter();
            throwable.printStackTrace(new PrintWriter(writer));
            exc = writer.toString();
        }
        return errorMessage
           + LS + getReportingInfo()
           + LS + getSystemInfo()
           + LS + exc;
    }
}
