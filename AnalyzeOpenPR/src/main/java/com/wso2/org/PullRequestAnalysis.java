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
        import java.sql.Connection;
        import java.sql.DriverManager;
        import java.sql.PreparedStatement;
        import java.sql.SQLException;
        import java.sql.Statement;
        import java.text.SimpleDateFormat;
        import java.text.ParseException;
        import java.util.*;
        import java.util.Map;
        import org.apache.http.HttpResponse;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.impl.client.CloseableHttpClient;
        import org.apache.http.impl.client.HttpClientBuilder;
        import org.apache.http.util.EntityUtils;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonElement;
        import com.google.gson.JsonObject;
        import com.google.gson.JsonParser;
        import org.apache.http.Header;


public class PullRequestAnalysis {

    private static String Tokenkey = ""; //Enter your Github token here

    public static void main(String[] args) throws  ParseException {
        try {
            getPull();
            PullRequestAnalysisSQL.getOpenPr();
                
        } catch (SQLException e) {
            e.printStackTrace();

        }

    }
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


    public static void getPull() throws ParseException, SQLException {
        String baseURL = "https://api.github.com";
        String url = "https://api.github.com/orgs/wso2/repos";
        String DbUrl="jdbc:mysql://localhost:3306/TaskOpenPulls?useSSL=false";
        String user="root"; 														//UserName of your mysql workbench
        String psw="wso2123";														 //Password of your mysql workbench


        try {

            CloseableHttpClient httpClient1 = HttpClientBuilder.create().build();
            HttpGet request1 = new HttpGet(url);
            request1.addHeader("content-type", "application/json");
            request1.addHeader("Authorization", "Bearer " + Tokenkey);
            HttpResponse resultNext = httpClient1.execute(request1);

            String repo_json = EntityUtils.toString(resultNext.getEntity(), "UTF-8");


            JsonElement jelement1 = new JsonParser().parse(repo_json);
            JsonArray jarr1 = jelement1.getAsJsonArray();

            boolean containsNext = true;

            while(containsNext){

                if (resultNext.containsHeader("Link")){

                    Header[] linkHeader = resultNext.getHeaders("Link");
                    Map<String, String> linkMap = splitLinkHeader(linkHeader[0].getValue());

                    System.out.println(linkMap.get("next"));

                    try{
                        HttpGet requestForNext = new HttpGet(linkMap.get("next"));
                        requestForNext.addHeader("content-type", "application/json");
                        requestForNext.addHeader("Authorization", "Bearer " + Tokenkey);
                        HttpResponse httpResponse = resultNext = httpClient1.execute(requestForNext);

                        String repo_json_next = EntityUtils.toString(resultNext.getEntity(), "UTF-8");
                        JsonElement jelementNext = new JsonParser().parse(repo_json_next);
                        JsonArray jarrNext = jelementNext.getAsJsonArray();


                        jarr1.addAll(jarrNext);
                    }
                    catch(Exception e){
                        containsNext = false;
                    }




                } else {
                    containsNext = false;
                }

            }




            Connection con=DriverManager.getConnection(DbUrl,user,psw);
            String DeleteTable="delete from OpenPulls";
            PreparedStatement st1=con.prepareStatement(DeleteTable,Statement.RETURN_GENERATED_KEYS);
            st1.executeUpdate();








            for (int i = 0; i <jarr1.size(); i++) {
                JsonObject jo1 = (JsonObject) jarr1.get(i);

                String RepoName = jo1.get("name").toString();
                RepoName = RepoName.substring(1, RepoName.length()-1);
                System.out.println("Name of the repository   :"+RepoName);

                String RepoUrl = jo1.get("html_url").toString();
                RepoUrl = RepoUrl.substring(1, RepoUrl.length()-1);
                System.out.println("Repository URL   :"+RepoUrl);





                CloseableHttpClient httpClient2 = HttpClientBuilder.create().build();
                HttpGet request2 = new HttpGet(baseURL + "/repos/wso2/"+RepoName+"/pulls");
                request2.addHeader("Authorization", "Bearer " + Tokenkey);
                HttpResponse result2 = httpClient2.execute(request2);
                String pulls_json = EntityUtils.toString(result2.getEntity(), "UTF-8");

                

                JsonElement jelement2 = new JsonParser().parse(pulls_json);
                JsonArray jarr2 = jelement2.getAsJsonArray();




                int NoOfPR=jarr2.size();


                if(NoOfPR>0){

                    for (int y = 0; y < jarr2.size(); y++) {
                        JsonObject jo2 = (JsonObject) jarr2.get(y);



                        String  GitId = jo2.getAsJsonObject("user").get("login").toString();
                        GitId= GitId.substring(1, GitId.length() - 1);
                        System.out.println("PR created by= "+GitId);

                        String  pullsUrl = jo2.get("html_url").toString();
                        pullsUrl = pullsUrl.substring(1, pullsUrl.length() - 1);
                        System.out.println("PR url= "+pullsUrl);


                        String pullsCreatedOn = jo2.get("created_at").toString();
                        pullsCreatedOn = pullsCreatedOn.substring(1, pullsCreatedOn.length()-1);

                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

                        String CurrentDate = df.format(new Date());



                        Date OpenPullPr = null;
                        Date Today = null;



                        OpenPullPr = df.parse(pullsCreatedOn);
                        Today  = df.parse(CurrentDate);

                        long diff = (Today.getTime() - OpenPullPr.getTime());

                        long numberOfHours =(int) ((diff *2.7778e-7)); // milliseconds to hours conversion






                        String sql2="insert into OpenPulls(RepoName,RepoUrl,NoofOpenPulls,GitId,PullUrl,NoOfHours) values(?,?,?,?,?,?);";


                        PreparedStatement st2=con.prepareStatement(sql2,Statement.RETURN_GENERATED_KEYS);
                        st2.setString(1,RepoName);
                        st2.setString(2,RepoUrl);
                        st2.setInt(3, NoOfPR);
                        st2.setString(4,GitId );
                        st2.setString(5,pullsUrl);
                        st2.setLong(6, numberOfHours);



                        st2.executeUpdate();
                    }
                }
                else
                {

                    String sql3="insert into OpenPulls(RepoName,RepoUrl,NoofOpenPulls,GitId,PullUrl,NoOfHours) values(?,?,?,?,?,?);";


                    PreparedStatement st3=con.prepareStatement(sql3,Statement.RETURN_GENERATED_KEYS);
                    st3.setString(1,RepoName);
                    st3.setString(2,RepoUrl);
                    st3.setInt(3, 0);
                    st3.setString(4,null);
                    st3.setString(5,null);
                    st3.setInt(6, 0);





                    st3.executeUpdate();

                }


            }


        }


        catch (IOException ex) {
            System.out.println(ex.getStackTrace());
        }

    }


}









