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
package org.wso2.engineering.efficiency.patch.analysis.util;

import com.google.api.services.gmail.GmailScopes;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * constant values.
 */
public final class Constants {

    public static final String GRAY_BACKGROUND = "#efefef";
    public static final String WHITE_BACKGROUND = "#ffffff";

    private Constants() {
    }

    /**
     * Configuration Constant values.
     */
    public static final class Configuration {

        public static final String CONFIG_FILE_PATH = "./config.properties";
        public static final String DB_USERNAME = "dbUser";
        public static final String DB_PASSWORD = "dbPassword";
        public static final String PMT_CONNECTION = "pmtConnection";
        public static final String JIRA_AUTHENTICATION = "JIRABasicAuth";
        public static final String EMAIL_SENDER = "emailUser";
        public static final String EMAIL_TO_LIST = "toList";
        public static final String EMAIL_CC_LIST = "ccList";
        public static final String URL_TO_JIRA_FILTER_CUSTOMER = "UrlToCustomerIssuesFilter";
        public static final String URL_TO_JIRA_FILTER_INTERNAL = "UrlToInternalIssuesFilter";
    }

    /**
     * SQL Constant values.
     */
    public static final class SQLStatement {

        public static final String SELECT_PATCHES_FOR_JIRA = "SELECT *, \n" +
                "(5 * (DATEDIFF(CURDATE(), REPORT_DATE) DIV 7)+ " +
                "MID('0123455401234434012332340122123401101234000123450'," +
                "7 * WEEKDAY((REPORT_DATE)) + WEEKDAY(CURDATE()) + 1, 1)) AS DAYS_SINCE_REPORT,\n" +
                "(5 *(DATEDIFF(CURDATE(),SIGN_REQUEST_SENT_ON) DIV 7)+" +
                "MID('0123455401234434012332340122123401101234000123450'," +
                "7 * WEEKDAY((SIGN_REQUEST_SENT_ON)) + WEEKDAY(CURDATE()) + 1, 1)) AS " +
                "DAYS_IN_SIGNING FROM PATCH_ETA e " +
                "RIGHT JOIN PATCH_QUEUE q ON e.PATCH_QUEUE_ID = q.ID\n" +
                "where SUPPORT_JIRA like '%";

        public static final String OR_JIRA = "OR SUPPORT_JIRA like '%";
        public static final String SELECT_SUPPORT_JIRAS = "SELECT SUPPORT_JIRA FROM PATCH_QUEUE\n" +
                "WHERE YEAR(REPORT_DATE) > '2017';";
    }

    /**
     * PMT Constant values.
     */
    public static final class PMT {

        public static final String JIRA_URL_PREFIX = "https://support.wso2.com/jira/browse/";
        public static final int JIRA_URL_PREFIX_LENGTH = 37;
        public static final String NO_ENTRY_IN_PMT = "No Entry in PMT";
        public static final String NA = "N/A";
        public static final String OFF_QUEUE = "No";
        public static final String IN_QUEUE = "Yes";
        public static final String STAGING = "Staging";
        public static final String DEVELOPMENT = "Development";
        public static final String TESTING = "Testing";
        public static final String PRE_QA = "PreQADevelopment";
        public static final String READY_FOR_QA = "ReadyForQA";
        public static final String FAILED_QA = "FailedQA";
        public static final String ON_HOLD = "OnHold";
        public static final String RELEASED_LC = "Released";
        public static final String RELEASED_NOT_AUTOMATED = "ReleasedNotAutomated";
        public static final String RELEASED_NOT_IN_PUBLIC_SVN = "ReleasedNotInPublicSVN";
        public static final String SUPPORT_JIRA_URL = "SUPPORT_JIRA";
    }

    /**
     * JIRA constant values.
     */
    public static final class JIRA {

