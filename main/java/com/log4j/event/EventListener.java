package com.log4j.event;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import org.apache.log4j.spi.LoggingEvent;

/**
 * A Server side Logging framework. This class receives the log messages
 * from different sockets and publishes the message to service bus for
 * persisting the log message
 *
 * @author sats
 */
public class EventListener implements Runnable {

	private final int port;

	public EventListener(int port) {
		this.port = port;
	}

	public EventListener() {
		this.port = 58584;
	}

	/**
	 * Retrieves the message from different sockets and publishes the message to
	 * publisher for persisting the message
	 */
	@Override
	public final void run() {
		try {
			System.out.println("Listening on port " + port);
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				LogReader reader = new LogReader((socket));
				readers.add(reader);
				Thread th = new Thread(reader);
				th.setName("Socket-Reader-" + (++readerCount));
				System.out.println("Created Log Socket Reader #" + readerCount
						+ " - " + readers.size()
						+ " readers exist and are active");
				th.start();
				// clear out any dead readers
				ArrayList<LogReader> deadReaders = new ArrayList<>();
				for (LogReader lr : readers) {
					if (!lr.isAlive()) {
						deadReaders.add(lr);
					}
				}

				readers.removeAll(deadReaders);
				System.out.println("Cleaned Log Socket Reader list - "
						+ readers.size() + " readers exist and are active");
			}
		} catch (IOException ex) {
		}
	}

	private int readerCount = 0;
	private ArrayList<LogReader> readers = new ArrayList<>();

	private class LogReader implements Runnable {

		private boolean alive;
		private Socket socket;

		public boolean isAlive() {
			return alive;
		}

		public void setAlive(boolean alive) {
			this.alive = alive;
		}

		public LogReader(Socket socket) {
			this.socket = socket;
			this.alive = true;
		}

		@Override
		public void run() {
			try {
				ObjectInputStream ois = new ObjectInputStream(
						new BufferedInputStream(socket.getInputStream()));
				while (alive && !socket.isClosed() && socket.isConnected()
						&& !socket.isInputShutdown()) {
					LoggingEvent incomingEvent = (LoggingEvent) ois
							.readObject();

					LogMessage msg = createLogMessage(incomingEvent, socket);
					publishLogMessage(msg);
					synchronized (lists) {
						lists.add(msg);
					}
				}
			} catch (ClassNotFoundException | IOException ex) {
				System.out.println("Exception " + ex);
			}
			try {
				socket.close();
			} catch (IOException ex) {
			}
			alive = false;
		}
	}

	private ArrayList<LogMessage> lists = new ArrayList<>();

	/**
	 *
	 * @param message
	 */
	synchronized private void publishLogMessage(LogMessage message) {
		// publish the message to log file or database, as per your design
	}

	/**
	 *
	 * @param logEvent
	 * @param socket
	 * @return
	 */
	private LogMessage createLogMessage(LoggingEvent logEvent, Socket socket) {
		LogMessage logMessage = new LogMessage();
		logMessage.setLogLevel(logEvent.getLevel().toString());
		logMessage.setLogMessage(logEvent.getMessage().toString());
		logMessage.setLoggerName(logEvent.getLoggerName());
		logMessage.setDate(logEvent.getTimeStamp());

		logMessage.setSender(socket.getInetAddress().getHostName());
		logMessage.setApplication((String) logEvent.getMDC("application"));
		return logMessage;
	}

	public void init() {
		/*
		 * String portList = null; try { portList =
		 * ConfigFileLoader.INSTANCE.loadPropertyFile
		 * (SOCKET_PORTS).getProperty("SOCKET_PORTS"); } catch
		 * (IOException ex) { } String ports[] = portList.split(";"); for
		 * (String port : ports) { new Thread(new
		 * EventListener(Integer.parseInt(port))).start(); }
		 */
		new Thread(this).start();
	}
}
