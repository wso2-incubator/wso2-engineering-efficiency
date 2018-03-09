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
package org.wso2.dependencyupdater.filehandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.exceptions.DependencyUpdaterConfigurationException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Contains the methods for reading configuration files
 */
public class ConfigFileReader {

    private static final Log log = LogFactory.getLog(ConfigFileReader.class);

    private static String mavenHome;
    private static String githubAccesToken;
    private static String rootPath;
    private static String mysqlUsername;
    private static String mysqlPassword;
    private static String mysqlDatabaseUrl;
    private static String mysqlDatabaseName;
    private static String aetherMicroServiceUrl;
    private static String reportPath;

    /**
     * Retrieves required configuration values from configuration file
     *
     * @throws DependencyUpdaterConfigurationException When fail to read configurations
     */
    public static void loadConfigurations() throws DependencyUpdaterConfigurationException {

        File configFile = new File(Constants.RESOURCE_PATH + File.separator + Constants.CONFIG_FILE_NAME);
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document configuration = documentBuilder.parse(configFile);
            configuration.getDocumentElement().normalize();

            //search for the first occurrence of each property tag and assign its text value

            mavenHome = configuration.getElementsByTagName(Constants.MAVEN_HOME_TAG).item(0)
                    .getTextContent();
            githubAccesToken = configuration.getElementsByTagName(Constants.GITHUB_ACCESS_TOKEN_TAG).item(0)
                    .getTextContent();
            mysqlUsername = configuration.getElementsByTagName(Constants.MYSQL_USERNAME_TAG).item(0)
                    .getTextContent();
            mysqlPassword = configuration.getElementsByTagName(Constants.MYSQL_PASSWORD_TAG).item(0)
                    .getTextContent();
            mysqlDatabaseUrl = configuration.getElementsByTagName(Constants.MYSQL_DATABASE_URL_TAG).item(0)
                    .getTextContent();
            mysqlDatabaseName = configuration.getElementsByTagName(Constants.MYSQL_DATABASE_NAME_TAG).item(0)
                    .getTextContent();
            rootPath = configuration.getElementsByTagName(Constants.ROOT_PATH_TAG).item(0)
                    .getTextContent();
            aetherMicroServiceUrl = configuration.getElementsByTagName(Constants.AETHER_MICRO_SERVICE).item(0)
                    .getTextContent();
            reportPath = configuration.getElementsByTagName(Constants.REPORT_PATH_TAG).item(0)
                    .getTextContent();

        } catch (ParserConfigurationException e) {
            throw new DependencyUpdaterConfigurationException("Error occurred in parsing Configurations", e);
        } catch (SAXException e) {
            throw new DependencyUpdaterConfigurationException("Error occurred in XML Parsing", e);
        } catch (IOException e) {
            throw new DependencyUpdaterConfigurationException("Configuration file Not Found", e);
        } catch (NullPointerException e) {
            throw new DependencyUpdaterConfigurationException("One or more required tags " +
                    "not found in the configurations file", e);

        }
    }

    //Set of getters for configuration variables
    public static String getMavenHome() {

        return mavenHome;
    }

    public static String getGithubAccesToken() {

        return githubAccesToken;
    }

    public static String getRootPath() {

        return rootPath;
    }

    public static String getMysqlUsername() {

        return mysqlUsername;
    }

    public static String getMysqlPassword() {

        return mysqlPassword;
    }

    public static String getMysqlDatabaseUrl() {

        return mysqlDatabaseUrl;
    }

    public static String getMysqlDatabaseName() {

        return mysqlDatabaseName;
    }

    public static String getAetherMicroServiceUrl() {

        return aetherMicroServiceUrl;
    }

    public static String getReportPath() {

        return reportPath;
    }

}
