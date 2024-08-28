package com.ericsson.eniq.events.ui.client.workspace.launchwindowconfig.datatype;

/**
 * -----------------------------------------------------------------------
 * Copyright (C) 2013 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

public enum ConfigLaunchType {

    /*TOTAL("4", "Total Failures"),*/
    TOTAL_RAB("3", "Total RAB Failures"),
    CIRCUIT_SWITCHED("0", "Circuit Switched RAB Failures"),
    PACKET_SWITCHED("1", "Packet Switched RAB Failures"),
    MULTI_RAB("2", "Multi RAB Failures");

    private final String configLaunchType;

    private String displayName;

    ConfigLaunchType(final String configLaunchTypeId, String displayName) {
        this.configLaunchType = configLaunchTypeId;
        this.displayName = displayName;
    }

    public static ConfigLaunchType fromString(final String type) {
        for (final ConfigLaunchType configLaunchType : ConfigLaunchType.values()) {
            if (configLaunchType.toString().equals(type)) {
                return configLaunchType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return configLaunchType;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
