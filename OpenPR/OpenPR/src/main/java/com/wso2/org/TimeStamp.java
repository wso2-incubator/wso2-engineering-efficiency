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
import java.text.SimpleDateFormat;
import java.util.Date;

class Timestamp {

    public static void getTimeStamp(){
        try {

            ReadConfigureFile credentials= new ReadConfigureFile();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String currentTimestamp = df.format(new Date());

            Connection myConn=DriverManager.getConnection(credentials.getDatabaseConn(),credentials.getUser(),credentials.getPassword()); //Get connection


            String DeleteTable="delete from TimeStamp";
            PreparedStatement st1=myConn.prepareStatement(DeleteTable,Statement.RETURN_GENERATED_KEYS);
            st1.executeUpdate();

            String InsertTimeStamp="insert into TimeStamp(CurrentTimestamp) values(?);";
            PreparedStatement st2=myConn.prepareStatement(InsertTimeStamp,Statement.RETURN_GENERATED_KEYS);
            st2.setString(1,currentTimestamp);
            st2.executeUpdate();



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}