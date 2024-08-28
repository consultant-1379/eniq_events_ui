/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2013
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 ******************************************************************************* */
package com.ericsson.eniq.events.ui.client.wcdmauertt;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

public interface EventGridViewUerttResourceBundle extends ClientBundle {

    @Source("EventGridViewUertt.css")
    UerttDisplayWindowOnLaunch css();

    interface UerttDisplayWindowOnLaunch extends CssResource {
        String vline();

        String uerttLabelStyleName();

        String uerttGridStyle();

        String messageRRCStyle();

        String messageRANAPStyle();

        String messageNBAPStyle();

        String messageRNSAPStyle();

        String uerttCellStyle();

        String uerttGridStyleHeader();

        String timeStampTextStyle();

        String arrowSelectionCss();

        String messageRRCStyleAndVline();

        String messageRANAPStyleAndVline();

        String messageNBAPStyleAndVline();

        String messageRNSAPStyleAndVline();

        String aranapAndArrowLeft();

        String arrcAndArrowLeft();

        String anbapAndArrowLeft();

        String arnsapAndArrowLeft();

        String arrcAndArrowRight();

        String aranapAndArrowRight();

        String arnsapAndArrowRight();

        String anbapAndArrowRight();

        String uerttCellStyleAndVline();

        String toolbar();

        String footer();
    }
}
