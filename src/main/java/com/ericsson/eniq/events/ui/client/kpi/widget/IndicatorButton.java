package com.ericsson.eniq.events.ui.client.kpi.widget;

import com.ericsson.eniq.events.ui.client.datatype.MultipleInstanceWinId;
import com.ericsson.eniq.events.ui.client.datatype.TimeInfoDataType;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEvent;
import com.ericsson.eniq.events.ui.client.events.RefreshWindowEventHandler;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEvent;
import com.ericsson.eniq.events.ui.client.events.TimeParameterValueChangeEventHandler;
import com.ericsson.eniq.events.ui.client.events.window.WindowClosedEvent;
import com.ericsson.eniq.events.ui.client.events.window.WindowClosedEventHandler;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationConstants;
import com.ericsson.eniq.events.ui.client.kpi.KPIConfigurationPresenter;
import com.ericsson.eniq.events.ui.client.kpi.events.StateChangeEvent;
import com.ericsson.eniq.events.ui.client.kpi.events.StateChangeHandler;
import com.ericsson.eniq.events.ui.client.kpi.resources.IButtonResourceBundle;
import com.ericsson.eniq.events.ui.client.mvp.MainEntryPoint;
import com.extjs.gxt.ui.client.core.XDOM;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author eaajssa
 * @since 2012
 */
