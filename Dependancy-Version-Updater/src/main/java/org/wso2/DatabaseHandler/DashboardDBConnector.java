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
package org.wso2.DatabaseHandler;

import org.wso2.Constants;
import org.wso2.Model.Product;
import org.wso2.Model.ProductComponent;

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
    Connection conn;
    public DashboardDBConnector(){
        try {

            Class.forName(Constants.JDBC_DRIVER_NAME).newInstance();
            conn = DriverManager.getConnection(Constants.MYSQL_DB_URL+"/UnifiedDashboards",Constants.MYSQL_DB_USERNAME, Constants.MYSQL_DB_PASSWORD);

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

    public ArrayList<Product> getAllProducts(){
        ArrayList<Product> productList = new ArrayList<Product>();
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT(Product) from GitRepositories");
            while (rs.next()) {

                Product product = new Product(rs.getString(1));
                product.setProductComponentsList(getAllComponents(product.getProductName()));
                productList.add(product);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;

    }

    private ArrayList<ProductComponent> getAllComponents(String productName) {
        ArrayList<ProductComponent> productComponents = new ArrayList<ProductComponent>();
        Statement stmt;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT(Component),URL from GitRepositories WHERE Product="+"\""+productName+"\" AND URL IS NOT NULL");
            while (rs.next()) {
                String componentName = rs.getString("Component");
                String url = rs.getString("URL");
                if(!componentName.equals("unknown")){
                    ProductComponent productComponent = new ProductComponent(componentName, url);
                    productComponents.add(productComponent);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productComponents;
    }
    public void insertBuildStatus(int status,String productName,String componentName){
        try {
            String updateStatus ="UPDATE GitRepositories SET CurrentStatus=? WHERE Product=? AND Component=?";
            PreparedStatement preparedStatement = conn.prepareStatement(updateStatus);
            preparedStatement.setInt(1,status);
            preparedStatement.setString(2,productName);
            preparedStatement.setString(3,componentName);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
