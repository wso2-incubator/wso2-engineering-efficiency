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

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class SqlHandler {
    private final static Logger logger = Logger.getLogger(SqlHandler.class);
    // queries
    private final static String GET_REPOS = "SELECT * from UnifiedDashboards.JNKS_COMPONENTPRODUCT";
    private static SqlHandler sqlHandler = null;
    private static BasicDataSource connectionPool = null;


    private SqlHandler(String databaseUrl, String databaseUser, String databasePassword) {
        if (connectionPool == null) {
            connectionPool = new BasicDataSource();
            connectionPool.setUsername(databaseUser);
            connectionPool.setPassword(databasePassword);
            connectionPool.setUrl(databaseUrl);
            connectionPool.setInitialSize(4);

            logger.info("Connected to the MySQL database");
        }

    }


    public static SqlHandler getHandler(String databaseUrl, String databaseUser, String databasePassword) {
        if (sqlHandler == null) {
            sqlHandler = new SqlHandler(databaseUrl, databaseUser, databasePassword);
        }
        return sqlHandler;
    }


    /**
     * Execute sql query and get result set
     *
     * @return - query result
     */
    public HashMap<String, ArrayList<String>> getProductVsRepos() {
        ResultSet resultSet;
        Statement statement = null;
        HashMap<String, ArrayList<String>> productRepoMap = new HashMap<>();
        try {
            Connection con = connectionPool.getConnection();
            statement = con.createStatement();
            resultSet = statement.executeQuery(GET_REPOS);

            while (resultSet.next()) {
                String product = resultSet.getString(1);
                String repo = resultSet.getString(2);

                ArrayList<String> repoList = productRepoMap.get(product);
                if (repoList == null) {
                    repoList = new ArrayList<>();
                    repoList.add(repo);
                    productRepoMap.put(product, repoList);
                } else {
                    repoList.add(repo);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception while executing the query");
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException ex) {
                logger.error("The statement is not closed properly");
            }
        }
        return productRepoMap;
    }


}
