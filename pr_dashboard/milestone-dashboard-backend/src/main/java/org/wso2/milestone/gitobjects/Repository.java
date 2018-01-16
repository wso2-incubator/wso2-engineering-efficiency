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

package org.wso2.milestone.gitobjects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.wso2.milestone.MilestoneProcessor;
import org.wso2.milestone.helpers.GitHandler;

import java.util.ArrayList;

public class Repository {
    private String repoName;
    private String productName;
    private ArrayList<Milestone> milestones;
    private String repoUrl;
    private String org;



    public Repository(String repoName, String org, String productName, String repoUrl) {
        this.repoName = repoName;
        this.productName = productName;
        this.repoUrl = repoUrl;
        this.org = org;

    }

    public String getOrg() {
        return org;
    }

    public String getRepoName() {
        return repoName;
    }


    public String getProductName() {
        return productName;
    }


    public ArrayList<Milestone> getMilestones() {
        if(milestones==null){
            milestones = this.getMilestoneList();
        }
        return milestones;
    }


    public String getRepoUrl() {
        return repoUrl.replace("\"","");
    }


    /**
     * generate milestone list related to the repository
     * @return milestone list
     */
    private ArrayList<Milestone> getMilestoneList(){
        String milestoneName,milestoneUrl,milestoneId,date;
        int openIssues,closedIssues;
        String apiBaseUrl = "https://api.github.com/repos/"+this.org+"/"+this.repoName+"/milestones";
        ArrayList<Milestone> milestonesList = new ArrayList<>();
        GitHandler gitHandler = new GitHandler(MilestoneProcessor.getGitToken());
        JsonArray milestoneArray = gitHandler.getJSONArrayFromGit(apiBaseUrl);

        for(int i=0;i<milestoneArray.size();i++){
            JsonObject milestoneJson = (JsonObject) milestoneArray.get(i);
            milestoneId = milestoneJson.get("number").toString();
            milestoneName = milestoneJson.get("title").toString();
            milestoneUrl = milestoneJson.get("html_url").toString();
            openIssues = Integer.parseInt(milestoneJson.get("open_issues").toString());
            closedIssues = Integer.parseInt(milestoneJson.get("closed_issues").toString());
            date = milestoneJson.get("due_on").toString();
            // do a print and check

            Milestone milestone = new Milestone(milestoneName,this.repoName);
            milestone.setMilestoneId(milestoneId);
            milestone.setMilestoneUrl(milestoneUrl);
            milestone.setClosedIssues(closedIssues);
            milestone.setOpenIssues(openIssues);
            milestone.setDueDate(date);
            milestone.setOrg(this.org);
            milestone.setProductName(this.getProductName());

            milestonesList.add(milestone);
        }
        return milestonesList;
    }

    /**
     * create json array of milestones
     * @return
     */
    public JsonArray getJsonArrayofMilestones(){
        ArrayList<Milestone> milestones = this.getMilestones();
        JsonArray jsonArray = new JsonArray();
        for(Milestone milestone:milestones){
            jsonArray.add(milestone.getJsonObject());
        }
        return jsonArray;
    }

}
