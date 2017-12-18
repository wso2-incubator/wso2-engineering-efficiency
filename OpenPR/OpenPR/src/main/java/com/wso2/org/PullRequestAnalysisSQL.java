/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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



import java.sql.*;


public class PullRequestAnalysisSQL {

    public void getOpenPullRequest(){

        try {


            ReadConfigureFile credentials= new ReadConfigureFile();
            Connection connectDB=DriverManager.getConnection(credentials.getDatabaseConn(),credentials.getUser(),credentials.getPassword()); //Get a connection to open-pr-requests
            Statement create=connectDB.createStatement(); //create a statement object




            String getOpenPRquery = "select distinct product.product,product.RepoName,OpenPR.RepoUrl,OpenPR.GitId,OpenPR.pullUrl,OpenPR.state,OpenPR.NoOfHours,(OpenPR.NoOfHours*0.0416667) as OpenDays,(OpenPR.NoOfHours*0.00595238) as OpenWeeks,(OpenPR.NoOfHours*0.00136986) as OpenMonths,(OpenPR.NoOfHours*0.000114155) as OpenYears from product,OpenPR where product.RepoName=OpenPR.RepoName ;" ;




            String DeleteTable="delete from RetrieveOpenPR"; //Delete RetrieveOpenPR table
            PreparedStatement proceed=connectDB.prepareStatement(DeleteTable,Statement.RETURN_GENERATED_KEYS);
            proceed.executeUpdate();

            //Execution of sql query
            ResultSet result=create.executeQuery(getOpenPRquery);



            //Process the result set
            while(result.next())
            {
                String product=result.getString("product.product");
                System.out.println("Product: "+product);

                String repoName=result.getString("product.RepoName");
                System.out.println("Repository Name: "+repoName);

                String repoUrl=result.getString("OpenPR.RepoUrl");
                System.out.println("Repository URL: "+repoUrl);

                String gitId=result.getString("OpenPR.GitId");
                System.out.println("Pull Request Made by: "+gitId);

                String prUrl=result.getString("OpenPR.PullUrl");
                System.out.println("Pull request URL "+prUrl);

                String state=result.getString("OpenPR.state");
                System.out.println("Pull request URL "+state);

                int noOfHours=result.getInt("OpenPR.NoOfHours");
                System.out.println("Pull request in open state(hours): "+noOfHours);

                int openDays=result.getInt("OpenDays");
                System.out.println("Pull request in open state(days) : "+openDays);
                
                int openWeeks=result.getInt("OpenWeeks");
                System.out.println("Pull request in open state(days) : "+openDays);

                int openMonths=result.getInt("OpenMonths");
                System.out.println("Pull request in open state(weeks) : "+openWeeks);

                int openYears=result.getInt("OpenYears");
                System.out.println("Pull request in open state(years) : "+openYears);

                //Insert into RetrieveOpenPR table
                proceed.executeUpdate("insert into RetrieveOpenPR(product,RepoName, RepoUrl,GitId,pullUrl,OpenHours,OpenDays,OpenWeeks,state,OpenMonths,OpenYears) values('"+product+"','"+repoName+"','"+repoUrl+"','"+gitId+"','"+prUrl+"','"+noOfHours+"','"+openDays+"','"+openWeeks+"','"+state+"','"+openMonths+"','"+openYears+"')");


                System.out.println();


            }




        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}




