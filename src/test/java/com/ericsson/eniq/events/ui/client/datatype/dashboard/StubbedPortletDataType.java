package com.ericsson.eniq.events.ui.client.datatype.dashboard;

import com.ericsson.eniq.events.common.client.datatype.ParametersDataType;
import com.ericsson.eniq.events.ui.client.datatype.SearchFieldUser;

/**
 * Test utility for PortletDataType
 * @author eeicmsy
 * @since 2011
 *
 */
public class StubbedPortletDataType extends PortletDataType {

    public StubbedPortletDataType(final String tabOwnerId, final String portalId, final String portalName,
            final String height, final String wsURL, final SearchFieldUser isSearchFieldUser, final String displayType,
            final String commaSeperatedDateFrom, final ParametersDataType parameters, final PortletType type,
            final String commaSeperatedExcludedSearchTypes) {
        super(tabOwnerId, portalId, portalName, height, wsURL, isSearchFieldUser, displayType, commaSeperatedDateFrom,
                parameters, type, commaSeperatedExcludedSearchTypes);

    }

    public StubbedPortletDataType(final Builder builder) {
        this(builder.tabOwnerId, builder.portalId, builder.portalName, builder.height, builder.wsURL,
                builder.isSearchFieldUser, builder.displayType, builder.commaSeperatedDateFrom, builder.parameters,
                builder.type, builder.commaSeperatedExcludedSearchTypes);
    }

    public static class Builder {
        private String tabOwnerId = "tabId";

        private String portalId = "porletId";

        private String portalName = "testPorlet";

        private String height = "1";

        private String wsURL = "DASHBOARD/ROAMER";

        private SearchFieldUser isSearchFieldUser = SearchFieldUser.FALSE;

        private String displayType = "bar";

        private String commaSeperatedDateFrom = "*,1440,CELL,10080,BSC,2880";

        private ParametersDataType parameters = new ParametersDataType();

        private PortletType type = PortletType.CHART;

        private String commaSeperatedExcludedSearchTypes = "";

        public StubbedPortletDataType build() {
            return new StubbedPortletDataType(this);
        }

        public Builder tabOwnerId(final String tabOwnerId) {
            this.tabOwnerId = tabOwnerId;
            return this;
        }

        public Builder portalId(final String porletId) {
            this.portalId = porletId;
            return this;
        }

        public Builder portalName(final String portalName) {
            this.portalName = portalName;
            return this;
        }

        public Builder height(final String height) {
            this.height = height;
            return this;
        }

        public Builder wsURL(final String wsURL) {
            this.wsURL = wsURL;
            return this;
        }

        public Builder isSearchFieldUser(final SearchFieldUser isSearchFieldUser) {
            this.isSearchFieldUser = isSearchFieldUser;
            return this;
        }

        public Builder displayType(final String displayType) {
            this.displayType = displayType;
            return this;
        }

        public Builder commaSeperatedDateFrom(final String commaSeperatedDateFrom) {
            this.commaSeperatedDateFrom = commaSeperatedDateFrom;
            return this;
        }

        public Builder parameters(final ParametersDataType parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder type(final PortletType type) {
            this.type = type;
            return this;
        }

        public Builder commaSeperatedExcludedSearchTypes(final String commaSeperatedExcludedSearchTypes) {
            this.commaSeperatedExcludedSearchTypes = commaSeperatedExcludedSearchTypes;
            return this;
        }

    }

}
