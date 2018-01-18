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
import org.apache.log4j.Logger;
import org.wso2.ltsdashboard.connectionshandlers.GitHandlerImplement;
import org.wso2.ltsdashboard.connectionshandlers.SqlHandlerImplement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Processor {
    final static Logger logger = Logger.getLogger(Processor.class);
    private HashMap<String, ArrayList<String>> productRepoMap;
    private String baseUrl = "https://api.github.com/";
    private GitHandlerImplement gitHandlerImplement;
    private String gitToken = "a3a0df4543b9d2751e9c060e78532cb6ac06d81b";

    public Processor() {
        this.productRepoMap = new HashMap<>();
        this.gitHandlerImplement = new GitHandlerImplement(gitToken);
    }

    public static void main(String[] args) {
        Processor processor = new Processor();
        ArrayList<String> arrayList = processor.getProductList();

        JsonArray jsonArray = processor.getIssues("Integration", "6.2.0");
        System.out.printf("f");

    }

    /**
     * Create Product repository map
     */
    private void getProductsAndRepos() {

        SqlHandlerImplement sqlHandlerImplement = new SqlHandlerImplement("jdbc:mysql://localhost:3306/UnifiedDashboards?useSSL=false", "root", "1234");
        ResultSet resultSet = sqlHandlerImplement.executeQuery("SELECT * from UnifiedDashboards.JNKS_COMPONENTPRODUCT;");
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
     * Get the product list
     *
     * @return ArrayList of Product names
     */

    ArrayList<String> getProductList() {
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
    ArrayList<String> getLabels(ArrayList<String> repos) {
        ArrayList<String> labels = new ArrayList<>();
        for (String repo : repos) {
            String url = this.baseUrl + "repos/wso2/" + repo + "/labels";
            JsonArray labelArray = gitHandlerImplement.getJSONArrayFromGit(url);
            for (JsonElement object : labelArray) {
                String label = object.getAsJsonObject().get("name").toString();
                // TODO : Filter Labels
                labels.add(label);
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
    JsonArray getIssues(String productName, String label) {
        ArrayList<String> repos = this.productRepoMap.get(productName);
        JsonArray issueArray = new JsonArray();
        for (String repo : repos) {
            logger.info("Repository " + repo);
            String url = this.baseUrl + "search/issues?q=label:" + label + "+repo:wso2/" + repo;
            JsonArray issues = gitHandlerImplement.getJSONArrayFromGit(url);
            issueArray.addAll(issues);
        }

        // TODO : process issues

        return issueArray;
    }

    /**
     * Get Milestone features extracted from git
     *
     * @param milestoneId - Milestone Id
     * @return Feature Set as a json array
     */
    JsonArray getMilestoneFeatures(String milestoneId) {
        JsonArray jsonArray = new JsonArray();
        return jsonArray;
    }


}
