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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Connector to Dashboard Database
 */
public class DashboardDBConnector {

    private static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(Constants.MYSQL_DB_URL + "/UnifiedDashboards", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
    }

    private static ArrayList<Component> getAllComponents(String productName) throws SQLException {

        ArrayList<Component> components = new ArrayList<Component>();
        Statement statement = null;
        ResultSet resultSet = null;
        Connection connection =null;
        try {
            connection = getConnection();
            statement = connection.createStatement();

            resultSet = statement.executeQuery("SELECT DISTINCT(Component),URL from GitRepositories WHERE Product=" + "\"" + productName + "\" AND URL IS NOT NULL");
            while (resultSet.next()) {
                String componentName = resultSet.getString("Component");
                String url = resultSet.getString("URL");
                if (!componentName.equals("unknown")) {
                    Component component = new Component(componentName);
                    components.add(component);
                }
            }


        } finally {
            statement.close();
            resultSet.close();
            connection.close();
        }

        return components;
    }

    public void insertBuildStatus(int status, String productName, String componentName) throws SQLException {

        Connection connection = getConnection();
        String updateStatus = "UPDATE GitRepositories SET CurrentStatus=? WHERE Product=? AND Component=?";
        PreparedStatement preparedStatement = connection.prepareStatement(updateStatus);;

        try {

            preparedStatement.setInt(1, status);
            preparedStatement.setString(2, productName);
            preparedStatement.setString(3, componentName);
            preparedStatement.executeUpdate();

        } finally {
            preparedStatement.close();

        }

    }

}
