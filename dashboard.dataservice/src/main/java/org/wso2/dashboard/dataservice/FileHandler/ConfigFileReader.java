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
package org.wso2.dashboard.dataservice.FileHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wso2.dashboard.dataservice.Constants;
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

    public static String MYSQL_USERNAME;
    public static String MYSQL_PASSWORD;
    public static String MYSQL_DATABASE_URL;

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
            Node mysqlUsernameNode = doc.getElementsByTagName(Constants.MYSQL_USERNAME_TAG).item(0);
            Node mysqlPasswordNode = doc.getElementsByTagName(Constants.MYSQL_PASSWORD_TAG).item(0);
            Node mysqlDatabaseURLNode = doc.getElementsByTagName(Constants.MYSQL_DATABASE_URL_TAG).item(0);

            MYSQL_USERNAME = mysqlUsernameNode.getTextContent();
            MYSQL_PASSWORD = mysqlPasswordNode.getTextContent();
            MYSQL_DATABASE_URL = mysqlDatabaseURLNode.getTextContent();

        } catch (ParserConfigurationException e) {
            log.error("Error occurred in parsing Configurations ", e);
        } catch (SAXException e) {
            log.error("Error occurred in XML Parsing", e);
        } catch (IOException e) {
            log.error("Configuration file Not Found", e);
        }
    }

}
