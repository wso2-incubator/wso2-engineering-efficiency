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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Milestone {
    private String milestoneName;
    private String productVersion;
    private ArrayList<Issues> issues;
    private String milestoneUrl;
    private int closedIssues;
    private int openIssues;
    private String milestoneId;
    private String dueDate;
    private String repoName;
    private String org;
    private String productName;

    public Milestone(String milestoneName,String repoName) {
        this.milestoneName = milestoneName;
        this.repoName = repoName;
        this.productVersion=null;
    }


    public void setIssues(ArrayList<Issues> issues) {
        this.issues = issues;
    }

    public void setMilestoneUrl(String milestoneUrl) {
        this.milestoneUrl = milestoneUrl;
    }

    public void setClosedIssues(int closedIssues) {
        this.closedIssues = closedIssues;
    }

    public void setOpenIssues(int openIssues) {
        this.openIssues = openIssues;
    }

    public void setMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getMilestoneName() {
        return milestoneName.replace("\"","");
    }

    public String getRepoName() {
        return repoName;
    }

    public String getProductVersion() {
        if (this.productVersion != null) {
            this.productVersion = this.productVersion.replace("\"", "");
        }
        return this.productVersion;
    }

    public ArrayList<Issues> getIssues() {
        if(issues==null){
            issues = this.getIssueList();
        }

        return issues;
    }

    public String getMilestoneUrl() {
        return milestoneUrl.replace("\"","");
    }

    public int getClosedIssues() {
        return closedIssues;
    }

    public int getOpenIssues() {
        return openIssues;
    }

    public String getMilestoneId() {
        return milestoneId;
    }

    public String getDueDate() {
        return dueDate.replace("\"","").split("T")[0];
    }

    public String getProductName() {
        return productName;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * create json object from instance data
     * @return json object
     */
    public JsonObject getJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name",this.getMilestoneName());
        jsonObject.addProperty("closed-issues",this.getClosedIssues());
        jsonObject.addProperty("open-issues",this.getOpenIssues());
        jsonObject.addProperty("milestone-id",this.getMilestoneId());
        jsonObject.addProperty("url",this.getMilestoneUrl());
        jsonObject.addProperty("due-date",this.getDueDate());
        jsonObject.add("issues",this.getJsonArrayofIssues());
        jsonObject.addProperty("version",this.getProductVersion());
        jsonObject.addProperty("product-name",this.getProductName());

        return jsonObject;
    }

    /**
     * generate json string of instance data
     * @return json string
     */
    public String getJsonString(){
        return this.getJsonObject().toString();
    }


    /**
     * returns the issues related to the milestone
     * @return list of issues
     */
    private ArrayList<Issues> getIssueList(){
        ArrayList<Issues> issuesList = new ArrayList<>();
        String apiBaseUrl = "https://api.github.com/repos/"+this.org+"/"+this.repoName+"/issues?milestone="+
                this.milestoneId+"&state=all";
        GitHandler gitHandler = new GitHandler(MilestoneProcessor.gitAccessToken);
        JsonArray issueArray = gitHandler.getJSONArrayFromGit(apiBaseUrl);
        String issueId,status,issueUrl,title;
        String productVersionUpdate = null;


        for(int i=0;i<issueArray.size();i++){
            JsonObject issueJson = (JsonObject) issueArray.get(i);
            issueId = issueJson.get("number").toString();
            status = issueJson.get("state").toString();
            issueUrl = issueJson.get("html_url").toString();
            title = issueJson.get("title").toString();
            JsonArray labels = issueJson.getAsJsonArray("labels");
            ArrayList<String> labelList = this.generateLabels(labels);

            Issues issue = new Issues(issueId);
            issue.setIssueUrl(issueUrl);
            issue.setStatus(status);
            issue.setLabels(labelList);
            issue.setOrg(this.org);
            issue.setIssueTitle(title);
            issue.setResolvingRepoName(this.repoName);

            issuesList.add(issue);

            //get the product version
            if(this.productVersion==null){
                productVersionUpdate = this.getProductVersion(labelList);
                if(productVersionUpdate!=null){
                    this.setProductVersion(productVersionUpdate);
                }
            }
        }
        return issuesList;
    }


    /**
     * Create list of label names which is extracted from json array
     * @param jsonArray - json array containing label data
     * @return arraylist of label names
     */
    private ArrayList<String> generateLabels(JsonArray jsonArray){
        ArrayList<String> labelArray = new ArrayList<>();
        for(int i=0;i<jsonArray.size();i++){
            JsonObject obj = (JsonObject) jsonArray.get(i);
            String name = obj.get("name").toString();
            labelArray.add(name);
        }

        return labelArray;
    }


    /**
     * resolves the product name from the lables of the issues
     * @param labels - labels of a issue
     * @return - the product version
     */
    private String getProductVersion(ArrayList<String> labels){
        String productVersion = null;
        String pattern = "[0-9]+\\.[0-9x]+\\.[0-9x]+(.*)";
        Pattern r = Pattern.compile(pattern);

        for(String label:labels){
            if(label.toLowerCase().contains("affected")){
                productVersion = label.split("/")[1];
                break;
            }
            //check regex
            Matcher m = r.matcher(label);
            if(m.find()){
                productVersion = label;
                break;
            }

        }
        return productVersion;
    }

    /**
     * create json array of issues
     * @return
     */
    private JsonArray getJsonArrayofIssues(){
        JsonArray jsonArray = new JsonArray();
        ArrayList<Issues> issues = this.getIssues();

        for(Issues issue:issues){
            jsonArray.add(issue.getJsonObject());
        }

        return jsonArray;
    }


    public static void main(String [] args){
        ArrayList<String> li = new ArrayList<>();
        li.add("5.x.x");
        li.add("Test");
        Milestone milestone = new Milestone("op","dsf");
        String version = milestone.getProductVersion(li);
        System.out.println(version);

    }


}
