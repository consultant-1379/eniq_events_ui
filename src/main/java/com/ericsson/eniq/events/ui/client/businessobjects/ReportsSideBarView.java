/**
 * -----------------------------------------------------------------------
 *     Copyright (C) 2011 LM Ericsson Limited.  All rights reserved.
 * -----------------------------------------------------------------------
 */

package com.ericsson.eniq.events.ui.client.businessobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ericsson.eniq.events.common.client.reports.ReportTreeNode;
import com.ericsson.eniq.events.common.client.reports.ReportTreeNode.NodeType;
import com.ericsson.eniq.events.common.client.reports.ReportTreeParser;
import com.ericsson.eniq.events.ui.client.businessobjects.resources.ReportsSideBarResourceBundle;
import com.ericsson.eniq.events.widgets.client.mask.MaskHelper;
import com.ericsson.eniq.events.widgets.client.tree.ETree;
import com.ericsson.eniq.events.widgets.client.tree.resources.TreeResourceBundle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasTreeItems;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * ReportsSideBarView  - Widget for Reports Side Bar
 *
 * @author ecarsea
 * @since October 2011
 */
public class ReportsSideBarView extends Composite implements IReportsSideBarView {

    private static final int SLIDE_LEFT_OFFSET = 5;

    private static final String SLIDE_IN_TITLE = "Click to hide the report panel, or drag to resize it.";

    private static final String SLIDE_OUT_TITLE = "Click here to show report panel.";

    interface ReportsSideBarUiBinder extends UiBinder<Widget, ReportsSideBarView> {
    }

    private static ReportsSideBarUiBinder uiBinder;

    @UiField(provided = true)
    ReportsSideBarResourceBundle resourceBundle;

    @UiField(provided = true)
    TreeResourceBundle treeResourceBundle;

    @UiField
    FlowPanel container;

    @UiField
    Label header;

    @UiField
    Image handle;

    @UiField
    BoResizerPanel resizerPanel;

    @UiField
    ETree reportsTree;

    private boolean isSlideInState;

    private IReportsSideBarUiHandler handler;

    private final Map<TreeItem, ReportInfo> treeItemMap = new HashMap<TreeItem, ReportInfo>();

    private final MaskHelper maskHelper;

    @UiHandler("reportsTree")
    void onItemSelected(final SelectionEvent<TreeItem> event) {
        final ReportInfo reportInfo = treeItemMap.get(event.getSelectedItem());
        final boolean arrowKeyDown = reportsTree.isArrowKeyDown();
        if (reportInfo != null && !arrowKeyDown) {
            handler.onItemSelected(reportInfo.getHeader(), reportInfo.getUrl());
        } else if (arrowKeyDown) {
            reportsTree.setArrowKeyDown(false);
        }
    }

    @SuppressWarnings("unused")
    @UiHandler("handle")
    void onClick(final ClickEvent event) {
        if (isSlideInState) {
            slideOut();
        } else {
            slideIn();
        }
    }

    public ReportsSideBarView(final ReportsSideBarResourceBundle resourceBundle, final MaskHelper maskHelper,
            final TreeResourceBundle treeResourceBundle) {
        resourceBundle.style().ensureInjected();
        this.resourceBundle = resourceBundle;
        this.maskHelper = maskHelper;
        this.treeResourceBundle = treeResourceBundle;
        uiBinder = GWT.create(ReportsSideBarUiBinder.class);
        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void init(final IReportsSideBarUiHandler handler) {
        this.handler = handler;
        reportsTree.addStyleName(resourceBundle.style().reportTree());
        this.resizerPanel.setResizeElement(container.getElement());
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.ISlideable#slideIn()
     */
    @Override
    public void slideIn() {
        container.getElement().getStyle().setLeft(0, Unit.PX);
        handle.setResource(resourceBundle.boHandleClose());
        handle.addStyleName(resourceBundle.style().handle());
        handle.setTitle(SLIDE_IN_TITLE);
        isSlideInState = true;

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.ISlideable#slideOut()
     */
    @Override
    public void slideOut() {
        container.getElement().getStyle().setLeft(-container.getOffsetWidth() + SLIDE_LEFT_OFFSET, Unit.PX);
        handle.setResource(resourceBundle.boHandleOpen());
        handle.addStyleName(resourceBundle.style().handle());
        handle.setTitle(SLIDE_OUT_TITLE);
        isSlideInState = false;

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.ISlideable#isSlideInState()
     */
    @Override
    public boolean isSlideInState() {
        return isSlideInState;
    }

    @Override
    public void clear() {
        treeItemMap.clear();
        reportsTree.removeItems();
    }

    @Override
    public void createTree(final JSONValue dataRoot) {
        /** recursive call to create Branch until tree is created **/
        reportsTree.getElement().setPropertyString("unselectable", "on"); // Disable text selection for IE.
        final List<ReportTreeNode> nodeList = ReportTreeParser.parseData(dataRoot);
        createNode(reportsTree, nodeList);

    }

    private void createNode(final HasTreeItems tree, final List<ReportTreeNode> items) {
        if (items != null) {
            for (final ReportTreeNode node : items) {
                final SafeHtmlBuilder sb = new SafeHtmlBuilder();
                sb.appendEscaped(node.getName());
                final TreeItem treeItem = new TreeItem(sb.toSafeHtml());
                tree.addItem(treeItem);
                if (node.getType().equals(NodeType.FILE)) {
                    treeItemMap.put(treeItem, new ReportInfo(node.getName(), node.getUrl()));
                } else {
                    createNode(treeItem, node.getChildNodes());
                }
            }
        }
    }

    private static class ReportInfo {
        private final String header;

        private final String url;

        /**
         * @param header
         * @param url
         */
        public ReportInfo(final String header, final String url) {
            super();
            this.header = header;
            this.url = url;
        }

        /**
         * @return the header
         */
        protected String getHeader() {
            return header;
        }

        /**
         * @return the url
         */
        protected String getUrl() {
            return url;
        }

    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.IReportsSideBarView#unmask()
     */
    @Override
    public void unmask() {
        maskHelper.unmask();
    }

    /* (non-Javadoc)
     * @see com.ericsson.eniq.events.ui.client.businessobjects.IReportsSideBarView#mask(java.lang.String)
     */
    @Override
    public void mask(final String maskText) {
        maskHelper.mask(getElement(), maskText, this.getOffsetHeight());
    }
}