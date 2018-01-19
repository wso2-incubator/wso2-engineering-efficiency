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

package org.wso2.ltsdashboard;/*
 * TODO - comment class work
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandlerImplement;
import org.wso2.ltsdashboard.gitobjects.Issue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ProcessorImplement implements Processor {
    final static Logger logger = Logger.getLogger(ProcessorImplement.class);
    private HashMap<String, ArrayList<String>> productRepoMap;
    private String baseUrl = "https://api.github.com/";
    private GitHandlerImplement gitHandlerImplement;
    private String gitToken = "5f6a6a361d4a253a18178148167dd92f8b588130";

    public ProcessorImplement() {
        this.productRepoMap = new HashMap<>();
        this.gitHandlerImplement = new GitHandlerImplement(gitToken);
    }

    public static void main(String[] args) {
        ProcessorImplement processorImplement = new ProcessorImplement();
        ArrayList<String> arrayList = processorImplement.getProductList();

//        JsonArray jsonArray = processorImplement.getIssues("Integration", "6.2.0");
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("https://api.github.com/repos/wso2/product-ei/issues/1764");
        processorImplement.getMilestoneFeatures(jsonArray);
        System.out.printf("f");

    }

    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */
    @Override
    public ArrayList<String> getProductList() {
        ArrayList<String> productList = new ArrayList<>();
        if (this.productRepoMap.isEmpty()) {
            this.getProductsAndRepos();
        }
        for (Map.Entry<String, ArrayList<String>> map : this.productRepoMap.entrySet()) {
            productList.add(map.getKey());
        }
        return productList;
    }

    /**
     * Get the labels for particular product
     *
     * @param repos - repo name
     * @return - ArrayList of Label names
     */
    @Override
    public ArrayList<String> getLabels(ArrayList<String> repos) {
        ArrayList<String> labels = new ArrayList<>();
        for (String repo : repos) {
            String url = this.baseUrl + "repos/wso2/" + repo + "/labels";
            JsonArray labelArray = gitHandlerImplement.getJSONArrayFromGit(url);
            for (JsonElement object : labelArray) {
                String label = object.getAsJsonObject().get("name").toString();
                // check product version label
                String productVersion = this.getProductVersionFromIssue(label);
                if (productVersion != null) {
                    if (!label.contains(productVersion)) {
                        labels.add(productVersion);
                    }
                }
            }
        }
        return labels;

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
        ArrayList<String> repos = this.productRepoMap.get(productName);
        JsonArray issueArray = new JsonArray();
        for (String repo : repos) {
            logger.info("Repository " + repo);
            String url = this.baseUrl + "search/issues?q=label:" + label + "+repo:wso2/" + repo;
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

        // getting event lists from issue list
        for (JsonElement url : issueUrlList) {
            String issueUrl = url.getAsString() + "/timeline";
            JsonArray eventTempList = gitHandlerImplement.getJSONArrayFromGit(issueUrl,
                    "application/vnd.github.mockingbird-preview");
            eventList.addAll(eventTempList);
        }

        for (JsonElement event : eventList) {
            if (this.checkCrossReferenced(event)) {
                System.out.println("dsf");
            }
        }

        // get cross referenced PR urls
        // TODO : need to send milestone data with feature list
        return eventList;
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

        if (labelName.toLowerCase().contains("affected")) {
            productVersion = labelName.split("/")[1];
        }
        //check regex
        Matcher m = r.matcher(labelName);
        if (m.find()) {
            productVersion = labelName;
        }

        return productVersion;

    }

    /**
     * Create Product repository map
     */
    private void getProductsAndRepos() {

        SqlHandlerImplement sqlHandlerImplement = new SqlHandlerImplement("jdbc:mysql://localhost:3306/UnifiedDashboards?useSSL=false", "root", "1234");
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
            logger.error("Iterating through DB RequestSet failed");
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
