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

package org.wso2.engineering.efficiency.patch.analysis.email;

import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.jira.ReportDateComparator;
import org.wso2.engineering.efficiency.patch.analysis.pmt.InactivePatch;
import org.wso2.engineering.efficiency.patch.analysis.pmt.OpenPatch;
import org.wso2.engineering.efficiency.patch.analysis.pmt.Patch;
import org.wso2.engineering.efficiency.patch.analysis.util.Constants;

import java.util.ArrayList;
import java.util.Collections;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.COLUMN_NAMES;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.COLUMN_NAMES_DEV;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.COLUMN_NAMES_INACTIVE;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.COLUMN_NAMES_RELEASED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.COLUMN_NAMES_SUMMARY;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.EMAIL_FOOTER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SECTION_HEADER_DEV;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SECTION_HEADER_INACTIVE;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SECTION_HEADER_RELEASED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SECTION_HEADER_SIGNING;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SECTION_HEADER_SUMMARY;

/**
 * Creates and returns the body of the email.
 */
public class EmailContentCreator {

    private static EmailContentCreator emailContentCreator = new EmailContentCreator();

    private EmailContentCreator() {

    }

    public static EmailContentCreator getInstance() {

        return emailContentCreator;
    }

    /**
     * Returns the body of the email.
     *
     * @param jiraIssues  all JIRA issues.
     * @param emailHeader email header dependent on if its the internal or customer related email.
     * @return the email body.
     */
    public String getEmailBody(ArrayList<JIRAIssue> jiraIssues, String emailHeader) {

        String emailBody = emailHeader;
        ArrayList<OpenPatch> openPatches = new ArrayList<>(getAllOpenPatches(jiraIssues));
        ArrayList<OpenPatch> patchesInDev = new ArrayList<>();
        ArrayList<OpenPatch> patchesInSigning = new ArrayList<>();
        assignPatchesToStates(openPatches, patchesInSigning, patchesInDev);

        emailBody += getStateTable(SECTION_HEADER_DEV, COLUMN_NAMES_DEV, "Work Days Since Report Date",
                new ArrayList<>(patchesInDev));
        ArrayList<Patch> inactivePatches = new ArrayList<>(getAllInactivePatches(jiraIssues));
        emailBody += getStateTable(SECTION_HEADER_INACTIVE, COLUMN_NAMES_INACTIVE, "JIRA Create Date",
                inactivePatches);
        emailBody += getStateTable(SECTION_HEADER_SIGNING, COLUMN_NAMES, "Work Days In Signing",
                new ArrayList<>(patchesInSigning));
        emailBody += getReleasedTable(jiraIssues);
        emailBody += getSummaryTable(jiraIssues);
        emailBody += EMAIL_FOOTER;
        return emailBody;
    }

    /**
     * Builds the html string for the Summary table.
     *
     * @param jiraIssues ArrayList of all JIRA issues
     * @return the html code for the table
     */
    private String getSummaryTable(ArrayList<JIRAIssue> jiraIssues) {

        String table = SECTION_HEADER_SUMMARY;
        table += COLUMN_NAMES_SUMMARY;
        Collections.sort(jiraIssues);
        ArrayList<HtmlTableRow> jirasToHtml = new ArrayList<>(jiraIssues);
        table += getTableRows(jirasToHtml);
        table += "</table>";
        return table;
    }

