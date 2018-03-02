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
import org.wso2.dependencyupdater.Application;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.Model.OutdatedDependency;
import org.wso2.dependencyupdater.ReportGenerator.OutdatedDependencyReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Updater for updating WSO2 dependencies to latest available Minor version
 */
public class WSO2DependencyMinorUpdater extends WSO2DependencyUpdater {

    private static final Log log = LogFactory.getLog(Application.class);

    /**
     * Retrieves the set of dependencies used in a model and set their version to latest available minor version
     * (version that returns from MavenCentralConnector.getLatestMinorVersion(dependency) method )
     *
     * @param pomLocation      Location of the pom file
     * @param dependencies     Set of dependencies
     * @param globalProperties Global properties (properties included in the root pom file)
     * @param localProperties  local properties (properties included in the current pom file)
     * @return org.apache.maven.model.Model with updated dependencies
     */
    protected Model updateToLatestInLocation(String pomLocation, List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {

        List<Dependency> updatedDependencies = new ArrayList<Dependency>(dependencies);
        List<OutdatedDependency> outdatedDependencies = new ArrayList<OutdatedDependency>();
        OutdatedDependencyReporter outdatedDependencyReporter = new OutdatedDependencyReporter();
        Model model = new Model();
        for (Dependency dependency : dependencies) {
            log.info(Constants.LOG_SEPERATOR);
            if (isValidUpdate(dependency, localProperties, globalProperties)) {
                String latestVersion = MavenCentralConnector.getLatestMinorVersion(dependency);
                updatedDependencies = updateDependencyList(updatedDependencies, dependency, latestVersion);
                outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies, dependency, latestVersion);
            }
            log.info(Constants.LOG_SEPERATOR);
        }
        localProperties = addUpdateStatus(localProperties, outdatedDependencies.size());
        model.setDependencies(updatedDependencies);
        model.setProperties(localProperties);
        outdatedDependencyReporter.setReportEntries(outdatedDependencies);
        log.info(outdatedDependencies.size() + " Dependencies updated in the pom located in " + pomLocation);

        boolean written = outdatedDependencyReporter.saveToCSV(ConfigFileReader.ROOT_PATH + "/Reports/" + pomLocation.replace('/', '_'));
        if (written) {
            log.info("dependency update report saved successfully");
        } else {
            log.error("dependency update report saving failed");
        }
        return model;
    }

}
