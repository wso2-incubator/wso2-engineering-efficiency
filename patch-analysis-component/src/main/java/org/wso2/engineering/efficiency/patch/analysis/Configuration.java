/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.wso2.engineering.efficiency.patch.analysis;

import org.wso2.engineering.efficiency.patch.analysis.exceptions.PatchAnalysisConfigurationException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.CONFIG_FILE_PATH;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.DB_PASSWORD;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.DB_USERNAME;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EEOP_CONNECTION;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EEOP_PASSWORD;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EEOP_USERNAME;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EMAIL_CC_LIST;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EMAIL_SENDER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.EMAIL_TO_LIST;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.JIRA_AUTHENTICATION;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.PMT_CONNECTION;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.URL_TO_JIRA_FILTER_CUSTOMER;
import static org.wso2.engineering.efficiency.patch.analysis.util.Constants.Configuration.URL_TO_JIRA_FILTER_INTERNAL;

/**
 * Stores Property file values.
 */
public class Configuration {

    private static Configuration configuration;
    private String pmtUser;
    private String pmtPassword;
    private String pmtConnection;
    private String jiraAuthentication;
    private String emailUser;
    private String toList;
    private String ccList;
    private String urlToJIRAFilterCustomer;
    private String urlToJIRAFilterInternal;
    private String dbUser;
    private String dbPassword;
    private String dbConnection;

    private Configuration() throws PatchAnalysisConfigurationException {

        try {
            Properties prop = new Properties();
            File file = new File(CONFIG_FILE_PATH);
            InputStream propertyFile = new FileInputStream(file);
            prop.load(propertyFile);
            this.pmtUser = prop.getProperty(DB_USERNAME);
            this.pmtPassword = prop.getProperty(DB_PASSWORD);
            this.pmtConnection = prop.getProperty(PMT_CONNECTION);
            this.jiraAuthentication = prop.getProperty(JIRA_AUTHENTICATION);
            this.emailUser = prop.getProperty(EMAIL_SENDER);
            this.toList = prop.getProperty(EMAIL_TO_LIST);
            this.ccList = prop.getProperty(EMAIL_CC_LIST);
            this.urlToJIRAFilterCustomer = prop.getProperty(URL_TO_JIRA_FILTER_CUSTOMER);
            this.urlToJIRAFilterInternal = prop.getProperty(URL_TO_JIRA_FILTER_INTERNAL);
            this.dbUser = prop.getProperty(EEOP_USERNAME);
            this.dbPassword = prop.getProperty(EEOP_PASSWORD);
            this.dbConnection = prop.getProperty(EEOP_CONNECTION);
        } catch (IOException e) {
            throw new PatchAnalysisConfigurationException("Could not read values from Properties file", e);
        }

    }

    public static Configuration getInstance() throws PatchAnalysisConfigurationException {

        if (configuration == null) {
            synchronized (Configuration.class) {
                if (configuration == null) {
                    configuration = new Configuration();
                }
            }
        }
        return configuration;
    }

    public String getPmtUser() {

        return pmtUser;
    }

   public String getPmtPassword() {

        return pmtPassword;
    }

    public String getPmtConnection() {

        return pmtConnection;
    }

    public String getEEOPUser() {

        return dbUser;
    }

    public String getEEOPPassword() {

        return dbPassword;
    }

    public String getEEOPConnection() {

        return dbConnection;
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
