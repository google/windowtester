package abbot.util;

import java.util.Timer;
import java.lang.reflect.Field;
import java.util.TimerTask;
import java.util.Date;
import abbot.Log;

/** Prevents misbehaving TimerTasks from canceling the timer thread by
    throwing exceptions and/or errors.  Also extends the basic Timer to use a
    name for its thread.  Naming the timer thread facilitates discerning
    different threads in a full stack dump.<p> 
*/
public class NamedTimer extends Timer {

    /** Creates a non-daemon named timer. */
    public NamedTimer(final String name) {
        this(name, false);
    }

    /** Creates a named timer, optionally running as a daemon thread. */
    public NamedTimer(final String name, boolean isDaemon) {
        super(isDaemon);
        schedule(new TimerTask() {
            public void run() {
                Thread.currentThread().setName(name);
            }
        }, 0);
    }
    
    /** Handle an exception thrown by a TimerTask.  The default does
        nothing. */ 
    protected void handleException(Throwable thrown) {
        Log.warn(thrown);
    }

    // TODO: prevent scheduled tasks from throwing uncaught exceptions and
    // thus canceling the Timer.
    // We can easily wrap scheduled tasks with a catcher, but we can't readily
    // cancel the wrapper when 

    private class ProtectingTimerTask extends TimerTask {
        private TimerTask task;
        public ProtectingTimerTask(TimerTask orig) {
            this.task = orig;
        }
        public void run() {
            if (isCanceled()) {
                cancel();
            }
            else {
                try { task.run(); }
                catch(Throwable thrown) { handleException(thrown); }
            }
        }
        private boolean isCanceled() {
            boolean canceled = false;
            final int CANCELED = 3;
            try {
                Field f = TimerTask.class.getDeclaredField("state");
                f.setAccessible(true);
                int state = ((Integer)f.get(task)).intValue();
                canceled = state == CANCELED;
            }
            catch(Exception e) {
                Log.warn(e);
            }
            return canceled;
        }
    }

    public void schedule(TimerTask task, Date time) {
        super.schedule(new ProtectingTimerTask(task), time);
    }
    public void schedule(TimerTask task, Date firstTime, long period) {
        super.schedule(new ProtectingTimerTask(task), firstTime, period);
    }
    public void schedule(TimerTask task, long delay) {
        super.schedule(new ProtectingTimerTask(task), delay);
    }
    public void schedule(TimerTask task, long delay, long period) {
        super.schedule(new ProtectingTimerTask(task), delay, period);
    }
    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        super.scheduleAtFixedRate(new ProtectingTimerTask(task), firstTime, period);
    }
    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        super.scheduleAtFixedRate(new ProtectingTimerTask(task), delay, period);
    }
}


