package abbot.swt.eclipse.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import junit.framework.Assert;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import abbot.Log;
import abbot.swt.eclipse.EclipsePlugin;

/**
 * Try to keep everything static.
 * Utilities for creating, manipulating, and testing files.
 * 
 * @author tlroche
 * @version $Id: FileUtils.java,v 1.1 2005-12-19 20:28:33 pq Exp $
 */
public class FileUtils extends FileCompareUtils {
	// none in UI
	public static final String copyright = "Licensed Materials -- Property of IBM\n(c) Copyright International Business Machines Corporation, 2000,2003\nUS Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp."; //$NON-NLS-1$
	public static final String DEVSPACE_RELATIVE_PATH_TO_GOLD = "GoldFolder"; //$NON-NLS-1$ 
	public static IFolder goldFolder = null;

	/**
	 * @return
	 */
	public static IFolder getGoldFolder() {
		return goldFolder;
	}

	/**
	 * @return the absolute path of the gold folder in the filesystem
	 */
	public static String getGoldFolderAbsolutePath() {
		Assert.assertNotNull(goldFolder);
		IPath abspath = goldFolder.getFullPath();
		Assert.assertNotNull(abspath);
		String ret = abspath.toString();
		assertNotEmpty(ret); 
		return ret;
	}

	/**
	 * Probably won't work outside of a project!
	 * @return the path of the gold folder relative to the workspace
	 */
	public static String getGoldFolderRelativePath() {
		Assert.assertNotNull(goldFolder);
		IPath relpath = goldFolder.getLocation();
		Assert.assertNotNull(relpath);
		String ret = relpath.toString();
		assertNotEmpty(ret); 
		return ret;
	}

	/**
	 * Is the file denoted by the project-relative path and simple filename
	 * "golden," i.e. the same as the file with the same simple name
	 * in the Gold Folder?
	 * 
	 * @param projectName name of the project containing the file under test. 
	 * @param projectRelativePath to the file under test. No terminal filename or path separator. 
	 * @param simpleFilename of the file under test.
	 */
	public static boolean isGolden(
		String projectName, String projectRelativePath, String simpleFilename) {
		return isGolden(
			ProjectUtils.getProject(projectName), projectRelativePath, simpleFilename);
	}

	/**
	 * Is the file denoted by the project-relative path and simple filename
	 * "golden," i.e. the same as the file with the same simple name
	 * in the Gold Folder?
	 * 
	 * @param project containing the file under test. 
	 * @param projectRelativePath to the file under test. No terminal filename or path separator. 
	 * @param simpleFilename of the file under test.
	 */
	public static boolean isGolden(
		IProject project, String projectRelativePath, String simpleFilename) {
		Assert.assertNotNull(project);
		Assert.assertNotNull(projectRelativePath);
		Assert.assertNotNull(simpleFilename);
		// does the denoted FUT (file under test) exist?
		String futPathString = Utils.slashAppend(projectRelativePath, simpleFilename);
		IFile fut = project.getFile(new Path(futPathString));
		Assert.assertNotNull(fut);
		// does a corresponding gold file exist?
		String goldPathString = getGoldFolderAbsolutePath();
		assertNotEmpty(goldPathString);
		String goldFQString = Utils.slashAppend(goldPathString, simpleFilename);
		File gold = new File(goldFQString);
		Assert.assertNotNull(gold);
		// use gold file as touchstone (to mix metaphors :-)
		FileCompareUtils.compareFileContents(fut, gold);
		// if we got here, we must be golden
		return true;
	}

	/**
	 * Fail if param is null or length <= 0
	 */
	public static void assertNotEmpty(String s) {
		if (Utils.isEmpty(s)) Assert.fail();
	}

	/**
	 * Fail if param is null or length <= 0
	 */
	public static void assertNotEmpty(String message, String s) {
		if (Utils.isEmpty(s)) Assert.fail(message);
	}

	/**
	 * Set the folder, in which we will look for files against which
	 * to compare other files-under-test, from its workspace-relative path.
	 * CONTRACT: path string is not null or empty.
	 */
	public static void setGoldFolder(String workspaceRelativePath) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IPath rootPath = workspaceRoot.getLocation();
		IPath goldPath = rootPath.append(workspaceRelativePath);
		IFolder gf = workspaceRoot.getFolder(goldPath);
		setGoldFolder(gf);
	}

	/**
	 * Set the folder in which we will look for files against which
	 * to compare other files-under-test.
	 * CONTRACT: folder exists and is not null.
	 */
	public static void setGoldFolder(IFolder gf) {
		goldFolder = gf;
	}

	
	public static void copyFolder(File srcDir, File destDir) {
		/* the source directory will be created under the destination directory with the 
		 * same name.  Note that the contents are NOT currently recursively copied. */
		try {
			if (!destDir.exists()) {
						destDir.mkdir();				
			}
			File filesToCopy[] = srcDir.listFiles();
			for (int i=0;i<filesToCopy.length;i++) {
				/* make sure we have a file and not a directory */
				if (filesToCopy[i].isFile() && filesToCopy[i].exists()) {
					/* copy each file to the destination directory */
					File dest = new File(destDir, filesToCopy[i].getName());
					FileOutputStream out = new FileOutputStream(dest);
			        FileInputStream in = new FileInputStream(filesToCopy[i]);
			        int c;
			        while ((c = in.read()) != -1)
			           out.write(c);
			        in.close();
			        out.close();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Copies the gold folder to the runtime workspace
	 * CONTRACT: folder exists in the devspace, goldFolder has been set
	 * In this implementation, the gold folder is copied from bin/test/GoldFolder
	 */
	public static void copyGoldFolder() {
		/* goldFolder (location where goldfolder should go in runspace) must not be null */
		Assert.assertTrue(goldFolder!=null);

		Bundle model2testsBundle = EclipsePlugin.getDefault().getBundle();
		/* this is the source for our goldfolder in the devspace that we're copying to the runspace */
		URL gfURL = model2testsBundle.getEntry("/");
		
		/* devspace GoldFolder must exist */
		//Assert.assertTrue(goldFolder.exists());
		//Assert.assertTrue(gf.exists());
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot workspaceRoot = workspace.getRoot();
		IPath rootPath = workspaceRoot.getLocation();
		try {
			gfURL = Platform.asLocalURL(gfURL);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println ("URL path: " + gfURL.getPath());
		System.out.println ("URL file: " + gfURL.getFile());
		System.out.println ("URL ref: " + gfURL.getRef());
		File srcDir = new File(gfURL.getPath(), "GoldFolder");
		Path p = new Path(gfURL.getPath());
		IResource igf = ((Workspace)workspace).newResource(p,IResource.FOLDER);
		IPath gfpath = goldFolder.getFullPath();
		File destDir = new File(gfpath.toOSString()); 
		/* Make sure both of these directories exist */
		System.out.println ("Source directory exists: " + srcDir.exists());
		/* destination directory shouldn't exist because we're going to create it */
		//System.out.println ("Dest directory exists: " + destDir.exists());
		/* Since assert isn't working for the moment... */
		if (!srcDir.exists()) {
			System.out.println ("GoldFolder not found");
			Log.log ("GoldFolder not found");
			Assert.fail("GoldFolder not found");
		}
//		Assert.assertTrue("Source goldfolder does not exist", srcDir.exists());
//		Assert.assertTrue("Destination goldfolder does not exist", destDir.exists());
		copyFolder (srcDir, destDir);
		
	}

}
