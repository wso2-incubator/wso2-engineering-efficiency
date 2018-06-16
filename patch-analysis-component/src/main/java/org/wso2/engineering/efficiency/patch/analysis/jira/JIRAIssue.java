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

package org.wso2.engineering.efficiency.patch.analysis.jira;

import org.wso2.engineering.efficiency.patch.analysis.email.HtmlTableRow;
import org.wso2.engineering.efficiency.patch.analysis.pmt.InactivePatch;
import org.wso2.engineering.efficiency.patch.analysis.pmt.OpenPatch;
import org.wso2.engineering.efficiency.patch.analysis.pmt.Patch;
import org.wso2.engineering.efficiency.patch.analysis.util.State;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.JIRA.NOT_SPECIFIED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.JIRA_URL_PREFIX;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.PMT.NO_ENTRY_IN_PMT;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_JIRA;

/**
 * Represents a JIRA Issue.
 */
public class JIRAIssue implements HtmlTableRow, Comparable<JIRAIssue> {

    private String name;
    private String link;
    private String assignee;
    private String createDate;
    private String jiraState;
    private ArrayList<OpenPatch> openPatches;
    private ArrayList<OpenPatch> releasedPatches;
    private ArrayList<InactivePatch> inactivePatches;
    private String reportDate; //oldest of all the patches associated with the JIRA
    private String reportDateReleasedPatches; //oldest of all the released patches associated with the JIRA

    JIRAIssue(String jiraName, String assignee, String jiraCreateDate, String jiraState) {

        this.name = jiraName;
        this.link = JIRA_URL_PREFIX + jiraName;
        this.assignee = assignee;
        this.createDate = stripDate(jiraCreateDate);
        this.jiraState = jiraState;
        //patch details are set to empty ArrayLists
        this.openPatches = new ArrayList<>();
        this.releasedPatches = new ArrayList<>();
        this.inactivePatches = new ArrayList<>();
        this.reportDate = NO_ENTRY_IN_PMT;
        this.reportDateReleasedPatches = NO_ENTRY_IN_PMT;
    }

    /**
     * Returns the oldest of two date.
     *
     * @param currentDateStr the date val currently recorded.
     * @param newDateStr     new date.
     * @return the oldest date.
     */
    private static String dateCompare(String currentDateStr, String newDateStr) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date currentDate = sdf.parse(currentDateStr);
            Date newDate = sdf.parse(newDateStr);
            if (currentDate.before(newDate)) {
                return currentDateStr;
            } else {
                return newDateStr;
            }
        } catch (ParseException e) {
            return currentDateStr;
        }
    }

    public ArrayList<Patch> getPatches() {

        ArrayList<Patch> patches = new ArrayList<>(this.inactivePatches);
        patches.addAll(openPatches);
        return patches;

    }

    public String getJiraState() {

        return jiraState;
    }

    public String getLink() {

        return link;
    }

    private String getReportDate() {

        return reportDate;
    }

    public void setReportDate(String currentReportDate) {

        if (NO_ENTRY_IN_PMT.equals(reportDate)) {
            reportDate = currentReportDate;
        } else {
            reportDate = dateCompare(reportDate, currentReportDate);
        }
    }

    String getReportDateReleasedPatches() {

        return reportDateReleasedPatches;
    }

    public String getCreateDate() {

        return createDate;
    }

    public ArrayList<OpenPatch> getReleasedPatches() {

        return releasedPatches;
    }

    public ArrayList<OpenPatch> getOpenPatchesInJIRA() {

        return this.openPatches;
    }

    public ArrayList<InactivePatch> getInactivePatches() {

        return this.inactivePatches;
    }

    public String getName() {

        return name;
    }

    public String getAssignee() {

        return this.assignee;
    }

    public void addInactivePatch(InactivePatch patch) {

        this.inactivePatches.add(patch);
    }

    public void addOpenPatch(OpenPatch patch) {

        this.openPatches.add(patch);
        if (patch.getState().equals(State.RELEASED)) {
            addReleasedPatches(patch, patch.getReportDate());
        }
    }

    private void addReleasedPatches(OpenPatch patch, String currentReportDate) {

        this.releasedPatches.add(patch);
        if (NO_ENTRY_IN_PMT.equals(reportDateReleasedPatches)) {
            reportDateReleasedPatches = currentReportDate;
        } else {
            reportDateReleasedPatches = dateCompare(reportDateReleasedPatches, currentReportDate);
        }
    }

    /**
     * Builds the JIRA data as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    public String objectToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                link + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assignee + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                openPatches.size() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                reportDate;
    }

    /**
     * Builds the JIRA data on dev patches as a HTML table row.
     *
     * @param backgroundColor of table row.
     * @return Returns the HTML code for a table row.
     */
    public String devPatchesToHTML(String backgroundColor) {

        return "<tr><td width=\"" + "30%" + "\" align=\"left\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                link + "<td width=\"" + "20%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                assignee + "<td width=\"" + "15%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                releasedPatches.size() + "<td width=\"" + "10%" + "\" align=\"center\" bgcolor=" + backgroundColor +
                " style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight:" +
                " 400; line-height: 20px; padding: 15px 10px 5px 10px;\">" +
                reportDateReleasedPatches;
    }

    /**
     * Returns the date from a "date time" value.
     *
     * @param dateTime date time value returned by JIRA.
     * @return the date.
     */
    private String stripDate(String dateTime) {

        if (dateTime == null || !(dateTime.contains("T"))) {
            return NOT_SPECIFIED;
        } else {
            String[] dateSplit = dateTime.split("T");
            return dateSplit[0];
        }
    }

    @Override
    public int compareTo(JIRAIssue jiraIssue1) {

        return this.getReportDate().compareTo(jiraIssue1.getReportDate());
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        JIRAIssue jiraIssue = (JIRAIssue) o;
        return Objects.equals(name, jiraIssue.name) &&
                Objects.equals(link, jiraIssue.link) &&
                Objects.equals(assignee, jiraIssue.assignee) &&
                Objects.equals(createDate, jiraIssue.createDate) &&
                Objects.equals(jiraState, jiraIssue.jiraState) &&
                Objects.equals(openPatches, jiraIssue.openPatches) &&
                Objects.equals(releasedPatches, jiraIssue.releasedPatches) &&
                Objects.equals(inactivePatches, jiraIssue.inactivePatches) &&
                Objects.equals(reportDate, jiraIssue.reportDate) &&
                Objects.equals(reportDateReleasedPatches, jiraIssue.reportDateReleasedPatches);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name, link, assignee, createDate,
                jiraState, openPatches, releasedPatches, inactivePatches, reportDate, reportDateReleasedPatches);
    }

    public String getInsertQuery() {

        return INSERT_JIRA + this.assignee + "','" + this.link + "','" + this.createDate + "','";
    }
}
