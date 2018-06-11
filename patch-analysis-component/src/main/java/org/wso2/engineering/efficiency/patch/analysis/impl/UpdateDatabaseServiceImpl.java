package org.wso2.engineering.efficiency.patch.analysis.impl;

import org.apache.log4j.Logger;
import org.wso2.engineering.efficiency.patch.analysis.Configuration;
import org.wso2.engineering.efficiency.patch.analysis.database.DatabaseUpdater;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.ConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAAccessor;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.pmt.PMTAccessor;

import java.util.ArrayList;

/**
 * Updates the DB with the Patch and JIRA data.
 */
public class UpdateDatabaseServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(UpdateDatabaseServiceImpl.class);
    private static UpdateDatabaseServiceImpl updateDatabaseService = new UpdateDatabaseServiceImpl();

    public static UpdateDatabaseServiceImpl getInstance() {

        return updateDatabaseService;
    }

    public ArrayList<JIRAIssue> updateDB(boolean isMailOnCustomerIssues)
            throws PatchAnalysisException {

        Configuration configuration = Configuration.getInstance();
        ArrayList<JIRAIssue> jiraIssues = accessJIRA(isMailOnCustomerIssues, configuration.getUrlToJIRAFilterCustomer(),
                configuration.getUrlToJIRAFilterInternal(), configuration.getJiraAuthentication());
        ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = accessPMT(configuration.getPmtConnection(),
                configuration.getPmtUser(), configuration.getPmtPassword(), jiraIssues);
        try {
            DatabaseUpdater.getInstance().updateDB(jiraIssues, isMailOnCustomerIssues, jiraIssuesInPmtAndJIRA,
                    configuration.getEEOPUser(), configuration.getEEOPPassword(), configuration.getEEOPConnection());
            LOGGER.info("Successfully updated DB.");
        } catch (ConnectionException e) {
            String errorMessage = "Failed to update DB.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
        return jiraIssues;
    }

    /**
     * Gets the JIRA issues returned by the JIRA filter.
     *
     * @param isMailOnCustomerIssues is it customer related.
     * @param jiraAuthentication     Basic authentication.
     * @return JIRA issues.
     * @throws PatchAnalysisException could not extract JIRA issues.
     */
    private ArrayList<JIRAIssue> accessJIRA(boolean isMailOnCustomerIssues, String urlToJIRAFilterCustomer,
                                            String urlToJIRAFilterInternal, String jiraAuthentication)
            throws PatchAnalysisException {

        String urlToJIRAFilter;
        if (isMailOnCustomerIssues) {
            urlToJIRAFilter = urlToJIRAFilterCustomer;
        } else {
            urlToJIRAFilter = urlToJIRAFilterInternal;
        }
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
    private ArrayList<JIRAIssue> accessPMT(String pmtConnection, String pmtUserName, String pmtUserPassword,
                                           ArrayList<JIRAIssue> jiraIssues) throws PatchAnalysisException {

        try {
            PMTAccessor pmtAccessor = PMTAccessor.getInstance();
            ArrayList<JIRAIssue> jiraIssuesInPmtAndJIRA = new ArrayList<>(
                    pmtAccessor.filterJIRAIssues(jiraIssues, pmtConnection, pmtUserName, pmtUserPassword));
            pmtAccessor.populatePatches(jiraIssuesInPmtAndJIRA, pmtConnection, pmtUserName, pmtUserPassword);
            LOGGER.info("Successfully extracted patch information from the pmt.");
            return jiraIssuesInPmtAndJIRA;
        } catch (PatchAnalysisException e) {
            String errorMessage = "Failed to extract OpenPatch information from the pmt.";
            LOGGER.error(errorMessage, e);
            throw new PatchAnalysisException(errorMessage, e);
        }
    }

}
