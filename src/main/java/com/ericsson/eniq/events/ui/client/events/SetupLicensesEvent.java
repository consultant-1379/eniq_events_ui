package com.ericsson.eniq.events.ui.client.events;

import com.ericsson.eniq.events.ui.client.datatype.LicenseInfoDataType;
import com.google.gwt.event.shared.GwtEvent;

import java.util.List;

public class SetupLicensesEvent extends GwtEvent<SetupLicensesEventHandler> {

    public final static Type<SetupLicensesEventHandler> TYPE = new Type<SetupLicensesEventHandler>();

    private List<LicenseInfoDataType> licenses;

    public SetupLicensesEvent(List<LicenseInfoDataType> licenses) {
        this.licenses = licenses;
    }

    @Override
    protected void dispatch(final SetupLicensesEventHandler handler) {
        handler.onLicensesEvent(this);
    }

    @Override
    public Type<SetupLicensesEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<LicenseInfoDataType> getLicenses() {
        return licenses;
    }


}