        public static final int RESULTS_PER_PAGE = 50;
        public static final int OK = 200;
        public static final String AUTH = "Authorization";
        public static final String CONTENT = "Content-Type";
        public static final String CONTENT_TYPE = "application/json; charset=UTF-8";
        public static final String SEARCH_URL = "searchUrl";
        public static final String TOTAL = "total";
        public static final String ISSUES = "issues";
        public static final String FIELDS = "fields";
        public static final String ASSIGNEE = "assignee";
        public static final String STATUS = "status";
        public static final String DATE_CREATED = "created";
        public static final String JIRA_KEY = "key";
        public static final String EMAIL = "emailAddress";
        public static final String NAME = "name";
        public static final String NOT_SPECIFIED = "Not Specified";
    }

    /**
     * Email constant values
     */
    public static final class Email {

        public static final String APPLICATION_NAME = "Patch Information Emailer";
        public static final String CREDENTIALS_FOLDER = "gmail-credentials";
        public static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_SEND);
        public static final String CLIENT_SECRET_DIR = "/clientSecret.json";

        public static final String MAIN_HEADER_INTERNAL =
                "<html>\n" +
                        "   <head>\n" +
                        "      <title></title>\n" +
                        "   </head>\n" +
                        "   <body>\n" +
                        "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" " +
                        "width=\"100%\" " +
                        "style=\"max-width:100%;\">\n" +
                        "      <tr>\n" +
                        "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size:" +
                        " 18px;" +
                        " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
                        "            <p style=\"font-size: 24px; font-weight: 600; line-height: 26px; " +
                        "color: #000000;\">" +
                        "Proactive Patches as of " + LocalDate.now() + " : Ongoing - ";

        public static final String MAIN_HEADER_CUSTOMER =
                "<html>\n" +
                        "   <head>\n" +
                        "      <title></title>\n" +
                        "   </head>\n" +
                        "   <body>\n" +
                        "      <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" " +
                        "width=\"100%\" " +
                        "style=\"max-width:100%;\">\n" +
                        "      <tr>\n" +
                        "         <td align=\"center\" style=\"font-family: Helvetica, Arial, sans-serif; font-size: " +
                        "18px;" +
                        " font-weight: 400; line-height: 15px; padding-top: 0px;\">\n" +
                        "            <p style=\"font-size: 24px; font-weight: 600; line-height: 26px; color:" +
                        " #000000;\">" +
                        "Customer Patches as of " + LocalDate.now() + " : Ongoing - ";

        public static final String MAIN_HEADER_END = "</p>\n</td>\n" +
                "      </tr>\n" +
                "      </table>";

        public static final String COLUMN_NAMES_SUMMARY = "<table align=\"center\" cellspacing=\"0\" " +
                "cellpadding=\"0\"" +
                " border=\"0\" width=\"95%\">" +
                "<tr>" +
                " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "JIRA Issue" +
                " </td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Assignee" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Open Patches" +
                "</td>" +
                " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px;" +
                " padding: 10px;\">" +
                "Date Reported" +
                "</td>" +
                "</tr>";

        public static final String COLUMN_NAMES = "<table align=\"center\" cellspacing=\"0\" " +
                "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
                "<tr>" +
                " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">" +
                "JIRA Issue" +
                " </td>" +
                "<td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Patch Name" +
                "</td>" +
                "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
                "20px; padding: 10px;\">" +
                "Product" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Assignee" +
                "</td>" +
                " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">" +
                "LC State </td>" +
                "<td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">";

        public static final String COLUMN_NAMES_INACTIVE = "<table align=\"center\" cellspacing=\"0\" " +
                "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
                "<tr>" +
                " <td width=\"15%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">" +
                "JIRA Issue" +
                " </td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Patch Name" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
                "20px; padding: 10px;\">" +
                "Product" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Assignee" +
                "</td>" +
                " <td width=\"7%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">" +
                "JIRA State" +
                " </td>" +
                " <td width=\"7%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">" +
                "LC State </td>" +
                "<td width=\"7%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height:" +
                " 20px; padding: 10px;\">";

