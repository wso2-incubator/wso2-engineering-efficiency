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

package org.wso2.ltsdashboard;/*
 * TODO - comment class work
 */

import com.google.gson.JsonArray;

import java.util.ArrayList;

public interface Processor {

    /**
     * Get the product list
     *
     * @return ArrayList of Product names
     */

    public JsonArray getProductList();

    /**
     * Get the labels for particular product
     *
     * @param productName -product name
     * @return - json array of Label names
     */
    public JsonArray getVersions(String productName);

    /**
     * Get issues to give product name and label
     *
     * @param productName - Product name that map to database
     * @param label       - label extracted
     * @return a json array of issue details
     */
    public JsonArray getIssues(String productName, String label);

    /**
     * Get Milestone features extracted from git
     *
     * @param issueUrlList - issue Url list from front end
     * @return Feature Set as a json array
     */
    public JsonArray getMilestoneFeatures(JsonArray issueUrlList);
}
