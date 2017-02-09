package abbot;

import java.util.StringTokenizer;

/** Simple utility to figure out what platform we're on, what java version we're running.
 * 
 * Last update from abbot-1.3.0
 * + modifications to fix problem with recognizing Mac OS X
 */

public class Platform {

    public static final int JAVA_1_0 = 0x1000;
    public static final int JAVA_1_1 = 0x1100;
    public static final int JAVA_1_2 = 0x1200;
    public static final int JAVA_1_3 = 0x1300;
    public static final int JAVA_1_4 = 0x1400;
    public static final int JAVA_1_5 = 0x1500;
    public static final int JAVA_1_6 = 0x1600;
    public static final int JAVA_1_7 = 0x1700;
    public static final int JAVA_1_8 = 0x1800;
    public static final int JAVA_1_9 = 0x1900;

    public static final String OS_NAME;
    public static final String JAVA_VERSION_STRING;
    public static final int JAVA_VERSION;

    static {
        OS_NAME = System.getProperty("os.name");
        JAVA_VERSION_STRING = System.getProperty("java.version");
        JAVA_VERSION = parse(JAVA_VERSION_STRING);
    }
    
    private static boolean isWindows = OS_NAME.startsWith("Windows");
    private static boolean isWindows9X = isWindows
        && (OS_NAME.indexOf("95") != -1
            || OS_NAME.indexOf("98") != -1
            || OS_NAME.indexOf("ME") != -1);
    private static boolean isWindowsXP = isWindows && OS_NAME.indexOf("XP") != -1;
    //With Mac OS X 10.9.1 (Mavericks) mrj.version returns null, apparently
    private static boolean isMac = System.getProperty("mrj.version") != null;
    private static boolean isOSX = OS_NAME.indexOf("OS X") != -1;
    private static boolean isSunOS = (OS_NAME.startsWith("SunOS")
                                      || OS_NAME.startsWith("Solaris"));
    private static boolean isHPUX = OS_NAME.equals("HP-UX");
    private static boolean isLinux = OS_NAME.equals("Linux");

    /** No instantiations. */
    private Platform() {
    }

    private static String strip(String number) {
        while (number.startsWith("0") && number.length() > 1)
            number = number.substring(1);
        return number;
    }

    static int parse(String vs) {
        int version = 0;
        try {
            StringTokenizer st = new StringTokenizer(vs, "._");
            version = Integer.parseInt(strip(st.nextToken())) * 0x1000;
            version += Integer.parseInt(strip(st.nextToken())) * 0x100;
            version += Integer.parseInt(strip(st.nextToken())) * 0x10;
            version += Integer.parseInt(strip(st.nextToken()));
        }
        catch(NumberFormatException nfe) {
        }
        catch(java.util.NoSuchElementException nse) {
        }
        return version;
    }

    // FIXME this isn't entirely correct, maybe should look for a motif class
    // instead. 
    public static boolean isX11() { return !isOSX && !isWindows; }
    public static boolean isWindows() { return isWindows; }
    public static boolean isWindows9X() { return isWindows9X; }
    public static boolean isWindowsXP() { return isWindowsXP; }
    public static boolean isMacintosh() { return isMac; }
    public static boolean isOSX() { return isOSX; }
    public static boolean isSolaris() { return isSunOS; }
    public static boolean isHPUX() { return isHPUX; }
    public static boolean isLinux() { return isLinux; }
    
    public static boolean is6OrAfter() { return JAVA_VERSION>=JAVA_1_6; }
    public static boolean is7OrAfter() { return JAVA_VERSION>=JAVA_1_7; }
    public static boolean is8OrAfter() { return JAVA_VERSION>=JAVA_1_8; }

    public static boolean isBefore7() { return JAVA_VERSION<JAVA_1_7; }
    public static boolean isBefore8() { return JAVA_VERSION<JAVA_1_8; }

        
    public static boolean is6() { return JAVA_VERSION>=JAVA_1_6 && JAVA_VERSION<JAVA_1_7; }
    public static boolean is7() { return JAVA_VERSION>=JAVA_1_7 && JAVA_VERSION<JAVA_1_8; }
    public static boolean is8() { return JAVA_VERSION>=JAVA_1_8 && JAVA_VERSION<JAVA_1_9; }
    
    
    public static void main(String[] args) {
        System.out.println("Java version is " + JAVA_VERSION_STRING);
        System.out.println("Version number is " + Integer.toHexString(JAVA_VERSION));
        System.out.println("os.name=" + OS_NAME);
    }
}
