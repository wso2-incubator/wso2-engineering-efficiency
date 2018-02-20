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
import org.wso2.dependencyupdater.Model.Product;

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

    Connection connection;

    public LocalDBConnector() {

        try {
            Class.forName(Constants.JDBC_DRIVER_NAME).newInstance();
            connection = DriverManager.getConnection(Constants.MYSQL_DB_URL + "/DependencyUpdateDB", Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean insertBuildData(Product product, int status) {

        String sql = "INSERT INTO BuildData(ProductID,Status,Timestamp) VALUES (?,?,?)";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        try {
            int productId = getProductID(product.getProductName());
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, status);
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(timestamp.getTime()));
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private int getProductID(String productName) throws SQLException {

        Statement stmt;
        stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT ProductID from Products WHERE ProductName=" + "\"" + productName + "\"");
        if (!rs.next()) {
            insertProduct(productName);
            return getProductID(productName);
        } else {
            return Integer.parseInt(rs.getString(1));
        }
    }

    private void insertProduct(String productName) {

        String sql = "INSERT INTO Products(ProductName) VALUES(?)";
        try {
            PreparedStatement prepareStatement = connection.prepareStatement(sql);
            prepareStatement.setString(1, productName);
            prepareStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}


