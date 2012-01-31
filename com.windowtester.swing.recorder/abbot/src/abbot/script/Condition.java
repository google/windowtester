package abbot.script;

/** Abstract a condition test. */
public interface Condition {
    /** Return the condition state. */
    boolean test();
    /** Return a description of what the condition is testing. */
    String toString();
}
