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

    private static final Log log = LogFactory.getLog(WSO2DependencyMinorUpdater.class);

    /**
     * Retrieves the set of dependencies used in a model and set their version to latest available minor version
     * (version that returns from NexusRepoManagerConnector.getLatestMinorVersion(dependency) method )
     *
     * @param pomLocation      Location of the pom file
     * @param dependencies     Set of dependencies
     * @param globalProperties Global properties (properties included in the root pom file)
     * @param localProperties  local properties (properties included in the current pom file)
     * @return org.apache.maven.model.Model with updated dependencies
     */
    protected Model updateToLatestInLocation(String pomLocation, List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {

        //NOTE:- This Model object does not represent a pom.xml file. It is used to return updated dependencies with update state

        List<Dependency> updatedDependencies = new ArrayList<>(dependencies);
        List<OutdatedDependency> outdatedDependencies = new ArrayList<>();

        Model model = new Model();
        for (Dependency dependency : dependencies) {
            dependency = replaceVersionFromPropertyValue(dependency, localProperties, globalProperties);
            log.info(Constants.LOG_SEPARATOR);
            if (isValidUpdate(dependency)) {
                String latestVersion = NexusRepoManagerConnector.getLatestMinorVersion(dependency);
                updatedDependencies = updateDependencyList(updatedDependencies, dependency, latestVersion);

                outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies, dependency, latestVersion);
            }
            log.info(Constants.LOG_SEPARATOR);
        }
        localProperties = addUpdateStatus(localProperties, outdatedDependencies.size());
        model.setDependencies(updatedDependencies);
        model.setProperties(localProperties);

        //used for reporting
        OutdatedDependencyReporter outdatedDependencyReporter = new OutdatedDependencyReporter();
        outdatedDependencyReporter.setReportEntries(outdatedDependencies);
        log.info(outdatedDependencies.size() + " Dependencies updated in the pom located in " + pomLocation);

        boolean written = outdatedDependencyReporter.saveToCSV(ConfigFileReader.getReportPath() + pomLocation.replace('/', '_'));
        if (written) {
            log.info("dependency update report saved successfully");
        } else {
            log.error("dependency update report saving failed");
        }
        return model;
    }

    /**
     * This method validate a Dependency against a set of rules to identify whether dependency needs a update or not
     *
     * @param dependency Dependency Object
     * @return boolean value indicating whether dependency should update or not
     */
    private boolean isValidUpdate(Dependency dependency) {

        log.info(dependency.getGroupId() + ":" + dependency.getArtifactId());
        String currentVersion = dependency.getVersion();
        if (currentVersion == null) {
            log.info("version value not mentioned in the pom file");
            return false;
        }
        if (!dependency.getGroupId().contains(Constants.WSO2_GROUP_TAG)) {
            log.info("dependency does not belong to org.wso2");
            return false;
        }
        if (currentVersion.toLowerCase().contains("snapshot")) {
            log.info("current version is a snapshot version");
            return false;
        }

        String latestVersion = NexusRepoManagerConnector.getLatestMinorVersion(dependency);

        if (latestVersion.length() == 0) {
            log.info("Latest Minor version not found");
            return false;
        } else if (latestVersion.equals(currentVersion)) {
            log.info("Already in the latest Minor version");
            return false;
        }
        log.info("dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + " updated from version " + currentVersion + " to " + latestVersion);
        return true;
    }

}
