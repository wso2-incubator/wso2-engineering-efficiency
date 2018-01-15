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

package org.wso2.ltsdashboard.helpers;

import com.google.gson.JsonArray;
import org.wso2.ltsdashboard.connectionshandlers.GitHandler;

public class GitGraphQLHandler {
    public static  String graphBaseUrl =null;
    public static String gitAcccessToken = null;

    public GitGraphQLHandler(String gitToken,String graphBaseUrl){
        graphBaseUrl = graphBaseUrl;
        gitAcccessToken = gitToken;
    }

    public JsonArray getMilestoneData(String owner,String repository){
        GitHandler gitHandler = new GitHandler(GitGraphQLHandler.gitAcccessToken);
//        String response = gitHandler.postJSONObjectString();

        return  new JsonArray();

    }


    private String getGraphQLQuery(String owner,String repository, int count){
        String qeury = "query {\n  " +
                            "repository(owner:\"wso2\", name:\"product-ei\") {\n " +
                                "milestones(first:2) {\n " +
                                    "edges {\n" +
                                        "node {\n " +
                                            "title\n " +
                                            "url\n  " +
                                            "issues(first:100){\n" +
                                                "edges{\n" +
                                                    "node{\n" +
                                                        "title\n" +
                                                        "\n " +
                                                    "}\n            " +
                                                "}\n          " +
                                            "}\n        " +
                                        "}\n      " +
                                    "}\n\t\t\t" +
                                    "pageInfo {\n" +
                                        "hasNextPage\n" +
                                    "}\n    " +
                                "}\n  " +
                            "}\n" +
                        "}";
        return qeury;
    }

//    private String getGraphQLQuery(String owner,String repository, int count, String lastNode){
//
//    }
}