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

package org.wso2.dependencyupdater.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains Details about a CSV report
 *
 * @param <Type> Different Reports may contain different types of object.
 */
public abstract class Report<Type> {

    protected List<Type> reportEntries;

    /**
     * Constructor to create new report
     */
    protected Report() {

        reportEntries = new ArrayList<>();
    }

    /**
     * Add set of entries to outputs
     *
     * @param entries set of items to output
     */
    public void setReportEntries(List<Type> entries) {

        this.reportEntries = entries;
    }

    /**
     * This method handle the reporting part based on Object type reporting
     *
     * @param filename Name of the file to save outputs
     * @return boolean value indicating output status
     */
    public abstract boolean saveToCSV(String filename);

}
