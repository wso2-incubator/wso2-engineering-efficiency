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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;

public class GitHandlerImplement implements GitHandler {

    private final static Logger logger = Logger.getLogger(GitHandlerImplement.class);
    private String gitToken = null;

    public GitHandlerImplement(String gitToken) {
        this.gitToken = gitToken;
    }


    /**
     * Retrive json array of objects from github
     *
     * @param url       - rest endpoint with queries
     * @param mediaType - Accept media type for the request header
     * @return json array of git objects
     */
    private JsonArray getJSONArrayBasic(String url, String mediaType) {
        String responseString;
        JsonElement element;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        request.addHeader("Accept", mediaType);
        request.addHeader("Authorization", "Bearer " + gitToken);
        HttpResponse response;
        JsonArray jsonArray = new JsonArray();
        HashMap<String, String> linkState = null;
        boolean containsNext = false;

        try {
            JsonArray firstArray;
            response = httpClient.execute(request);
            logger.info("Request successful for " + url);
            responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            element = new JsonParser().parse(responseString);
            if (element.isJsonArray()) {
                firstArray = element.getAsJsonArray();
                jsonArray.addAll(firstArray);
            } else {
                firstArray = element.getAsJsonObject().get("items").getAsJsonArray();
            }
            jsonArray.addAll(firstArray);

            if (response.getHeaders("Link").length > 0) {
                linkState = this.checkNextHeader(response.getHeaders("Link")[0].getValue());
                containsNext = linkState.containsKey("next");
            }

        } catch (IllegalStateException e) {
            logger.info("The response is empty");
        } catch (NullPointerException e) {
            logger.info("The response with bad request");
        } catch (IOException e) {
            logger.info("Cannot connect to get receive data");
        }


        // handle pagination
        while (containsNext) {

            String nextLink = linkState.get("next");
            HttpGet requestForNext = new HttpGet(nextLink);
            requestForNext.addHeader("Accept", mediaType);
            requestForNext.addHeader("Authorization", "Bearer " + gitToken);
            try {
                JsonArray jarNext;
                response = httpClient.execute(requestForNext);
                String repo_json_next = EntityUtils.toString(response.getEntity(), "UTF-8");
                JsonElement jElementNext = new JsonParser().parse(repo_json_next);

                if (jElementNext.isJsonArray()) {
                    jarNext = jElementNext.getAsJsonArray();
                } else {
                    jarNext = jElementNext.getAsJsonObject().get("items").getAsJsonArray();
                }
                jsonArray.addAll(jarNext);
                linkState = this.checkNextHeader(response.getHeaders("Link")[0].getValue());
                containsNext = linkState.containsKey("next");
                logger.info("The request successful for " + nextLink);
            } catch (IOException e) {
                logger.info("The response failed");
            }
        }


        return jsonArray;
    }


    /**
     * retrive json array of objects from git with default mediatype application/json
     *
     * @param url - rest enpoint with queries
     * @return - json array of git objects
     */
    @Override
    public JsonArray getJSONArrayFromGit(String url) {
        return this.getJSONArrayBasic(url, "application/json");

    }

    /**
     * retrieve json array of objects from git with custom mediatypes
     *
     * @param url       - rest endpint with queries
     * @param mediaType - custome mediatype
     * @return - json array of git objects
     */
    @Override
    public JsonArray getJSONArrayFromGit(String url, String mediaType) {
        return this.getJSONArrayBasic(url, mediaType);
    }


    /**
     * Check request have more pages
     *
     * @param header - String header
     * @return hashmap if header has next
     */
    private HashMap<String, String> checkNextHeader(String header) {
        HashMap<String, String> headerDetail = new HashMap<>();
        String[] parts = header.split(",");

        for (String url : parts) {
            String[] urlParts = url.split(";");
            String state = urlParts[1].split("=")[1].replace("\"", "");
            String link = urlParts[0].replace(">", "").replace("<", "")
                    .replace("%3A", ":").trim();
            headerDetail.put(state, link);
        }

        return headerDetail;
    }

}


