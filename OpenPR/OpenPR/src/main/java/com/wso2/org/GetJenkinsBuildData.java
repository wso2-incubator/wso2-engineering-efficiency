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

public class GetJenkinsBuildData {


        public void GetJenkinsData(){

            try {


                ReadConfigureFile credentials= new ReadConfigureFile();
                Connection connectDB=DriverManager.getConnection(credentials.getDatabaseConn(),credentials.getUser(),credentials.getPassword());


                Connection connectJenkinsDb=DriverManager.getConnection(credentials.getJenkinsDb(),credentials.getJenkinsUserName(),credentials.getJenkinsPassword());
                Statement create=connectJenkinsDb.createStatement(); //create a statement object




                String getJenkinsData = "select * from JNKS_COMPONENTPRODUCT ;" ;




                String DeleteTable="delete from product"; //Delete RetrieveOpenPR table
                PreparedStatement proceed=connectDB.prepareStatement(DeleteTable,Statement.RETURN_GENERATED_KEYS);
                proceed.executeUpdate();

                //Execution of sql query
                ResultSet result=create.executeQuery(getJenkinsData);



                //Process the result set
                while(result.next())
                {
                    String product=result.getString("JNKS_COMPONENTPRODUCT.product");
                    System.out.println("Product: "+product);

                    String repoName=result.getString("JNKS_COMPONENTPRODUCT.Component");
                    System.out.println("Repository Name: "+repoName);


                    //Insert into RetrieveOpenPR table
                    proceed.executeUpdate("insert into product(Product,RepoName) values('"+product+"','"+repoName+"')");


                    System.out.println();


                }




            } catch (Exception e) {
                e.printStackTrace();

            }

        }

    }






