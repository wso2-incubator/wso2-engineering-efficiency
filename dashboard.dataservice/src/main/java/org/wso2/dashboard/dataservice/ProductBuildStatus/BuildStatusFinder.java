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

    /**
     * This method identifies the build status of a productArea for a given date
     *
     * @param productArea ProductArea object
     * @param timeStamp   Timestamp value for identifying the required date
     * @return build status of product area
     */
    public static int getProductAreaBuildStatusForDay(ProductArea productArea, long timeStamp) {

        ArrayList<String> componentList = productArea.getComponents();
        for (String componentName : componentList) {
            int status = getComponentBuildStatusForDay(componentName, timeStamp);
            if (status == Constants.BUIlD_FAILED_CODE || status == Constants.BUIlD_NOT_AVAILABLE_CODE || status == Constants.FAILED_TO_UPDATE) {
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
    private static int getComponentBuildStatusForDay(String componentName, long timeStamp) {

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
        return (date1.getDay() == date2.getDay() && date1.getMonth() == date2.getMonth() && date1.getYear() == date2.getYear());
    }

    /**
     * This method returns average monthly build state for a particular product area
     * it counts fraction of successful builds for every component and average it again and map it with getStatusCodeForScore() method
     *
     * @param productArea product area object
     * @param timestamp   long value indicating the end date of timestamp
     * @return int score value indicating level of build success
     */
    public static int getMonthlyState(ProductArea productArea, long timestamp) {

        double totalScore = 0;
        ArrayList<String> components = productArea.getComponents();
        for (String component : components) {
            //subtract one number of seconds in one month to getBuildStatus a timestamp belong to date one month ago
            long startTime = (BigInteger.valueOf(timestamp).subtract(Constants.ONE_MONTH).longValue());
            double componentScore = LocalDBConnector.getComponentScore(component, startTime, timestamp);
            totalScore += componentScore;
        }
        return getStatusCodeForScore(totalScore / components.size());
    }

    /**
     * This method assigns status code values for averaged monthly/weekly successful build statuses
     *
     * @param score average of successful build components over a defined time period
     * @return coded values for different status to be displayed in the dashboard
     */
    private static int getStatusCodeForScore(double score) {

        if (score <= 0.2) {
            return Constants.STORMY;
        } else if (score <= 0.4) {
            return Constants.RAINY;
        } else if (score <= 0.6) {
            return Constants.CLOUDY;
        } else if (score < 1.0) {
            return Constants.PARTLY_CLOUDY;
        } else {

            return Constants.SUNNY_STATE;
        }
    }

    /**
     * Method for retrieving fraction of successful build components for a product in a given week.
     *
     * @param productArea productArea component
     * @param timestamp   unix timestamp to determine week
     * @return state indicating the level of success
     */
    public static int getWeeklyState(ProductArea productArea, long timestamp) {

        double totalScore = 0;
        ArrayList<String> components = productArea.getComponents();
        for (String component : components) {
            double componentScore = LocalDBConnector.getComponentScore(component, (timestamp - (7 * Constants.TWENTY_FOUR_HOURS)), timestamp);
            totalScore += componentScore;
        }
        return getStatusCodeForScore(totalScore / components.size());

    }

    /**
     * Method to find failing components for the current day
     *
     * @param productArea product area component
     * @param currentTime unix timestamp to determine current day
     * @return string with all the currently failing components for the given product area
     */
    public static String getFailingComponents(ProductArea productArea, long currentTime) {

        ArrayList<String> components = productArea.getComponents();
        ArrayList<String> failedComponents = new ArrayList<>();
        for (String component : components) {
            if (getComponentBuildStatusForDay(component, currentTime) == Constants.BUIlD_FAILED_CODE) {
                failedComponents.add(component);
            }
        }
        if (failedComponents.size() == 0) {
            return "N/A";
        }
        String output = failedComponents.toString();
        //removing the starting and ending curly braces
        return output.substring(1, output.length() - 1);
    }
}
