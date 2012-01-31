package abbot.editor;

import java.io.*;
import java.util.Properties;

import abbot.Log;

public class Preferences extends Properties {

    public static final String PROPS_FILENAME = ".abbot.properties";
    private File file;

    public Preferences() {
        this(PROPS_FILENAME);
    }

    public Preferences(String filename) {
        file = new File(new File(System.getProperty("user.home")), filename);
        load();
    }

    public int getIntegerProperty(String name, int defaultValue) {
        String prop = getProperty(name);
        if (prop != null) {
            try {
                return Integer.parseInt(prop);
            }
            catch(Exception e) {
                Log.warn(e);
            }
        }
        return defaultValue;
    }

    private void load() {
        if (file.exists()) {
            try {
                load(new BufferedInputStream(new FileInputStream(file)));
            }
            catch(IOException io) {
                Log.warn(io);
            }
        }
    }

    public void save() {
        try { store(new BufferedOutputStream(new FileOutputStream(file)),
                    "Abbot view preferences"); }
        catch(IOException io) { Log.warn(io); }
    }
}
