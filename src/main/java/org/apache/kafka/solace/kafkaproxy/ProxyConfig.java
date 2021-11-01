package org.apache.kafka.solace.kafkaproxy;

/*
 * Copyright 2021 Solace Corporation. All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */

import static org.apache.kafka.common.utils.Utils.getHost;
import static org.apache.kafka.common.utils.Utils.getPort;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;
import org.apache.kafka.common.security.auth.SecurityProtocol;

public class ProxyConfig  extends AbstractConfig {
	
    public static final String LISTENERS_CONFIG = "listeners";
    public static final String LISTENERS_DOC = "A list of [protocol://]host:[port] tuples to listen on.";
	
	
    private static final Pattern SECURITY_PROTOCOL_PATTERN = Pattern.compile("(.*)?://.*");
    
    /**
     * Extracts the security protocol from a "protocol://host:port" address string.
     * @param address address string to parse
     * @return security protocol or null if the given address is incorrect
     */
    public static String getSecurityProtocol(String address) {
        Matcher matcher = SECURITY_PROTOCOL_PATTERN.matcher(address);
        return matcher.matches() ? matcher.group(1) : null;
    }
	
	// Similar to ClientsUtils::parseAndValidateAddresses but added support for protocol as part of string 
	// to be of style of "listener" configuration item for broker
    public static List<ProxyReactor.ListenEntry> parseAndValidateListenAddresses(List<String> urls) {
        List<ProxyReactor.ListenEntry> addresses = new ArrayList<ProxyReactor.ListenEntry>();
        for (String url : urls) {
            if (url != null && url.length() > 0) {
            	String protocolString = getSecurityProtocol(url);
            	SecurityProtocol securityProtocol = SecurityProtocol.PLAINTEXT;
            	if (protocolString != null) {
            		try {
            			securityProtocol = SecurityProtocol.forName(protocolString);
            		} catch (IllegalArgumentException e) {
                        throw new ConfigException("Invalid security protocol " + LISTENERS_CONFIG + ": " + url);
            		}
            	}
                String host = getHost(url);
                Integer port = getPort(url);
                if (host == null || port == null)
                    throw new ConfigException("Invalid url in " + LISTENERS_CONFIG + ": " + url);
                try {
                    InetSocketAddress address = new InetSocketAddress(host, port);
                    addresses.add(new ProxyReactor.ListenEntry(securityProtocol, address));
                } catch (NumberFormatException e) {
                    throw new ConfigException("Invalid host:port in " + LISTENERS_CONFIG + ": " + url);
                }
            }
        }
        if (addresses.size() < 1)
            throw new ConfigException("No urls given in " + LISTENERS_CONFIG);
        return addresses;
    }


    /*
     * NOTE: DO NOT CHANGE EITHER CONFIG STRINGS OR THEIR JAVA VARIABLE NAMES AS THESE ARE PART OF THE PUBLIC API AND
     * CHANGE WILL BREAK USER CODE.
     */

    private static final ConfigDef CONFIG;

    static {
        CONFIG = new ConfigDef().define(LISTENERS_CONFIG, Type.LIST, Collections.emptyList(), new ConfigDef.NonNullValidator(), Importance.HIGH, LISTENERS_DOC)
                                .withClientSslSupport();
    }
    
    public ProxyConfig(Properties props) {
        super(CONFIG, props, false /* do not log values */);
    }

    public ProxyConfig(Map<String, Object> props) {
        super(CONFIG, props, false /* do not log values */);
    }

}