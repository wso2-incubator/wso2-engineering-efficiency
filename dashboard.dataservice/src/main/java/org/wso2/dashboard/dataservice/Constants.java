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
package org.wso2.dashboard.dataservice;

import java.math.BigInteger;

/**
 * This class contains constant variables used by all other classes
 */
public class Constants {

    public static final int TWENTY_FOUR_HOURS = 86400000;
    public static final BigInteger ONE_MONTH = new BigInteger("2592000000");
    public static final int BUIlD_SUCCESS_CODE = 1;
    public static final int BUIlD_FAILED_CODE = 0;
    public static final int BUIlD_NOT_AVAILABLE_CODE = 2;

    public static final int SUNNY_STATE = 15;
    public static final int PARTLY_CLOUDY = 14;
    public static final int CLOUDY = 13;
    public static final int RAINY = 12;
    public static final int STORMY = 11;
    public static final int FAILED_TO_UPDATE = 3;

    public static final String MYSQL_USERNAME_TAG = "MYSQL_USERNAME";
    public static final String MYSQL_PASSWORD_TAG = "MYSQL_PASSWORD";
    public static final String MYSQL_DATABASE_URL_TAG = "MYSQL_DATABASE_URL";
    public static final String RESOURCE_PATH = "Resources";
    public static final String CONFIG_FILE_NAME = "config.xml";
}
