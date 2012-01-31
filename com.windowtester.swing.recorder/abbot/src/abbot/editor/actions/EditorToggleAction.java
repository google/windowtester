package abbot.editor.actions;

import java.awt.event.*;

/** Encapsulate GUI attributes for an editor action. */

public class EditorToggleAction extends EditorAction {

    public static final String STATE = "STATE";
  
    public EditorToggleAction(String base) {
        super(base);
        setSelected(false);
    }
    
    // FIXME
    public void actionPerformed(ActionEvent ev) {
        setSelected(!isSelected());
        //super.actionPerformed(ev);
    }

    public boolean isSelected() {
        return getValue(STATE) == Boolean.TRUE; 
    }
    
    public void setSelected(boolean state) {
        putValue(STATE, state ? Boolean.TRUE : Boolean.FALSE);
    }
}
