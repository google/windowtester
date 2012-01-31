package abbot.editor.editors;

import abbot.script.Step;

/** Provide a data change notification when a Step changes. */

public interface StepChangeListener {
    void stepChanged(Step step);
}
