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

package org.wso2.dependencyupdater;

public class Constants {

    public static final String ROOT_PATH = "/home/dimuth/Documents/DM-Root/";
    public static final String POM_NAME = "pom.xml";
    public static final String GET_LATEST_VERSION_URL = "http://localhost:9094/aethermicroservice/getLatest";
    public static final String EMPTY_STRING = "";
    public static final String CONFIG_FILE_NAME = "config.xml";
    public static final String GET_VERSION_LIST = "http://localhost:9094/aethermicroservice/getVersions";
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_END_OF_LINE = "\n";
    public static final String PROJECT_VERSION_STRING = "project.version";
    public static final String MAVEN_HOME_TAG = "M2_HOME";
    public static final String GITHUB_USERNAME_TAG = "GITHUB_USERNAME";
    public static final String GITHUB_PASSWORD_TAG = "GITHUB_PASSWORD";

    public static final String WSO2_GROUP_TAG = "org.wso2";
    public static final String PROPERTY_START_TAG = "${";
    public static final String PROPERTY_END_TAG = "}";
    public static final String JSON_OBJECT_START_TAG = "{";
    public static final String JSON_OBJECT_END_TAG = "}";
    public static final String GROUP_ID_TAG = "groupID";
    public static final String ARTIFACT_ID_TAG = "artifactID";
    public static final String JSON_OBJECT_KEY_VALUE_SEPARATOR = ":";
    public static final String JSON_OBJECT_ELEMENT_SEPARATOR = ",";
    public static final String AVAILABLE_VERSIONS_KEY = "AvailableVersions";
    public static final String LATEST_VERSION_KEY = "NewestVersion";
    public static final String UTF_8_CHARSET_NAME = "UTF-8";
    public static final String MAVEN_INVOKE_COMMAND_WITHOUT_TESTS = "clean install -DskipTests";
    public static final String MAVEN_INVOKE_COMMAND = "clean install";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final String MYSQL_DB_URL = "jdbc:mysql://127.0.0.1:3308";
    public static final String MYSQL_DB_USERNAME = "root";
    public static final String MYSQL_DB_PASSWORD = "";
    public static final String MAVEN_LOG_SUBDIRECTORY = "Error Log";
    public static final String LOG_SEPERATOR = "==============================================================";

    public static final int NOT_FOUND_STATUS_CODE = 404;
    public static final int SUCCESSFUL_STATUS_CODE = 200;
    public static final int BUILD_FAIL_CODE = 0;
    public static final int BUILD_SUCCESS_CODE = 1;
    public static final int BUILD_NOT_AVAILABLE_CODE = 2;

    public static final String SUFFIX_TEMP_FILE = "-temp";
}
