package com.sparta.spartatigers.domain.liveboard.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

public class GlobalSessionIdGenerator {

	public static final String SERVER_ID;

	static {
		String serverId;
		try {
			serverId = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			serverId = UUID.randomUUID().toString();
		}
		SERVER_ID = serverId;
	}
	public static String generate(String sessionId) {
		return SERVER_ID + "-" +sessionId;
	}
}

