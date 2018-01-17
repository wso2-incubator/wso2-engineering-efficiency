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

import org.apache.log4j.Logger;

import java.sql.*;

public class SQLHandler {
    private final static Logger logger = Logger.getLogger(SQLHandler.class);
    private static Connection con;
    private String databaseUrl, databaseUser, databasePassword;


    public SQLHandler(String databaseUrl, String databaseUser, String databasePassword) {
        this.databaseUser = databaseUser;
        this.databaseUrl = databaseUrl;
        this.databasePassword = databasePassword;
    }


    private Connection getCon(String databaseUrl, String databaseUser, String databasePassword) {
        try {
            if (con == null) {
                con = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
                logger.info("Connected to the MySQL database");
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while connecting to the MySQL database");
        }
        return con;
    }

    /**
     * Execute sql query and get result set
     *
     * @param query - sql query
     * @return - query result
     */
    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        try {
            con = this.getCon(this.databaseUrl, this.databaseUser, this.databasePassword);
            Statement statement = con.createStatement();
            resultSet = statement.executeQuery(query);
        } catch (SQLException e) {
            logger.error("SQL Exception while executing the query");
        }
        return resultSet;
    }

}
