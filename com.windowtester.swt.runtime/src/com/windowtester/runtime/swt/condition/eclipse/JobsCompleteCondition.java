/*******************************************************************************
 *  Copyright (c) 2012 Google, Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *  Google, Inc. - initial API and implementation
 *******************************************************************************/
package com.windowtester.runtime.swt.condition.eclipse;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.core.runtime.jobs.Job;

import com.windowtester.internal.runtime.IDiagnostic;
import com.windowtester.internal.runtime.IDiagnosticParticipant;
import com.windowtester.runtime.condition.ICondition;

/**
 * Tests if no Eclipse Jobs are running.
 * 
 */
public class JobsCompleteCondition
	implements ICondition, IDiagnosticParticipant
{
	public static final int IGNORE_USER_JOBS = 1 << 0;
	public static final int IGNORE_SYSTEM_JOBS = 1 << 1;
	public static final int IGNORE_MYLYN_JOBS = 1 << 2;
	public static final int IGNORE_UDC_JOBS = 1 << 3;

	
	public static final int DEFAULT_FLAGS = IGNORE_MYLYN_JOBS | IGNORE_UDC_JOBS;

	/**
	 * Flags indicating which jobs to be ignored.
	 */
	public final int flags;

	/**
	 * A collection of names of jobs not to be checked
	 * or <code>null</code> if none
	 */
	private final HashSet<String> excludedJobNames;

	/**
	 * Construct a condition that returns true if no Eclipse Jobs are running,
	 * ignoring jobs associated with Mylyn and the EPP Usage Data Collector (UDC).
	 */
	public JobsCompleteCondition() {
		this(DEFAULT_FLAGS);
	}

	/**
	 * Construct a condition that returns true if no Eclipse User or System Jobs are
	 * running depending upon the argument and ignoring jobs associated with Mylyn and the EPP
	 * Usage Data Collector (UDC).
	 * 
	 * @param userJobs <code>true</code> if only user jobs should be checked, or
	 *            <code>false</code> if only system jobs should be checked
	 * @deprecated To be removed sometime after June 2008.
	 * 			  Use {@link #JobsCompleteCondition(int)} instead.
	 */
	public JobsCompleteCondition(boolean userJobs) {
		this(userJobs ? IGNORE_SYSTEM_JOBS : IGNORE_USER_JOBS);
	}

	/**
	 * Construct a condition that returns true if no Eclipse User Jobs are
	 * running ignoring jobs with the specified names and jobs associated with Mylyn.
	 * 
	 * @param excluded an array of names of jobs not to be checked
	 */
	public JobsCompleteCondition(String[] excluded) {
		this(DEFAULT_FLAGS, excluded);
	}

	/**
	 * Construct a condition that returns true if no Eclipse User or System Jobs are
	 * running depending upon the argument and ignoring jobs with the specified names 
	 * and jobs associated with Mylyn and the EPP Usage Data Collector (UDC).
	 * 
	 * @param userJobs <code>true</code> if only user jobs should be checked, or
	 *            <code>false</code> if only system jobs should be checked
	 * @param excluded an array of names of jobs not to be checked
	 * @deprecated To be removed sometime after June 2008.
	 * 			  Use {@link #JobsCompleteCondition(int, String[])} instead.
	 */
	public JobsCompleteCondition(boolean userJobs, String[] excluded) {
		this(userJobs ? IGNORE_SYSTEM_JOBS : IGNORE_USER_JOBS, excluded);
	}

	/**
	 * Construct a condition that returns true if no Eclipse Jobs are running 
	 * ignoring jobs as specified by the flags argument.
	 * 
	 * @param flags The bitwise flags specifying when types of jobs should be ignored.
	 * 		This can be any combination of {@link #IGNORE_USER_JOBS}, {@link #IGNORE_SYSTEM_JOBS},
	 * 		{@link #IGNORE_UDC_JOBS} and {@link #IGNORE_MYLYN_JOBS}.
	 */
	public JobsCompleteCondition(int flags) {
		this(flags, null);
	}

	/**
	 * Construct a condition that returns true if no Eclipse Jobs are running 
	 * ignoring jobs as specified by the flags argument.
	 * 
	 * @param flags The bitwise flags specifying when types of jobs should be ignored.
	 * 		This can be any combination of {@link #IGNORE_USER_JOBS}, {@link #IGNORE_SYSTEM_JOBS},
	 * 		{@link #IGNORE_UDC_JOBS} and {@link #IGNORE_MYLYN_JOBS}.
	 * @param excluded an array of names of jobs not to be checked
	 */
	public JobsCompleteCondition(int flags, String[] excluded) {
		this.flags = flags;
		this.excludedJobNames = newExcludedJobNames(excluded);
	}

	private static HashSet<String> newExcludedJobNames(String[] excluded) {
		if (excluded == null || excluded.length == 0)
			return null;
		HashSet<String> result = new HashSet<String>(excluded.length);
		result.addAll(Arrays.asList(excluded));
		return result;
	}

	/**
	 * Check if at least one job of interest (e.g. not flagged for exclusion or excluded by job name)
	 * is either running or waiting. Subclasses may override this method or {@link #checkComplete(Job)}.
	 */
	public boolean test() {
		Job[] allJobs = Job.getJobManager().find(null);
		for (int i = 0; i < allJobs.length; i++)
			if (checkComplete(allJobs[i]))
				return false;
		return true;
	}

	/**
	 * Determine if the specified job is of interest (e.g. not excluded by job name) and
	 * either running or waiting.
	 * 
	 * @param job the job of interest (not <code>null</code>)
	 * @return <code>true</code> if the job *is* of interest and *is* running or waiting.
	 */
	protected boolean checkComplete(Job job) {
		int state = job.getState();
		if (state != Job.RUNNING && state != Job.WAITING)
			return false;
		if ((flags & IGNORE_USER_JOBS) != 0 && job.isUser())
			return false;
		if ((flags & IGNORE_SYSTEM_JOBS) != 0 && !job.isUser())
			return false;
		if ((flags & IGNORE_MYLYN_JOBS) != 0) {
			String jobClassName = job.getClass().getName();
			if (jobClassName.startsWith("org.eclipse.mylyn.") || jobClassName.startsWith("org.eclipse.mylar."))
				return false;
		}
		if ((flags & IGNORE_UDC_JOBS) != 0) {
			String jobClassName = job.getClass().getName();
			if (jobClassName.startsWith("org.eclipse.epp.usagedata.internal.gathering."))
				return false;
		}	
		
		if (excludedJobNames != null && excludedJobNames.contains(job.getName()))
			return false;
		return true;
	}

	/**
	 * Provide additional diagnostic information
	 */
	public void diagnose(IDiagnostic diagnostic) {
		diagnostic.attribute("class", getClass().getName());
		Job[] allJobs = Job.getJobManager().find(null);
		for (int i = 0; i < allJobs.length; i++) {
			final Job job = allJobs[i];
			if (checkComplete(job)) {
				diagnostic.diagnose("job", new IDiagnosticParticipant() {
					public void diagnose(IDiagnostic diagnostic) {
						diagnostic.attribute("user", job.isUser());
						diagnostic.attribute("state", getJobStateName(job));
						diagnostic.attribute("name", job.getName());
						diagnostic.attribute("class", job.getClass().getName());
					}
				});
			}
		}
	}

	private String getJobStateName(final Job job) {
		switch (job.getState()) {
			case Job.RUNNING :
				return "running";
			case Job.WAITING :
				return "waiting";
			case Job.SLEEPING :
				return "sleeping";
			default :
				return "none";
		}
	}
}