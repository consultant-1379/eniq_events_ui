package com.ericsson.eniq.events.ui.client.kpi;

import javax.inject.Inject;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.ui.client.kpi.resources.IButtonResourceBundle;
import com.ericsson.eniq.events.ui.client.kpi.resources.KpiResourceBundle;
import com.ericsson.eniq.events.ui.client.kpi.widget.IndicatorButton;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class KpiPanelView extends BaseView<KpiPanelPresenter> {

    interface Binder extends UiBinder<Widget, KpiPanelView> {
    }

    Binder uiBinder = GWT.create(Binder.class);

    KpiResourceBundle kpiResourceBundle;

    @UiField(provided = true)
    IButtonResourceBundle buttonResources;

    @UiField
    IndicatorButton criticalPanel;

    @UiField
    IndicatorButton majorPanel;

    @UiField
    IndicatorButton minorPanel;

    @UiField
    IndicatorButton warningPanel;

    @UiField
    Image configImage;

    @Inject
    public KpiPanelView(final KpiResourceBundle resource, final IButtonResourceBundle buttonResourceBundle) {
        this.kpiResourceBundle = resource;
        this.buttonResources = buttonResourceBundle;

        onInitialize();
    }

    @UiHandler("configImage")
    public void onClick(final ClickEvent event) {
        getPresenter().launchKPIConfiguration();
    }

    public void onInitialize() {
        final Widget widget = uiBinder.createAndBindUi(this);
        kpiResourceBundle.style().ensureInjected();
        initWidget(widget);

        getElement().setId("kpiPanel");
        configImage.getElement().setId("configImage");
    }

}
