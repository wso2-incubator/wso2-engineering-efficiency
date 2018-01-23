/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */


package org.wso2.ltsdashboard;

/*
 * Handle basic exposing methods for the API
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandlerImplement;
import org.wso2.ltsdashboard.gitobjects.Issue;
import org.wso2.ltsdashboard.gitobjects.PullRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ProcessorImplement implements Processor {
    private final static Logger logger = Logger.getLogger(ProcessorImplement.class);
    private HashMap<String, ArrayList<String>> productRepoMap;
    private String baseUrl = "https://api.github.com/";
    private GitHandlerImplement gitHandlerImplement;
    private String org = "wso2";
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;


    ProcessorImplement(String gitToken, String databaseUrl, String databaseUser, String databasePassword) {
        this.productRepoMap = new HashMap<>();
        this.gitHandlerImplement = new GitHandlerImplement(gitToken);
        this.databaseUrl = databaseUrl;
        this.databasePassword = databasePassword;
        this.databaseUser = databaseUser;
        this.getProductsAndRepos();
    }


    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */
    @Override
    public JsonArray getProductList() {
        ArrayList<String> productList = new ArrayList<>();
        if (this.productRepoMap.isEmpty()) {
            this.getProductsAndRepos();
        }
        for (Map.Entry<String, ArrayList<String>> map : this.productRepoMap.entrySet()) {
            productList.add(map.getKey());
        }

        JsonArray productJsonArray = new JsonArray();
        for (String product : productList) {
            productJsonArray.add(product);
        }
        return productJsonArray;
    }


    /**
     * Get the versions for particular product
     *
     * @param productName - repo name
     * @return - json array of Label names
     */
    public JsonArray getVersions(String productName) {
        if (this.productRepoMap.isEmpty()) {
            this.getProductsAndRepos();
        }

        ArrayList<String> repos = this.productRepoMap.get(productName.replace("\"", ""));
        ArrayList<String> labels = new ArrayList<>();

        for (String repo : repos) {
            String url = this.baseUrl + "repos/" + this.org + "/" + repo + "/labels";
            JsonArray labelArray = gitHandlerImplement.getJSONArrayFromGit(url);

            for (JsonElement object : labelArray) {
                String label = object.getAsJsonObject().get("name").toString();
                // check product version label
                String productVersion = this.getProductVersionFromIssue(label);
                if (productVersion != null) {
                    if (!labels.contains(productVersion)) {
                        labels.add(productVersion);
                    } //end if
                }// end if
            }
        }

        JsonArray versionJsonArray = new JsonArray();
        for (String versionName : labels) {
            versionJsonArray.add(versionName);
        }

        return versionJsonArray;

    }


    /**
     * Get issues to give product name and label
     *
     * @param productName - Product name that map to database
     * @param label       - label extracted
     * @return a json array of issue details
     */
    @Override
    public JsonArray getIssues(String productName, String label) {

        ArrayList<String> repos = this.productRepoMap.get(productName.replace("\"", ""));
        String finalLabel = label.replace("\"", "");
        JsonArray issueArray = new JsonArray();

        for (String repo : repos) {
            logger.info("Repository " + repo);
            String url = this.baseUrl + "search/issues?q=label:Affected/" + finalLabel + "+repo:" + this.org + "/" + repo;
            JsonArray issues = gitHandlerImplement.getJSONArrayFromGit(url);

            for (JsonElement issue : issues) {
                JsonObject issueObject = new Issue(issue.getAsJsonObject()).createJsonObject();
                issueArray.add(issueObject);
            }
        }

        return issueArray;
    }


    /**
     * Get Milestone features extracted from git
     *
     * @param issueUrlList - issue Url list from front end
     * @return Feature Set as a json array
     */
    @Override
    public JsonArray getMilestoneFeatures(JsonArray issueUrlList) {
        JsonArray eventList = new JsonArray();
        JsonArray featureList = new JsonArray();

        // getting event lists from issue list
        for (JsonElement url : issueUrlList) {
            String issueUrl = url.getAsString() + "/timeline";
            JsonArray eventTempList = gitHandlerImplement.getJSONArrayFromGit(issueUrl,
                    "application/vnd.github.mockingbird-preview");
            eventList.addAll(eventTempList);
        }

        for (JsonElement event : eventList) {
            if (this.checkCrossReferenced(event)) {
                PullRequest featureComponent = new PullRequest(event.getAsJsonObject());
                // change features / title
                featureList.addAll(featureComponent.getFeatures());
            } //end if
        }

        // get cross referenced PR urls
        return featureList;
    }


    /**
     * Get prodcut version from label
     *
     * @param labelName - label name
     * @return product Version
     */
    private String getProductVersionFromIssue(String labelName) {
        String productVersion = null;

        if (labelName.toLowerCase().contains("affected")) {
            productVersion = labelName.split("/")[1]
                    .replace("\"", "")
                    .replace("\\", "");
        }


        return productVersion;

    }


    /**
     * Create Product repository map
     */
    private void getProductsAndRepos() {

        SqlHandlerImplement sqlHandlerImplement =
                SqlHandlerImplement.getHandler(this.databaseUrl, this.databaseUser, this.databasePassword);
        ResultSet resultSet = sqlHandlerImplement.
                executeQuery("SELECT * from UnifiedDashboards.JNKS_COMPONENTPRODUCT;");
        try {
            while (resultSet.next()) {
                String product = resultSet.getString(1);
                String repo = resultSet.getString(2);

                ArrayList<String> repoList = this.productRepoMap.get(product);
                if (repoList == null) {
                    repoList = new ArrayList<>();
                    repoList.add(repo);
                    this.productRepoMap.put(product, repoList);
                } else {
                    repoList.add(repo);
                }
            }
            logger.info("The map between product and repos created");
        } catch (SQLException e) {
            logger.info("Iterating through DB RequestSet failed");
        }

    }



    /**
     * Check whether the event is cross referenced
     *
     * @param event - event of a issue
     * @return - if cross-referenced True
     */
    private boolean checkCrossReferenced(JsonElement event) {
        boolean status = false;
        JsonObject eventObject = event.getAsJsonObject();
        String cross = eventObject.get("event").getAsString();
        if (cross.equals("cross-referenced")) {
            status = true;
        }

        return status;
    }

}
