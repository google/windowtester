package abbot.script;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import abbot.Platform;
import abbot.i18n.Strings;

public class ScriptFilter extends FileFilter {
    /** Indicate whether the given file should appear in the browser. */
    public boolean accept(File file) {
        // OSX has a buggy file chooser, gets NPE if you open a directory
        if (Platform.isOSX()
            && Platform.JAVA_VERSION <= 0x1400) {
        }
        return Script.isScript(file) || file.isDirectory();
    }
    /** Indicate the combo box entry used to describe files of this type. */
    public String getDescription() {
        return Strings.get("editor.filechooser.script_desc");
    }
}
