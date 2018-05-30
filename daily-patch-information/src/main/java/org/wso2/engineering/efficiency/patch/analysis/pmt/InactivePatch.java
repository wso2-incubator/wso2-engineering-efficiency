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
 * A patch which is onhold, broken, in regression, has no entry in the pmt, has an entry in PATCH_QUEUE table but not
 * in PATCH_ETA (Patches with "state" set to [reason for not going forward with the Patch]
 */
public class InactivePatch extends Patch implements HtmlTableRow, Comparable<InactivePatch> {

    private String jiraCreateDate;
    private String jiraState;

    InactivePatch(String jiraLink, String name, String productName, String assignee,
                  String patchLCState, String jiraCreateDate, String jiraState) {
        super(jiraLink, name, productName, assignee, Constants.State.INACTIVE, patchLCState);
        this.jiraCreateDate = jiraCreateDate;
        this.jiraState = jiraState;
    }

    @Override
    public int compareTo(InactivePatch patch1) {
        return this.getJiraCreateDate().compareTo(patch1.getJiraCreateDate());
    }

    private String getJiraCreateDate() {
        return jiraCreateDate;
    }

    /**
     * Builds the patch data as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    @Override
    public String objectToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "15%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px;" +
                " font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getJiraLink() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getProductName() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getAssignee() + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400;  line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiraState + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                getPatchLCState() + "<td width=\"" + "7%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; " +
                "font-weight: 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                jiraCreateDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InactivePatch that = (InactivePatch) o;
        return Objects.equals(jiraCreateDate, that.jiraCreateDate) &&
                Objects.equals(jiraState, that.jiraState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jiraCreateDate, jiraState);
    }
}
