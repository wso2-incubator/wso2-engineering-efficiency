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
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class GitHandler {
    final static Logger logger = Logger.getLogger(GitHandler.class);
    String gitToken = null;

    public GitHandler(String gitToken){
        this.gitToken = gitToken;
    }


    /**
     * Retrive json array of objects from github
     * @param url - rest endpoint with queries
     * @param mediaType - Accept media type for the request header
     * @return json array of git objects
     */
    private JsonArray getJSONArrayBasic(String url, String mediaType){
        String responseString = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet requset = new HttpGet(url);
        requset.addHeader("Accept", mediaType);
        requset.addHeader("Authorization", "Bearer "+gitToken);
        HttpResponse response = null;
        JsonElement element = null;
        JsonArray jsonArray = new JsonArray();
        boolean containsNext = true;

        try {
            response = httpClient.execute(requset);
            logger.info("Request successful for " + url);
            responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
            element = new JsonParser().parse(responseString);
            JsonArray firstArray = element.getAsJsonArray();
            jsonArray.addAll(firstArray);
        }
        catch (IOException e){
            logger.error("Cannot connect to get receive data");
        }

        // handle pagination
        while (containsNext){
            if(response.containsHeader("Link")){
                Header[] linkHeader = response.getHeaders("Link");
                Map<String, String> linkMap = this.splitLinkHeader(linkHeader[0].getValue());

                try{
                    HttpGet requestForNext = new HttpGet(linkMap.get("next"));
                    requestForNext.addHeader("Accept", mediaType);
                    requestForNext.addHeader("Authorization", "Bearer " + gitToken);
                    response = httpClient.execute(requestForNext);

                    String repo_json_next = EntityUtils.toString(response.getEntity(), "UTF-8");
                    JsonElement jelementNext = new JsonParser().parse(repo_json_next);
                    JsonArray jarrNext = jelementNext.getAsJsonArray();

                    for(JsonElement jsonElement: jarrNext){
                        jsonArray.add(jsonElement.getAsJsonObject());
                    }
                }catch (IOException e){
                    logger.error("The request is failed for "+url+" : page =>"+linkMap.get("next"));
                }catch (NullPointerException e){
                    logger.error(("No data received from http request "+url));
                    containsNext=false;
                }
            }
            else {
                containsNext=false;
            }
        }

        return jsonArray;
    }






    /**
     * retrive json array of objects from git with default mediatype application/json
     * @param url - rest enpoint with queries
     * @return - json array of git objects
     */
    public JsonArray getJSONArrayFromGit(String url){
        JsonArray jArray = this.getJSONArrayBasic(url,"application/json");
        return jArray;
    }

    /**
     * retrieve json array of objects from git with custom mediatypes
     * @param url - rest endpint with queries
     * @param mediaType - custome mediatype
     * @return - json array of git objects
     */
    public JsonArray getJSONArrayFromGit(String url, String mediaType){
        JsonArray jArray = this.getJSONArrayBasic(url,mediaType);
        return jArray;
    }


    /**return the page changes
     * @param header - the linkHeader of the response
     * @return map containing next page
     */

    private Map<String, String> splitLinkHeader(String header){
        String[] parts = header.split(",");
        Map <String, String> map = new HashMap<String, String>();
        for(int i = 0; i < parts.length; i++){
            String[] sections = parts[i].split(";");
            String PaginationUrl = sections[0].replaceFirst("<(.*)>", "$1");
            String urlPagChange =  PaginationUrl.trim();
            String name = sections[1].substring(6, sections[1].length() - 1);
            map.put(name, urlPagChange);
        }

        return map;
    }


    /**
     *
     * @param url - request url
     * @param query - post body as string
     * @return the body of response as string
     */
    public String postJSONObjectString(String url, String query){

        String responseString = null;
        HttpResponse response = null;
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        post.setHeader("Authorization", "Bearer "+gitToken);
        post.addHeader("Content_Type", "application/json");
        post.setHeader("Connection", "keep-alive");

        try{
            HttpEntity entity = new ByteArrayEntity(query.getBytes("UTF-8"));
            post.setEntity(entity);
            response = client.execute(post);
            responseString = EntityUtils.toString(response.getEntity(),"UTF-8");
            logger.info("POST request successful for "+url);

        }catch (UnsupportedEncodingException e){
            logger.info("POST body cannot be encoded");
        }
        catch (IOException e){
            logger.info("POST request unsuccessful for "+url);
        }


        return responseString;
    }


}
