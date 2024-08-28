/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.eniq.events.ui.client.resources;

import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.gwt.resources.client.ImageResource;

public class ToolbarIconResourceHelper {

    public enum IconType {
        FORWARD("btnForward"), BACK("btnBack"), REFRESH("btnRefresh"), TIME("btnTime"), PROPERTIES("btnProperties"), EXPORT("btnExport"), RECURRING(
                "btnRecur"), SAC("btnSac"), KPI("btnKPI"), CAUSE_CODE("btnCC"), SUB_CAUSE_CODE("btnSCC"), DISCONNECTION_CODE("btnDC"), SUBSCRIBER_DETAILS(
                "btnSubscriberDetails"), SUBSCRIBER_DETAILS_PTMSI("btnSubscriberDetailsPTMSI"), SUBSCRIBER_DETAILS_WCDMA_CFA(
                "btnSubscriberDetailsWcdmaCFA"), HIDE_SHOW_LEGEND("btnHideShowLegend"), UNKNOWN("");

        private final String iconType;

        private IconType(final String iconType) {
            this.iconType = iconType;
        }

        /**
         * @return icon type
         */
        public String getType() {
            return iconType;
        }

        public static IconType fromString(final String type) {
            for (final IconType iconType : IconType.values()) {
                if (iconType.getType().equals(type)) {
                    return iconType;
                }
            }
            return IconType.UNKNOWN;
        }

    }

    private final EniqResourceBundle eniqResourceBundle;

    public ToolbarIconResourceHelper() {
        eniqResourceBundle = MainEntryPoint.getInjector().getEniqResourceBundle();
    }

    public ImageResource[] getIcon(final String name) {
        final ImageResource imageResources[] = new ImageResource[3];
        final IconType iconType = IconType.fromString(name);

        switch (iconType) {
            case REFRESH:
                imageResources[0] = eniqResourceBundle.refreshIcon();
                imageResources[1] = eniqResourceBundle.refreshIconDisabled();
                imageResources[2] = eniqResourceBundle.refreshIconHover();
                break;

            case BACK:
                imageResources[0] = eniqResourceBundle.backArrowIcon();
                imageResources[1] = eniqResourceBundle.backArrowIconDisabled();
                imageResources[2] = eniqResourceBundle.backArrowIconHover();
                break;

            case FORWARD:
                imageResources[0] = eniqResourceBundle.forwardArrowIcon();
                imageResources[1] = eniqResourceBundle.forwardArrowIconDisabled();
                imageResources[2] = eniqResourceBundle.forwardArrowIconHover();
                break;

            case TIME:
                imageResources[0] = eniqResourceBundle.viewScheduleIcon();
                imageResources[1] = eniqResourceBundle.viewScheduleIconDisabled();
                imageResources[2] = eniqResourceBundle.viewScheduleIconHover();
                break;

            case PROPERTIES:
                imageResources[0] = eniqResourceBundle.propertiesIcon();
                imageResources[1] = eniqResourceBundle.propertiesIconDisabled();
                imageResources[2] = eniqResourceBundle.propertiesIconHover();
                break;

            case EXPORT:
                imageResources[0] = eniqResourceBundle.exportToIcon();
                imageResources[1] = eniqResourceBundle.exportToIconDisabled();
                imageResources[2] = eniqResourceBundle.exportToIconHover();
                break;

            case RECURRING:
                imageResources[0] = eniqResourceBundle.recurrErrEventIcon();
                imageResources[1] = eniqResourceBundle.recurrErrEventIconDisabled();
                imageResources[2] = eniqResourceBundle.recurrErrEventIconHover();
                break;

            case SAC:
                imageResources[0] = eniqResourceBundle.iconCellAdjIcon();
                imageResources[1] = eniqResourceBundle.iconCellAdjIconDisabled();
                imageResources[2] = eniqResourceBundle.iconCellAdjIconHover();
                break;

            case KPI:
                imageResources[0] = eniqResourceBundle.kpiIcon();
                imageResources[1] = eniqResourceBundle.kpiIconDisable();
                imageResources[2] = eniqResourceBundle.kpiIconHover();
                break;

            case CAUSE_CODE:
                imageResources[0] = eniqResourceBundle.causeCodeIcon();
                imageResources[1] = eniqResourceBundle.causeCodeIconDisabled();
                imageResources[2] = eniqResourceBundle.causeCodeIconHover();
                break;

            case SUB_CAUSE_CODE:
                imageResources[0] = eniqResourceBundle.subCauseCodeIcon();
                imageResources[1] = eniqResourceBundle.subCauseCodeIconDisabled();
                imageResources[2] = eniqResourceBundle.subCauseCodeIconHover();
                break;

            case DISCONNECTION_CODE:
                imageResources[0] = eniqResourceBundle.disconnectionCodeIcon();
                imageResources[1] = eniqResourceBundle.disconnectionCodeIconDisabled();
                imageResources[2] = eniqResourceBundle.disconnectionCodeIconHover();
                break;

            case SUBSCRIBER_DETAILS:
            case SUBSCRIBER_DETAILS_WCDMA_CFA:
                imageResources[0] = eniqResourceBundle.userBlueIcon();
                imageResources[1] = eniqResourceBundle.userBlueIconDisabled();
                imageResources[2] = eniqResourceBundle.userBlueIconHover();
                break;

            case SUBSCRIBER_DETAILS_PTMSI:
                imageResources[0] = eniqResourceBundle.userBluePTIMSIIcon();
                imageResources[1] = eniqResourceBundle.userBluePTIMSIIconDisabled();
                imageResources[2] = eniqResourceBundle.userBluePTIMSIIconHover();
                break;

            case HIDE_SHOW_LEGEND:
                imageResources[0] = eniqResourceBundle.pieLegendIcon();
                imageResources[1] = eniqResourceBundle.pieLegendIconDisabled();
                imageResources[2] = eniqResourceBundle.pieLegendIconHover();
                break;

            default:

                break;
        }
        return imageResources;
    }
}
