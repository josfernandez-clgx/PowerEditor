package com.mindbox.pe.common;

import java.io.File;

/**
 * Runnable that stops if and only if the specified file exists.
 * 
 * @author Gene Kim
 */
class FileChecker implements Runnable {

	private final File file;

	public FileChecker(File file) {
		super();
		this.file = file;
	}

	@Override
	public void run() {
		while (true) {
			if (file.exists()) {
				return;
			}

			try {
				Thread.sleep(25);
			}
			catch (InterruptedException e) {
				// ignore
			}
		}
	}
}