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
package org.wso2.engineering.efficiency.patch.analysis.service;

import org.apache.log4j.Logger;
import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisException;
import org.wso2.engineering.efficiency.patch.analysis.impl.SendEmailsServiceImpl;
import org.wso2.engineering.efficiency.patch.analysis.impl.UpdateDatabaseServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Main Service class which contains all the micro service endpoints.
 */
@Path("/patchAnalysis")
public class Service {

    private static final Logger LOGGER = Logger.getLogger(Service.class);

    @GET
    @Path("/{name}")
    public String hello(@PathParam("name") String name) {

        return "Hello " + name;
    }

    @POST
    @Path("/emails")
    public String sendEmails() {

        try {
            LOGGER.info("Executing process to send email on Internal JIRA issues.");
            SendEmailsServiceImpl.getinstance().sendEmail(false);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
            return "Emails on Proactive Patches Not Sent.\n" + e;

        }
        try {
            LOGGER.info("Executing process to send email on Customer related JIRA issues.");
            SendEmailsServiceImpl.getinstance().sendEmail(true);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
            return "Emails on Customer Patches Not Sent.\n" + e;
        }
        return "Emails sent successfully.";
    }

    @POST
    @Path("/database")
    public String updateDatabase() {

        try {
            LOGGER.info("Executing process to update DB with data on Internal JIRA issues.");
            UpdateDatabaseServiceImpl.getInstance().updateDB(false);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
            return "Database not updated with Proactive Patch data\n" + e;
        }
        try {
            LOGGER.info("Executing process to update DB with data on Customer related JIRA issues.");
            UpdateDatabaseServiceImpl.getInstance().updateDB(true);
            LOGGER.info("Execution completed successfully.");
        } catch (PatchAnalysisException e) {
            LOGGER.error("Execution failed, process was not completed", e);
            return "Database not updated with Customer Patch data.\n" + e;
        }
        return "Database updated successfully.";
    }
}
