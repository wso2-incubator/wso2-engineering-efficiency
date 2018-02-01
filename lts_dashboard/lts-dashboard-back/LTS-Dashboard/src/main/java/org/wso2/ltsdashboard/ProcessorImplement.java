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
import org.wso2.ltsdashboard.connectionshandlers.SqlHandler;
import org.wso2.ltsdashboard.gitobjects.Issue;
import org.wso2.ltsdashboard.gitobjects.Milestone;
import org.wso2.ltsdashboard.gitobjects.PullRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProcessorImplement implements Processor {
    private final static Logger logger = Logger.getLogger(ProcessorImplement.class);
    private static HashMap<String, ArrayList<String>> productRepoMap = new HashMap<>();
    private static HashMap<String, ArrayList<Milestone>> productMilestoneMap = new HashMap<>();
    private String baseUrl = "https://api.github.com/";
    private GitHandlerImplement gitHandlerImplement;
    private String org = "wso2";
    private String databaseUrl;
    private String databaseUser;
    private String databasePassword;


    ProcessorImplement(String gitToken, String databaseUrl, String databaseUser, String databasePassword) {
        this.gitHandlerImplement = new GitHandlerImplement(gitToken);
        this.databaseUrl = databaseUrl;
        this.databasePassword = databasePassword;
        this.databaseUser = databaseUser;
        if (productRepoMap.isEmpty()) {
            this.createProductAndRepos();
        }
    }


    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */
    @Override
    public JsonArray getProductList() {
        ArrayList<String> productList = new ArrayList<>();
        if (productRepoMap.isEmpty()) {
            this.createProductAndRepos();
        }
        for (Map.Entry<String, ArrayList<String>> map : productRepoMap.entrySet()) {
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
        if (productRepoMap.isEmpty()) {
            this.createProductAndRepos();
        }

        ArrayList<String> repos = productRepoMap.get(productName.replace("\"", ""));
        ArrayList<String> labels = new ArrayList<>();

        for (String repo : repos) {
            // TODO - uncheck revert

            if (checkValidRepo(repo) || repo.equals("label-test")) {
                String url;
                if (repo.equals("label-test")) {
                    url = this.baseUrl + "repos/wso2-incubator/" + repo + "/milestones";
                } else {
                    url = this.baseUrl + "repos/" + this.org + "/" + repo + "/milestones";
                }

                JsonArray milestoneArray = gitHandlerImplement.getJSONArrayFromGit(url);

                for (JsonElement object : milestoneArray) {
                    String milestoneName = object.getAsJsonObject().get("title").toString();
                    // check product version label
                    String productVersion = this.getProductVersionFromIssue(milestoneName);
                    if (productVersion != null) {
                        if (!labels.contains(productVersion)) {
                            labels.add(productVersion);
                        } //end if
                    }// end if
                }
            }
        }

        JsonArray versionJsonArray = new JsonArray();
        for (String versionName : labels) {
            versionJsonArray.add(versionName);
        }

        logger.info("Version List created for product :" + productName);

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
        String productNameFormatted = productName.replace("\"", "");
        ArrayList<Milestone> milestones;

        //check hashmap has version milestone data
        if (!productMilestoneMap.containsKey(productNameFormatted)) {
            milestones = this.getMilestoneForProduct(productNameFormatted);
            productMilestoneMap.put(productNameFormatted, milestones);
        } else {
            milestones = productMilestoneMap.get(productNameFormatted);
        }

        String finalLabel = label.replace("\"", "");
        JsonArray issueArray = new JsonArray();

        for (Milestone milestone : milestones) {
            System.out.println(milestone.getTitle());
            logger.debug("Milestone name :: " + milestone.getTitle());
            if (checkVersion(milestone.getTitle(), finalLabel)) {
                String url = this.baseUrl + "repos/" + milestone.getRepo() + "/issues?milestone=" + milestone.getId() +
                        "&state=all";
                JsonArray issues = gitHandlerImplement.getJSONArrayFromGit(url);

                for (JsonElement issue : issues) {
                    JsonObject issueObject = new Issue(issue.getAsJsonObject()).createJsonObject();
                    issueArray.add(issueObject);
                }
            }

        }

        logger.info(productName + ":" + label + " issues extracted");

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
        JsonArray featureList = new JsonArray();


        // getting event lists from issue list
        for (JsonElement issue : issueUrlList) {
            JsonObject issueObject = issue.getAsJsonObject();

            String issueUrl = issueObject.get("url").getAsString() + "/timeline";
            JsonArray eventTempList = gitHandlerImplement.getJSONArrayFromGit(issueUrl,
                    "application/vnd.github.mockingbird-preview");

            for (JsonElement event : eventTempList) {
                // check event is cross referenced
                if (this.checkCrossReferenced(event)) {
                    PullRequest featureComponent = new PullRequest(event.getAsJsonObject());
                    JsonArray featureArray = featureComponent.getFeatures();
                    for (JsonElement feature : featureArray) {

                        // make new json object
                        JsonObject object = new JsonObject();
                        object.addProperty("feature", this.trimJsonElementString(feature));
                        object.addProperty("html_url", this.trimJsonElementString(issueObject.get("html_url")));
                        object.addProperty("title", this.trimJsonElementString(issueObject.get("title")));
                        if(featureArray.size()>0) {
                            featureList.add(object);
                        }
                    }

                } //end if
            }
        }

        return featureList;
    }


    /**
     * Get All features for product version
     *
     * @param issueUrlList - issue Url list from front end
     * @return Feature Set as a json array
     */
    @Override
    public JsonArray getAllFeatures(JsonArray issueUrlList) {
        JsonArray issueList = new JsonArray();


        // getting event lists from issue list
        for (JsonElement issue : issueUrlList) {
            JsonObject issueObject = issue.getAsJsonObject();

            String issueUrl = issueObject.get("url").getAsString() + "/timeline";
            JsonArray eventTempList = gitHandlerImplement.getJSONArrayFromGit(issueUrl,
                    "application/vnd.github.mockingbird-preview");

            for (JsonElement event : eventTempList) {
                // check event is cross referenced
                if (this.checkCrossReferenced(event)) {
                    PullRequest featureComponent = new PullRequest(event.getAsJsonObject());
                    JsonArray featureArray = featureComponent.getFeatures();
                    JsonObject issueTempObject = new JsonObject();
                    issueTempObject.addProperty("html_url",
                            this.trimJsonElementString(issueObject.get("html_url")));
                    issueTempObject.add("features", featureArray);
                    issueTempObject.addProperty("title", this.trimJsonElementString(issueObject.get("title")));
                    if(featureArray.size()>0) {
                        issueList.add(issueTempObject);
                    }

                } //end if
            }
        }

        return issueList;
    }


    private String trimJsonElementString(JsonElement text) {
        return text.toString().replace("\"", "");
    }

    /**
     * Get prodcut version from label
     *
     * @param labelName - label name
     * @return product Version
     */
    private String getProductVersionFromIssue(String labelName) {
        String productVersion = null;
        String pattern = "[0-9]+\\.[0-9]+\\.[0-9]+(.*)";
        Pattern r = Pattern.compile(pattern);

        //check regex
        Matcher m = r.matcher(labelName);
        if (m.find()) {
            productVersion = labelName.split(" ")[0]
                                        .split("-")[0]
                                        .replace("\"", "");
        }

        return productVersion;

    }

    /**
     * Check whether the milestone name match with version
     *
     * @param milestoneName - milestone name as a string
     * @param version       - version as a string
     * @return - checked version
     */
    private boolean checkVersion(String milestoneName, String version) {
        boolean isCheckedVersion = false;
        if (milestoneName.contains(version)) {
            isCheckedVersion = true;
        }

        return isCheckedVersion;
    }

    /**
     * Check the repository is a valid repository
     *
     * @param repoName - name of the repository
     * @return - true or false
     */
    private boolean checkValidRepo(String repoName) {
        boolean isValid = false;
        if (repoName.contains("product")|| repoName.contains("ballerina")) {
            isValid = true;
        }

        return isValid;
    }

    /**
     * Create Product repository map
     */
    private void createProductAndRepos() {
        SqlHandler sqlHandler =
                SqlHandler.getHandler(this.databaseUrl, this.databaseUser, this.databasePassword);
        productRepoMap = sqlHandler.getProductVsRepos();

        if (productRepoMap.isEmpty()) {
            logger.error("Product vs Repository List is empty");
        } else {
            logger.info("Product vs Repository List created");
        }

        // add test repo
        this.getLabelTestRepo();
        logger.info("Test product added");

    }

    /**
     * Make product list from incubator test repo
     */
    private void getLabelTestRepo() {
        ArrayList<String> repoList = new ArrayList<>();
        repoList.add("label-test");
        productRepoMap.put("Integration Test", repoList);
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


    /**
     * Get the milstones for particular product
     *
     * @param productName - repo name
     * @return - ArrayList of milestone objects
     */
    private ArrayList<Milestone> getMilestoneForProduct(String productName) {
        if (productRepoMap.isEmpty()) {
            this.createProductAndRepos();
        }

        ArrayList<String> repos = productRepoMap.get(productName.replace("\"", ""));
        ArrayList<Milestone> milestones = new ArrayList<>();

        for (String repo : repos) {
            // TODO - uncheck revert
            if (checkValidRepo(repo) || repo.equals("label-test")) {
                String url;
                if (repo.equals("label-test")) {
                    url = this.baseUrl + "repos/wso2-incubator/" + repo + "/milestones";
                } else {
                    url = this.baseUrl + "repos/" + this.org + "/" + repo + "/milestones";
                }

                JsonArray milestoneArray = gitHandlerImplement.getJSONArrayFromGit(url);

                for (JsonElement object : milestoneArray) {
                    String milestoneName = object.getAsJsonObject().get("title").toString();
                    // check product version label
                    String productVersion = this.getProductVersionFromIssue(milestoneName);
                    if (productVersion != null) {
                        milestones.add(new Milestone(object.getAsJsonObject(), productVersion));
                    }// end if
                }
            }
        }


        return milestones;

    }
}
