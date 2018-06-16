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

import org.wso2.engineering.efficiency.patch.analysis.exceptions.ConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisDataException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.util.State;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.JIRA.NOT_SPECIFIED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.DEVELOPMENT;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.FAILED_QA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.IN_QUEUE;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.JIRA_URL_PREFIX;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.JIRA_URL_PREFIX_LENGTH;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.NA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.NO_ENTRY_IN_PMT;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.OFF_QUEUE;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.ON_HOLD;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.PATCH_ID_NOT_GENERATED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.PRE_QA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.READY_FOR_QA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.RELEASED_LC;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.RELEASED_NOT_AUTOMATED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.RELEASED_NOT_IN_PUBLIC_SVN;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.STAGING;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.SUPPORT_JIRA_URL;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.TESTING;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.SELECT_PATCHES_FOR_JIRA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.SELECT_SUPPORT_JIRAS;

/**
 * Accesses the PMT and queries it to get the JIRA issues that have a corresponding entry in the pmt and then
 * gets the patch information for each of the JIRA issues.
 */
public class PMTAccessor {

    private static PMTAccessor pmtAccessor = new PMTAccessor();

    private PMTAccessor() {

    }

    public static PMTAccessor getInstance() {

        return pmtAccessor;
    }

    /**
     * Returns date from date time value.
     *
     * @param dateAndTime date time value.
     * @return Date.
     */
    private static String getDate(String dateAndTime) {

        if (dateAndTime == null || !(dateAndTime.contains(" "))) {
            return NOT_SPECIFIED;
        } else {
            String[] dateSplit = dateAndTime.split(" ");
            return dateSplit[0];
        }
    }

    /**
     * Filters the JIRA issues so the JIRAS with a corresponding entry in the pmt are returned.
     *
     * @param jiraIssues JIRA issues returned by the filter
     * @param dbURL      PMT connection.
     * @param userName   PMT username.
     * @param password   PMT password.
     * @return JIRAS returned by the filter which are in the PMT.
     * @throws PatchAnalysisException Could not access PMT and filter JIRA issues.
     */
    public ArrayList<JIRAIssue> filterJIRAIssues(ArrayList<JIRAIssue> jiraIssues, String dbURL, String userName,
                                                 String password) throws PatchAnalysisException {

        try (Connection con = DriverManager.getConnection(dbURL, userName, password);
             PreparedStatement pst = con.prepareStatement(SELECT_SUPPORT_JIRAS);
             ResultSet result = pst.executeQuery()) {
            ArrayList<String> allJIRANamesInPmt = new ArrayList<>();
            try {
                while (result.next()) {
                    String jiraUrl = result.getString(SUPPORT_JIRA_URL);
                    allJIRANamesInPmt.add(getJiraName(jiraUrl));
                }
            } catch (SQLException e) {
                throw new PatchAnalysisDataException("Failed to extract data from returned pmt ResultSet", e);
            }
            return getJIRAIssuesInPmtAndJIRA(jiraIssues, allJIRANamesInPmt);
        } catch (SQLException e) {
            throw new ConnectionException("Failed to connect to the Pmt database", e);
        }
    }

    /**
     * Extracts JIRA name from JIRA link.
     *
     * @param jiraUrl JIRA link.
     * @return the JIRA name.
     */
    private String getJiraName(String jiraUrl) {

        String jiraName = "";
        if (jiraUrl.length() >= JIRA_URL_PREFIX_LENGTH) {
            jiraName = jiraUrl.substring(JIRA_URL_PREFIX_LENGTH);
        }
        return jiraName;
    }

    /**
     * Returns ArrayList of JIRA issues with a corresponding entry in the PMT.
     *
     * @param jiraIssues        JIRA issues from JIRA.
     * @param allJIRANamesInPmt JIRA issues in PMT.
     * @return JIRA issues in PMT.
     */
    private ArrayList<JIRAIssue> getJIRAIssuesInPmtAndJIRA(ArrayList<JIRAIssue> jiraIssues,
                                                           ArrayList<String> allJIRANamesInPmt) {

        ArrayList<JIRAIssue> jiraIssuesInPmtAndJira = new ArrayList<>();
        for (JIRAIssue jiraIssue : jiraIssues) {
            if (allJIRANamesInPmt.contains(jiraIssue.getName())) {
                jiraIssuesInPmtAndJira.add(jiraIssue);
            } else {
                //no entry in pmt
                String jiraLink = JIRA_URL_PREFIX + jiraIssue.getName();
                jiraIssue.addInactivePatch(new InactivePatch(jiraLink, NO_ENTRY_IN_PMT, NA, jiraIssue.getAssignee(),
                        NA, jiraIssue.getCreateDate(), jiraIssue.getJiraState(), NO_ENTRY_IN_PMT, NO_ENTRY_IN_PMT));
            }
        }
        return jiraIssuesInPmtAndJira;
    }

