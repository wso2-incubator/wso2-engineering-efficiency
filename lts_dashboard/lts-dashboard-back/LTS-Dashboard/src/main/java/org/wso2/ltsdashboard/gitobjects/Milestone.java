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

package org.wso2.ltsdashboard.gitobjects;
/*
 * The data about a milestone
 */

import com.google.gson.JsonObject;

public class Milestone {
    private String url;
    private String htmlUrl;
    private String closedIssues;
    private String openIssues;
    private String releaseDate;
    private String version;
    private String title;
    private String id;

    public Milestone(JsonObject milestoneObject, String version) {
        this.url = milestoneObject.get("url").toString();
        this.htmlUrl = milestoneObject.get("html_url").toString();
        this.releaseDate = this.splitDate(milestoneObject.get("due_on").toString());
        this.openIssues = milestoneObject.get("open_issues").toString();
        this.closedIssues = milestoneObject.get("closed_issues").toString();
        this.version = version;
        this.title = milestoneObject.get("title").toString();
        this.id = milestoneObject.get("number").toString();


    }

    public String getTitle() {
        return title.replace("\"","");
    }

    public String getId() {
        return id.replace("\"","");
    }

    public String getRepo(){
        String [] urlParts = this.url.split("/");
        String org = urlParts[4];
        String repoName = urlParts[5];
        String fullRepoName = org+"/"+repoName;

        return fullRepoName.replace("\"","");
    }


    /**
     * Get Milestone as a json Object
     *
     * @return - JsonObject
     */
    JsonObject createJsonObject() {
        JsonObject milestoneObject = new JsonObject();
        milestoneObject.addProperty("url", this.trimString(this.url));
        milestoneObject.addProperty("html_url", this.trimString(this.htmlUrl));
        milestoneObject.addProperty("due_on", this.trimString(this.releaseDate));
        milestoneObject.addProperty("closed_issues", this.closedIssues);
        milestoneObject.addProperty("open_issues", this.openIssues);
        milestoneObject.addProperty("version", this.trimString(this.version));
        milestoneObject.addProperty("title", this.trimString(this.title));

        return milestoneObject;
    }

    /**
     * Get Formatted Date
     *
     * @param originalDateString - original date as a string
     * @return - formatted date
     */
    private String splitDate(String originalDateString) {
        return originalDateString.split("T")[0];
    }


    /**
     * Format the string
     *
     * @param stringValue - string to be formatted
     * @return - return
     */
    private String trimString(String stringValue) {
        return stringValue.replace("\"", "")
                .replace("\\", "");
    }





}
