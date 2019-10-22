package com.mindbox.pe.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;

import org.apache.log4j.Logger;

public class ExternalProcessUtil {

    public static class ExternalProcessRuntimeException extends RuntimeException {

        private static final long serialVersionUID = 20101104113200L;

        public ExternalProcessRuntimeException(String message, Throwable cause) {
            super(message, cause);
        }

        public ExternalProcessRuntimeException(Throwable cause) {
            super(cause);
        }

    }


    private class StreamConsumer implements Runnable {

        private String type;
        private InputStream in;

        public StreamConsumer(String type, InputStream in) {
            super();
            this.type = type;
            this.in = in;
        }

        @Override
        public void run() {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(in));

                logger.info(String.format("[%s] BEGIN", type));

                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    logger.info(String.format("[%s] %s", type, line));
                }
                logger.info(String.format("[%s] DONE", type));
            }
            catch (IOException e) {
                logger.error("Failed to read " + type, e);
            }
            finally {
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch (Exception e2) {
                        logger.warn("Failed to close " + reader, e2);
                    }
                }
            }
        }
    }

    private class ExternalProcessRunnable implements Runnable {

        private final ProcessBuilder processBuilder;

        ExternalProcessRunnable(ProcessBuilder processBuilder) {
            this.processBuilder = processBuilder;
        }

        @Override
        public void run() {
            try {
                Process process = processBuilder.start(); // VeraCode Flaw ID 67 CWE ID 78 (OS Command Injection)

                Thread thread = new Thread(new StreamConsumer(process + " OUTPUT", process.getInputStream()));
                thread.setDaemon(true);
                thread.start();
                thread = null;

                thread = new Thread(new StreamConsumer(process + " ERROR", process.getErrorStream()));
                thread.setDaemon(true);
                thread.start();

                int returnValue = process.waitFor();
                logger.info(String.format("[%s] Return Value = %d", process, returnValue));
            }
            catch (Exception e) {
                throw new ExternalProcessRuntimeException(e);
            }
        }
    }

    private Logger logger;

    public ExternalProcessUtil(Logger logger) {
        super();
        this.logger = logger;
    }

    public void executeProcess(Map<String, String> environmentVarMap, String... commandArray) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        processBuilder.environment().putAll(environmentVarMap);

        ExternalProcessRunnable externalProcessRunnable = new ExternalProcessRunnable(processBuilder);

        Thread thread = new Thread(externalProcessRunnable);
        thread.setDaemon(true);

        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Uncaught Exception in external process", e);
            }
        });

        thread.start();
    }

}
