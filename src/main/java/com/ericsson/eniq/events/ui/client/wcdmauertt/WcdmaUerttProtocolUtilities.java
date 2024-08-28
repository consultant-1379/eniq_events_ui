/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.wcdmauertt;

public class WcdmaUerttProtocolUtilities {
    private static final String EVENT_PROTOCOL_RRC = "EVENT_PROTOCOL_RRC";
    private static final String EVENT_PROTOCOL_RANAP = "EVENT_PROTOCOL_RANAP";
    private static final String EVENT_PROTOCOL_NBAP = "EVENT_PROTOCOL_NBAP";
    private static final String EVENT_PROTOCOL_RNSAP = "EVENT_PROTOCOL_RNSAP";

    private enum Direction {
        SENT, RECEIVED
    }

    protected static boolean isRnsapReceived(final String direction, final String protocol) {
        return isRnsap(protocol) && isDirectionReceived(direction);
    }

    protected static boolean isRnsapSent(final String direction, final String protocol) {
        return isRnsap(protocol) && isDirectionSent(direction);
    }

    protected static boolean isNbapReceived(final String direction, final String protocol) {
        return isNbap(protocol) && isDirectionReceived(direction);
    }

    protected static boolean isNBapSent(final String direction, final String protocol) {
        return isNbap(protocol) && isDirectionSent(direction);
    }

    protected static boolean isRanapReceived(final String direction, final String protocol) {
        return isRanap(protocol) && isDirectionReceived(direction);
    }

    protected static boolean isRanapSent(final String direction, final String protocol) {
        return isRanap(protocol) && isDirectionSent(direction);
    }

    protected static boolean isRRCReceived(final String direction, final String protocol) {
        return isRrc(protocol) && isDirectionReceived(direction);
    }

    protected static boolean isRRCSent(final String direction, final String protocol) {
        return isRrc(protocol) && isDirectionSent(direction);
    }

    private static boolean isDirectionReceived(final String direction) {
        return direction.equals(Direction.RECEIVED.toString());
    }

    private static boolean isDirectionSent(final String direction) {
        return direction.equals(Direction.SENT.toString());
    }

    private static boolean isRrc(final String protocol) {
        return protocol.equals(EVENT_PROTOCOL_RRC);
    }

    private static boolean isRanap(final String protocol) {
        return protocol.equals(EVENT_PROTOCOL_RANAP);
    }

    private static boolean isNbap(final String protocol) {
        return protocol.equals(EVENT_PROTOCOL_NBAP);
    }

    private static boolean isRnsap(final String protocol) {
        return protocol.equals(EVENT_PROTOCOL_RNSAP);
    }
}
