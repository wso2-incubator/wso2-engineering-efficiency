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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Connector to Local Database
 */
public class LocalDBConnector {

    private int getProductID(String productName) throws SQLException {
        Connection connection = DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT ProductID from Products WHERE ProductName=" + "\"" + productName + "\"");
        try{
            if (!resultSet.next()) {
                insertProduct(productName);
                return getProductID(productName);
            } else {

                return Integer.parseInt(resultSet.getString(1));
            }
        }
        finally {
            resultSet.close();
            statement.close();
            connection.close();
        }


    }

    private void insertProduct(String productName) throws SQLException {

        String sql = "INSERT INTO Products(ProductName) VALUES(?)";

        Connection connection =DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
        PreparedStatement prepareStatement=connection.prepareStatement(sql);
        try{
            prepareStatement.setString(1, productName);
            prepareStatement.executeUpdate();
        }
        finally {
            prepareStatement.close();
            connection.close();
        }

    }
}


