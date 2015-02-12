package com.log4j.socketappender;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import org.apache.log4j.Appender;
import org.apache.log4j.net.SocketAppender;

import com.log4j.socket.comms.Command;
import com.log4j.socket.comms.CommsPacket;

/**
 * An asynchronous listener, and communicates between logging server and different logging applications(clients) 
 * @author sats
 *
 */
public class AsynchListener implements Runnable {

	private volatile boolean active = false;
	// Parameter("MulticastAddress to be used to poll for client requests)
	private String MultiCastAddress = "231.5.6.7";
	// Parameter("Port used for multi cast connections")
	private int MultiCastPort = 9903;
	// Parameter("Time in seconds between re-transmits")
	private int retry = 5;
	private String ipAddress = "0.0.0.0";
	private final AsynchLogAppender parent;

	// <editor-fold defaultstate="collapsed"
	// desc="Parameters settable from the client requests">
	public String getMultiCastAddress() {
		return MultiCastAddress;
	}

	public void setMultiCastAddress(String MultiCastAddress) {
		this.MultiCastAddress = MultiCastAddress;
	}

	public int getMultiCastPort() {
		return MultiCastPort;
	}

	public void setMultiCastPort(int MultiCastPort) {
		this.MultiCastPort = MultiCastPort;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

	// </editor-fold>
	public AsynchListener(final AsynchLogAppender parent) {
		this.parent = parent;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException ex) {
			System.out.println("Could not determine local IP Address");
		}
	}

	@Override
	public void run() {
		while (active) {
			listenForServers();
		}

	}

	public void start() {
		Thread th = new Thread(this);
		th.setName("Logger-Command-Listener");
		th.setPriority(Thread.MIN_PRIORITY);
		active = true;
		th.start();
	}

	public void stop() {
		active = false;
	}

	private void listenForServers() {
		while (active) {
			try {
				MulticastSocket socket = new MulticastSocket(MultiCastPort);
				InetAddress group = InetAddress.getByName(MultiCastAddress);
				socket.joinGroup(group);
				while (active) {
					byte[] buf = new byte[1000];
					DatagramPacket recv = new DatagramPacket(buf, buf.length);
					socket.receive(recv);
					ObjectInputStream oin = new ObjectInputStream(
							new ByteArrayInputStream(recv.getData()));
					CommsPacket request = (CommsPacket) oin.readObject();
					processCommand(request);
				}
			} catch (ClassNotFoundException | IOException ex) {
			}
		}
	}

	protected void processCommand(final CommsPacket request) {
		if (checkTarget(request.getTarget())) {
			switch (request.getCommand()) {
			case NOTIFY:
				// notify the logserver that we are here
				// only notify if we are not already sending logs
				notifyCommand(request);
				break;
			case CONNECT:
				connectCommand(request);
				break;
			case DISCONNECT:
				disconnectCommand(request);
				break;
			default:
				break;
			}
		}
	}

	private HashMap<InetAddress, Appender> registered = new HashMap<InetAddress, Appender>();

	private boolean checkTarget(final String target) {
		boolean result = false;
		if ("*".equals(target) || target.equals(ipAddress)) {
			result = true;
		}
		return result;
	}

	private void notifyCommand(final CommsPacket request) {
		if (!registered.containsKey(request.getSender())) {
			try {
				// random delay (up to a second to avoid saturating the server
				// with responses
				long delay = (long) (1000L * Math.random());
				Thread.sleep(delay);
				try {
					Socket socket = new Socket(request.getSender(),
							request.getPort());
					CommsPacket packet = new CommsPacket();
					packet.setCommand(Command.IAMHERE);
					packet.setInfo(parent.getName());
					ObjectOutputStream sockout = new ObjectOutputStream(
							socket.getOutputStream());
					sockout.writeObject(packet);

					sockout.close();
					socket.close();
				} catch (IOException ex) {
				}
			} catch (InterruptedException ex) {
			}
		}
	}

	private void connectCommand(final CommsPacket request) {
		// start sending logs to the server
		if (!registered.containsKey(request.getSender())) {
			Log4jSocketAppender appender = new Log4jSocketAppender(
					request.getSender(), request.getPort());
			registered.put(request.getSender(), appender);
			appender.setApplication(parent.getName());
			parent.addAppender(appender);
		}
	}

	private void disconnectCommand(final CommsPacket request) {
		// stop sending logs to the server
		if (registered.containsKey(request.getSender())) {
			Log4jSocketAppender appender = (Log4jSocketAppender) registered
					.remove(request.getSender());
			appender.close();
			parent.removeAppender(appender);
		}
	}

	/**
	 * Log4jSocketAppender extends org.apache.log4j.net.SocketAppender
	 * @author sats
	 *
	 */
	private class Log4jSocketAppender extends SocketAppender {

		public Log4jSocketAppender() {
			super();
		}

		public Log4jSocketAppender(final InetAddress address, final int port) {
			super(address, port);
		}

		public Log4jSocketAppender(final String host, final int port) {
			super(host, port);
		}
	}

}
