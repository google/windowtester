package abbot.swt.eclipse.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import junit.framework.Assert;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

public class FileCompareUtils {

	/*
	 * A file system filter used to filter out CVS metadata directories.
	 */
	public static final FileFilter CVSFILTER= new CVSFilter();
	private static class CVSFilter implements FileFilter {
		/**
		 * @see java.io.FileFilter#accept(File, String)
		 */
		public boolean accept(File pathname) {
			return pathname.isFile() || !pathname.getName().equals("CVS");
		}
	}
	
	/**
	 * Compare the contents of two files:
	 * @param project containing file1
	 * @param path1 complete <code>project</code>-relative path to <code>Resource</code> file1
	 * @param path2 complete absolute path to a file (in the filesystem)
	 */
	public static void compareFileContents(IProject project, String path1, String path2) {
		Assert.assertNotNull("project is null", project);
		Assert.assertNotNull("path1 is null", path1);
		Assert.assertNotNull("path2 is null", path2);
		compareFileContents(project.getFile(path1), path2);
	}

	/**
	 * Compare the contents of two files
	 * @param path1 absolute path of file1 in the file system
	 * @param path2 absolute path of file2 in the file system
	 */
	public static void compareFileContents(String path1, String path2) {
		try {
			File file1= new File(path1);
			if (!file1.exists()) Assert.fail(path1+" doesn't exist.");
			if (!file1.isFile()) Assert.fail(path1+" is not a file.");
			File file2= new File(path2);
			if (!file2.exists()) Assert.fail(path2+" doesn't exist.");
			if (!file2.isFile()) Assert.fail(path2+" is not a file.");
			compareFileContents(path1, new FileReader(file1), path2, new FileReader(file2));
		} catch (IOException ex) {
			Assert.fail("IO failed");
		}
	}

	/**
	 * Compare the contents of two files
	 * @param file1 a file resource in the Workspace
	 * @param path2 absolute path of file2 in the file system
	 */
	public static void compareFileContents(IFile file1, String path2) {
		Assert.assertNotNull("file1 is null.", file1);
		try{
			String path1= file1.getFullPath().toString();
			File file2= new File(path2);
			if (!file1.exists()) Assert.fail(path1+" doesn't exist.");
			if (file1.getType() != IResource.FILE) Assert.fail(path1+" is not a file.");
			if (!file2.exists()) Assert.fail(path2+" doesn't exist.");
			if (!file2.isFile()) Assert.fail(path2+" is not a file.");
			compareFileContents(path1, new InputStreamReader(file1.getContents()), path2, new FileReader(file2));
		} catch (Exception ex) {
			Assert.fail("IO failed");
		}
	}
	
	/**
	 * Compare the contents of two files
	 * @param file1 a file resource in the Workspace
	 * @param file2 a file in the file system
	 */
	public static void compareFileContents(IFile file1, File file2) {
		Assert.assertNotNull("file1 is null.", file1);
		Assert.assertNotNull("file2 is null.", file2);
		try{
			String path1= file1.getFullPath().toString();
			String path2= file2.getPath();
			if (!file1.exists()) Assert.fail(path1+" doesn't exist.");
			if (file1.getType() != IResource.FILE) Assert.fail(path1+" is not a file.");
			if (!file2.exists()) Assert.fail(path2+" doesn't exist.");
			if (!file2.isFile()) Assert.fail(path2+" is not a file.");
			compareFileContents(path1, new InputStreamReader(file1.getContents()), path2, new FileReader(file2));
		} catch (Exception ex) {
			Assert.fail("IO failed");
		}
	}
	
	/**
	 * Compare the contents of two files
	 * @param file1 a file in the file system
	 * @param file2 a file in the file system
	 */
	public static void compareFileContents(File file1, File file2) {
		Assert.assertNotNull("file1 is null.", file1);
		Assert.assertNotNull("file2 is null.", file2);
		try{
			String path1= file1.getPath();
			String path2= file2.getPath();
			if (!file1.exists()) Assert.fail(path1+" doesn't exist.");
			if (!file1.isFile()) Assert.fail(path1+" is not a file.");
			if (!file2.exists()) Assert.fail(path2+" doesn't exist.");
			if (!file2.isFile()) Assert.fail(path2+" is not a file.");
			compareFileContents(path1, new FileReader(file1), path2, new FileReader(file2));
		} catch (Exception ex) {
			Assert.fail("IO failed");
		}
	}
	
	/**
	 * Compare the contents of two files.
	 * @param name1 the name of file1
	 * @param r1 a reader for file1
	 * @param name2 the name of file2
	 * @param r2 a reader for file2
	 */
	public static void compareFileContents(String name1, Reader r1, String name2, Reader r2) {
		Assert.assertNotNull("name1 is null.", name1);
		Assert.assertNotNull("r1 is null.", r1);
		Assert.assertNotNull("name2 is null.", name2);
		Assert.assertNotNull("r2 is null.", r2);
		BufferedReader reader1= null;
		BufferedReader reader2= null;
		try {
			reader1= new BufferedReader(r1);
			reader2= new BufferedReader(r2);
			int lineno = 1;
			String line1 = "", line2 = "";
			while (reader1.ready() && reader2.ready()) {
				line1 = reader1.readLine();
				line2 = reader2.readLine();
				if (!line1.equals(line2)) {
					Assert.fail("File content mismatch\n" + name1 + '\n' + name2 + "\nDifference at line #: " + lineno + '\n' + line1 + '\n' + line2);
				}
				lineno++;
			}
			if (reader1.ready() || reader2.ready())
				Assert.fail("File content mismatch, files have different number of lines:\n" + name1 + '\n' + name2);
		} catch (IOException ex) {
			Assert.fail("IO failed");
		} finally {
			try {
				if (reader1 != null) reader1.close();
			} catch (IOException e) {}
			try {
				if (reader2 != null) reader2.close();
			} catch (IOException e) {}
		}
	}

