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

package org.wso2.dependencyupdater.DependencyProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.wso2.dependencyupdater.App;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.OutdatedDependency;
import org.wso2.dependencyupdater.ReportGenerator.OutdatedDependencyReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Updater for updating dependencies to latest available version
 */
public class WSO2DependencyMajorUpdater extends WSO2DependencyUpdater {
    private static final Log log = LogFactory.getLog(App.class);

    /**
     * @param pomLocation
     * @param dependencies
     * @param globalProperties
     * @param localProperties
     * @return
     */
    protected Model updateToLatestInLocation(String pomLocation, List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {

        List<Dependency> updatedDependencies = getListCopy(dependencies);
        List<OutdatedDependency> outdatedDependencies = new ArrayList<OutdatedDependency>();
        OutdatedDependencyReporter outdatedDependencyReporter = new OutdatedDependencyReporter();
        Model model = new Model();
        for (Dependency dependency : dependencies) {
            if (isValidUpdate(dependency, localProperties, globalProperties)) {
                String latestVersion = MavenCentralConnector.getLatestVersion(dependency);
                updatedDependencies = updateDependencyList(updatedDependencies, dependency, latestVersion);
                outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies, dependency, latestVersion);
            }
        }
        localProperties = addUpdateStatus(localProperties, outdatedDependencies.size());
        model.setDependencies(updatedDependencies);
        model.setProperties(localProperties);
        outdatedDependencyReporter.setReportEntries(outdatedDependencies);
        log.info(outdatedDependencies.size() + " Dependencies updated in the pom located in " + pomLocation);

        outdatedDependencyReporter.saveToCSV(Constants.ROOT_PATH + "/Reports/" + pomLocation.replace('/', '_'));
        return model;
    }

    private Properties addUpdateStatus(Properties localProperties, int updateCount) {

        if (updateCount == 0) {
            localProperties.put("update.status", "false");
        } else {
            localProperties.put("update.status", "true");
        }
        return localProperties;

    }
}
