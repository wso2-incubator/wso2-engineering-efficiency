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
 * TODO - comment class work
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Issue {
    private String issueUrl;
    private String htmlUrl;
    private String issueTitle;
    private Milestone milestone;
    private String version;


    public Issue(JsonObject issueData) {
        this.issueUrl = issueData.get("url").toString();
        this.htmlUrl = issueData.get("html_url").toString();
        this.issueTitle = issueData.get("title").toString();
        ArrayList<String> labels = createLabelList(issueData.get("labels").getAsJsonArray());
        this.version = this.getProductVersion(labels);
        if (issueData.get("milestone") != null &&
                !issueData.get("milestone").toString().equals("null")) {
            this.milestone = new Milestone(issueData.get("milestone").getAsJsonObject(), this.version);
        }
    }

    /**
     * create Json object from issue
     *
     * @return - JsonObject
     */
    public JsonObject createJsonObject() {
        JsonObject issueObject = new JsonObject();
        issueObject.addProperty("url", this.trimString(this.issueUrl));
        issueObject.addProperty("html_url", this.trimString(this.htmlUrl));
        issueObject.addProperty("issue_title", this.trimString(this.issueTitle));
        issueObject.addProperty("version", this.trimString(this.version));
        if (this.milestone != null) {
            issueObject.add("milestone", this.milestone.createJsonObject());
        } else {
            issueObject.add("milestone", null);

        }

        return issueObject;
    }


    /**
     * Create array list of labels
     *
     * @param labels - Json array of labels
     * @return - ArrayList of labels
     */
    private ArrayList<String> createLabelList(JsonArray labels) {
        ArrayList<String> labelList = new ArrayList<>();
        for (JsonElement label : labels) {
            String labelName = label.getAsJsonObject().get("name").toString();
            labelList.add(labelName);
        }
        return labelList;
    }

    /**
     * resolves the product name from the lables of the issues
     *
     * @param labels - labels of a issue
     * @return - the product version
     */
    private String getProductVersion(ArrayList<String> labels) {
        String productVersion = null;
        String pattern = "[0-9]+\\.[0-9x]+\\.[0-9x]+(.*)";
        Pattern r = Pattern.compile(pattern);

        for (String label : labels) {
            if (label.toLowerCase().contains("affected")) {
                productVersion = label.split("/")[1];
                break;
            }
            //check regex
            Matcher m = r.matcher(label);
            if (m.find()) {
                productVersion = label;
                break;
            }

        }
        return productVersion;
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
