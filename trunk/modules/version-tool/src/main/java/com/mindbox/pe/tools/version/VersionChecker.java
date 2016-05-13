/*
 * Created on Feb 17, 2006
 *
 */
package com.mindbox.pe.tools.version;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Run this to check the version of PE, given the webapps/powereditor directory.
 * <p>
 * <b>Usage:</b><br>
 * <code>java -classpath powereditor-version.jar <powereditor-dir> <output-filename>
 * 
 * @author Geneho Kim
 * @since PowerEditor 4.4.0
 */
public class VersionChecker {

	private static final String CHECKER_FLAG = "{pe.version.check}";
	private static final String JAR_NAME = "powereditor-server.jar";
	private static final String WEBINF_LIB_PATH = "WEB-INF" + System.getProperty("file.separator") + "lib" + System.getProperty("file.separator") + JAR_NAME;
	private static final long MAX_WAIT = 1 * 60 * 1000L; // 2 minutes

	private static void printUsage() {
		System.out.println("USAGE:");
		System.out.println("java -jar powereditor-version.jar <pe-dir> <output-file>");
		System.out.println("");
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 2) {
			String peDir = args[0];
			String outputFilename = args[1];
			if (peDir.equals(CHECKER_FLAG)) {
				writeVersionInfo(outputFilename);
			}
			else {
				VersionChecker checker = new VersionChecker(peDir, outputFilename);
				checker.run();
			}
		}
		else {
			printUsage();
		}

	}

	private static void writeVersionInfo(String outputFilename) throws IOException {
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFilename, false)));
		try {
			System.out.println("Checking version info...");
			Class<?> serverUtilClass = Class.forName("com.mindbox.pe.server.ServerException");

			Package serverPackage = serverUtilClass.getPackage(); //Package.getPackage("com.mindbox.pe.server");
			System.out.println("Classpath: " + System.getProperty("java.class.path"));
			if (serverPackage == null) throw new IllegalStateException("PowerEditor server package not found");
			String version = serverPackage.getSpecificationVersion();
			System.out.println("Writing version " + version + "...");
			out.println(version);
			out.flush();
			System.out.println("Version " + version + " written to " + outputFilename);
		}
		catch (Exception ex) {
			System.out.println("There were errors: " + ex.getMessage());
			System.out.println("See " + outputFilename + " for details!");
			out.println("***ERROR***");
			ex.printStackTrace(out);
		}
		finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private static class TimeoutThread extends Thread {

		public TimeoutThread() {
			super("TO");
			setDaemon(true);
		}

		public void run() {
			long target = System.currentTimeMillis() + MAX_WAIT;
			while (target > System.currentTimeMillis()) {
				try {
					Thread.sleep(10);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.err.println("Timed out while waiting for checker process to complete.");
			System.exit(-1);
		}
	}

	private final String serverJarPath;
	private final String filename;

	private VersionChecker(String peDir, String filename) {
		validateFile(filename);
		this.filename = filename;
		this.serverJarPath = extractServerJarPath(peDir);
		System.out.print("Using " + serverJarPath + " to determine version...");
	}

	public void run() {
		try {
			// start a new process for version processing
			String classPath = System.getProperty("java.class.path") + System.getProperty("path.separator") + serverJarPath;
			List<String> cmdList = new ArrayList<String>();
			cmdList.add("java");
			cmdList.add("-Xms1m");
			cmdList.add("-classpath");
			cmdList.add(classPath);
			cmdList.add(VersionChecker.class.getName());
			cmdList.add(CHECKER_FLAG);
			cmdList.add(filename);
			System.out.print(".");

			Runtime.getRuntime().exec((String[]) cmdList.toArray(new String[0]));
			System.out.print(".");

			// spawn timeout thread
			new TimeoutThread().start();
			System.out.print(".");

			// wait until file's modified time has changed
			System.out.print(".");
			System.out.println(" Done!");
		}
		catch (Exception ex) {
			System.out.println("Failed to complete the request");
			ex.printStackTrace();
		}
	}

	private String extractServerJarPath(String peDir) {
		File file = new File(peDir);
		if (!file.exists()) throw new IllegalArgumentException(filename + " does not exist.");
		if (!file.isDirectory()) throw new IllegalArgumentException(filename + " is not a directory.");
		File candidate = new File(file, WEBINF_LIB_PATH);
		if (candidate.exists()) {
			return candidate.getAbsolutePath();
		}
		else {
			candidate = new File(file, "powereditor" + System.getProperty("file.separator") + WEBINF_LIB_PATH);
			if (candidate.exists()) {
				return candidate.getAbsolutePath();
			}
			else {
				throw new IllegalArgumentException("No powereditor installation found in " + peDir);
			}
		}
	}

	private void validateFile(String filename) {
		File file = new File(filename);
		if (file.exists() && file.isDirectory()) throw new IllegalArgumentException(filename + " is a directory. Please specify a path to a file.");
	}

}
