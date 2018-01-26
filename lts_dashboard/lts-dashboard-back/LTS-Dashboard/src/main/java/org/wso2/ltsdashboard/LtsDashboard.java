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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 1.0-SNAPSHOT
 */

@Path("/lts")
public class LtsDashboard {
    private static String databaseUrl;
    private static String databaseUser;
    private static String databasePassword;
    private static String gitToken;


    LtsDashboard(String gitTokenPam, String databaseUrlPam, String databaseUserPam, String databasePasswordPam) {
        gitToken = gitTokenPam;
        databaseUrl = databaseUrlPam;
        databaseUser = databaseUserPam;
        databasePassword = databasePasswordPam;

    }

    @GET
    @Path("/products")
    public Response getProducts() {
        ProcessorImplement processorImplement = new ProcessorImplement(
                gitToken, databaseUrl, databaseUser, databasePassword);
        JsonArray productList = processorImplement.getProductList();

        return makeResponseWithBody(productList);
    }


    @POST
    @Path("/versions")
    public Response getLabels(JsonObject product) {
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
