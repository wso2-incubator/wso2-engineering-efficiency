//
// Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//
package org.wso2.engineering.efficiency.patch.analysis.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.CONFIG_FILE_PATH;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.DB_PASSWORD;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.DB_USERNAME;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.EMAIL_CC_LIST;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.EMAIL_SENDER;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.EMAIL_TO_LIST;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.JIRA_AUTHENTICATION;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.PMT_CONNECTION;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.URL_TO_JIRA_FILTER_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.constants.Constants.URL_TO_JIRA_FILTER_INTERNAL;

/**
 * Stores Property file values.
 */
public class Configuration {

    private static Configuration configuration;
    private String dbUser;
    private String dbPassword;
    private String pmtConnection;
    private String jiraAuthentication;
    private String emailUser;
    private String toList;
    private String ccList;
    private String urlToJIRAFilterCustomer;
    private String urlToJIRAFilterInternal;

    private Configuration() throws IOException {

        Properties prop = new Properties();
        File file = new File(CONFIG_FILE_PATH);
        try (InputStream propertyFile = new FileInputStream(file)) {
            prop.load(propertyFile);
        }
        this.dbUser = prop.getProperty(DB_USERNAME);
        this.dbPassword = prop.getProperty(DB_PASSWORD);
        this.pmtConnection = prop.getProperty(PMT_CONNECTION);
        this.jiraAuthentication = prop.getProperty(JIRA_AUTHENTICATION);
        this.emailUser = prop.getProperty(EMAIL_SENDER);
        this.toList = prop.getProperty(EMAIL_TO_LIST);
        this.ccList = prop.getProperty(EMAIL_CC_LIST);
        this.urlToJIRAFilterCustomer = prop.getProperty(URL_TO_JIRA_FILTER_CUSTOMER);
        this.urlToJIRAFilterInternal = prop.getProperty(URL_TO_JIRA_FILTER_INTERNAL);
    }

    public static Configuration getConfiguration() throws IOException {
        if (configuration == null) {
            configuration = new Configuration();
        }
        return configuration;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getPmtConnection() {
        return pmtConnection;
    }

    public String getJiraAuthentication() {
        return jiraAuthentication;
    }

    public String getEmailUser() {
        return emailUser;
    }

    public String getToList() {
        return toList;
    }

    public String getCcList() {
        return ccList;
    }

    public String getUrlToJIRAFilterCustomer() {
        return urlToJIRAFilterCustomer;
    }

    public String getUrlToJIRAFilterInternal() {
        return urlToJIRAFilterInternal;
    }
}
