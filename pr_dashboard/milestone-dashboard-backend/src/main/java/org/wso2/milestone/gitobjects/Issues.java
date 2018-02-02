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
    private String resolvingRepoName;
    private String issueTitle;
    private String org;

    Issues(String issueId) {
        this.issueId = issueId;
    }

    void setResolvingRepoName(String resolvingRepoName) {
        this.resolvingRepoName = resolvingRepoName;
    }

    void setIssueTitle(String issueTitle) {
        this.issueTitle = issueTitle;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    private String getIssueTitie() {
        return issueTitle.replace("\"", "");
    }

    private String getIssueId() {
        return issueId;
    }

    private String getStatus() {
        return status.replace("\"", "");
    }

    void setStatus(String status) {
        this.status = status;
    }

    private ArrayList<ResolvingPRs> getPrList() {
        if (prList == null) {
            prList = this.getPullRequests();
        }
        return prList;
    }

    private String getIssueUrl() {
        return issueUrl.replace("\"", "");
    }

    void setIssueUrl(String issueUrl) {
        this.issueUrl = issueUrl;
    }

    private String getRepoName() {
        return resolvingRepoName;
    }

    /**
     * create a json object of current instance
     *
     * @return josn object
     */
    JsonObject getJsonObject() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("issue-id", this.getIssueId());
        jsonObject.addProperty("title", this.getIssueTitie());
        jsonObject.addProperty("url", this.getIssueUrl());
        jsonObject.addProperty("repository", this.getRepoName());
        jsonObject.add("pr-list", this.getResolvingPRsJson());
        jsonObject.addProperty("status", this.getStatus());


        return jsonObject;
    }


    /**
     * get the pull request related to the issue
     *
     * @return list of issues
     */
    private ArrayList<ResolvingPRs> getPullRequests() {

        ArrayList<ResolvingPRs> prList = new ArrayList<>();
        String prId, prLink, status, title;
        ArrayList<String> labels;

        String apiBaseUrl = "https://api.github.com/repos/" + this.org + "/" + this.resolvingRepoName + "/issues/" + issueId + "/timeline";
        GitHandler gitHandler = new GitHandler(MilestoneProcessor.getGitToken());
        JsonArray events = gitHandler.getJSONArrayFromGit(apiBaseUrl,
                "application/vnd.github.mockingbird-preview");

        for (int i = 0; i < events.size(); i++) {
            JsonObject eventJson = (JsonObject) events.get(i);
            String event = eventJson.get("event").toString().replace("\"", "");

            if (event.equals("cross-referenced")) {
                JsonElement source = eventJson.get("source");
                JsonElement pr = source.getAsJsonObject().get("issue");
                JsonObject prObject = pr.getAsJsonObject();
                prId = prObject.get("number").toString();
                prLink = prObject.get("html_url").toString();
                status = prObject.get("state").toString().replace("\"", "");
                title = prObject.get("title").toString();
                labels = this.generateLabels(prObject.getAsJsonArray("labels"));


                ResolvingPRs resolvingPRs = new ResolvingPRs(prId);
                resolvingPRs.setPrLink(prLink);
                resolvingPRs.setStatus(status);
                resolvingPRs.setTitle(title);
                resolvingPRs.setLabels(labels);

                prList.add(resolvingPRs);

            }

        }

        return prList;

    }


    /**
     * generate label list for the prs
     *
     * @param jsonArray - pull request label json array
     * @return - label name array containing only label name
     */
    private ArrayList<String> generateLabels(JsonArray jsonArray) {
        ArrayList<String> labelArray = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject obj = (JsonObject) jsonArray.get(i);
            String name = obj.get("name").toString();
            labelArray.add(name);
        }

        return labelArray;
    }


    /**
     * create json array of PRs
     *
     * @return josn array
     */
    private JsonArray getResolvingPRsJson() {
        JsonArray jsonArray = new JsonArray();
        ArrayList<ResolvingPRs> resolvingPRs = this.getPrList();

        for (ResolvingPRs pr : resolvingPRs) {
            jsonArray.add(pr.getJsonObject());
        }
        return jsonArray;
    }

}
