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

import org.wso2.engineering.efficiency.patch.analysis.email.HtmlTableRow;
import org.wso2.engineering.efficiency.patch.analysis.util.State;

import java.util.Objects;

/**
 * Represents a PMT Open Patch - Patches that are not on hold, in regression or broken.
 */
public class OpenPatch extends Patch implements HtmlTableRow, Comparable<OpenPatch> {

    private String daysInState;

    OpenPatch(String jiraLink, String name, String productName, String assignee, State state,
              String patchLCState, String daysInState) {

        super(jiraLink, name, productName, assignee, state, patchLCState);
        this.daysInState = daysInState;
    }

    @Override
    public Integer getDaysInState() {

        return Integer.parseInt(daysInState);
    }

    /**
     * Builds the patch data as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    public String objectToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px;" +
                " font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getJiraLink() + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getName() + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getProductName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getAssignee() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getPatchLCState() + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                daysInState;
    }

    @Override
    public int compareTo(OpenPatch patch1) {

        return patch1.getDaysInState().compareTo(this.getDaysInState());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenPatch openPatch = (OpenPatch) o;
        return Objects.equals(daysInState, openPatch.daysInState);
    }

    @Override
    public int hashCode() {

        return Objects.hash(daysInState);
    }
}
