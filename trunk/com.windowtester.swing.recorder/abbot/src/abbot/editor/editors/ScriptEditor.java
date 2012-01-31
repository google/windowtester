package abbot.editor.editors;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import java.util.Collection;

import abbot.Log;
import abbot.i18n.Strings;
import abbot.script.*;
import abbot.script.Script;
import abbot.editor.EditorConstants;
import abbot.editor.widgets.Mnemonic;

/** Provide convenient editing of a Script step. */
public class ScriptEditor extends SequenceEditor {

    private Script script;

    private JTextField path;
    private JCheckBox fork;
    private JTextField vmargs;

    public ScriptEditor(Script script) {
        super(script);
        this.script = script;

        path = addTextField(Strings.get("FilePath"),
                            script.getFilename());

        if (!(script instanceof Fixture)) {
            fork = addCheckBox("", script.isForked());
            String key = EditorConstants.ACTION_PREFIX
                + EditorConstants.ACTION_TOGGLE_FORKED;
            Mnemonic mnemonic = Mnemonic.getMnemonic(Strings.get(key));
            mnemonic.setMnemonic(fork);
            addVMArgs();
        }
    }

    private void addVMArgs() {
        if (script.isForked()) {
            vmargs = addTextField(Strings.get("VMArgs"),
                                  script.getVMArgs());
        }
        else if (vmargs != null) {
            while (getComponent(getComponentCount()-1) != fork) {
                remove(getComponentCount()-1);
            }
            vmargs = null;
        }
        revalidate();
        repaint();
    }

    public void actionPerformed(ActionEvent ev) {
        Object src = ev.getSource();
        if (src == path) {
            String filename = path.getText().trim();
            File file = new File(script.getRelativeTo(), filename);
            script.setFile(file);
            try {
                script.load();
            }
            catch(Exception exc) {
                Log.warn(exc);
            }
            fireStepChanged();
        }
        else if (src == fork) {
            script.setForked(!script.isForked());
            addVMArgs();
            fireStepChanged();
        }
        else if (src == vmargs) {
            String text = vmargs.getText();
            if ("".equals(text))
                text = null;
            script.setVMArgs(text);
            fireStepChanged();
        }
        else {
            super.actionPerformed(ev);
        }
    }
}
