package abbot.script;

/** Listener for script step feedback. */
public interface StepListener {
    void stateChanged(StepEvent event);
}
