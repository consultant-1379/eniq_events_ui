package com.ericsson.eniq.events.ui.client.kpi.widget;

public interface IIndicatorButton {

    void setLabel(String labelText);

    void hidePopup();

    void showPopup();

    void setBreaches(int breachesCount);

    void reset();

    /**
     * @param state
     */
    void setState(String state);

    /**
     * @return
     */
    String getState();

    String getWindowId();
}
