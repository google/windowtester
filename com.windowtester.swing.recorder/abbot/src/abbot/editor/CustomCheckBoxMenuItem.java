
package abbot.editor;

import java.beans.*;

import javax.swing.*;

import abbot.Log;
import abbot.editor.actions.*;
import abbot.editor.widgets.Mnemonic;

/** A custom JCheckBoxMenuItem that listens to the selected
 * state of its toggle action, reflecting its state when the action changes. 
 */
public class CustomCheckBoxMenuItem extends JCheckBoxMenuItem {

    private PropertyChangeListener pcl;
    
    public CustomCheckBoxMenuItem(EditorToggleAction a) {
        super(a);
        setName((String)a.getValue(EditorAction.NAME));
        Integer i = (Integer)a.getValue(EditorAction.MNEMONIC_INDEX);
        if (i != null)
            Mnemonic.setDisplayedMnemonicIndex(this, i.intValue());
        // prior to 1.4, the accelerator key is not automatically set
        setAccelerator((KeyStroke)a.getValue(Action.ACCELERATOR_KEY));
    }
    
    protected void configurePropertiesFromAction(javax.swing.Action a) { 
        super.configurePropertiesFromAction(a);
        boolean s = a!= null && ((EditorToggleAction)a).isSelected();
        super.setSelected(s);
    }
    
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        pcl = super.createActionPropertyChangeListener(a);
        return new CustomCheckBoxPropertyListener();
    }
    
    private class CustomCheckBoxPropertyListener
        implements PropertyChangeListener
    {
        public void propertyChange(PropertyChangeEvent e) {                    
            Log.debug("Got action prop change: "                               
                      + e.getPropertyName() + ":" + e.getNewValue());          
            pcl.propertyChange(e);                                             
            if (e.getPropertyName().equals(EditorToggleAction.STATE)) {    
                Boolean val = (Boolean)e.getNewValue();                        
                CustomCheckBoxMenuItem.this.                                  
                    setSelected(val == Boolean.TRUE);                          
            }                                               
            else if (e.getPropertyName().equals(EditorAction.MNEMONIC_INDEX)) {
                Integer i = (Integer)e.getNewValue();
                int index = i != null ? i.intValue() : -1;
                Mnemonic.setDisplayedMnemonicIndex(CustomCheckBoxMenuItem.this, index);
            }
        }                                                                  
    }
}
