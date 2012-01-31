package abbot.script;

import abbot.finder.Hierarchy;

/** Provides generic support to set up and tear down a UI context or
 * fixture.
 */ 
public interface UIContext {
    /** @return A {@link ClassLoader} providing access to classes in this
     * context.
     */ 
    ClassLoader getContextClassLoader();
    /** Launch this context.  If any <code>UIContext</code> is extant, 
     * this <code>UIContext</code> should terminate it before launching.  
     * If this context is already launched, this method
     * should do nothing.
     */
    void launch(StepRunner runner) throws Throwable;
    /** @return Whether this <code>UIContext</code> is currently launched. */ 
    boolean isLaunched();
    /** Terminate this context.  All UI components found in the 
     * {@link Hierarchy} returned by {@link #getHierarchy()} 
     *  will be disposed.
     */
    void terminate();
    /** @return Whether this <code>UIContext</code> is equivalent to another. */
    boolean equivalent(UIContext context);
    /** A context must maintain the same {@link Hierarchy} for the lifetime of 
     * the fixture. 
     */
    public Hierarchy getHierarchy();
}
