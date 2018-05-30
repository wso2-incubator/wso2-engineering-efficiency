/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.wso2.engineering.efficiency.patch.analysis.pmt;

import org.wso2.engineering.efficiency.patch.analysis.constants.Constants;
import org.wso2.engineering.efficiency.patch.analysis.email.HtmlTableRow;

import java.util.Objects;

/**
 * Represents a Patch currently in Development.
 */
public class DevOpenPatch extends OpenPatch implements HtmlTableRow {

    private String reportDate;

    DevOpenPatch(String url, String name, String productName, String assignee, Constants.State state,
                 String patchLCState, String reportDate, String daysInState) {
        super(url, name, productName, assignee, state, patchLCState, daysInState);
        this.reportDate = reportDate;
    }

    /**
     * Builds the patch data as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    @Override
    public String objectToHTML(String backgroundColor) {

        String tableBody = "<tr><td width=\"" + "20%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getJiraLink();

        //add alert image
        if (Constants.State.IN_PATCH_QUEUE.equals(getState())) {
            tableBody += "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                    " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                    "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\"> " +
                    "<a href=\"https://www.freeiconspng.com/img/1555\" >" +
                    "<img src=\"https://www.freeiconspng.com/uploads/message-alert-red-icon-" +
                    "-message-types-icons--softiconsm-4.png\" width=\"12\" alt=\"\" /></a> " + getName();
        } else {
            tableBody += "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                    " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; " +
                    "font-size: 14px; font-weight: 400; " +
                    "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                    getName();
        }
        tableBody += "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getProductName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor
                + " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400;" +
                " line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getAssignee() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getPatchLCState() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                this.reportDate + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 400; " +
                "line-height: 20px;  padding: 15px 10px 5px 10px;\">" +
                getDaysInState();
        return tableBody;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DevOpenPatch that = (DevOpenPatch) o;
        return Objects.equals(reportDate, that.reportDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reportDate);
    }
}
