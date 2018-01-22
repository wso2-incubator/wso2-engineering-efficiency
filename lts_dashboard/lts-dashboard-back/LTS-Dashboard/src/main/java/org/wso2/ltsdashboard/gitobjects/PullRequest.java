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

package org.wso2.ltsdashboard.gitobjects;/*
 * TODO - comment class work
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PullRequest {
    private JsonArray feautures;
    private String title;

    public PullRequest(JsonObject event) {
        JsonObject pr = this.getPr(event);
        this.feautures = extractFeatures(pr.get("body").toString());
        this.title = pr.get("title").toString();
    }

    public JsonArray getFeautures() {
        return feautures;
    }

    /**
     * Get the title of the pr
     * @return - title as a array
     */
    public JsonArray getTitle() {
        JsonArray testJsonArray = new JsonArray();
        testJsonArray.add(this.title
                .replace("\"","")
                .replace("\\","")
        );

        return testJsonArray;
    }


    private JsonObject getPr(JsonObject event) {
        return event.get("source").getAsJsonObject().get("issue").getAsJsonObject();
    }


    /**
     * Extract features from PR body
     * @param prBody - the PR body as a string
     * @return - feature list as json array
     */
    private JsonArray extractFeatures(String prBody) {
        String[] marketStringPart = prBody.split("## Marketing");
        if (marketStringPart.length > 1) {
            marketStringPart = marketStringPart[1].split("## Automation tests");
        }
        String marketingString = marketStringPart[0];
        String[] features = marketingString.split("\r\n");

        JsonArray featureArray = new JsonArray();
        for (String feature : features) {
            if (feature.length() > 0) {
                featureArray.add(feature);
            }
        }

        return featureArray;
    }


}
