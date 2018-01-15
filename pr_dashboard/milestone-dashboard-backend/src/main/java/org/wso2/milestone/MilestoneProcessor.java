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

package org.wso2.milestone;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.log4j.Logger;
import org.wso2.milestone.gitobjects.Milestone;
import org.wso2.milestone.gitobjects.Repository;
import org.wso2.milestone.helpers.GitHandler;
import org.wso2.milestone.helpers.MongoHandler;
import org.wso2.milestone.helpers.SQLHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class MilestoneProcessor {
    public static String GIT_TOKEN = "8bca47bae8a4dd1009070791dba5abbc1f453c71";
    private Map<String,String> productRepoData;
    private String databaseUrl="jdbc:mysql://localhost:3306/UnifiedDashboards?useSSL=false";
    private String databaseUser="root";
    private String databasePassword = "1234";
    final static Logger logger = Logger.getLogger(MilestoneProcessor.class);
    private String [] organizations = {
            "wso2",
            "wso2-support",
            "wso2-extensions",
            "wso2-incubator",
            "ballerinalang"
    };




    public MilestoneProcessor(){}


    /**
     *  build the mapping between each repository and product
     */
    private void buildProductRepoData(){
        if(productRepoData == null){
            productRepoData = new HashMap<>();
        }
        SQLHandler sqlHandler = new SQLHandler(databaseUrl,databaseUser,databasePassword);
        ResultSet resultSet = sqlHandler.executeQuery("SELECT * FROM UnifiedDashboards.JNKS_COMPONENTPRODUCT");
        try {
            while (resultSet.next()) {
                String product = resultSet.getString(1);
                String repo = resultSet.getString(2);

                this.productRepoData.put(repo,product);
            }
            logger.info("The map between product and repos created");
        }catch (SQLException e){
            logger.error("Iterating through DB RequestSet failed");
        }
    }



    /**
     * get the repository array of selected organizations
     * @return
     */
    private ArrayList<Repository> getRepositoryArray(){
        String repoBaseURL = "https://api.github.com/orgs/";
        this.buildProductRepoData();
        ArrayList<Repository> repositoryList = new ArrayList<>();
        GitHandler gitHandler = new GitHandler(MilestoneProcessor.GIT_TOKEN);
        String repoName,productName,repoUrl;

        for(String org:this.organizations){
            String url = repoBaseURL+org+"/repos";
            JsonArray repoArray = gitHandler.getJSONArrayFromGit(url);
            for(int i=0;i<repoArray.size();i++){
                JsonObject jsonObject = repoArray.get(0).getAsJsonObject();
                repoName = jsonObject.get("name").toString();
                repoUrl = jsonObject.get("html_url").toString();
                productName = this.productRepoData.get(repoName);

                Repository repo = new Repository(repoName,org,productName,repoUrl);
                repositoryList.add(repo);
            }
        }

        return repositoryList;

    }

    /**
     * get milestone list from all repositories
     * @param repoArray
     * @return
     */
    public ArrayList<Milestone> getMilestones(ArrayList<Repository> repoArray){
        ArrayList<Milestone> milestonesAll = new ArrayList<>();
        for(Repository repository: repoArray){
           milestonesAll.addAll(repository.getMilestones());
        }
        return milestonesAll;
    }


    /**
     * create json array of milestones
     * @param milestones - list of milestones
     * @return json array of milestones
     */
    public JsonArray getJsonArrayOfMilestones(ArrayList<Milestone> milestones){
        JsonArray jsonArray = new JsonArray();
        for(Milestone milestone: milestones){
            jsonArray.add(milestone.getJsonObject());
        }
        logger.info("The milestone data string generated");
        return jsonArray;
    }

    /**
     * create json string of milestones
     * @param milestones - list of milestones
     * @return
     */
    public String getJsonStringofMilestones(ArrayList<Milestone> milestones){
        JsonArray jsonArray = this.getJsonArrayOfMilestones(milestones);
        return jsonArray.toString();
    }


    public static void main(String [] args){
        MilestoneProcessor milestoneProcessor = new MilestoneProcessor();
        ArrayList<Repository> repoArray = new ArrayList<>();
        Repository repository = new Repository("product-ei","wso2","Enterprise Integrator","https://github.com/wso2-incubator/label-test");
        repoArray.add(repository);
        ArrayList<Milestone> milestones = milestoneProcessor.getMilestones(repoArray);
        JsonArray returnArray = milestoneProcessor.getJsonArrayOfMilestones(milestones);
        MongoHandler mongoHandler = new MongoHandler("localhost",27017,"product_milestone");
        mongoHandler.insertToTable("milestones",returnArray);


    }
}

