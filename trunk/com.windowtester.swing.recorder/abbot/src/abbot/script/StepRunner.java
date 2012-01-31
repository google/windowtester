package abbot.script;

import java.util.*;
import java.io.File;

import javax.swing.SwingUtilities;

import abbot.*;
import abbot.finder.Hierarchy;
import abbot.finder.TestHierarchy;
import abbot.i18n.Strings;
import abbot.util.*;

/** Provides control and tracking of the execution of a step or series of
    steps.  By default the runner stops execution on the first encountered
    failure/error.  The running environment is preserved to the extent
    possible, which includes discarding any GUI components created by the
    code under test.<p>
    If you wish to preserve the application state when there is an error,
    you can use the method {@link #setTerminateOnError(boolean)}.
*/
public class StepRunner {

    private static UIContext currentContext = null;
    
    private boolean stopOnFailure = true;
    private boolean stopOnError = true;
    /** Whether to terminate the app after an error/failure. */
    private boolean terminateOnError = true;
    /** Whether to terminate the app after stopping. */
    private transient boolean terminateOnStop = false;
    private ArrayList listeners = new ArrayList();
    private Map errors = new HashMap();
    /** Whether to stop running. */
    private transient boolean stop = false;
    /** Use this to catch event dispatch exceptions. */
    private EDTExceptionCatcher catcher;
    protected AWTFixtureHelper helper;
    protected Hierarchy hierarchy;
    
    /** This ctor uses a new instance of TestHierarchy as the
     * default Hierarchy.  Note that any existing GUI components at the time
     * of this object's creation will be ignored.
     */
    public StepRunner() {
        this(new AWTFixtureHelper());
    }

    /** Create a new runner.  The given {@link Hierarchy} maintains which GUI
     * components are in or out of scope of the runner.  The {@link AWTFixtureHelper}
     * will be used to restore state if {@link #terminate()} is called. 
     */
    public StepRunner(AWTFixtureHelper helper) {
        this.helper = helper;
        this.catcher = new EDTExceptionCatcher();
        catcher.install();
        hierarchy = new TestHierarchy();
    }
    
    /** 
     * @return The designated hierarchy for this <code>StepRunner</code>,
     * or <code>null</code> if none.
     */
    public Hierarchy getHierarchy() { 
        Hierarchy h = currentContext != null && currentContext.isLaunched()
            ? currentContext.getHierarchy() : hierarchy;
        return h;
    }

    public UIContext getCurrentContext() {
        return currentContext;
    }
    
    public void setStopOnFailure(boolean stop) {
        stopOnFailure = stop;
    }

    public void setStopOnError(boolean stop) {
        stopOnError = stop;
    }

    public boolean getStopOnFailure() { return stopOnFailure; }
    public boolean getStopOnError() { return stopOnError; }

    /** Stop execution of the script after the current step completes.  The
     * launched application will be left in its current state.
     */
    public void stop() { stop(false); }

    /** Stop execution, indicating whether to terminate the app. */
    public void stop(boolean terminate) {
        stop = true;
        terminateOnStop = terminate;
    }

    /** Return whether the runner has been stopped. */
    public boolean stopped() { return stop; }

    /** Create a security manager to use for the duration of this runner's
        execution.  The default prevents invoked applications from 
        invoking {@link System#exit(int)} and invokes {@link #terminate()}
        instead.
    */
    protected SecurityManager createSecurityManager() {
        return new ExitHandler();
    }

    /** Install a security manager to ensure we prevent the AUT from
        exiting and can clean up when it tries to.
     */
    protected synchronized void installSecurityManager() {
        String doInstall = System.getProperty("abbot.use_security_manager");
        if (System.getSecurityManager() == null
            && !"false".equals(doInstall)) {
            // When the application tries to exit, throw control back to the
            // step runner to dispose of it
            Log.debug("Installing sm");
            System.setSecurityManager(createSecurityManager());
        }
    }

    protected synchronized void removeSecurityManager() {
        if (System.getSecurityManager() instanceof ExitHandler) {
            System.setSecurityManager(null);
        }
    }

    /** If the given context is not the current one, terminate the current one
     * and set this one as current.
     */
    private void updateContext(UIContext context) {
        if (!context.equivalent(currentContext)) {
            Log.debug("current=" + currentContext + ", new=" + context);
            if (currentContext != null)
                currentContext.terminate();
            currentContext = context;
        }
    }
    