public class IndicatorButton extends FlowPanel implements IIndicatorButton, StateChangeHandler, ClickHandler,
        WindowClosedEventHandler, MouseOverHandler, MouseOutHandler, TimeParameterValueChangeEventHandler,
        RefreshWindowEventHandler {

    private final Image image;

    private final HTML label;

    private final IButtonResourceBundle buttonResources;

    private final IconType iconType;

    private ImageResource normalIcon;

    private ImageResource clickedIcon;

    private IButtonHandler handler;

    private EventBus eventBus;

    private final KPIPopupPanel popup;

    private String state;

    private String winId;

    public static final String NONE_STATE = "NONE";

    public static final String ACTIVE_STATE = "ACTIVE";

    public static final String ACTIVE_LAUNCHED_STATE = "ACTIVE_LAUNCHED";

    public enum IconType {
        CRITICAL("critical"), MAJOR("major"), MINOR("minor"), WARNING("warning"), UNKNOWN("unknown");
        // TODO handling of unknown
        private final String type;

        private IconType(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
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

    public void addHandler(final IButtonHandler handler) {
        this.handler = handler;
        eventBus.addHandler(StateChangeEvent.TYPE, this);
        eventBus.addHandler(WindowClosedEvent.TYPE, this);
        eventBus.addHandler(RefreshWindowEvent.TYPE, this);
        eventBus.addHandler(TimeParameterValueChangeEvent.TYPE, this);
        addDomHandler(this, ClickEvent.getType());
        addDomHandler(this, MouseOutEvent.getType());
        addDomHandler(this, MouseOverEvent.getType());
    }

    public @UiConstructor
    IndicatorButton(final IButtonResourceBundle buttonResources, final String iconType) {
        this.image = new Image();
        this.label = new HTML();
        this.buttonResources = buttonResources;
        this.iconType = IconType.fromString(iconType);
        this.popup = new KPIPopupPanel();
        this.state = "NONE";

        init();
    }

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    private void init() {
        buttonResources.style().ensureInjected();
        addStyleName(buttonResources.style().container());
        addStyleName(buttonResources.style().buttonHover());
        DOM.sinkEvents(getElement(), Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);

        if (IconType.CRITICAL == iconType) {
            normalIcon = buttonResources.criticalImage();
            clickedIcon = buttonResources.criticalImageClicked();
            popup.setImage(buttonResources.criticalImageClicked());
            getElement().setId("criticalIndicator");

        } else if (IconType.MAJOR == iconType) {
            normalIcon = buttonResources.majorImage();
            clickedIcon = buttonResources.majorImageClicked();
            popup.setImage(buttonResources.majorImageClicked());
            getElement().setId("majorIndicator");

        } else if (IconType.MINOR == iconType) {
            normalIcon = buttonResources.minorImage();
            clickedIcon = buttonResources.minorImageClicked();
            popup.setImage(buttonResources.minorImageClicked());
            getElement().setId("minorIndicator");

        } else if (IconType.WARNING == iconType) {
            normalIcon = buttonResources.warningImage();
            clickedIcon = buttonResources.warningImageClicked();
            popup.setImage(buttonResources.warningImageClicked());
            getElement().setId("warningIndicator");
        }

        image.setResource(normalIcon);
        image.setStyleName(buttonResources.style().image());
        label.setStyleName(buttonResources.style().label());
        setLabel("0.0");

        add(image);
        add(label);

        popup.setStyles(buttonResources);
    }

    public void setPopLabel(final String labelText) {
        popup.setLabel(labelText);
    }

    public void setStyle() {
        reset();

        if (state == ACTIVE_LAUNCHED_STATE) {
            addStyleName(buttonResources.style().buttonActiveLaunched());
            image.setResource(clickedIcon);
        } else if (state == ACTIVE_STATE) {
            addStyleName(buttonResources.style().buttonActive());
            image.setResource(clickedIcon);
        }
    }

    @Override
    public void setLabel(final String labelText) {
        if (labelText != null) {
            label.setHTML(labelText + "<span class='" + buttonResources.style().percentLabel() + "'>%</span>");
        }
    }

    @Override
    public void setState(final String state) {
        this.state = state;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void reset() {
        removeStyleName(buttonResources.style().buttonActive());
        removeStyleName(buttonResources.style().buttonActiveLaunched());
        image.setResource(normalIcon);
    }

    @Override
    public void showPopup() {
        showPopup(getAbsoluteLeft() - 230, getAbsoluteTop());
    }

    private void showPopup(final int x, final int y) {
        popup.getElement().getStyle().setZIndex(XDOM.getTopZIndex() +10);  //due to GXT windowing, z-indexes increasing
        popup.setPopupPosition(x, y);
        popup.show();
    }

    @Override
    public void hidePopup() {
        popup.hide();
    }

    @Override
    public void setBreaches(final int noOfbreaches) {
        popup.setBreaches(noOfbreaches);
    }

    public IButtonResourceBundle getResourceBundle() {
        return buttonResources;
    }

    private static class KPIPopupPanel extends PopupPanel {
        private final FlowPanel panel;

        private final Image image;

        private final Label title;

        private final Label breaches;

        public KPIPopupPanel() {
            super(true);
            setPixelSize(220, 50);
            breaches = new Label();
            setBreaches(0);
            title = new Label();
            image = new Image();
            panel = new FlowPanel();
            panel.add(image);
            panel.add(title);
            panel.add(breaches);
            setWidget(panel);
        }

        public void setStyles(final IButtonResourceBundle resources) {
            setStyleName(resources.style().popup());
            image.setStyleName(resources.style().popupImage());
            title.setStyleName(resources.style().popupTitle());
            breaches.setStyleName(resources.style().breachesLabel());
        }

        void setBreaches(final int noOfbreaches) {
            breaches.setText(noOfbreaches + " Breaches");
        }

        void setImage(final ImageResource resource) {
            image.setResource(resource);
        }

        void setLabel(final String text) {
            title.setText(text);
        }
    }

    public interface IButtonHandler {
        void onClick(IconType iconType, String metaMenuItemID, String launchTime);
    }

    @Override
    public void onStateChange(final String type) {
        if (iconType.toString().equals(type)) {
            setState(ACTIVE_LAUNCHED_STATE);
        } else if (state.equals(ACTIVE_LAUNCHED_STATE)) {
            setState(ACTIVE_STATE);
        }
        setStyle();
    }

    @Override
    public void onClick(final ClickEvent event) {
        final String launchTime = getRefreshTime();
        handler.onClick(iconType, winId, launchTime);
    }

    @Override
    public void onWindowClosed(final WindowClosedEvent event) {
        if (winId.equals(event.getWindow().getBaseWindowID())) {
            reset();
            setState(NONE_STATE);
        }
    }

    @Override
    public void onMouseOut(final MouseOutEvent event) {
        hidePopup();
    }

    @Override
    public void onMouseOver(final MouseOverEvent event) {
        showPopup();
    }

    public void setWindowId(final String winId) {
        this.winId = winId;
    }

    @Override
    public String getWindowId() {
        return winId;
    }

    private String getRefreshTime() {
        final KPIConfigurationPresenter configPresenter = MainEntryPoint.getInjector().getKpiConfigPresenter();
        return configPresenter.getUserConfiguredSettings().get(KPIConfigurationConstants.REFRESH_TIME);
    }

    private void fireStateChangeEvent(final MultipleInstanceWinId multiWinID) {
        if (multiWinID != null && winId != null && winId.equals(multiWinID.getWinId())
                && state != ACTIVE_LAUNCHED_STATE) {

            final StateChangeEvent stateChangeEvent = new StateChangeEvent(iconType.toString());
            eventBus.fireEvent(stateChangeEvent);
        }
    }

    @Override
    public void handleWindowRefresh(final MultipleInstanceWinId multiWinID) {
        fireStateChangeEvent(multiWinID);
    }

    @Override
    public void handleWindowRefresh() {
        // Do nothing. 
    }

    @Override
    public void handleTimeParamUpdate(final MultipleInstanceWinId multiWinId, final TimeInfoDataType time) {
        fireStateChangeEvent(multiWinId);
    }

    @Override
    public void handleTimeParamUpdate(final TimeInfoDataType time) {
        // Do nothing. 
    }
}
