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
package org.wso2.dependencyupdater.FileHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.dependencyupdater.Constants;
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
    public static String MAVEN_HOME;
    public static String GITHUB_USERNAME;
    public static String GITHUB_PASSWORD;
    public static String ROOT_PATH;
    public static String MYSQL_USERNAME;
    public static String MYSQL_PASSWORD;
    public static String MYSQL_DATABASE_URL;
    public static String MYSQL_DATABASE_NAME;
    public static String DEPENDENCY_UPDATE_MICROSERVICE_URL;

    /**
     * Retrieves set of values from configuration file
     */
    public static void readConfigFile() {

        File configFile = new File(Constants.RESOURCE_PATH + File.separator + Constants.CONFIG_FILE_NAME);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(configFile);
            doc.getDocumentElement().normalize();
            Node mavenHomeNode = doc.getElementsByTagName(Constants.MAVEN_HOME_TAG).item(0);
            Node githubUsernameNode = doc.getElementsByTagName(Constants.GITHUB_USERNAME_TAG).item(0);
            Node githubPasswordNode = doc.getElementsByTagName(Constants.GITHUB_PASSWORD_TAG).item(0);
            Node mysqlUsernameNode = doc.getElementsByTagName(Constants.MYSQL_USERNAME_TAG).item(0);
            Node mysqlPasswordNode = doc.getElementsByTagName(Constants.MYSQL_PASSWORD_TAG).item(0);
            Node mysqlDatabaseURLNode = doc.getElementsByTagName(Constants.MYSQL_DATABASE_URL_TAG).item(0);
            Node mysqlDatabaseNameNode = doc.getElementsByTagName(Constants.MYSQL_DATABASE_NAME_TAG).item(0);
            Node rootPathNode = doc.getElementsByTagName(Constants.ROOT_PATH_TAG).item(0);
            Node dependencyUpdateMicroserviceURL = doc.getElementsByTagName(Constants.DEPENDENCY_UPDATE_MICROSERVICE_TAG).item(0);

            GITHUB_USERNAME = githubUsernameNode.getTextContent();
            GITHUB_PASSWORD = githubPasswordNode.getTextContent();
            MAVEN_HOME = mavenHomeNode.getTextContent();
            ROOT_PATH = rootPathNode.getTextContent();
            MYSQL_USERNAME = mysqlUsernameNode.getTextContent();
            MYSQL_PASSWORD = mysqlPasswordNode.getTextContent();
            MYSQL_DATABASE_URL = mysqlDatabaseURLNode.getTextContent();
            MYSQL_DATABASE_NAME = mysqlDatabaseNameNode.getTextContent();
            DEPENDENCY_UPDATE_MICROSERVICE_URL = dependencyUpdateMicroserviceURL.getTextContent();

        } catch (ParserConfigurationException e) {
            String errorMessage = "Error occurred in Configurations";
            log.error(errorMessage, e);
        } catch (SAXException e) {
            String errorMessage = "Error occurred in XML Parsing";
            log.error(errorMessage, e);
        } catch (IOException e) {
            String errorMessage = "File Not Found";
            log.error(errorMessage, e);
        }
    }

}
