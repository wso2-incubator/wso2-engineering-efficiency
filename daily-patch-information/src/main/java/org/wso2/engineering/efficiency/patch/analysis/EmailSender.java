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
package org.wso2.engineering.efficiency.patch.analysis;

import org.apache.log4j.Logger;
import org.wso2.engineering.efficiency.patch.analysis.configuration.Configuration;
import org.wso2.engineering.efficiency.patch.analysis.email.EmailContentCreator;
import org.wso2.engineering.efficiency.patch.analysis.email.GmailAccessor;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAAccessor;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.pmt.PMTAccessor;

import java.io.IOException;
import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_END;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_INTERNAL;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SUBJECT_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SUBJECT_INTERNAL;

/**
 * Sends 2 emails on behalf of the engineering efficiency team. One on Customer related JIRA issues,
 * and the other on Proactive JIRA issues and their corresponding patch information.
 */
public class EmailSender {

    private static final Logger LOGGER = Logger.getLogger(EmailSender.class);

    public static void main(String[] args) {

        EmailSender emailSender = new EmailSender();
        try {
            LOGGER.info("Executing process to send email on Internal JIRA issues.");
            emailSender.executeEmailSendingProcess(false);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
        }
        try {
            LOGGER.info("Executing process to send email on Customer related JIRA issues.");
            emailSender.executeEmailSendingProcess(true);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
        }
    }

    /**
     * The process gets internal JIRA issues or customer related JIRA issues, and sends an email
     * containing information on them and their associated patches.
     *
     * @param isMailOnCustomerReportedIssues boolean to determine if it is the internal or customer related mail
     *                                       being sent.
     * @throws PatchAnalysisException The process execution has halted.
     */
    private void executeEmailSendingProcess(boolean isMailOnCustomerReportedIssues)
            throws PatchAnalysisException {

        Configuration configuration;
        try {
            configuration = Configuration.getInstance();
        } catch (IOException e) {
            throw new PatchAnalysisException("Failed to read configuration file", e);
        }
        //set values dependent on email type.
        String urlToJIRAFilter;
        String emailSubject;
        String emailHeaderHTML;
        if (isMailOnCustomerReportedIssues) {
            urlToJIRAFilter = configuration.getUrlToJIRAFilterCustomer();
            emailSubject = SUBJECT_CUSTOMER;
            emailHeaderHTML = MAIN_HEADER_CUSTOMER;
        } else {
            urlToJIRAFilter = configuration.getUrlToJIRAFilterInternal();
            emailSubject = SUBJECT_INTERNAL;
            emailHeaderHTML = MAIN_HEADER_INTERNAL;
        }

        ArrayList<JIRAIssue> jiraIssues = accessJIRA(urlToJIRAFilter, configuration.getJiraAuthentication());
        accessPMT(configuration.getPmtConnection(), configuration.getDbUser(), configuration.getDbPassword(),
                jiraIssues);
        emailHeaderHTML += jiraIssues.size() + MAIN_HEADER_END;
        String emailBodyHTML = EmailContentCreator.getInstance().getEmailBody(jiraIssues, emailHeaderHTML);
        //send mail
        try {
            GmailAccessor.getInstance().sendMessage(emailBodyHTML, emailSubject, configuration.getEmailUser(),
                    configuration.getToList(), configuration.getCcList());
            LOGGER.info("Successfully sent email with patch information.");
        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to send email.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }

    /**
     * Gets the JIRA issues returned by the JIRA filter.
     *
     * @param urlToJIRAFilter    url to the results from applying the JIRA filter.
     * @param jiraAuthentication Basic authentication.
     * @return JIRA issues.
     * @throws PatchAnalysisException could not extract JIRA issues.
     */
    private ArrayList<JIRAIssue> accessJIRA(String urlToJIRAFilter, String jiraAuthentication)
            throws PatchAnalysisException {

        try {
            ArrayList<JIRAIssue> jiraIssues;
            jiraIssues = new ArrayList<>(JIRAAccessor.getInstance().getIssues(urlToJIRAFilter, jiraAuthentication));
            LOGGER.info("Successfully extracted JIRA issue information from JIRA.");
            return jiraIssues;

        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to extract JIRA issues from JIRA.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }

    /**
     * Assigns patches to the JIRA issues.
     *
     * @param pmtConnection   pmt connection.
     * @param pmtUserName     pmt username.
     * @param pmtUserPassword pmt password.
     * @param jiraIssues      JIRA issues returned by the JIRA filter.
     * @throws PatchAnalysisException Could not extract Patch information from pmt.
     */
    private void accessPMT(String pmtConnection, String pmtUserName, String pmtUserPassword,
                           ArrayList<JIRAIssue> jiraIssues)
            throws PatchAnalysisException {

        try {
            PMTAccessor pmtAccessor = PMTAccessor.getInstance();
            ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = new ArrayList<>(
                    pmtAccessor.filterJIRAIssues(jiraIssues, pmtConnection, pmtUserName, pmtUserPassword));
            pmtAccessor.populatePatches(jiraIssuesInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword);
            LOGGER.info("Successfully extracted patch information from the pmt.");
        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to extract OpenPatch information from the pmt.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }
}
