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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * TODO:Class level comment
 */
public class DashboardDBConnector {
    Connection conn;
    public DashboardDBConnector(){


        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3308/UnifiedDashboards","root","");

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
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT(Product) from JNKS_COMPONENTPRODUCT");
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
            ResultSet rs = stmt.executeQuery("SELECT DISTINCT(Component) from JNKS_COMPONENTPRODUCT WHERE Product="+"\""+productName+"\"");
            while (rs.next()) {
                String componentName = rs.getString(1);
                ProductComponent productComponent = new ProductComponent(componentName, Constants.GIT_URL_PREFIX+componentName+".git");
                productComponents.add(productComponent);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productComponents;
    }

}