    /**
     * @param jiraIssuesInPmtAndJira JIRA issues with an entry in the PMT.
     * @param pmtConnection          PMT connection .
     * @param user                   PMT username.
     * @param password               PMT password.
     * @throws PatchAnalysisException Failed to get Patch information.
     */
    public void populatePatches(ArrayList<JIRAIssue> jiraIssuesInPmtAndJira, String pmtConnection, String user,
                                String password) throws PatchAnalysisException {

        try (Connection con = DriverManager.getConnection(pmtConnection, user, password)) {
            for (JIRAIssue jiraIssue : jiraIssuesInPmtAndJira) {
                String query = SELECT_PATCHES_FOR_JIRA + jiraIssue.getName() + "';";
                try (PreparedStatement pst = con.prepareStatement(query); ResultSet result = pst.executeQuery()) {
                    populatePatchesFromResultSet(result, jiraIssue);
                } catch (SQLException e) {
                    throw new PatchAnalysisDataException("Failed to extract data from returned ResultSet for: " +
                            jiraIssue.getName(), e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionException("Failed to connect to the Pmt database", e);
        }
    }

    /**
     * Adds the patches found to its Jira issue after assigning it to a State.
     *
     * @param result    from mysql query.
     * @param jiraIssue the Jira issue.
     * @throws SQLException could not extract data from result set.
     */
    private void populatePatchesFromResultSet(ResultSet result, JIRAIssue jiraIssue) throws SQLException {

        while (result.next()) {
            String curReportDate = result.getString("REPORT_DATE");
            jiraIssue.setReportDate(curReportDate);
            String jiraLink = result.getString("SUPPORT_JIRA");
            String active = result.getString("ACTIVE");
            String productName = result.getString("PRODUCT_NAME");
            String daysSincePatchWasReported = result.getString("DAYS_SINCE_REPORT");
            String assignee = jiraIssue.getAssignee();
            String patchQueueId = result.getString("q.ID");

            if (OFF_QUEUE.equals(active)) {
                //Taken off queue.
                String lcState = result.getString("LC_STATE");
                String patchName = result.getString("PATCH_NAME");
                String signRequestSentOn = result.getString("SIGN_REQUEST_SENT_ON");
                if (!ON_HOLD.equals(lcState)) {
                    if (((STAGING.equals(lcState)) && (signRequestSentOn != null) ||
                            (TESTING.equals(lcState)) && (signRequestSentOn != null))) {
                        //Patch is in signing.
                        String daysInSigning = result.getString("DAYS_IN_SIGNING");
                        jiraIssue.addOpenPatch(new OpenPatch(jiraLink, patchName, productName, assignee,
                                State.IN_SIGNING, "InSigning", daysInSigning, patchQueueId, curReportDate));

                    } else if (STAGING.equals(lcState) || DEVELOPMENT.equals(lcState) || TESTING.equals(lcState) ||
                            PRE_QA.equals(lcState) || FAILED_QA.equals(lcState) ||
                            READY_FOR_QA.equals(lcState)) {
                        //Patch is in development.
                        jiraIssue.addOpenPatch(new DevOpenPatch(jiraLink, patchName, productName, assignee,
                                State.IN_DEV, lcState, daysSincePatchWasReported, patchQueueId, curReportDate));
                    } else if (RELEASED_LC.equals(lcState)) {
                        //Patch has been released.
                        jiraIssue.addOpenPatch(new OpenPatch(jiraLink, patchName, productName, assignee,
                                State.RELEASED, lcState, getDate(result.getString("RELEASED_ON")),
                                patchQueueId, curReportDate));
                    } else if (RELEASED_NOT_AUTOMATED.equals(lcState)) {
                        //Patch has been released.
                        jiraIssue.addOpenPatch(new OpenPatch(jiraLink, patchName, productName, assignee,
                                State.RELEASED, lcState,
                                getDate(result.getString("RELEASED_NOT_AUTOMATED_ON")),
                                patchQueueId, curReportDate));
                    } else if (RELEASED_NOT_IN_PUBLIC_SVN.equals(lcState)) {
                        //Patch has been released.
                        jiraIssue.addOpenPatch(new OpenPatch(jiraLink, patchName, productName, assignee,
                                State.RELEASED, lcState,
                                getDate(result.getString("RELEASED_NOT_IN_PUBLIC_SVN_ON")),
                                patchQueueId, curReportDate));
                    } else {
                        //patch is broken or in regression.
                        jiraIssue.addInactivePatch(new InactivePatch(jiraLink, patchName, productName, assignee,
                                lcState, jiraIssue.getCreateDate(), jiraIssue.getJiraState(), patchQueueId,
                                curReportDate));
                    }
                } else {
                    //patch is on hold.
                    jiraIssue.addInactivePatch(new InactivePatch(jiraLink, patchName, productName, assignee,
                            lcState, jiraIssue.getCreateDate(), jiraIssue.getJiraState(), patchQueueId, curReportDate));
                }
            } else if (IN_QUEUE.equals(active)) {
                jiraIssue.addOpenPatch(new DevOpenPatch(jiraLink, PATCH_ID_NOT_GENERATED, productName,
                        assignee, State.IN_PATCH_QUEUE, "InQueue", daysSincePatchWasReported,
                        patchQueueId, curReportDate));
            } else {
                //Not gone forward with Patch
                jiraIssue.addInactivePatch(new InactivePatch(jiraLink, PATCH_ID_NOT_GENERATED, productName,
                        assignee, "Not Gone Forward with Patch", jiraIssue.getCreateDate(),
                        jiraIssue.getJiraState(), patchQueueId, curReportDate));
            }
        }
    }
}
