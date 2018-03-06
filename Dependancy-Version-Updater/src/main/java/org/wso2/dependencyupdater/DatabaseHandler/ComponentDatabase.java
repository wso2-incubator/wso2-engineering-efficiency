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
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implements a singleton class for Database connection
 */
public class ComponentDatabase {

    private static final Log log = LogFactory.getLog(ComponentDatabase.class);
    private static ComponentDatabase instance;
    private Connection connection;

    private ComponentDatabase() throws SQLException {

        String className = Constants.DATABASE_CLASS_NAME;
        try {

            Class.forName(className);
            String url = ConfigFileReader.getMysqlDatabaseUrl() + Constants.URL_SEPARATOR + ConfigFileReader.getMysqlDatabaseName();
            this.connection = DriverManager.getConnection(url, ConfigFileReader.getMysqlUsername(), ConfigFileReader.getMysqlPassword());
        } catch (ClassNotFoundException e) {
            log.error("MYSQL Class not found for class" + className, e);
        }
    }

    public static ComponentDatabase getInstance() throws SQLException {

        if (instance == null) {
            instance = new ComponentDatabase();
        } else if (instance.getConnection().isClosed()) {
            instance = new ComponentDatabase();
        }

        return instance;
    }

    public Connection getConnection() {

        return connection;
    }
}