        public static final String COLUMN_NAMES_DEV = "<table align=\"center\" cellspacing=\"0\" " +
                "cellpadding=\"0\" border=\"0\" width=\"95%\">" +
                "<tr><td width=\"20%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\"> JIRA Issue</td><td width=\"20%\" align=\"center\" color=\"#044767\"" +
                " bgcolor=\"#bebebe\" style=\"font-family: Open Sans," +
                " Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">Patch Name" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans,Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\"> Product </td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, " +
                "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; padding: " +
                "10px;\"> Assignee </td>" +
                " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\"> " +
                "LC State </td>" + "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" " +
                "style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; " +
                "line-height: 20px; padding: 10px;\">" +
                "Reported Date" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, " +
                "Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: " +
                "20px; padding: 10px;\">";

        public static final String COLUMN_NAMES_RELEASED = "<table align=\"center\" cellspacing=\"0\"" +
                " cellpadding=\"0\"" +
                " border=\"0\" width=\"95%\">" +
                "<tr>" +
                " <td width=\"30%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "JIRA Issue" +
                " </td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Assignee" +
                "</td>" +
                "<td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family: " +
                "Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px; " +
                "padding: 10px;\">" +
                "Released Patches" +
                "</td>" +
                " <td width=\"10%\" align=\"center\" color=\"#044767\" bgcolor=\"#bebebe\" style=\"font-family:" +
                " Open Sans, Helvetica, Arial, sans-serif; font-size: 14px; font-weight: 800; line-height: 20px;" +
                " padding: 10px;\">" +
                "Date Reported" +
                "</td>" +
                "</tr>";

        public static final String SUBJECT_INTERNAL = "[Ongoing Patches][Proactive] Proactive Patch " +
                "Information: " +
                LocalDate.now();
        public static final String SUBJECT_CUSTOMER = "[Ongoing Patches][Customer] Customer " +
                "Patch Information: " + LocalDate.now();
        public static final String SECTION_HEADER_SUMMARY = "<br><p align=\"center\"style=\"font-size: 20px; " +
                "font-weight: 600; line-height: 26px; color: #000000;\">Summary of Patch Related JIRAs</p>";
        public static final String SECTION_HEADER_INACTIVE = "<br><p align=\"center\"style=\"font-size: 20px; " +
                "font-weight: 600; line-height: 26px; color: #000000;\">JIRAs Tagged with Patch Label Having " +
                "No Ongoing " +
                "Patches</p>";
        public static final String SECTION_HEADER_DEV = "<br><p align=\"center\"style=\"font-size: 20px; " +
                "font-weight: 600; line-height: 26px; color: #000000;\">Patches in Development</p>";
        public static final String SECTION_HEADER_SIGNING = "<br><p align=\"center\"style=\"font-size: 20px;" +
                " font-weight: 600; line-height: 26px; color: #000000;\">Patches Sent for Signing</p>";
        public static final String SECTION_HEADER_RELEASED = "<br><p align=\"center\"style=\"font-size: 20px; " +
                "font-weight: 600; line-height: 26px; color: #000000;\"> Released Patches with an Unresolved" +
                " JIRA Issue</p>";
        public static final String EMAIL_TYPE = "text/html";

        public static final String EMAIL_FOOTER = "<br><br><table align=\"center\" border=\"0\" cellpadding=\"0\" " +
                "cellspacing=\"0\" width=\"100%\" style=\"max-width:600px;\">\n" +
                "   <tr>\n" +
                "      <td align=\"center\">                           \n" +
                "         <img src=\"https://upload.wikimedia.org/wikipedia/en/5/56/WSO2_Software_Logo.png\" " +
                "width=\"90\" height=\"37\" style=\"display: block; border: 0px;\"/>                        \t  \n" +
                "      </td>\n" +
                "   </tr>\n" +
                "   <tr>\n" +
                "      <td align=\"center\" style=\"font-family: Open Sans, Helvetica, Arial, sans-serif; " +
                "font-size: 14px; font-weight: 400; line-height: 24px;\">\n" +
                "         <p style=\"font-size: 14px; font-weight: 400; line-height: 20px;" +
                " color: #777777;\">Copyright (c) 2018 | WSO2 Inc.<br/>All Right Reserved.                 " +
                "                     \t\t   </p>\n" +
                "      </td>\n" +
                "   </tr>\n" +
                "</table>\n" +
                "</body></html>\n";
    }
}
