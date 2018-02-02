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

import com.google.gson.JsonObject;

import java.util.ArrayList;

class ResolvingPRs {
    private String prId;
    private String prLink;
    private String status;
    private String title;
    private ArrayList<String> labels;


    ResolvingPRs(String prId) {
        this.prId = prId;
    }

    void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    private String getTitle() {
        return title.replace("\"", "");
    }

    void setTitle(String title) {
        this.title = title;
    }

    private String getPrId() {
        return prId;
    }

    private String getPrLink() {
        return prLink.replace("\"", "");
    }

    void setPrLink(String prLink) {
        this.prLink = prLink;
    }

    private String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    /**
     * create josn object of current pr instance
     *
     * @return json object of PRs
     */
    JsonObject getJsonObject() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pr-id", this.getPrId());
        jsonObject.addProperty("title", this.getTitle());
        jsonObject.addProperty("pr-link", this.getPrLink());
        jsonObject.addProperty("status", this.getStatus());
        jsonObject.addProperty("isCodePR", this.isCodePR());

        return jsonObject;
    }


    /**
     * check whether the pr is a doc pr or code pr
     *
     * @return True or False if the code is PR
     */
    private boolean isCodePR() {
        boolean type = true;
        for (String label : labels) {
            label = label.replace("\"", "");
            if (label.equals("doc") || label.equals("Doc")) {

                type = false;
            }
        }
        return type;
    }


}
