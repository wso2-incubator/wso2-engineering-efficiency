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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

import React from "react";
import {isMilestoneCompletedinRange} from './Utils';


export function getDate(dateString) {
    let cDate = Date.parse(dateString + " EDT");
    return new Date(cDate);
}


function getWeek(date) {
    let sDate = new Date(date.getFullYear(), date.getMonth(), 1);
    let actualDate = new Date(date.getFullYear(), date.getMonth(), date.getDate());
    let startDay = sDate.getDay();
    let guessWeek = Math.floor((actualDate.getDate() - 1) / 7);
    let THU = 4;


    if (startDay <= THU) {
        let dateDistance = actualDate.getDate() - 1 - (THU - startDay);
        if (dateDistance <= 0) {
            guessWeek = 0;
        }
        else {
            guessWeek = Math.floor(dateDistance / 7) + 1;
        }
    }

    return guessWeek;
}


export function getAllDates(data) {
    let milestones = data;
    let calendar = {};

    if (milestones !== undefined) {
        // adding years
        for (let i = 0; i < milestones.length; i++) {
            let mDate = getDate(milestones[i]["due-date"]);
            let year = mDate.getFullYear();
            calendar[year] = {}

        }

        for (let i = 0; i < milestones.length; i++) {
            let mDate = getDate(milestones[i]["due-date"]);
            let year = mDate.getFullYear();
            let month = mDate.getMonth() + 1;
            calendar[year][month] = [];

        }

        for (let i = 0; i < milestones.length; i++) {
            let mDate = getDate(milestones[i]["due-date"]);
            let year = mDate.getFullYear();
            let month = mDate.getMonth() + 1;
            let week = getWeek(mDate);
            let startEndDates = getWeekDates(mDate);
            let status = isMilestoneCompletedinRange(mDate, milestones[i]["closed-issues"], milestones[i]["open-issues"]);
            let weekData = {
                weekNumber: week,
                startDate: startEndDates[0],
                endDate: startEndDates[1],
                danger: status
            };

            calendar[year][month].push(weekData);

            for (let i = 0; i < calendar[year][month].length; i++) {
                if (calendar[year][month][i]["weekNumber"] == week) {
                    if (calendar[year][month][i]["danger"] == false && status == true) {
                        calendar[year][month].splice(i);
                        calendar[year][month].push(weekData);
                    }
                }
            }


        }
    }

    delete calendar["NaN"];
    return calendar;


}


export function getWeekDates(checkDate) {
    let startDate = null;
    let currDate = null;
    let sdistance = 0;

    //check date is not defined
    if (checkDate === undefined) {
        currDate = new Date();
        startDate = new Date();

    }
    else {
        currDate = getDate(checkDate);
        startDate = new Date(currDate.getFullYear(), currDate.getMonth(), currDate.getDate());
    }


    let currDay = currDate.getDay();
    if (currDay > 4) {
        sdistance = currDay - 5;
    }
    else {
        sdistance = currDay + 2;
    }

    startDate.setDate(startDate.getDate() - sdistance);
    let endDate = new Date(startDate.getFullYear(), startDate.getMonth(), startDate.getDate());
    endDate.setDate(endDate.getDate() + 6);

    return [startDate, endDate];
}


export function generateMonthName(monthNumber) {
    let months = {
        1: "Janurary",
        2: "Febbruary",
        3: "March",
        4: "April",
        5: "May",
        6: "June",
        7: "July",
        8: "August",
        9: "September",
        10: "October",
        11: "November",
        12: "December"
    }
    return months[monthNumber];
}
