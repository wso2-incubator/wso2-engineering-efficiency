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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.wso2.milestone.MilestoneProcessor;
import org.wso2.milestone.helpers.GitHandler;

import java.util.ArrayList;

public class Issues {
    private String issueId;
    private String status;
    private ArrayList<ResolvingPRs> prList;
    private String issueUrl;
    private ArrayList<String> labels;
    private String resolvingRepoName;
    private String issueTitle;
    private String org;

    public Issues(String issueId) {
        this.issueId = issueId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrList(ArrayList<ResolvingPRs> prList) {
        this.prList = prList;
    }

    public void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public void setResolvingRepoName(String resolvingRepoName) {
        this.resolvingRepoName = resolvingRepoName;
    }

    public void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getIssueTitie() {
        return issueTitle.replace("\"","");
    }

    public String getIssueId() {
        return issueId;
    }

    public String getStatus() {
        return status.replace("\"","");
    }

    public ArrayList<ResolvingPRs> getPrList() {
        if(prList==null){
            prList = this.getPullRequests();
        }
        return prList;
    }

    public String getIssueUrl() {
        return issueUrl.replace("\"","");
    }

    public ArrayList<String> getLabels() {
        return labels;
    }


    public String getRepoName() {
        return resolvingRepoName;
    }

    /**
     * create a json object of current instance
     * @return josn object
     */
    public JsonObject getJsonObject(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("issue-id",this.getIssueId());
        jsonObject.addProperty("title",this.getIssueTitie());
        jsonObject.addProperty("url",this.getIssueUrl());
        jsonObject.addProperty("repository",this.getRepoName());
        jsonObject.add("pr-list",this.getResolvingPRsJson());
        jsonObject.addProperty("status",this.getStatus());


        return jsonObject;
    }


    /**
     *  create json string of current instance
     * @return json string
     */
    public String getJsonString(){
        JsonObject jsonObject = this.getJsonObject();
        return jsonObject.toString();
    }

    /**
     * get the pull request related to the issue
     * @return list of issues
     */
    private ArrayList<ResolvingPRs> getPullRequests(){

        ArrayList<ResolvingPRs> prList = new ArrayList<>();
        String prId,prLink,status,title;
        ArrayList<String> labels = null;

        String apiBaseUrl = "https://api.github.com/repos/"+this.org+"/"+this.resolvingRepoName+"/issues/"+issueId+"/timeline";
        GitHandler gitHandler = new GitHandler(MilestoneProcessor.GIT_TOKEN);
        JsonArray events = gitHandler.getJSONArrayFromGit(apiBaseUrl,
                "application/vnd.github.mockingbird-preview");

        for(int i=0;i<events.size();i++){
            JsonObject eventJson = (JsonObject) events.get(i);
            String event = eventJson.get("event").toString().replace("\"","");

            if(event.toString().equals("cross-referenced")){
                JsonElement source = eventJson.get("source");
                JsonElement pr = source.getAsJsonObject().get("issue");
                JsonObject prObject = pr.getAsJsonObject();
                prId = prObject.get("number").toString();
                prLink = prObject.get("html_url").toString();
                status = prObject.get("state").toString().replace("\"","");
                title = prObject.get("title").toString();
                labels = this.generateLabels(prObject.getAsJsonArray("labels"));


                ResolvingPRs resolvingPRs = new ResolvingPRs(prId);
                resolvingPRs.setPrLink(prLink);
                resolvingPRs.setStatus(status);
                resolvingPRs.setTitle(title);
                resolvingPRs.setResovingIssue(this.getIssueId());
                resolvingPRs.setResolvingRepoName(this.getRepoName());
                resolvingPRs.setLabels(labels);

                prList.add(resolvingPRs);

            }

        }

        return prList;

    }


    /**
     * generate label list for the prs
     * @param jsonArray - pull request label json array
     * @return - label name array containing only label name
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
     * create json array of PRs
     * @return
     */
    private JsonArray getResolvingPRsJson(){
        JsonArray jsonArray = new JsonArray();
        ArrayList<ResolvingPRs> resolvingPRs = this.getPrList();

        for(ResolvingPRs pr:resolvingPRs){
            jsonArray.add(pr.getJsonObject());
        }
        return jsonArray;
    }

}
