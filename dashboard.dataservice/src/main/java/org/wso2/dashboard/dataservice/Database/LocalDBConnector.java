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

import org.wso2.dashboard.dataservice.Constants;
import org.wso2.dashboard.dataservice.Model.BuildStat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * TODO:Class level comment
 */
public class LocalDBConnector {

    public static ArrayList<BuildStat> getBuildStats(String componentName, long startTime, long endTime) {

        ArrayList<BuildStat> productList = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        ResultSet resultSet = null;

        String selectSQL = "select * from ComponentBuildStatistics where Component = ? AND BuildTime BETWEEN ? AND ? ORDER BY BuildTime DESC";
        try {
            connection = DriverManager.getConnection(Constants.DATABASE_URL, "root", "");
            preparedStatement = connection.prepareStatement(selectSQL);
            preparedStatement.setString(1, componentName);
            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(startTime));
            preparedStatement.setBigDecimal(3, BigDecimal.valueOf(endTime));

            // execute select SQL statement
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int status = resultSet.getInt(3);
                BigDecimal timeStamp = resultSet.getBigDecimal(2);
                BuildStat buildStat = new BuildStat(status,timeStamp);
                productList.add(buildStat);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return productList;

    }
}
