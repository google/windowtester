package abbot.editor.actions;

import java.util.ArrayList;

/** Keep a history of commands, enabling potentially unlimited undo.
    This class is not synchronized.<p>

    Note that undo is itself an undoable action.<p>
*/

public class CommandHistory {
    private ArrayList list = new ArrayList();
    /** The index of the most recent command "undone", or the size of the
     * history if the most recent action was "execute".
     */
    private int cursor = 0;

    private Command get(int idx) {
        return ((Command)list.get(idx));
    }

    public boolean canUndo() {
        return cursor > 0 && (get(cursor-1) instanceof Undoable);
    }

    public void undo() throws NoUndoException {
        if (canUndo()) {
            UndoableCommand undoable = (UndoableCommand)get(--cursor);
            undoable.undo();
            // Add the undo to the end of the history
            list.add(new CommandComplement(undoable));
        }
        else {
            // Reset the cursor to the end of the history
            cursor = list.size();
            throw new NoUndoException();
        }
    }

    /** Add the given command to the command history.  */
    public void add(Command command) {
        // If the command can't be undone, then clear the undo history
        if (!(command instanceof Undoable)) {
            clear();
        }
        list.add(command);
        // Put the cursor at the end of the command history
        cursor = list.size();
    }

    public void clear() {
        list.clear();
        cursor = 0;
    }

    /** Simple wrapper around an existing command to swap the sense of its
        execute/undo.
    */
    private class CommandComplement implements UndoableCommand {
        private UndoableCommand cmd;
        public CommandComplement(UndoableCommand orig) {
            cmd = orig;
        }
        public void execute() {
            cmd.undo();
        }
        public void undo() {
            cmd.execute();
        }
    }
}
