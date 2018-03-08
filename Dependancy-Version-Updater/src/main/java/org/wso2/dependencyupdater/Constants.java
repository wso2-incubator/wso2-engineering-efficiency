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

/**
 * Constants used in the application
 */
public class Constants {

    //File name constants used
    public static final String POM_NAME = "pom.xml";
    public static final String CONFIG_FILE_NAME = "config.xml";
    public static final String SUFFIX_TEMP_FILE = "-temp";
    public static final String RESOURCE_PATH = "Resources";

    //Character constants
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_END_OF_LINE = "\n";
    public static final String EMPTY_STRING = "";
    public static final String PROPERTY_START_TAG = "${";
    public static final String PROPERTY_END_TAG = "}";
    public static final String JSON_OBJECT_START_TAG = "{";
    public static final String JSON_OBJECT_END_TAG = "}";
    public static final String JSON_OBJECT_KEY_VALUE_SEPARATOR = ":";
    public static final String JSON_OBJECT_ELEMENT_SEPARATOR = ",";
    public static final String URL_SEPARATOR = "/";
    public static final String DOT_REGEX = "\\.";

    //String names
    public static final String PROJECT_VERSION_STRING = "project.version";
    public static final String WSO2_GROUP_TAG = "org.wso2";
    public static final String GROUP_ID_TAG = "groupID";
    public static final String ARTIFACT_ID_TAG = "artifactID";
    public static final String AVAILABLE_VERSIONS_KEY = "AvailableVersions";
    public static final String LATEST_VERSION_KEY = "NewestVersion";
    public static final String UTF_8_CHARSET_NAME = "UTF-8";
    public static final String MAVEN_INVOKE_COMMAND = "clean install";
    public static final String MAVEN_LOG_SUBDIRECTORY = "Error Log";
    public static final String CSV_FILE_EXTENSION = ".csv";
    public static final String LOG_SEPARATOR = "==============================================================";
    public static final String UPDATE_STATUS_TEMPORARY_PROPERTY = "update.status";

    //Integer status codes
    public static final int BUILD_FAIL_CODE = 0;
    public static final int BUILD_SUCCESS_CODE = 1;
    public static final int BUILD_NOT_AVAILABLE_CODE = 2;
    public static final int RETRIEVE_FAILED_CODE = 3;

    //Tags used to read configurations from config.xml
    public static final String MYSQL_USERNAME_TAG = "MYSQL_USERNAME";
    public static final String MYSQL_PASSWORD_TAG = "MYSQL_PASSWORD";
    public static final String MYSQL_DATABASE_URL_TAG = "MYSQL_DATABASE_URL";
    public static final String MYSQL_DATABASE_NAME_TAG = "MYSQL_DATABASE_NAME";
    public static final String AETHER_MICRO_SERVICE = "AETHER_MICRO_SERVICE";
    public static final String MAVEN_HOME_TAG = "M2_HOME";
    public static final String GITHUB_USERNAME_TAG = "GITHUB_USERNAME";
    public static final String GITHUB_PASSWORD_TAG = "GITHUB_PASSWORD";
    public static final String ROOT_PATH_TAG = "ROOT_PATH";
    public static final String REPORT_PATH_TAG = "REPORT_PATH";

}
