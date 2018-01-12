package org.wso2.ltsdashboard.helpers;

import com.google.gson.JsonArray;
import org.wso2.ltsdashboard.connectionshandlers.GitHandler;

public class GitGraphQLHandler {
    public static  String baseURL = "https://api.github.com/graphql";
    public static String gitToken = "8bca47bae8a4dd1009070791dba5abbc1f453c71";

    public JsonArray getMilestoneData(String owner,String repository){
        GitHandler gitHandler = new GitHandler(gitToken);
        String response = gitHandler.postJSONObjectString()
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