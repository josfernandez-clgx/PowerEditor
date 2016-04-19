package com.mindbox.pe.common;

import static com.mindbox.pe.unittest.TestObjectMother.createInt;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import org.junit.After;
import org.junit.Test;

public class IOUtilTest {

	private File targetFile;

	@After
	public void tearDown() throws Exception {
		IOUtil.delete(targetFile);
	}

	@Test
	public void transferBytesHappyCase() throws Exception {
		final File testFile = new File("src/test/data/transfer-test-1.txt");
		final FileInputStream sourceFileInputStream = new FileInputStream(testFile);
		final FileChannel sourceFileChannel = sourceFileInputStream.getChannel();

		targetFile = new File("target/test-output-" + System.currentTimeMillis() + createInt());
		final FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile, true);
		final FileChannel targetFileChannel = targetFileOutputStream.getChannel();
		try {
			IOUtil.transferBytes(sourceFileChannel, targetFileChannel);

			final String content = IOUtil.readAsStringUtf8(targetFile);
			assertEquals("Test file 1 for IOUtil.transferBytes method: 1234567890.", content);
		}
		finally {
			IOUtil.close(sourceFileChannel, sourceFileInputStream, targetFileChannel, targetFileOutputStream);
		}
	}

	@Test
	public void transferBytesLargeFileOneMBPlus() throws Exception {
		final FileInputStream sourceFileInputStream = new FileInputStream(new File("src/test/data/transfer-test-1mb.art"));
		final FileChannel sourceFileChannel = sourceFileInputStream.getChannel();

		targetFile = new File("target/test-1mb-" + createInt());
		final FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile, true);
		final FileChannel targetFileChannel = targetFileOutputStream.getChannel();
		try {
			IOUtil.transferBytes(sourceFileChannel, targetFileChannel);
			IOUtil.close(sourceFileChannel, sourceFileInputStream, targetFileChannel, targetFileOutputStream);

			assertEquals(1130837L, targetFile.length());
		}
		finally {
			IOUtil.close(sourceFileChannel, sourceFileInputStream, targetFileChannel, targetFileOutputStream);
		}
	}

	@Test
	public void transferBytesLargeFileThreeMBPlus() throws Exception {
		final FileInputStream sourceFileInputStream = new FileInputStream(new File("src/test/data/transfer-test-3mb.art"));
		final FileChannel sourceFileChannel = sourceFileInputStream.getChannel();

		targetFile = new File("target/test-3mb-" + createInt());
		final FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile, true);
		final FileChannel targetFileChannel = targetFileOutputStream.getChannel();
		try {
			IOUtil.transferBytes(sourceFileChannel, targetFileChannel);
			IOUtil.close(sourceFileChannel, sourceFileInputStream, targetFileChannel, targetFileOutputStream);

			assertEquals(4046514L, targetFile.length());
		}
		finally {
			IOUtil.close(sourceFileChannel, sourceFileInputStream, targetFileChannel, targetFileOutputStream);
		}
	}

	@Test
	public void transferBytesWithTwoFilesCase() throws Exception {
		targetFile = new File("target/test-output-" + System.currentTimeMillis() + createInt());
		final FileOutputStream targetFileOutputStream = new FileOutputStream(targetFile, true);
		final FileChannel targetFileChannel = targetFileOutputStream.getChannel();

		FileInputStream sourceInputStream = new FileInputStream("src/test/data/transfer-test-1.txt");
		FileChannel sourceFileChannel = sourceInputStream.getChannel();
		try {
			IOUtil.transferBytes(sourceFileChannel, targetFileChannel);
			IOUtil.close(sourceFileChannel, sourceInputStream);

			sourceInputStream = new FileInputStream("src/test/data/transfer-test-2.txt");
			sourceFileChannel = sourceInputStream.getChannel();
			IOUtil.transferBytes(sourceFileChannel, targetFileChannel);

			final String content = IOUtil.readAsStringUtf8(targetFile);
			assertEquals(String.format("Test file 1 for IOUtil.transferBytes method: 1234567890.Test file 2 for IOUtil.transferBytes method.%n"), content);
		}
		finally {
			IOUtil.close(sourceFileChannel, sourceInputStream, targetFileChannel, targetFileOutputStream);
		}
	}
}