    /** Run the given step, propagating any failures or errors to 
     * listeners.  This method should be used for any execution
     * that should be treated as a single logical action.
     * This method is primarily used to execute a script, but may
     * be used in other circumstances to execute one or more steps
     * in isolation.  
     * The {@link #terminate()} method will be invoked if the script is
     * stopped for any reason, unless {@link #setTerminateOnError(boolean)}
     * has been called with a <code>false</code> argument.  Otherwise
     * {@link #terminate()} will only be called if a
     * {@link Terminate} step is encountered.
     * @see #terminate()
     */ 
    public void run(Step step) throws Throwable {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new Error(Strings.get("runner.bad_invocation"));
        }

        // Terminate incorrect contexts prior to doing any setup.
        // Even though a UIContext will invoke terminate on a 
        // non-equivalent context, we need to make it happen 
        // before anything gets run.
        UIContext context = null;
        if (step instanceof Script) {
            context = step instanceof UIContext
                ? (UIContext)step
                : ((Script)step).getUIContext();
        }
        else if (step instanceof UIContext) {
            context = (UIContext)step;
        }
        if (context != null)
            updateContext(context);
        
        installSecurityManager();
        boolean completed = false;
        clearErrors();

        try {
            if ((step instanceof Script) && ((Script)step).isForked()) {
                Log.debug("Forking " + step);
                StepRunner runner = new ForkedStepRunner(this);
                runner.listeners.addAll(listeners);
                try {
                    runner.runStep(step);
                }
                finally {
                    errors.putAll(runner.errors);
                }
            }
            else {
                runStep(step);
            }
            completed = !stopped();
        }
        catch(ExitException ee) {
            // application tried to exit
            Log.debug("App tried to exit");
            terminate();
        }
        finally {
            if (step instanceof Script) {
                if (completed && errors.size() == 0) {
                    // Script was run successfully
                }
                else if (stopped() && terminateOnStop) {
                    terminate();
                }
            }
            removeSecurityManager();
        }
    }

    /** Set whether the application under test should be terminated when an
        error is encountered and script execution stopped.  The default
        implementation always terminates.
    */
    public void setTerminateOnError(boolean state) {
        terminateOnError = state;
    }

    public boolean getTerminateOnError() { return terminateOnError; }

    protected void clearErrors() {
        stop = false;
        errors.clear();
    }

    /** Throw an exception if the file does not exist. */
    protected void checkFile(Script script) throws InvalidScriptException {
        File file = script.getFile();
        if (!file.exists()
            && !file.getName().startsWith(Script.UNTITLED_FILE)) {
            String msg = "The script '" + script.getFilename()
                + "' does not exist at the expected location '"
                + file.getAbsolutePath() + "'";
            throw new InvalidScriptException(msg);
        }
    }

    /** Main run method, which stores any failures or exceptions for later
     * retrieval.  Any step will fire STEP_START events to all registered
     * {@link StepListener}s on starting, and exactly one
     * of STEP_END, STEP_FAILURE, or STEP_ERROR upon termination.  If
     * stopOnFailure/stopOnError is set false, then both STEP_FAILURE/ERROR
     * may be sent in addition to STEP_END.
     */
    protected void runStep(final Step step) throws Throwable {
        
        if (step instanceof Script) {
            checkFile((Script)step);
            ((Script)step).setHierarchy(getHierarchy());
        }

        Log.debug("Running " + step);
        fireStepStart(step);

        // checking for stopped here allows a listener to stop execution on a
        // particular step in response to its "start" event.
        if (stopped()) {
            Log.debug("Already stopped, skipping " + step);
        }
        else {
            Throwable exception = null;
            long exceptionTime = -1;
            try {
                if (step instanceof Launch) {
                    ((Launch)step).setThreadedLaunchListener(new LaunchListener());
                }
                // Recurse into sequences
                if (step instanceof Sequence) {
                    ((Sequence)step).runStep(this);
                }
                else {
                    step.run();
                }
                Log.debug("Finished " + step);
                if (step instanceof Terminate) {
                    terminate();
                }
            }
            catch(Throwable e) {
                exceptionTime = System.currentTimeMillis();
                exception = e;
            }
            finally {
                // Cf. ComponentTestFixture.runBare()
                // Any EDT exception which occurred *prior* to when the
                // exception on the main thread was thrown should be used
                // instead.
                long edtExceptionTime = catcher.getThrowableTime();
                Throwable edtException = catcher.getThrowable();
                if (edtException != null
                    && (exception == null
                        || edtExceptionTime < exceptionTime)) {
                    exception = edtException;
                }
            }
            if (exception != null) {
                if (exception instanceof AssertionFailedError) {
                    Log.debug("failure in " + step + ": " + exception);
                    fireStepFailure(step, exception);
                    if (stopOnFailure) {
                        stop(terminateOnError);
                        throw exception;
                    }
                }
                else {
                    Log.debug("error in " + step + ": " + exception);
                    fireStepError(step, exception);
                    if (stopOnError) {
                        stop(terminateOnError);
                        throw exception;
                    }
                }
            }

            fireStepEnd(step);
        }
    }

    /** Similar to {@link #run(Step)}, but defers to the {@link Script}
     * to determine what subset of steps should be run as the UI context.
     * @param step
     */
    public void launch(Script step) throws Throwable {
        UIContext ctxt = step.getUIContext();
        if (ctxt != null) {
            ctxt.launch(this);
        }
    }
    
    /** Dispose of any extant windows and restore any saved environment
     * state.
     */ 
    public void terminate() {
        // Allow the context to do specialized cleanup
        if (currentContext != null) {
            currentContext.terminate();
        }
        if (helper != null) {
            Log.debug("restoring UI state");
            helper.restore();
        }
    }

    protected void setError(Step step, Throwable thr) {
        if (thr != null)
            errors.put(step, thr);
        else
            errors.remove(step);
    }

    public Throwable getError(Step step) {
        return (Throwable)errors.get(step);
    }

    public void addStepListener(StepListener sl) {
        synchronized(listeners) {
            listeners.add(sl);
        }
    }

    public void removeStepListener(StepListener sl) {
        synchronized(listeners) {
            listeners.remove(sl);
        }
    }
    
    /** If this is used to propagate a failure/error, be sure to invoke
     * setError on the step first.
     */
    protected void fireStepEvent(StepEvent event) {
        Iterator iter;
        synchronized(listeners) {
            iter = ((ArrayList)listeners.clone()).iterator();
        }
        while (iter.hasNext()) {
            StepListener sl = (StepListener)iter.next();
            sl.stateChanged(event);
        }
    }
    
    private void fireStepEvent(Step step, String type,
                                 int val, Throwable throwable) {
        synchronized(listeners) {
            if (listeners.size() != 0) {
                StepEvent event = new StepEvent(step, type, val, throwable);
                fireStepEvent(event);
            }
        }
    }
    
    protected void fireStepStart(Step step) {
        fireStepEvent(step, StepEvent.STEP_START, 0, null);
    }
    
    protected void fireStepProgress(Step step, int val) {
        fireStepEvent(step, StepEvent.STEP_PROGRESS, val, null);
    }

    protected void fireStepEnd(Step step) {
        fireStepEvent(step, StepEvent.STEP_END, 0, null);
    }

    protected void fireStepFailure(Step step, Throwable afe) {
        setError(step, afe);
        fireStepEvent(step, StepEvent.STEP_FAILURE, 0, afe);
    }

    protected void fireStepError(Step step, Throwable thr) {
        setError(step, thr);
        fireStepEvent(step, StepEvent.STEP_ERROR, 0, thr);
    }

    private class LaunchListener implements Launch.ThreadedLaunchListener {
        public void stepFailure(Launch step, AssertionFailedError afe) {
            fireStepFailure(step, afe);
            if (stopOnFailure)
                stop(terminateOnError);
        }
        public void stepError(Launch step, Throwable thr) {
            fireStepError(step, thr);
            if (stopOnError)
                stop(terminateOnError);
        }
    }

    protected class ExitHandler extends NoExitSecurityManager {
        public void checkRead(String file) {
            // avoid annoying drive a: bug on w32 VM
        }
        protected void exitCalled(int status) {
            Log.debug("Terminating from security manager");
            terminate();
        }
    }
}

