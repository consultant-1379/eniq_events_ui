/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.ericsson.eniq.events.ui.client.groupmanagement.fileupload;

import com.ericsson.eniq.events.common.client.mvp.BaseView;
import com.ericsson.eniq.events.widgets.client.textbox.ExtendedTextBox;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowEvent;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowGwt;
import com.ericsson.eniq.events.ui.client.common.widget.window.EniqWindowListener;
import com.ericsson.eniq.events.ui.client.groupmanagement.GroupManagementConstants.GroupAction;
import com.ericsson.eniq.events.ui.client.groupmanagement.file.File;
import com.ericsson.eniq.events.ui.client.groupmanagement.file.FileList;
import com.ericsson.eniq.events.ui.client.groupmanagement.file.FileReader;
import com.ericsson.eniq.events.ui.client.groupmanagement.file.ProgressCallback;
import com.ericsson.eniq.events.ui.client.groupmanagement.file.ProgressEvent;
import com.ericsson.eniq.events.ui.client.groupmanagement.resources.GroupMgmtResourceBundle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;

/**
 * @author ecarsea
 * @since 2011
 * 
 */
public class GroupFileUploadView extends BaseView<GroupFileUploadPresenter> {

    private static GroupFileUploadViewUiBinder uiBinder = GWT.create(GroupFileUploadViewUiBinder.class);

    interface GroupFileUploadViewUiBinder extends UiBinder<Widget, GroupFileUploadView> {
    }

    private static final String DEFAULT_HEIGHT = "135px";

    private static final String DEFAULT_WIDTH = "375px";

    private static final String GROUP_IMPORT_WINDOW_ID = "selenium_tag_IMPORT_GROUPS";

    private static final String GROUP_DELETE_WINDOW_ID = "selenium_tag_DELETE_GROUPS";

    @UiField
    FileUpload fileUpload;

    @UiField
    ExtendedTextBox fakeUploadTextBox;

    @UiField
    Button fakeSelectButton;

    @UiField
    Button submitButton;

    @UiField
    Button cancelButton;

    @UiField
    HTMLPanel container;

    private final EniqWindowGwt window;

    private FileList fileList;

    private GroupAction action;

    @SuppressWarnings("unused")
    @UiHandler("fakeSelectButton")
    public void onSelectButtonClick(final ClickEvent event) {
        jsClickUpload(fileUpload.getElement());
    }

    // @formatter:off
    private native void jsClickUpload(Element pElement) /*-{
		pElement.click();
    }-*/;

    // @formatter:on

    @UiHandler("cancelButton")
    public void onCancelButtonClick(final ClickEvent event) {
        window.onClose(event);
    }

    @SuppressWarnings("unused")
    @UiHandler("submitButton")
    public void onSubmitButtonClick(final ClickEvent event) {
        if (fileList != null && fileList.length() > 0) {
            processFiles();
        }
    }

    /**
     * 
     */
    @Inject
    public GroupFileUploadView(final GroupMgmtResourceBundle resourceBundle) {
        resourceBundle.style().ensureInjected();
        initWidget(uiBinder.createAndBindUi(this));
        window = new EniqWindowGwt();
        window.setContent(this.asWidget());
        window.setGlassEnabled(true);
        window.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        bind();
    }

    /**
     * 
     */
    private void bind() {
        window.addWindowListener(new EniqWindowListener() {

            @Override
            public void windowClosed(final EniqWindowEvent eniqWindowEvent) {
                getPresenter().close();
            }
        });
    }

    /* (non-Javadoc)
     * @see com.google.gwt.user.client.ui.Composite#onAttach()
     */
    @Override
    protected void onAttach() {
        super.onAttach();
        getPresenter().bind();
    }

    @UiHandler("fileUpload")
    void onFileUploadChangeEvent(final ChangeEvent e) {
        fileList = FileList.fromEvent(e.getNativeEvent());

        if (fileList != null && fileList.length() > 0) {
            fakeUploadTextBox.setText(toCsvString(fileList));
            fakeUploadTextBox.valueEntered();
        }
    }

    @SuppressWarnings("hiding")
    private String toCsvString(final FileList fileList) {
        final StringBuffer sb = new StringBuffer();

        for (int i = 0; i < fileList.length(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fileList.get(i).getName());
        }
        return sb.toString();
    }

    void processFiles() {
        for (int i = 0; i < fileList.length(); i++) {
            final File file = fileList.get(i);
            if (file == null) {
                break;
            }
            processFile(file);
        }
    }

    void processFile(final File file) {
        final FileReader reader = FileReader.create();
        reader.readAsText(file, new ProgressCallback() {

            @Override
            public void onError(final ProgressEvent e) {
            }

            @Override
            public void onLoad(final ProgressEvent e) {
                final String result = e.getResult();
                getPresenter().upload(file.getName(), result, action);

            }
        });
    }

    public void launch(final GroupAction actionType) {
        this.action = actionType;
        if (action.equals(GroupAction.ADD)) {
            window.getElement().setId(GROUP_IMPORT_WINDOW_ID);
            submitButton.setText("Import Group");
            window.setTitle("Import Groups via XML");
        } else {
            window.getElement().setId(GROUP_DELETE_WINDOW_ID);
            window.setTitle("Delete Groups via XML");
            submitButton.setText("Delete Group");
        }
        RootPanel.get().add(window);
        window.center();
    }
}
