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
 * The data about a PR
 */

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PullRequest {
    private JsonArray features;

    public PullRequest(JsonObject event) {
        JsonObject pr = this.getPr(event);
        this.features = extractFeatures(pr.get("body").toString());
    }

    /**
     * Get the marketing features
     *
     * @return - features as array
     */
    public JsonArray getFeatures() {
        return features;
    }


    private JsonObject getPr(JsonObject event) {
        return event.get("source").getAsJsonObject().get("issue").getAsJsonObject();
    }


    /**
     * Extract features from PR body
     *
     * @param prBody - the PR body as a string
     * @return - feature list as json array
     */
    private JsonArray extractFeatures(String prBody) {
        String[] marketStringPart = prBody.split("## Marketing");
        JsonArray featureArray = new JsonArray();
        if (marketStringPart.length > 1) {
            marketStringPart = marketStringPart[1].split("##");

            String marketingString = marketStringPart[0].replace("\\r\\n", "%%%%");
            String[] features = marketingString.split("%%%%");


            for (String feature : features) {
                if (feature.length() > 0) {
                    featureArray.add(feature);
                }
            }
        }


        return featureArray;
    }


}
