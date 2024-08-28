package com.ericsson.eniq.events.ui.client.groupmanagement.file;

public interface ProgressCallback {

    /**
     * When the read has failed (see errors).
     */
    void onError(ProgressEvent e);

    /**
     * When the read has successfully completed.
     */
    void onLoad(ProgressEvent e);
}
