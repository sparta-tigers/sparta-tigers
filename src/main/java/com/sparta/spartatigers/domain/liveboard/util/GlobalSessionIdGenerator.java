package com.sparta.spartatigers.domain.liveboard.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class GlobalSessionIdGenerator {

    public static final String SERVER_ID;

    static {
        String serverId;
        try {
            serverId = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        SERVER_ID = serverId;
    }

    public static String generate(String sessionId) {
        return SERVER_ID + "-" + sessionId;
    }
}
