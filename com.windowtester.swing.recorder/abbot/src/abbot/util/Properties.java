package abbot.util;

public class Properties {

    /** Load an integer system property, keeping it within the given valid
        range. */
    public static int getProperty(String name, int defValue, int min, int max){
        try {
            int value = Integer.getInteger(name, defValue).intValue();
            return Math.max(min, Math.min(max, value));
        }
        catch(NumberFormatException e) {
            return defValue;
        }
    }
    /** Load a long system property, keeping it within the given valid
        range. */
    public static long getProperty(String name, long min, long max, long defValue){
        try {
            long value = Long.getLong(name, defValue).longValue();
            return Math.max(min, Math.min(max, value));
        }
        catch(NumberFormatException e) {
            return defValue;
        }
    }
}
