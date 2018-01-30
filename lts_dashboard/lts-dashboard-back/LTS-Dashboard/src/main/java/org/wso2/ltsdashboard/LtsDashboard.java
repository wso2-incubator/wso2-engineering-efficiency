/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.ltsdashboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 1.0-SNAPSHOT
 */

@Path("/lts")
public class LtsDashboard {
    private final static Logger logger = Logger.getLogger(LtsDashboard.class);
    private final static String CONFIG_FILE = "config.ini";
    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;
    private static String gitToken;

    LtsDashboard() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
        loadConfigs(inputStream);
    }

    /**
     * Load configs from the file
     *
     * @param input - input stream of the file
     */
    private static void loadConfigs(InputStream input) {
        Properties prop = new Properties();
        try {
            prop.load(input);
            gitToken = prop.getProperty("git_token");
            databaseUrl = prop.getProperty("db_url");
            databaseUser = prop.getProperty("db_user");
            databasePassword = prop.getProperty("db_password");


        } catch (FileNotFoundException e) {
            logger.error("The configuration file is not found");
        } catch (IOException e) {
            logger.error("The File cannot be read");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error("The File InputStream is not closed");
                }
            }
        }


    }


    @GET
    @Path("/products")
    public Response getProducts() {
        logger.debug("Request to products");
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        JsonArray productList = processorImplement.getProductList();

        return makeResponseWithBody(productList);
    }


    @POST
    @Path("/versions")
    public Response getLabels(JsonObject product) {
        logger.debug("Request to versions");
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        String productName = product.get("product").toString();
        JsonArray productList = processorImplement.getVersions(productName);

        return makeResponseWithBody(productList);

    }

    @POST
    @Path("/issues")
    @Consumes("application/json")
    public Response postIssues(JsonObject versionData) {
        logger.debug("Request to issues");
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        String productName = versionData.get("product").toString();
        String version = versionData.get("version").toString();
        JsonArray issueList = processorImplement.getIssues(productName, version);

        return makeResponseWithBody(issueList);

    }

    @POST
    @Path("/milestone")
    @Consumes("application/json")
    public Response postMilestone(JsonArray issueList) {
        logger.debug("Request to milestone");
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        JsonArray featureList = processorImplement.getMilestoneFeatures(issueList);

        return makeResponseWithBody(featureList);
    }

    @POST
    @Path("/features")
    @Consumes("application/json")
    public Response postIssues(JsonArray issueList) {
        logger.debug("Request to features");
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        JsonArray featureList = processorImplement.getMilestoneFeatures(issueList);

        return makeResponseWithBody(featureList);
    }


    @OPTIONS
    @Path("/versions")
    public Response versionOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/products")
    public Response productsOptions() {
        return makeResponse();
    }


    @OPTIONS
    @Path("/issues")
    public Response issuesOptions() {
        return makeResponse();
    }

    @OPTIONS
    @Path("/milestone")
    public Response milestoneOptions() {
        return makeResponse();
    }

    private Response makeResponse() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT,UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .build();
    }


    private Response makeResponseWithBody(Object sendingObject) {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT,UPDATE, DELETE, OPTIONS, HEAD")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .entity(sendingObject)
                .build();
    }


}