    /**
     * Returns an ArrayList of all inactive patches.
     *
     * @param jiraIssues ArrayList of JIRA issues
     * @return all patches
     */
    private ArrayList<InactivePatch> getAllInactivePatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<InactivePatch> inactivePatches = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (jiraIssue.getOpenPatchesInJIRA().isEmpty()) {
                inactivePatches.addAll(jiraIssue.getInactivePatches());
            }
        }
        Collections.sort(inactivePatches);
        return inactivePatches;
    }

    /**
     * Returns an ArrayList of all open patches.
     *
     * @param jiraIssues ArrayList of JIRA issues
     * @return all open patches
     */
    private ArrayList<OpenPatch> getAllOpenPatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<OpenPatch> openPatches = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            openPatches.addAll(jiraIssue.getOpenPatchesInJIRA());
        }
        return openPatches;
    }

    /**
     * Assigns patches to States.
     *
     * @param openPatches ArrayList of all patches to be recorded.
     */
    private void assignPatchesToStates(ArrayList<OpenPatch> openPatches, ArrayList<OpenPatch> patchesInSigning,
                                       ArrayList<OpenPatch> patchesInDevelopment) {

        for (OpenPatch openPatch : openPatches) {
            switch (openPatch.getState()) {
                case IN_DEV:
                    patchesInDevelopment.add(openPatch);
                    break;
                case IN_PATCH_QUEUE:
                    patchesInDevelopment.add(openPatch);
                    break;
                case IN_SIGNING:
                    patchesInSigning.add(openPatch);
                    break;
                default:
                    break;
            }
        }
        Collections.sort(patchesInDevelopment);
        Collections.sort(patchesInSigning);
    }

    /**
     * Builds the html table corresponding to a states information.
     *
     * @param header         email header.
     * @param dateColumnName table attribute name for "date" column on table.
     * @param patches        ArrayList of patches.
     * @return html code showing the state data in a table.
     */
    private String getStateTable(String header, String columnNames, String dateColumnName, ArrayList<Patch> patches) {

        String table = header + columnNames + dateColumnName + "</td></tr>";
        ArrayList<HtmlTableRow> patchesToHtml = new ArrayList<>(patches);
        table += getTableRows(patchesToHtml);
        table += "</table>";
        return table;
    }

    /**
     * Builds the table body.
     *
     * @param rows ArrayList of all issues or patches.
     * @return html code for row values of table.
     */
    private String getTableRows(ArrayList<HtmlTableRow> rows) {

        StringBuilder htmlRows = new StringBuilder();
        boolean toggleFlag = true;
        String backgroundColor;
        //set background colour
        for (HtmlTableRow row : rows) {
            if (toggleFlag) {
                backgroundColor = Constants.WHITE_BACKGROUND;
                toggleFlag = false;
            } else {
                backgroundColor = Constants.GRAY_BACKGROUND;
                toggleFlag = true;
            }
            htmlRows.append(row.objectToHTML(backgroundColor));
        }
        return htmlRows.toString();
    }

    /**
     * Builds the html table for the released state.
     *
     * @param jiraIssues all JIRA issues.
     * @return html code showing the state data in a table.
     */
    private String getReleasedTable(ArrayList<JIRAIssue> jiraIssues) {

        String table = SECTION_HEADER_RELEASED;
        table += COLUMN_NAMES_RELEASED;
        ArrayList<JIRAIssue> releasedJiras = getJirasWithReleasedPatches(jiraIssues);
        releasedJiras.sort(new ReportDateComparator());
        table += getReleasedTableRows(releasedJiras);
        table += "</table>";
        return table;
    }

    /**
     * Returns JIRAs that have released Patches associated with it.
     *
     * @param jiraIssues all JIRA issues.
     * @return JIRAs with released Patches.
     */
    private ArrayList<JIRAIssue> getJirasWithReleasedPatches(ArrayList<JIRAIssue> jiraIssues) {

        ArrayList<JIRAIssue> releasedJiras = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            ArrayList<OpenPatch> releasedPatches = new ArrayList<>(jiraIssue.getReleasedPatches());
            if (!releasedPatches.isEmpty()) {
                releasedJiras.add(jiraIssue);
            }
        }
        return releasedJiras;
    }

    /**
     * Builds the Released State table body.
     *
     * @param jiraIssues ArrayList of Jira issues.
     * @return html code for row values of table.
     */
    private String getReleasedTableRows(ArrayList<JIRAIssue> jiraIssues) {

        StringBuilder htmlRows = new StringBuilder();
        boolean toggleFlag = true;
        String backgroundColor;
        //set background colour
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (toggleFlag) {
                backgroundColor = Constants.WHITE_BACKGROUND;
                toggleFlag = false;
            } else {
                backgroundColor = Constants.GRAY_BACKGROUND;
                toggleFlag = true;
            }
            htmlRows.append(jiraIssue.devPatchesToHTML(backgroundColor));
        }
        return htmlRows.toString();
    }
}
