/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wso2.org;

import java.io.IOException;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.Header;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PullRequestAnalysis{

    private static Logger logger = LoggerFactory.getLogger(PullRequestAnalysis.class);
    //Done with pagination using split
    public static Map<String, String> splitLinkHeader(String header){
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


    public void getOpenPullRequest() throws ParseException, SQLException, IOException {
        String baseURL = "https://api.github.com";
        String initialUrl = "https://api.github.com/orgs/wso2/repos";
        ReadConfigureFile credentials= new ReadConfigureFile();

        try {



            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet initialUrlRequest = new HttpGet(initialUrl);

            /**
             * @param InitialUrlRequest
             *            GET /orgs/:org/repos
             *
             *
             */
            initialUrlRequest.addHeader("content-type", "application/json");
            initialUrlRequest.addHeader("Authorization", "Bearer "
                    + credentials.getTokenKey());
            HttpResponse responseOfReq = httpClient.execute(initialUrlRequest); // Return
            // the
            // request
            // response
            // as
            // a
            // json
            // array
            String repoJson = EntityUtils.toString(responseOfReq.getEntity(),
                    "UTF-8");

            JsonElement reposJsonElement = new JsonParser().parse(repoJson);
            JsonArray reposJsonArray = reposJsonElement.getAsJsonArray();

            /**
             * @param RepolJsonArray
             *            Used to get all repositories contents as a Json array
             *
             */

            boolean containsNext = true;
            while (containsNext) {

                if (responseOfReq.containsHeader("Link")) {

                    Header[] linkHeader = responseOfReq.getHeaders("Link");
                    Map<String, String> linkMap = splitLinkHeader(linkHeader[0]
                            .getValue());
                    
                    HttpClientUtils.closeQuietly(responseOfReq);

                    logger.info(linkMap.get("next")); // Print all the
                    // page link that
                    // traverse through

                    try {
                        HttpGet requestForNext = new HttpGet(
                                linkMap.get("next"));
                        requestForNext.addHeader("content-type",
                                "application/json");
                        requestForNext.addHeader("Authorization", "Bearer "
                                + credentials.getTokenKey());
                        
                        responseOfReq = httpClient.execute(requestForNext);
                        String repoJsonNext = EntityUtils.toString(
                                responseOfReq.getEntity(), "UTF-8");
                        JsonElement jelementNext = new JsonParser()
                        .parse(repoJsonNext);
                        JsonArray jarrNext = jelementNext.getAsJsonArray();
                        reposJsonArray.addAll(jarrNext);
                        
                        HttpClientUtils.closeQuietly(responseOfReq);
                        
                        System.out.println((jarrNext));

                    } catch (Exception e) {
                        containsNext = false;
                    }



                } else {
                    containsNext = false;
                }

            }

            Connection connectDatabase = DriverManager.getConnection(
                    credentials.getDatabaseConn(),credentials.getUser(),
                    credentials.getPassword());
            String DeleteTable = "delete from OpenPR";
            PreparedStatement deleteTable = connectDatabase.prepareStatement(
                    DeleteTable, Statement.RETURN_GENERATED_KEYS);
            deleteTable.executeUpdate();

            for (int i = 0; i < reposJsonArray.size(); i++) {
                JsonObject repos = (JsonObject) reposJsonArray.get(i);

                String repoName = repos.get("name").toString(); // RepoName
                // returns all
                // repositories
                // names of wso2
                // in GitHub
                repoName = repoName.substring(1, repoName.length() - 1);
                logger.info("Name of the repository   :" + repoName);

                String repoUrl = repos.get("html_url").toString(); // RepoUrl
                // returns
                // all
                // repositories
                // url of
                // wso2 in
                // GitHub
                repoUrl = repoUrl.substring(1, repoUrl.length() - 1);
                logger.info("Repository URL   :" + repoUrl);

                CloseableHttpClient httpClient2 = HttpClientBuilder.create()
                        .build();
                HttpGet RequestPRurl = new HttpGet(baseURL + "/repos/wso2/"
                        + repoName + "/pulls");
                RequestPRurl.addHeader("Authorization", "Bearer "
                        + credentials.getTokenKey());
                HttpResponse responseOfUrl = httpClient2.execute(RequestPRurl);
                String openPRJson = EntityUtils.toString(
                        responseOfUrl.getEntity(), "UTF-8");

                JsonElement prJsonElement = new JsonParser().parse(openPRJson);
                JsonArray prJsonArray = prJsonElement.getAsJsonArray();


                for (int y = 0; y < prJsonArray.size(); y++) {
                    JsonObject openPR = (JsonObject) prJsonArray.get(y);

                    String gitId = openPR.getAsJsonObject("user").get("login")
                            .toString(); // GitId returns GitId of the person
                    // who create the pull request
                    gitId = gitId.substring(1, gitId.length() - 1);
                    logger.info("PR created by= " + gitId);

                    String openPRUrl = openPR.get("html_url").toString(); // OpenUrl
                    // returns
                    // url
                    // of
                    // the
                    // open
                    // pull
                    // request
                    openPRUrl = openPRUrl.substring(1, openPRUrl.length() - 1);
                    logger.info(" Open PR url= " + openPRUrl);

                    String prstate = openPR.get("state").toString();
                    prstate = prstate.substring(1, prstate.length() - 1);
                    logger.info("state= " + prstate);

                    String prCreatedOn =openPR.get("created_at").toString();
                    prCreatedOn =  prCreatedOn.substring(1,
                            prCreatedOn.length() - 1);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss'Z'");
                    String currentDate = dateFormat.format(new Date());

                    Date prOpenDate = null;
                    Date today = null;
                    prOpenDate = dateFormat.parse(prCreatedOn);
                    today = dateFormat.parse(currentDate);

                    long prOpenInMilliseconds = ( today.getTime() - prOpenDate
                            .getTime());

                    long prOpenInHours = (int) ((prOpenInMilliseconds * 2.7778e-7)); // milliseconds
                    // to
                    // hours
                    // conversion

                    String insert = "insert into OpenPR(RepoName,RepoUrl,GitId,PullUrl,NoOfHours,state) values(?,?,?,?,?,?);";

                    PreparedStatement MakeInsert = connectDatabase
                            .prepareStatement(insert,
                                    Statement.RETURN_GENERATED_KEYS);
                    MakeInsert.setString(1, repoName);
                    MakeInsert.setString(2, repoUrl);
                    MakeInsert.setString(3, gitId);
                    MakeInsert.setString(4, openPRUrl);
                    MakeInsert.setLong(5, prOpenInHours);
                    MakeInsert.setString(6, prstate);

                    MakeInsert.executeUpdate();
                }

            }
        }

        catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }

    } 




}





