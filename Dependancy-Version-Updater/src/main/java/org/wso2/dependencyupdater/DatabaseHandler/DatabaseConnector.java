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
package org.wso2.dependencyupdater.DatabaseHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Connector to Local Database
 */
public class DatabaseConnector {

    private static final Log log = LogFactory.getLog(DatabaseConnector.class);

    /**
     * Method to insert build status to database
     *
     * @param component component object corresponding to build status
     * @param timeStamp unix timestamp of update operation
     */
    public static void insertBuildStatus(Component component, long timeStamp) {

        String insertSql = "INSERT INTO ComponentBuildStatistics(Component,BuildTime,Status)VALUES(?,?,?)";
        PreparedStatement preparedStatement = null;

        try {
            ComponentDatabase componentDatabase = ComponentDatabase.getInstance();
            Connection connection = componentDatabase.getConnection();
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, component.getName());
            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(timeStamp));
            preparedStatement.setInt(3, component.getStatus());
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Problem occurred in Database Connection", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, null);
        }
    }

    /**
     * This method returns all the components from database
     *
     * @return list of components
     */
    public static ArrayList<Component> getAllComponents() {

        String selectSql = "SELECT REPO_NAME,REPO_URL FROM PRODUCT_COMPONENT_MAP";
        ArrayList<Component> components = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ComponentDatabase componentDatabase = ComponentDatabase.getInstance();
            Connection connection = componentDatabase.getConnection();
            preparedStatement = connection.prepareStatement(selectSql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Component component = new Component(resultSet.getString("REPO_NAME"), resultSet.getString("REPO_URL"));
                components.add(component);
            }
        } catch (SQLException e) {
            log.error("Problem occurred when connecting to  database ", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, resultSet);

        }
        return components;

    }

    /**
     * Method for retrieving the latest available build status data for a particular component
     *
     * @param component Component object
     * @return integer indicating the latest build status (0 - Build Failed, 1 - Build Successful, 2 - Not available)
     */
    public static int getLatestBuild(Component component) {

        String selectSql = "SELECT * FROM ComponentBuildStatistics WHERE Component=? GROUP BY BuildTime DESC LIMIT 1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            ComponentDatabase componentDatabase = ComponentDatabase.getInstance();
            Connection connection = componentDatabase.getConnection();
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, component.getName());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return Constants.BUILD_NOT_AVAILABLE_CODE;
            } else {
                return Integer.parseInt(resultSet.getString("Status"));
            }

        } catch (SQLException e) {
            log.error("Problem occurred in Database Connection", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, resultSet);

        }
        return Constants.BUILD_NOT_AVAILABLE_CODE;

    }

    /**
     * Method used for closing database attributes such as prepared statement or result set
     *
     * @param preparedStatement prepared statement object that need closing
     * @param resultSet         result set object that need closing
     */
    private static void closeDatabaseAttributes(PreparedStatement preparedStatement, ResultSet resultSet) {

        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            log.error("Problem occurred in closing database attributes", e);
        }
    }
}


