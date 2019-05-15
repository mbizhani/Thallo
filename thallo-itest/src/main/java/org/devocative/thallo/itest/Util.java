package org.devocative.thallo.itest;

import java.net.ServerSocket;

public class Util {
	public static Integer findRandomOpenPortOnAllLocalInterfaces() {
		try (ServerSocket socket = new ServerSocket(0)) {
			return socket.getLocalPort();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