	/**
	 * Compare two directories trees in the file system.
	 * All folders and files are compared, including file contents.
	 * @param container1 a Workspace container (WorkspaceRoot, Project, or Folder)
	 * @param path2 a directory in the file system
	 * @param filter2 a file system filter for the second dirctory tree
	 * @param compareContainerNames true if the root directory names should be compared
	 */
	public static void compareResourceTree(IContainer container1, String path2, FileFilter filter2, boolean compareContainerNames) {
		Assert.assertNotNull("path2 is null.", path2);
		compareResourceTree(container1, new File(path2), filter2, compareContainerNames);
	}
	
	/**
	 * Compare two directories trees in the file system.
	 * All folders and files are compared, including file contents.
	 * @param container1 a Workspace container (WorkspaceRoot, Project, or Folder)
	 * @param directory2 a directory in the file system
	 * @param filter2 a file system filter for the second dirctory tree
	 * @param compareContainerNames true if the root directory names should be compared
	 */
	public static void compareResourceTree(IContainer container1, File directory2, FileFilter filter2, boolean compareContainerNames) {
		Assert.assertNotNull("container1 is null", container1);
		IPath loc= container1.getLocation();
		Assert.assertNotNull("Container location is null.", loc);
		compareResourceTree(loc.toFile(), null, directory2, filter2, compareContainerNames);
	}

	/**
	 * Compare two directories trees in the file system.
	 * All folders and files are compared, including file contents.
	 * @param path1 a directory in the file system
	 * @param filter1 a file system filter for the first directory tree
	 * @param path2 a directory in the file system
	 * @param filter2 a file system filter for the second dirctory tree
	 * @param compareContainerNames true if the root directory names should be compared
	 */
	public static void compareResourceTree(String path1, FileFilter filter1, String path2, FileFilter filter2, boolean compareContainerNames) {
		Assert.assertNotNull("path1 is null.", path1);
		Assert.assertNotNull("path2 is null.", path2);
		compareResourceTree(new File(path1), filter1, new File(path2), filter2, compareContainerNames);
	}
	
	/**
	 * Compare two directories trees in the file system.
	 * All folders and files are compared, including file contents.
	 * @param directory1 a directory in the file system
	 * @param filter1 a file system filter for the first directory tree
	 * @param directory2 a directory in the file system
	 * @param filter2 a file system filter for the second dirctory tree
	 * @param compareContainerNames true if the root directory names should be compared
	 */
	public static void compareResourceTree(File directory1, FileFilter filter1, File directory2, FileFilter filter2, boolean compareContainerNames) {
		Assert.assertNotNull("directory1 is null", directory1);
		Assert.assertNotNull("directory2 is null", directory2);
		if (!directory1.exists())
			Assert.fail("File system directory " + directory1.getPath()+" does not exist");
		if (!directory2.exists())
			Assert.fail("File system directory " + directory2.getPath()+" does not exist");
		if (!directory1.isDirectory())
			Assert.fail("File system path is not a directory: " + directory1.getPath());
		if (!directory2.isDirectory())
			Assert.fail("File system path is not a directory: " + directory2.getPath());
		if (compareContainerNames && !directory1.getName().equals(directory2.getName()))
			Assert.fail("Names are not equal:\n"+ directory1.getPath() + '\n' + directory2.getPath());

		// Get children and ensure they are sorted the same
		File[] dir1Children, dir2Children;
		dir1Children= directory1.listFiles(filter1);
		dir2Children= directory2.listFiles(filter2);
		Arrays.sort(dir1Children);
		Arrays.sort(dir2Children);
		
		int i=0;
		for (; i<dir1Children.length; i++) {
			File dir1Child= dir1Children[i];
			if (i>=dir2Children.length)
				Assert.fail(
					(dir1Child.isFile() ? "File " : "Directory ")
					+ dir1Child.getName() + " missing from file system directory "
					+ directory2.getPath());
			File dir2Child= dir2Children[i];
			if ((dir1Child.isFile() && !dir2Child.isFile())
				|| (dir1Child.isDirectory() && !dir2Child.isDirectory()))
					Assert.fail("File/directory type mismatch:\n" + dir1Child.getPath() + '\n' + dir2Child.getPath());
			if (!dir1Child.getName().equals(dir2Child.getName()))
				Assert.fail(
					"Directory mismatch (missing "
					+ (dir1Child.isFile() ? "file" : "directory") + "):\n"
					+ dir1Child.getPath() + '\n'
					+ dir2Child.getPath());
			if (dir1Child.isFile())
				compareFileContents(dir1Child, dir2Child);
			else
				compareResourceTree(dir1Child, filter1, dir2Child, filter2, false);
		}
		
		if (i != dir2Children.length)
			Assert.fail("Extra children in directory, starting with:\n" + dir2Children[i].getPath());
	}

}
