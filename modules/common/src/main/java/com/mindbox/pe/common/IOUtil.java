package com.mindbox.pe.common;

import static com.mindbox.pe.common.LogUtil.logDebug;
import static com.mindbox.pe.common.LogUtil.logError;
import static com.mindbox.pe.common.LogUtil.logInfo;
import static com.mindbox.pe.common.LogUtil.logWarn;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public final class IOUtil {

	private static final boolean OS_WINDOWS = System.getProperty("path.separator").equals(";");
	public static final String RENAME_COMMAND = (OS_WINDOWS ? "rename" : "mv");
	public static final String COPY_COMMAND = (OS_WINDOWS ? "copy" : "cp");

	private static final Logger LOG = Logger.getLogger(IOUtil.class);
	private static final int ZIP_BUFFER_SIZE = 1024;

	private static final long FILE_CHANNEL_TRANSFER_SIZE = 1024 * 1024; // one MB

	public static void addZipEntry(ZipOutputStream out, ZipEntry zipEntry, InputStream entryIS) throws IOException {
		BufferedInputStream fileIn = new BufferedInputStream(entryIS);
		out.putNextEntry(zipEntry);
		int count;
		byte buffer[] = new byte[ZIP_BUFFER_SIZE];
		while ((count = fileIn.read(buffer, 0, ZIP_BUFFER_SIZE)) != -1) {
			out.write(buffer, 0, count);
		}
		close(fileIn);
		out.flush();
	}

	public static void appendFile(File sourceFile, File targetFile) throws IOException {
		if (sourceFile == null) {
			throw new IllegalArgumentException("sourceFile cannot be null");
		}
		if (targetFile == null) {
			throw new IllegalArgumentException("targetFile cannot be null");
		}

		writeToFile(new FileInputStream(sourceFile), true, targetFile, true);
	}

	/**
	 * Close the specified closeables (reader, output streams, etc.). This consumes any exceptions.
	 * 
	 * @param closeables closeables to close
	 */
	public static void close(Closeable... closeables) {
		if (closeables != null) {
			for (Closeable closeable : closeables) {
				if (closeable != null) {
					try {
						closeable.close();
					}
					catch (Exception e) {
						logWarn(LOG, e, "Failed to close %s", closeable);
					}
				}
			}
		}
	}

	public static void copyBytes(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
		out.flush();
	}

	public static void copyBytes(InputStream in, OutputStream out, boolean closeInAndOut) throws IOException {
		try {
			copyBytes(in, out);
		}
		finally {
			if (closeInAndOut) {
				close(in, out);
			}
		}
	}

	public static void copyFile(File sourceFile, File targetFile) throws IOException {
		saveToFile(new FileInputStream(sourceFile), targetFile, true);
	}

	private static void createDir(File dir) throws IOException {
		if (!dir.mkdirs()) {
			throw new IOException("Can not create dir " + dir.getAbsolutePath());
		}
	}

	public static void delete(File... files) {
		for (File file : files) {
			if (file != null) {
				deleteFile(file);
			}
		}
	}

	private static void deleteFile(File file) {
		if (file.exists()) {
			if (!file.delete()) {
				logInfo(LOG, "Failed to delete file [%s]; it will be deleted when JVM process is terminated.", file.getAbsolutePath());

				file.deleteOnExit();
			}
			else {
				logDebug(LOG, "Deleted %s.", file.getAbsolutePath());
			}
		}
	}

	public static void executeCommand(String command, String... args) throws IOException, InterruptedException {
		logDebug(LOG, "Running %s with %d arguments...", command, (args == null ? 0 : args.length));

		List<String> commands = new ArrayList<String>();
		commands.add(command);
		commands.addAll(Arrays.asList(args));

		Process process = new ProcessBuilder(commands).start();
		int returnValue = process.waitFor();
		logDebug(LOG, "Return value = %d", returnValue);
	}

	public static void executeCopyCommand(File sourceFile, File targetFile) throws IOException, InterruptedException {
		logDebug(LOG, "Running %s %s %s...", COPY_COMMAND, sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());

		Process process = new ProcessBuilder(COPY_COMMAND, sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()).start();
		int returnValue = process.waitFor();
		logDebug(LOG, "Return value = %d", returnValue);
	}

	public static void executeRenameCommand(File sourceFile, File targetFile) throws IOException, InterruptedException {
		logDebug(LOG, "Running %s %s %s...", RENAME_COMMAND, sourceFile.getAbsolutePath(), targetFile.getAbsolutePath());

		Process process = new ProcessBuilder(RENAME_COMMAND, sourceFile.getAbsolutePath(), targetFile.getAbsolutePath()).start();
		int returnValue = process.waitFor();
		logDebug(LOG, "Return value = %d", returnValue);
	}

	public static File findFileInDir(File sourceDir, String filename) {
		return findFileWithFilename(sourceDir, filename);
	}

	private static File findFileWithFilename(File dir, String filename) {
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				File fileFromDir = findFileWithFilename(file, filename);
				if (fileFromDir != null) {
					return fileFromDir;
				}
			}
			else if (file.getName().equalsIgnoreCase(filename)) {
				return file;
			}
		}
		return null;
	}

	public static String findRelativePath(File sourceFile, File relativeParent) {
		StringBuilder buff = new StringBuilder();
		File parent = sourceFile.getParentFile();
		while (parent != null && !relativeParent.equals(parent)) {
			buff.insert(0, parent.getName() + "/");
			parent = parent.getParentFile();
		}
		buff.append(sourceFile.getName());

		return buff.toString();
	}

	public static String readAsStringUtf8(File file) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copyBytes(new FileInputStream(file), out);

		return new String(out.toByteArray(), "UTF-8");
	}

	public static void saveToFile(InputStream in, File targetFile) throws IOException {
		saveToFile(in, targetFile, false);
	}

	public static void saveToFile(InputStream in, File targetFile, boolean closeIn) throws IOException {
		writeToFile(in, closeIn, targetFile, false);
	}

	public static String toFileUrl(final File file) {
		return "file:///" + file.getAbsolutePath().replace('\\', '/');
	}

	public static void transferBytes(final FileChannel sourceFileChannel, final WritableByteChannel targetChannel) throws IOException {
		final long targetLength = sourceFileChannel.size();
		long transferred = 0;
		while ((targetLength - transferred) > 0) {
			transferred += sourceFileChannel.transferTo(transferred, FILE_CHANNEL_TRANSFER_SIZE, targetChannel);
		}
	}

	public static void unzip(ZipFile zipFile, File targetDir) throws IOException {
		unzipAndCheckFilename(zipFile, targetDir, null);
	}

	/**
	 * 
	 * @param zipFile zipFile
	 * @param targetDir targetDir
	 * @param patternToCheck pattern to check; can be <code>null</code> (no check performed)
	 * @return <code>true</code> if the specified zip contains a file that doesn't match <code>patternToCheck</code>;
	 *         <code>false</code>, otherwise
	 * @throws IOException on I/O error
	 */
	public static boolean unzipAndCheckFilename(ZipFile zipFile, File targetDir, Pattern patternToCheck) throws IOException {
		boolean nonMatchingFileFound = false;
		Enumeration<? extends ZipEntry> enumeration = zipFile.entries();
		while (enumeration.hasMoreElements()) {
			ZipEntry zipEntry = enumeration.nextElement();
			unzipEntry(zipFile, zipEntry, targetDir);

			if (!nonMatchingFileFound && !zipEntry.isDirectory()) {
				if (!patternToCheck.matcher(zipEntry.getName()).matches()) {
					nonMatchingFileFound = true;
				}
			}
		}
		return nonMatchingFileFound;
	}

	private static void unzipEntry(ZipFile zipfile, ZipEntry entry, File targetDir) throws IOException {
		if (entry.isDirectory()) {
			createDir(new File(targetDir, entry.getName()));
		}
		else {
			File outputFile = new File(targetDir, entry.getName());
			if (!outputFile.getParentFile().exists()) {
				createDir(outputFile.getParentFile());
			}

			BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

			try {
				copyBytes(inputStream, outputStream);
			}
			finally {
				close(outputStream, inputStream);
			}
		}
	}

	/**
	 * 
	 * @param sourceFile sourceFile
	 * @param timeOutInSec timeout in seconds
	 * @return <code>true</code> if sourceFile exists; <code>false</code>, otherwise
	 */
	public static boolean waitUntilFileExists(File sourceFile, long timeOutInSec) {
		if (sourceFile.exists()) {
			return true;
		}

		try {
			FutureTaskRunnerHelper.runAndWait(new FileChecker(sourceFile), timeOutInSec, LOG);
			return true;
		}
		catch (TimeoutException e) {
			return false;
		}
		catch (Exception e) {
			logError(LOG, e, "Error while waiting for file %s: %s", sourceFile.getAbsolutePath(), e.getMessage());
			return false;
		}
	}

	public static void writeToFile(InputStream in, boolean closeIn, File targetFile, boolean append) throws IOException {
		if (in == null) {
			throw new IllegalArgumentException("in cannot be null");
		}
		if (targetFile == null) {
			throw new IllegalArgumentException("targetFile cannot be null");
		}

		FileOutputStream out = new FileOutputStream(targetFile, append);
		try {
			copyBytes(in, out);
		}
		finally {
			close(out);
			if (closeIn) {
				close(in);
			}
		}
	}

	private IOUtil() {
	}
}
