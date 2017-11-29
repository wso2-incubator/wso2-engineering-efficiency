package com.wso2.org;


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





        import java.sql.*;

public class PullRequestAnalysisSQL  {






    public static void main(String[] args) {

        try {
            Connection myConn=DriverManager.getConnection("jdbc:mysql://localhost:3306/TaskOpenPulls?useSSL=false","root","wso2123"); //Get a connection to employee
            Statement myst=myConn.createStatement(); //create a statement object




            String query = "select distinct product.product,product.RepoName,OpenPulls.RepoUrl,OpenPulls.GitId,OpenPulls.pullUrl,OpenPulls.NoOfHours,(OpenPulls.NoOfHours*0.0416667) as OpenDays,(OpenPulls.NoOfHours*0.00136986) as OpenMonths,(OpenPulls.NoOfHours*0.000114155) as OpenYears from product,OpenPulls where product.RepoName=OpenPulls.RepoName ;" ;




            String DeleteTable="delete from RetrievePullData";
            PreparedStatement st1=myConn.prepareStatement(DeleteTable,Statement.RETURN_GENERATED_KEYS);
            st1.executeUpdate();

            //Execution of sql query
            ResultSet result=myst.executeQuery(query);



            //Process the result set
            while(result.next())
            {
                String product=result.getString("product.product");
                System.out.println("Product: "+product);

                String RepoName=result.getString("product.RepoName");
                System.out.println("Repository Name: "+RepoName);

                String RepoUrl=result.getString("OpenPulls.RepoUrl");
                System.out.println("Repository URL: "+RepoUrl);

                String GitId=result.getString("OpenPulls.GitId");
                System.out.println("Pull Request Made by: "+GitId);

                String PullUrl=result.getString("OpenPulls.PullUrl");
                System.out.println("Pull request URL "+PullUrl);

                int NoOfHours=result.getInt("OpenPulls.NoOfHours");
                System.out.println("Pull request in open state(hours): "+NoOfHours);

                int OpenDays=result.getInt("OpenDays");
                System.out.println("Pull request in open state(days) : "+OpenDays);

                int OpenMonths=result.getInt("OpenMonths");
                System.out.println("Pull request in open state(months) : "+OpenMonths);

                int OpenYears=result.getInt("OpenYears");
                System.out.println("Pull request in open state(years) : "+OpenYears);


                st1.executeUpdate("insert into RetrievePullData(product,RepoName, RepoUrl,GitId,pullUrl,OpenHours,OpenDays,OpenMonths,OpenYears) values('"+product+"','"+RepoName+"','"+RepoUrl+"','"+GitId+"','"+PullUrl+"','"+NoOfHours+"','"+OpenDays+"','"+OpenMonths+"','"+OpenYears+"')");





                System.out.println();


            }




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}


