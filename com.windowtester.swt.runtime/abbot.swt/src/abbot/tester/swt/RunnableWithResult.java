/*
 * Created on 12.04.2005
 * by Richard Birenheide (D035816)
 *
 * Copyright SAP AG 2005
 */
package abbot.tester.swt;
/**
 * Implements a runnable which is able to be queried for a result when finished.
 * <p>
 * Synchronizing on this object may lead to deadlocks. Access to the result is
 * thread safe.
 * @author Richard Birenheide
 */
public abstract class RunnableWithResult implements Runnable {
	private Object result;
	/**
	 * Executes {@link #runWithResult()} and stores the result for later retrieval.
	 * @see java.lang.Runnable#run()
	 */
	public final void run() {
		this.setResult(this.runWithResult());
	}
	/**
	 * Sets the result.
	 * <p/>
	 * The method is thread safe.
	 * @param result
	 */
	private void setResult(Object result) {
		synchronized (this) {
			this.result = result;
		}
	}
	/**
	 * Retrieves the result of the executed operation.
	 * <p/>
	 * Access is thread safe and synchronized on this object.
	 * @return the result of the operation.
	 */
	public Object getResult() {
		synchronized (this) {
			return result;
		}
	}
	/**
	 * Contains the runnable coding returning a result.
	 * <p/>
	 * @return the result of the operation.
	 */
	public abstract Object runWithResult();
}
