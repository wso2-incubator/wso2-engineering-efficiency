package org.wso2.engineering.efficiency.patch.analysis.impl;

import org.apache.log4j.Logger;
import org.wso2.engineering.efficiency.patch.analysis.Configuration;
import org.wso2.engineering.efficiency.patch.analysis.email.EmailContentCreator;
import org.wso2.engineering.efficiency.patch.analysis.email.GmailAccessor;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;

import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_END;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.MAIN_HEADER_INTERNAL;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SUBJECT_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Email.SUBJECT_INTERNAL;

/**
 * Sends 2 emails. One on Customer related JIRA issues, and the other on Proactive JIRA issues
 */
public class SendEmailsServiceImpl {

    private static final Logger LOGGER = Logger.getLogger(SendEmailsServiceImpl.class);
    private static SendEmailsServiceImpl sendEmailsService = new SendEmailsServiceImpl();

    public static SendEmailsServiceImpl getinstance() {

        return sendEmailsService;
    }

    public void sendEmail(boolean isMailOnCustomerIssues)
            throws PatchAnalysisException {

        Configuration configuration = Configuration.getInstance();
        ArrayList<JIRAIssue> jiraIssues = UpdateDatabaseServiceImpl.getInstance().updateDB(isMailOnCustomerIssues);
        String emailSubject;
        String emailHeaderHTML;
        if (isMailOnCustomerIssues) {
            emailSubject = SUBJECT_CUSTOMER;
            emailHeaderHTML = MAIN_HEADER_CUSTOMER;
        } else {
            emailSubject = SUBJECT_INTERNAL;
            emailHeaderHTML = MAIN_HEADER_INTERNAL;
        }
        emailHeaderHTML += jiraIssues.size() + MAIN_HEADER_END;
        String emailBodyHTML = EmailContentCreator.getInstance().getEmailBody(jiraIssues, emailHeaderHTML);
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

}
