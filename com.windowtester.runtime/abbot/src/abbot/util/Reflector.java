package abbot.util;

import java.lang.reflect.*;

import abbot.Log;

/** Utility for performing some common reflection tasks. */

public class Reflector {

    private Reflector() {
    }

    /** Convert the value back into a field name. */
    // NOTE: since this is an expensive lookup, maybe it should be deferred
    // until just before needed?
    public static String getFieldName(Class cls, int value, String prefix) {
        try {
            Field[] fields = cls.getFields();
            for (int i=0;i < fields.length;i++) {
                // Perform fastest tests first...
                if ((fields[i].getModifiers() & Modifier.STATIC) != 0
                    && (fields[i].getType().equals(Integer.class)
                        || fields[i].getType().equals(int.class))
                    && fields[i].getInt(null) == value
                    && fields[i].getName().startsWith(prefix)
                    // kind of a hack, but we don't want these two included...
                    && !fields[i].getName().endsWith("_LAST")
                    && !fields[i].getName().endsWith("_FIRST")) {
                    return fields[i].getName();
                }
            }
        }
        catch(Exception exc) {
            Log.log(exc);
        }
        throw new IllegalArgumentException("No available field has value " + value);
    }

    /** Look up the given static field value. */
    public static int getFieldValue(Class cls, String fieldName) {
        try {
            Field field = cls.getField(fieldName);
            return field.getInt(null);
        }
        catch(Exception exc) {
            Log.log(exc);
            // Don't want to ignore these...
            throw new IllegalArgumentException("No field " + fieldName
                                               + " found in " + cls);
        }
    }
}
