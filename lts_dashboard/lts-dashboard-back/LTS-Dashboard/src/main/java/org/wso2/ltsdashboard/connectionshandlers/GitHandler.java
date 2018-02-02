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

package org.wso2.ltsdashboard.connectionshandlers;
/*
 * The interface for Git handler
 */

import com.google.gson.JsonArray;

public interface GitHandler {
    /**
     * retrive json array of objects from git with default mediatype application/json
     *
     * @param url - rest enpoint with queries
     * @return - json array of git objects
     */
    JsonArray getJSONArrayFromGit(String url);


    /**
     * retrieve json array of objects from git with custom mediatypes
     *
     * @param url       - rest endpint with queries
     * @param mediaType - custome mediatype
     * @return - json array of git objects
     */
    JsonArray getJSONArrayFromGit(String url, String mediaType);
}
