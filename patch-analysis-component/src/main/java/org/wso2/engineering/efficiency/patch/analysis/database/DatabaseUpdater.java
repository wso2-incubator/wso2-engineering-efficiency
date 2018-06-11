package org.wso2.engineering.efficiency.patch.analysis.database;

import org.wso2.engineering.efficiency.patch.analysis.exceptions.ConnectionException;
import org.wso2.engineering.efficiency.patch.analysis.jira.JIRAIssue;
import org.wso2.engineering.efficiency.patch.analysis.pmt.Patch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.CUSTOMER_JIRA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_JIRA_PATCH_END;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_JIRA_PATCH_MID;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_JIRA_PATCH_START;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.ON_PATCH_DUPLICATION;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.PROACTIVE_JIRA;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.SELECT_LAST_STATE_END;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.SELECT_LAST_STATE_START;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.SET_CUSTOMER_JIRAS_AS_RESOLVED;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.
        SET_PROACTIVE_JIRAS_AS_RESOLVED;

/**
 * Writes JIRA and Patch data to a database.
 */
public class DatabaseUpdater {

    private static DatabaseUpdater databaseAccessor = new DatabaseUpdater();

    private DatabaseUpdater() {

    }

    public static DatabaseUpdater getInstance() {

        return databaseAccessor;
    }

    /**
     * Connects to database and updates tables.
     *
     * @param jiraIssues                     JIRA issues returned by the JIRA filter.
     * @param isMailOnCustomerReportedIssues is it customer related.
     * @param jiraIssuesInPMT                JIRA issues with a corresponding entry in the PMT.
     * @param dbUser                         database username.
     * @param dbPassword                     database password.
     * @param dbConnection                   database connection.
     */
    public void updateDB(ArrayList<JIRAIssue> jiraIssues, boolean isMailOnCustomerReportedIssues,
                         ArrayList<JIRAIssue> jiraIssuesInPMT, String dbUser, String dbPassword,
                         String dbConnection) throws ConnectionException {

        try (Connection con = DriverManager.getConnection(dbConnection, dbUser, dbPassword);
             Statement statement = con.createStatement()) {

            updateJIRATable(jiraIssues, isMailOnCustomerReportedIssues, statement);
            updatePATCHTable(jiraIssuesInPMT, statement);
            updateSTATETable(jiraIssuesInPMT, statement);

        } catch (SQLException e) {
            throw new ConnectionException("Database was not updated successfully", e);
        }
    }

    /**
     * Updates the JIRA table.
     *
     * @param jiraIssues                     JIRA issues returned by filter.
     * @param isMailOnCustomerReportedIssues mail is customer related or not.
     * @param statement                      JDBC Statement.
     */
    private void updateJIRATable(ArrayList<JIRAIssue> jiraIssues, boolean isMailOnCustomerReportedIssues,
                                 Statement statement) throws SQLException {

        String jiraType;
        if (isMailOnCustomerReportedIssues) {
            statement.executeUpdate(SET_CUSTOMER_JIRAS_AS_RESOLVED);
            jiraType = CUSTOMER_JIRA;
        } else {
            statement.executeUpdate(SET_PROACTIVE_JIRAS_AS_RESOLVED);
            jiraType = PROACTIVE_JIRA;
        }
        for (JIRAIssue jiraIssue : jiraIssues) {
            statement.addBatch(jiraIssue.getInsertQuery() + jiraType);
        }
        statement.executeBatch();
    }

    /**
     * Updates the Patch table and the JIRA_PATCH table.
     *
     * @param jiraIssuesInPMT JIRA issues that have a corresponding entry in the PMT.
     * @param statement       JDBC Statement.
     */
    private void updatePATCHTable(ArrayList<JIRAIssue> jiraIssuesInPMT, Statement statement) throws SQLException {

        for (JIRAIssue jiraIssue : jiraIssuesInPMT) {
            for (Patch patch : jiraIssue.getPatches()) {
                statement.addBatch(patch.getInsertStatementPatchTable() + ON_PATCH_DUPLICATION);
                statement.addBatch(INSERT_JIRA_PATCH_START + patch.getPatchQueueId() + INSERT_JIRA_PATCH_MID +
                        jiraIssue.getLink() + INSERT_JIRA_PATCH_END);
            }
        }
        statement.executeBatch();
    }

    /**
     * Update the State table.
     *
     * @param jiraIssuesInPMT JIRA issues that have a corresponding entry in the PMT.
     * @param statement       JDBC Statement.
     */
    private void updateSTATETable(ArrayList<JIRAIssue> jiraIssuesInPMT, Statement statement) throws SQLException {

        for (JIRAIssue jiraIssue : jiraIssuesInPMT) {
            for (Patch patch : jiraIssue.getPatches()) {
                ResultSet resultSet = statement.executeQuery(SELECT_LAST_STATE_START + patch.getPatchQueueId()
                        + SELECT_LAST_STATE_END);
                if (resultSet.next()) {
                    String lastState = resultSet.getString("STATE_NAME");
                    if (checkStateChange(patch, lastState)) {
                        statement.executeUpdate(patch.getInsertStatementStateTable());
                    }
                } else {
                    statement.executeUpdate(patch.getInsertStatementStateTable());
                }
            }
        }
    }

    /**
     * For a given patch, if the state has changed since the last entry in the PMT.
     *
     * @param patch     Patch
     * @param lastState the last state it was in
     * @return if the state has change from the last record in the DB
     */
    private boolean checkStateChange(Patch patch, String lastState) {

        boolean stateChange = true;
        switch (patch.getState()) {
            case IN_DEV:
                if ("InDevelopment".equals(lastState)) {
                    stateChange = false;
                }
                break;
            case IN_PATCH_QUEUE:
                if ("InQueue".equals(lastState)) {
                    stateChange = false;
                }
                break;
            case IN_SIGNING:
                if ("InSigning".equals(lastState)) {
                    stateChange = false;
                }
                break;
            case RELEASED:
                if ("IsReleased".equals(lastState)) {
                    stateChange = false;
                }
                break;
            case INACTIVE:
                if ("Inactive".equals(lastState)) {
                    stateChange = false;
                }
                break;
            default:
                break;
        }
        return stateChange;
    }
}
