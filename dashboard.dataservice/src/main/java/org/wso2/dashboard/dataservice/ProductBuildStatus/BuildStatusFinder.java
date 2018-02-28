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
package org.wso2.dashboard.dataservice.ProductBuildStatus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dashboard.dataservice.Constants;
import org.wso2.dashboard.dataservice.Database.LocalDBConnector;
import org.wso2.dashboard.dataservice.Model.BuildStat;
import org.wso2.dashboard.dataservice.Model.ProductArea;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;

/**
 * Contains methods for retrieving build status
 */
public class BuildStatusFinder {

    private static final Log log = LogFactory.getLog(BuildStatusFinder.class);

    /**
     * This method identifies the build status of a productArea for a given date
     *
     * @param productArea ProductArea object
     * @param timeStamp   Timestamp value for identifying the required date
     * @return build status of product area
     */
    public static int getBuildStatusForDay(ProductArea productArea, long timeStamp) {

        ArrayList<String> componentList = productArea.getComponents();
        for (String componentName : componentList) {
            int status = getLatestBuildStatisticsForDay(componentName, timeStamp);
            if (status == Constants.BUIlD_FAILED_CODE || status == Constants.BUIlD_NOT_AVAILABLE_CODE) {
                return status;
            }
        }
        return Constants.BUIlD_SUCCESS_CODE;

    }

    /**
     * Retrieve latest build statistics belong to a particular component in a particular day
     * by searching from a range of build statistics
     *
     * @param componentName Component name
     * @param timeStamp     timestamp value belong to that day
     * @return latest build status of component for the given date
     */
    private static int getLatestBuildStatisticsForDay(String componentName, long timeStamp) {

        long startTime = timeStamp - Constants.TWENTY_FOUR_HOURS;
        long endTime = timeStamp + Constants.TWENTY_FOUR_HOURS;
        ArrayList<BuildStat> buildStats = LocalDBConnector.getBuildStats(componentName, startTime, endTime);
        if (buildStats.size() == 0) {
            return Constants.BUIlD_NOT_AVAILABLE_CODE;
        } else {
            BuildStat latestBuildStat = buildStats.get(0);
            int index = 0;
            while (index < buildStats.size() && !isSameDate(latestBuildStat.getTimestamp().longValue(), timeStamp)) {
                latestBuildStat = buildStats.get(index);
                index += 1;
            }
            if (index == buildStats.size()) {
                return Constants.BUIlD_NOT_AVAILABLE_CODE;
            }
            return latestBuildStat.getStatus();
        }

    }

    /**
     * Compares two dates and identifies whether they are the same date or different dates
     *
     * @param timeStamp1 timestamp value for day 1
     * @param timeStamp2 timestamp value for day 2
     * @return boolean value indicating whether dates are same or not
     */
    private static boolean isSameDate(long timeStamp1, long timeStamp2) {

        Date date1 = new Date(timeStamp1);
        Date date2 = new Date(timeStamp2);
        boolean state = (date1.getDay() == date2.getDay() && date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear());
        return state;
    }

    public static int getMonthlyState(ProductArea productArea, long timestamp) {

        double totalScore = 0;
        ArrayList<String> components = productArea.getComponents();
        for (String component : components) {
            long startTime = (BigInteger.valueOf(timestamp).subtract(Constants.ONE_MONTH).longValue());
            double componentScore = LocalDBConnector.getComponentScore(component, startTime, timestamp);
            totalScore += componentScore;
        }
        return getStatusCodeForScore(totalScore / components.size());
    }

    private static int getStatusCodeForScore(double score) {

        if (score < 0.3) {
            return 12;
        } else if (score < 0.6) {
            return 13;
        } else if (score < 0.9) {
            return 14;
        } else if (score == 1) {
            return 15;
        } else {
            return 11;
        }
    }

    public static int getWeeklyState(ProductArea productArea, long timestamp) {

        double totalScore = 0;
        ArrayList<String> components = productArea.getComponents();
        for (String component : components) {
            double componentScore = LocalDBConnector.getComponentScore(component, (timestamp - (7 * Constants.TWENTY_FOUR_HOURS)), timestamp);
            totalScore += componentScore;
        }
        return getStatusCodeForScore(totalScore / components.size());

    }

}
