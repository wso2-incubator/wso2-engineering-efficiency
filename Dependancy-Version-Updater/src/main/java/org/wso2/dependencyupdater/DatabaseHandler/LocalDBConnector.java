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

import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.Component;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Connector to Local Database
 */
public class LocalDBConnector {

    /**
     * @param component
     * @param timeStamp
     */
    public static void insertBuildStatus(Component component, long timeStamp) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String insertSql = "INSERT INTO ComponentBuildStatistics(Component,BuildTime,Status)  VALUES (?,?,?)";

        try {
            try {
                connection = DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
                preparedStatement = connection.prepareStatement(insertSql);
                preparedStatement.setString(1, component.getName());
                preparedStatement.setBigDecimal(2, BigDecimal.valueOf(timeStamp));
                preparedStatement.setInt(3, component.getStatus());
                preparedStatement.execute();
            } catch (SQLException e) {
                //TODO
            }
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                //TODO
            }

        }

    }

    public static ArrayList<Component> getAllComponents() {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String selectSql = "select REPO_NAME,REPO_URL from PRODUCT_COMPONENT_MAP ";
        ArrayList<Component> components = new ArrayList<Component>();

        try {

            connection = DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
            preparedStatement = connection.prepareStatement(selectSql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Component component = new Component(resultSet.getString(1), resultSet.getString(2));
                components.add(component);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, resultSet);

        }
        return components;

    }

    private static void closeConnection(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {

        try {
            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int getLatestBuild(Component component) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        String selectSql = "select * from ComponentBuildStatistics where Component=? GROUP BY BuildTime DESC limit 1";

        try {
            connection = DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, component.getName());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return 2;
            } else {
                return Integer.parseInt(resultSet.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection, preparedStatement, resultSet);

        }
        return 2;

    }

}


