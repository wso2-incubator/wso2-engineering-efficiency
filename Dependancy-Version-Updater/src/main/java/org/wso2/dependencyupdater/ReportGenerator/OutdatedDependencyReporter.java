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
package org.wso2.dependencyupdater.ReportGenerator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.OutdatedDependency;
import org.wso2.dependencyupdater.Model.Report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Implementation of Report model to report Outdated Dependencies included in a pom.xml file
 */
public class OutdatedDependencyReporter extends Report<OutdatedDependency> {

    private static final Log log = LogFactory.getLog(OutdatedDependencyReporter.class);

    /**
     * Implementation of abstract method to report set of outdated dependencies
     *
     * @param componentPath path for csv file
     * @return status of report generating process.
     */
    public boolean saveToCSV(String componentPath) {
        //If no entries in the entry list, report will not be generated
        if (reportEntries.isEmpty()) {
            log.info("Outdated dependency list is empty. Therefore report file will not be generated");
            return false;
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new
                    FileOutputStream(new File(componentPath + Constants.CSV_FILE_EXTENSION)),
                    Charset.forName(Constants.UTF_8_CHARSET_NAME).newEncoder()
            );
            //header row for csv
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Dependency Group ID");
            stringBuilder.append(Constants.CSV_DELIMITER);
            stringBuilder.append("Dependency Artifact ID");
            stringBuilder.append(Constants.CSV_DELIMITER);
            stringBuilder.append("Current Version");
            stringBuilder.append(Constants.CSV_DELIMITER);
            stringBuilder.append("Latest Version");
            stringBuilder.append(Constants.CSV_DELIMITER);
            stringBuilder.append("All newer Versions");
            stringBuilder.append(Constants.CSV_END_OF_LINE);
            //set of rows to report updated dependencies
            for (OutdatedDependency dependency : reportEntries) {

                stringBuilder.append(dependency.getGroupId());
                stringBuilder.append(Constants.CSV_DELIMITER);
                stringBuilder.append(dependency.getArtifactId());
                stringBuilder.append(Constants.CSV_DELIMITER);
                stringBuilder.append(dependency.getVersion());
                stringBuilder.append(Constants.CSV_DELIMITER);
                stringBuilder.append(dependency.getLatestVersion());
                stringBuilder.append(Constants.CSV_DELIMITER);
                stringBuilder.append(dependency.getNewVersions());
                stringBuilder.append(Constants.CSV_END_OF_LINE);

            }
            printWriter.write(stringBuilder.toString());
            printWriter.close();
            return true;

        } catch (FileNotFoundException e) {
            log.error("Directory not found to save Report :" + componentPath);
        }
        return false;
    }

}