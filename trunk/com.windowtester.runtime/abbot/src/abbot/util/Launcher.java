package abbot.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import abbot.Platform;

/** Mail and browser launcher which augments {@link Runtime#exec}
 * functions.  Provides for built-in email and web browser support.
 */
public class Launcher {

    // TODO: provide an array of known mailto: handlers
    // for now we assume the browsers will make an attempt to handle mailto:
    private static final String[] HTTP = {
        "galeon", "konqueror", "opera",
        "firefox", "mozilla", "netscape", "mosaic",
    };

    /** Perform trickery to get the right contents into the email handler from
        a mailto: line.
    */
    private static String encodeForMail(String base) {
        StringBuffer buf = new StringBuffer(base);
        // Avoid URLEncoder.encode for spaces; it replaces them with plus
        // signs, which remain pluses when decoded.
        String SPACE = "--SPACE--";
        for (int idx = buf.toString().indexOf(" ");
             idx != -1;idx = buf.toString().indexOf(" ")) {
            buf.replace(idx, idx+1, SPACE);
        }
        buf = new StringBuffer(URLEncoder.encode(buf.toString()));
        for (int idx = buf.toString().indexOf(SPACE);
             idx != -1;idx = buf.toString().indexOf(SPACE)) {
            if (Platform.isOSX()) {
                // The "open" command parses spaces
                buf.replace(idx, idx + SPACE.length(), "%20");
            }
            else {
                buf.replace(idx, idx + SPACE.length(), " ");
            }
        }
        return buf.toString();
    }

    /** Format a message to the given user with the given subject and message
        body. */
    public static void mail(String user, String subject, String body)
        throws IOException {
        mail(user, subject, body, null);
    }
    /** Format a message to the given user with the given subject and message
        body, including a CC list. */
    public static void mail(String user, String subject, String body,
                            String cc) throws IOException {
        mail(user, subject, body, cc, null);
    }
    /** Format a message to the given user with the given subject and message
        body, including CC and BCC lists. */
    public static void mail(String user, String subject, String body,
                            String cc, String bcc) throws IOException {
        StringBuffer mailto = new StringBuffer("mailto:" + user + "?");
        if (cc != null)
            mailto.append("CC=" + cc + "&");
        if (bcc != null)
            mailto.append("BCC=" + bcc + "&");
        
        mailto.append("Subject=" + encodeForMail(subject)
                      + "&" + "Body=" + encodeForMail(body) + "");
        open(mailto.toString());
    }

    /** Open the given target URL in the platform's browser. */
    public static void open(String target) throws IOException {
        open(null, target);
    }

    /** Use the given command/program to open the given target. */
    public static void open(String command, String target) throws IOException {
        boolean tryBrowsers = false;
        ArrayList args = new ArrayList();
        if (command != null) {
            args.add(command);
        }
        else {
            if (Platform.isOSX()) {
                args.add("open");
            }
            else if (Platform.isWindows()) {
                if (Platform.isWindows9X()) {
                    args.add("command.com");
                    args.add("/o");
                }
                else {
                    args.add("cmd.exe");
                    args.add("/c");
                    args.add("start");
                    args.add("\"Title\"");
                }
                // Always quote the argument, just in case
                // See MS docs for cmd.exe; &, |, and () must be escaped with
                // ^ or double-quoted.  semicolon and comma are command
                // argument separators, and probably require quoting as well. 
                target = "\"" + target + "\"";
            }
            else {
                args.add("placeholder");
                tryBrowsers = true;
            }
        }
        args.add(target);

        String[] cmd = (String[])args.toArray(new String[args.size()]);
        if (!tryBrowsers) {
            ProcessOutputHandler.exec(cmd);
        }
        else {
            // TODO: choose the appropriate application based on the target
            // URL format, instead of relying on browsers to do it.
            for (int i=0;i < HTTP.length;i++) {
                try {
                    cmd[0] = HTTP[i];
                    ProcessOutputHandler.exec(cmd);
                    return;
                }
                catch(IOException e) {
                    // not found, try another one
                }
            }
            throw new IOException("No target handler found (tried "
                                  + Arrays.asList(HTTP) + ")");
        }
    }
}
