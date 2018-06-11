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

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_PATCH;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_STATE_END;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.SQLStatement.INSERT_STATE_START;

/**
 * A patch associated with a JIRA issue
 */
public class Patch implements HtmlTableRow {

    private String jiraLink;
    private String name;
    private String productName;
    private String assignee;
    private State state;
    private String patchLCState;
    private String patchQueueId;
    private String reportDate;

    Patch(String jiraLink, String name, String productName, String assignee, State state,
          String patchLCState, String patchQueueId, String reportDate) {

        this.jiraLink = jiraLink;
        this.name = name;
        this.productName = productName;
        this.assignee = assignee;
        this.state = state;
        this.patchLCState = patchLCState;
        this.patchQueueId = patchQueueId;
        this.reportDate = reportDate;
    }

    public String getPatchQueueId() {

        return patchQueueId;
    }

    public String getInsertStatementPatchTable() {

        return INSERT_PATCH + this.productName + "','" + this.patchLCState + "','" + this.reportDate + "','" +
                this.name + "','" + this.patchQueueId;
    }

    public String getInsertStatementStateTable() {

        String query = INSERT_STATE_START;
        switch (this.state) {
            case IN_DEV:
                query += "InDevelopment";
                break;
            case IN_PATCH_QUEUE:
                query += "InQueue";
                break;
            case IN_SIGNING:
                query += "InSigning";
                break;
            case RELEASED:
                query += "IsReleased";
                break;
            case INACTIVE:
                query += "Inactive";
                break;
            default:
                break;
        }
        return query + INSERT_STATE_END + this.getPatchQueueId() + "');";
    }

    public Integer getDaysInState() {

        return 0;
    }

    String getName() {

        return name;
    }

    public State getState() {

        return state;
    }

    String getPatchLCState() {

        return this.patchLCState;
    }

    String getJiraLink() {

        return jiraLink;
    }

    String getProductName() {

        return productName;
    }

    String getAssignee() {

        return assignee;
    }

    @Override
    public String objectToHTML(String backgroundColor) {

        return null;
    }

    public String getReportDate() {

        return reportDate;
    }

}
