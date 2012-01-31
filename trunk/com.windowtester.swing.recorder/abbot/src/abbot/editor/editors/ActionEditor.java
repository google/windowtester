package abbot.editor.editors;

import java.lang.reflect.Method;
import java.util.ArrayList;

import abbot.script.Action;

/** Provide convenient editing of a Action step. */
public class ActionEditor extends CallEditor {

    public ActionEditor(Action action) {
        super(action);
    }

    // FIXME return the editor menu names instead
    protected String[] getMethodNames(Method[] methods) {
        ArrayList list = new ArrayList();
        for (int i=0;i < methods.length;i++) {
            if (methods[i].getName().startsWith("action"))
                list.add(methods[i].getName());
        }
        return (String[])list.toArray(new String[list.size()]);
    }
}
