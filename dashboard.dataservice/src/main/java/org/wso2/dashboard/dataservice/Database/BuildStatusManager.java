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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.dashboard.dataservice.Database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.dataservice.Constants;
import org.wso2.dashboard.dataservice.FileHandler.ConfigFileReader;
import org.wso2.dashboard.dataservice.Model.BuildStat;
import org.wso2.dashboard.dataservice.Model.ProductArea;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Contains methods for connecting to the database
 */
public class BuildStatusManager {

    private static final Log log = LogFactory.getLog(BuildStatusManager.class);
    private HikariDataSource hikariDataSource;

    public BuildStatusManager() {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3308/DependencyUpdateDB");
        config.setUsername(ConfigFileReader.getMysqlUsername());
        config.setPassword(ConfigFileReader.getMysqlPassword());
        hikariDataSource = new HikariDataSource(config);
    }

    /**
     * Retrieves build statistics from the database for a given time range and component name
     *
     * @param componentName name of the component
     * @param startTime     starting time
     * @param endTime       ending time
     * @return Array List of Build statistics belong to that component, time range
     */
    public ArrayList<BuildStat> getBuildStats(String componentName, long startTime, long endTime) {

        ArrayList<BuildStat> productList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "SELECT * FROM ComponentBuildStatistics WHERE Component = ?" +
                " AND BuildTime BETWEEN ? AND ? ORDER BY BuildTime DESC";
        try {
            connection = hikariDataSource.getConnection();

            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, componentName);
            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(startTime));
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(endTime));

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int status = resultSet.getInt("Status");
                BigDecimal timeStamp = resultSet.getBigDecimal("BuildTime");
                BuildStat buildStat = new BuildStat(status, timeStamp);
                productList.add(buildStat);
            }

        } catch (SQLException e) {
            log.error("Error occurred while connecting to the MYSQL database ", e);
        } catch (UnsupportedOperationException e) {
            log.info("Unsupported ", e);
        } finally {
            closeConnectionAttributes(preparedStatement, connection, resultSet);

        }

        return productList;

    }

    /**
     * Method for closing attributes created with a database connection
     *
     * @param preparedStatement prepared statement object used for executing a query
     * @param connection        SQL Connection object
     * @param resultSet         result set object with set of results
     */
    private void closeConnectionAttributes(PreparedStatement preparedStatement
            , Connection connection, ResultSet resultSet) {

        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Error occurred while closing database connection attributes ", e);
        }
    }

    /**
     * Method for retrieving Product areas from database
     *
     * @return Array list of productArea objects
     */
    public ArrayList<ProductArea> getAllProductAreas() {

        ArrayList<ProductArea> productAreaList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "SELECT DISTINCT(PRODUCT) FROM PRODUCT_COMPONENT_MAP " +
                "WHERE NOT PRODUCT = 'null' AND NOT PRODUCT = 'unknown'";
        try {
            connection = hikariDataSource.getConnection();

            preparedStatement = connection.prepareStatement(selectSQL);

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                ProductArea productArea = new ProductArea(resultSet.getString(1));
                productAreaList.add(productArea);
            }

        } catch (SQLException e) {
            log.error("Error occurred while connecting to the MYSQL database ", e);
        } finally {
            closeConnectionAttributes(preparedStatement, connection, resultSet);

        }
        return productAreaList;
    }

    /**
     * Method for retrieving component names belong to a given product area
     *
     * @param productName name of product area
     * @return array list of component names
     */
    public ArrayList<String> getComponentsForArea(String productName) {

        ArrayList<String> componentList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "SELECT REPO_NAME FROM PRODUCT_COMPONENT_MAP WHERE PRODUCT =?";
        try {
            connection = hikariDataSource.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, productName);

            // execute select SQL statement
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                componentList.add(resultSet.getString("REPO_NAME"));
            }

        } catch (SQLException e) {
            log.error("Error occurred while connecting to the MYSQL database ", e);
        } finally {
            closeConnectionAttributes(preparedStatement, connection, resultSet);

        }
        return componentList;
    }

    /**
     * This method determines the fraction of successful build stats in a time range
     *
     * @param componentName name of the component
     * @param startTime     start time
     * @param endTime       end time
     * @return fraction of successful build stats. Will return 0 if no build stats found
     */
    public double getComponentScore(String componentName, long startTime, long endTime) {

        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "SELECT Status FROM ComponentBuildStatistics " +
                "WHERE Component = ? AND BuildTime BETWEEN ? AND ? ";
        try {
            connection = hikariDataSource.getConnection();
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, componentName);
            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(startTime));
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(endTime));
            // execute select SQL statement
            resultSet = preparedStatement.executeQuery();
            int score = 0;
            int buildStatsCount = 0;
            while (resultSet.next()) {
                int status = resultSet.getInt("Status");
                if (status == Constants.BUIlD_SUCCESS_CODE) {
                    score += 1;
                }
                buildStatsCount += 1;
            }
            if (buildStatsCount != 0) {
                return score * 1.0 / buildStatsCount;
            }

        } catch (SQLException e) {
            log.error("Error occurred while connecting to the MYSQL database ", e);
        } finally {
            closeConnectionAttributes(preparedStatement, connection, resultSet);

        }
        return 0;
    }
}
