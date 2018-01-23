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

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class SqlHandlerImplement implements SqlHandler {
    private final static Logger logger = Logger.getLogger(SqlHandlerImplement.class);
    private static SqlHandlerImplement sqlHandler = null;
    private Connection con = null;


    private SqlHandlerImplement(String databaseUrl, String databaseUser, String databasePassword) {
        try {
            if (this.con == null) {
                con = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
                logger.info("Connected to the MySQL database");
            }
        } catch (SQLException e) {
            logger.info("SQL Exception while connecting to the MySQL database");
        }
    }


    public static SqlHandlerImplement getHandler(String databaseUrl, String databaseUser, String databasePassword) {
        if (sqlHandler == null) {
            sqlHandler = new SqlHandlerImplement(databaseUrl, databaseUser, databasePassword);
        }
        return sqlHandler;
    }


    /**
     * Execute sql query and get result set
     *
     * @param query - sql query
     * @return - query result
     */
    @Override
    public ResultSet executeQuery(String query) {
        ResultSet resultSet = null;
        Statement statement = null;
        try {
            statement = this.con.createStatement();
            resultSet = statement.executeQuery(query);
            statement.close();
        } catch (SQLException e) {
            logger.error("SQL Exception while executing the query");
        } finally {
            try {
                if(statement!=null) {
                    statement.close();
                }
            }catch (SQLException e){
                logger.error("SQL Exception while closing the statement");
            }
        }

        return resultSet;
    }

}
