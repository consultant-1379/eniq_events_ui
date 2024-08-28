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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface EniqResourceBundle extends ClientBundle {

    /* Info Button in the Main Task bar */
    @Source("images/imageButtons/Info.png")
    ImageResource infoIconToolbar();

    @Source("images/imageButtons/Info_Disable.png")
    ImageResource infoIconToolbarDisable();

    @Source("images/imageButtons/Info_Hover.png")
    ImageResource infoIconToolbarHover();

    /* Cascade Button in the Main Task bar */
    @Source("images/buttons/Cascade_Windows.png")
    ImageResource cascadeIconToolbar();

    @Source("images/buttons/Cascade_Windows_Disable.png")
    ImageResource cascadeIconToolbarDisable();

    @Source("images/buttons/Cascade_Windows_Disable.png")
    ImageResource cascadeIconToolbarHover();

    /* Title Button in the Main Task bar */
    @Source("images/buttons/Tile_Windows.png")
    ImageResource tileIconToolbar();

    @Source("images/buttons/Tile_Windows_Hover.png")
    ImageResource tileIconToolbarHover();

    @Source("images/buttons/Tile_Windows_Disable.png")
    ImageResource tileIconToolbarDisable();

    /* Excluded TACs information button */
    @Source("images/imageButtons/Excluded_TAC.png")
    ImageResource exclTacIconToolbar();

    /* Launch Button */
    @Source("images/buttons/go_button_normal.png")
    ImageResource launchIconToolbar();

    @Source("images/buttons/go_button_hover.png")
    ImageResource launchIconToolbarHover();

    @Source("images/buttons/go_button_disabled.png")
    ImageResource launchIconToolbarDisable();

    /****** Image Button Icons ********/
    /* ImageButton Icons - Normal State */
    @Source("images/imageButtons/Next.png")
    ImageResource forwardArrowIcon();

    @Source("images/imageButtons/Previous.png")
    ImageResource backArrowIcon();

    @Source("images/imageButtons/cause_code.png")
    ImageResource causeCodeIcon();

    @Source("images/imageButtons/cause_code.png")
    ImageResource disconnectionCodeIcon();

    @Source("images/imageButtons/Bar_Chart.png")
    ImageResource chartBarIcon();

    @Source("images/imageButtons/Export_14px.png")
    ImageResource exportToIcon();

    @Source("images/imageButtons/Adjacent_Cell.png")
    ImageResource iconCellAdjIcon();

    @Source("images/imageButtons/Info.png")
    ImageResource infoBlueBoxIcon();

    @Source("images/imageButtons/Legend_On.png")
    ImageResource pieLegendIcon();

    @Source("images/imageButtons/Row_Info.png")
    ImageResource propertiesIcon();

    @Source("images/imageButtons/Recurring_Error.png")
    ImageResource recurrErrEventIcon();

    @Source("images/imageButtons/refresh.png")
    ImageResource refreshIcon();

    @Source("images/imageButtons/Sub_Cause_Code.png")
    ImageResource subCauseCodeIcon();

    @Source("images/imageButtons/Grid.png")
    ImageResource tableIcon();

    @Source("images/imageButtons/toolbar-split.gif")
    ImageResource toolbarSplitIcon();

    @Source("images/imageButtons/Custom_Time.png")
    ImageResource viewScheduleIcon();

    @Source("images/imageButtons/Subscriber.png")
    ImageResource userBlueIcon();

    @Source("images/imageButtons/Kpi.png")
    ImageResource kpiIcon();

    @Source("images/imageButtons/Subscriber.png")
    ImageResource userBluePTIMSIIcon();

    /* ImageButton Icons - Hover State */
    @Source("images/imageButtons/Next_Hover.png")
    ImageResource forwardArrowIconHover();

    @Source("images/imageButtons/Previous_Hover.png")
    ImageResource backArrowIconHover();

    @Source("images/imageButtons/cause_code_hover.png")
    ImageResource causeCodeIconHover();

    @Source("images/imageButtons/cause_code_hover.png")
    ImageResource disconnectionCodeIconHover();

    @Source("images/imageButtons/Bar_Chart_Hover.png")
    ImageResource chartBarIconHover();

    @Source("images/imageButtons/Export_14px_Hover.png")
    ImageResource exportToIconHover();

    @Source("images/imageButtons/Adjacent_Cell_Hover.png")
    ImageResource iconCellAdjIconHover();

    @Source("images/imageButtons/Subscriber_Hover.png")
    ImageResource infoBlueBoxIconHover();

    @Source("images/imageButtons/Legend_On_Hover.png")
    ImageResource pieLegendIconHover();

    @Source("images/imageButtons/Row_Info_Hover.png")
    ImageResource propertiesIconHover();

    @Source("images/imageButtons/Recurring_Error_Hover.png")
    ImageResource recurrErrEventIconHover();

    @Source("images/imageButtons/refresh_hover.png")
    ImageResource refreshIconHover();

    @Source("images/imageButtons/Sub_Cause_Code_Hover.png")
    ImageResource subCauseCodeIconHover();

    @Source("images/imageButtons/Grid_Hover.png")
    ImageResource tableIconHover();

    @Source("images/imageButtons/Custom_Time_Hover.png")
    ImageResource viewScheduleIconHover();

    @Source("images/imageButtons/Subscriber_Hover.png")
    ImageResource userBlueIconHover();

    @Source("images/imageButtons/Subscriber_Hover.png")
    ImageResource userBluePTIMSIIconHover();

    @Source("images/imageButtons/Kpi_hover.png")
    ImageResource kpiIconHover();

    /* ImageButton Icons - Disabled State */
    @Source("images/imageButtons/Next_Disable.png")
    ImageResource forwardArrowIconDisabled();

    @Source("images/imageButtons/Previous_Disable.png")
    ImageResource backArrowIconDisabled();

    @Source("images/imageButtons/Cause_Code_Disable.png")
    ImageResource causeCodeIconDisabled();

    @Source("images/imageButtons/Cause_Code_Disable.png")
    ImageResource disconnectionCodeIconDisabled();

    @Source("images/imageButtons/Bar_Chart_Disable.png")
    ImageResource chartBarIconDisabled();

    @Source("images/imageButtons/Export_14px_Disable.png")
    ImageResource exportToIconDisabled();

    @Source("images/imageButtons/Adjacent_Cell_Disable.png")
    ImageResource iconCellAdjIconDisabled();

    @Source("images/imageButtons/Info_Disable.png")
    ImageResource infoBlueBoxIconDisabled();

    @Source("images/imageButtons/Legend_OnOff_Disable.png")
    ImageResource pieLegendIconDisabled();

    @Source("images/imageButtons/Grid_Disable.png")
    ImageResource propertiesIconDisabled();

    @Source("images/imageButtons/Recurring_Error_Disable.png")
    ImageResource recurrErrEventIconDisabled();

    @Source("images/imageButtons/Refresh_Disable.png")
    ImageResource refreshIconDisabled();

    @Source("images/imageButtons/Sub_Cause_Code_Disable.png")
    ImageResource subCauseCodeIconDisabled();

    @Source("images/imageButtons/Grid_Disable.png")
    ImageResource tableIconDisabled();

    @Source("images/imageButtons/Custom_Time_Disable.png")
    ImageResource viewScheduleIconDisabled();

    @Source("images/imageButtons/Subscriber_Disable.png")
    ImageResource userBlueIconDisabled();

    @Source("images/imageButtons/Subscriber_Disable.png")
    ImageResource userBluePTIMSIIconDisabled();

    @Source("images/imageButtons/New_KPI_Disable.png")
    ImageResource kpiIconDisable();

    //@Source("images/imageButtons/Kpi_disabled.png")
    // ImageResource kpiIconDisabled();

}
