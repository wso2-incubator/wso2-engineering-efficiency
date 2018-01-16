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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;

public class ResolvingPRs {
    private String prId;
    private String prLink;
    private String resolvingIssue;
    private String status;
    private String resolvingRepoName;
    private String title;
    private ArrayList<String> labels;



    public ResolvingPRs(String prId) {
        this.prId = prId;
    }

    public void setPrLink(String prLink) {
        this.prLink = prLink;
    }

    public void setResovingIssue(String resovingIssue) {
        this.resolvingIssue = resovingIssue;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setResolvingRepoName(String resolvingRepoName) {
        this.resolvingRepoName = resolvingRepoName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public String getTitle() {
        return title.replace("\"","");
    }

    public String getPrId() {
        return prId;
    }

    public String getPrLink() {
        return prLink.replace("\"","");
    }

    public String getResovingIssue() {
        return resolvingIssue;
    }

    public String getStatus() {
        return status;
    }

    public String getRepoName() {
        return resolvingRepoName;
    }


    /**
     * create josn object of current pr instance
     * @return json object of PRs
     */
    public JsonObject getJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pr-id",this.getPrId());
        jsonObject.addProperty("title",this.getTitle());
        jsonObject.addProperty("pr-link",this.getPrLink());
        jsonObject.addProperty("status",this.getStatus());
        jsonObject.addProperty("isCodePR",this.isCodePR());

        return jsonObject;
    }

    /**
     * get json string of PR
     * @return json string
     */
    public String getJsonString(){
        return this.getJsonObject().toString();
    }


    /**
     * check whether the pr is a doc pr or code pr
     * @return True or False if the code is PR
     */
    private boolean isCodePR(){
        boolean type = true;
        for(String label: labels){
            label = label.replace("\"","");
            if(label.equals("doc") || label.equals("Doc")){

                type =false;
            }
        }
        return type;
    }

    /**
     * create json array of lables
     * @return json array of labels
     */
    private JsonArray generateLabelJsonArray(){
        JsonArray jsonArray = new JsonArray();
        for(String label: labels){
            jsonArray.add(label);
        }

        return jsonArray;
    }


}
