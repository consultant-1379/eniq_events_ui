package com.ericsson.eniq.events.ui.client.dashboard.portlet;

import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundle;
import com.ericsson.eniq.events.widgets.client.window.title.TitleWindowResourceBundleHelper;
import com.google.web.bindery.event.shared.EventBus;

import com.ericsson.eniq.events.ui.client.common.Maskable;
import com.ericsson.eniq.events.ui.client.datatype.dashboard.PortletDataType;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapMaximizeEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.MapRestoreEvent;
import com.ericsson.eniq.events.ui.client.events.dashboard.PortletRemoveEvent;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Separate portlet window for map.
 * Map is not a draggable widget and has to adjust to available height
 * when all other portlets are in place.
 *
 * @author evyagrz
 * @since 11 2011
 */
public class MapPortletWindow extends Composite implements HasPortletId, Maskable, ClickHandler {

    interface MapPortletWindowUiBinder extends UiBinder<Widget, MapPortletWindow> {
    }

    private static MapPortletWindowUiBinder uiBinder = GWT.create(MapPortletWindowUiBinder.class);

    private enum MapState {
        MAP_SCALE_TO_CONTENT, MAP_MAXIMIZED
    }

    @UiField(provided = true)
    TitleWindowResourceBundle resourceBundle = TitleWindowResourceBundleHelper.getBundle();

    @UiField
    HorizontalPanel topContainer;

    @UiField
    SimplePanel bodyContainer;

    @UiField
    SimplePanel closeButtonContainer;

    @UiField
    SimplePanel maximizeRestoreButtonContainer;

    @UiField
    SimplePanel optionButton;

    @UiField
    Label titleLabel;

    @UiField
    FocusPanel dragHandle;

    private MaskHelper maskHelper;

    private final PortletDataType portletData;

    private final EventBus eventBus;

    private final String portletId;

    private static MapState mapState;

    private Image maximizeRestoreImageButton;

    // This is needed for masking
    private int mapHeightLeft;

    public MapPortletWindow(final PortletDataType portletData, final EventBus eventBus) {

        this.portletData = portletData;
        this.eventBus = eventBus;
        this.portletId = portletData.getPortletId();

        initWidget(uiBinder.createAndBindUi(this));

        configureMap();
    }

    @Override
    public void onClick(final ClickEvent event) {
        // empty, option menu for map should be implemented here
    }

    @Override
    public String getPortletId() {
        return portletId;
    }

    public void setBody(final Widget body) {
        bodyContainer.setWidget(body);
    }

    @Override
    public void mask() {
        if (maskHelper == null) {
            maskHelper = new MaskHelper();
        }

        // This is required for getting correct height for mask
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                maskHelper.mask(getElement(), "Loading Map ...", mapHeightLeft + 27); // needs to be calculated
            }
        });
    }

    @Override
    public void unmask() {
        if (maskHelper != null) {
            maskHelper.unmask();
        }
    }

    public void calculateMapHeight(final int portletAreaAbsoluteTop, final int portletAreaHeight) {
        final int clientWindowHeight = Window.getClientHeight(); // Height of client view (browser)
        final int usedAreaHeight = portletAreaAbsoluteTop + portletAreaHeight;
        mapHeightLeft = clientWindowHeight - usedAreaHeight - 50;

        if (mapHeightLeft < 0) {
            mapHeightLeft = 0;
        }

        bodyContainer.setHeight(mapHeightLeft + "px");
    }

    private void configureMap() {
        // Map initial state is scale to content
        mapState = MapState.MAP_SCALE_TO_CONTENT;
        titleLabel.setText(portletData.getPortletTitle());
        attachOptionButton();
        createTitleButtons(portletData.getPortletId());

        bodyContainer.setHeight("1000px"); //This is needed for map to be resized
    }

    private void createTitleButtons(final String portletId) {
        final Image maximizeRestore = createMaximizeRestoreButton();
        final Image closeButton = createCloseButton(portletId);

        maximizeRestoreButtonContainer.setWidget(maximizeRestore);
        closeButtonContainer.setWidget(closeButton);
    }

    private Image createMaximizeRestoreButton() {
        maximizeRestoreImageButton = new Image(resourceBundle.maximiseButton());
        maximizeRestoreImageButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                if (MapState.MAP_SCALE_TO_CONTENT.equals(mapState)) {
                    // maximize map
                    AbstractImagePrototype.create(resourceBundle.restoreButton()).applyTo(maximizeRestoreImageButton);
                    mapState = MapState.MAP_MAXIMIZED;
                    eventBus.fireEvent(new MapMaximizeEvent());
                } else {
                    // restore map
                    AbstractImagePrototype.create(resourceBundle.maximiseButton()).applyTo(maximizeRestoreImageButton);
                    mapState = MapState.MAP_SCALE_TO_CONTENT;
                    eventBus.fireEvent(new MapRestoreEvent());
                }
            }
        });
        return maximizeRestoreImageButton;
    }

    private Image createCloseButton(final String portletId) {
        final Image closeButton = new Image(resourceBundle.closeButton());
        closeButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(final ClickEvent event) {
                MapPortletWindow.this.eventBus.fireEvent(new PortletRemoveEvent(portletId));
            }
        });
        return closeButton;
    }

    private void attachOptionButton() {
        final ImageResource resource = resourceBundle.arrowForActionMenu();
        final int width = resource.getWidth() + 5;

        final Image arrowImage = new Image(resource);
        arrowImage.getElement().getStyle().setPropertyPx("marginTop", 5);
        arrowImage.getElement().getStyle().setPropertyPx("marginLeft", 5);
        arrowImage.getElement().getStyle().setProperty("cursor", "pointer");

        optionButton.setWidget(arrowImage);
        topContainer.setCellWidth(optionButton, width + "px");
        arrowImage.addClickHandler(this);
    }
}