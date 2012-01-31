package junit.extensions.abbot;


/**
   Time and performance measurement utilities.

   @author      twall
*/

public class Timer {
    /** Time base for elapsed time calculations. */
    private long start;

    /** Basic constructor which sets the timer base to the current time. */
    public Timer() {
        reset();
    }

    /** Return the number of milliseconds elapsed since the last timer
        reset. */ 
    public long elapsed() { 
        return System.currentTimeMillis() - start;
    }

    /** Return the length of time elapsed to run the given runnable. */
    public long elapsed(Runnable action) {
        long start = System.currentTimeMillis();
        action.run();
        return System.currentTimeMillis() - start;
    }

    /** Set the start time to the current time. */
    public void reset() {
        start = System.currentTimeMillis();
    }
}
