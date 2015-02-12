package com.log4j.appender.test;

import org.apache.log4j.Logger;

/**
 * sats
 *
 */
public class LoggerClient {
	private final static Logger log = Logger.getLogger(LoggerClient.class);

	public static void main(String[] args) throws InterruptedException {
		while (true) {
			log.info("Info log from java application ");
			log.debug("Debug log from java application ");
			log.trace("Trace log from java application ");
			log.error("Erro log from java application ");
			log.warn("Warn log from java application ");
			log.fatal("fatal log from java application ");

		}

	}
}
