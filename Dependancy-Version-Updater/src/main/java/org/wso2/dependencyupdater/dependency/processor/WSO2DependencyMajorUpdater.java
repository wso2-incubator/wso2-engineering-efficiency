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

package org.wso2.dependencyupdater.dependency.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.filehandler.ConfigFileReader;
import org.wso2.dependencyupdater.model.OutdatedDependency;
import org.wso2.dependencyupdater.report.generator.OutdatedDependencyReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Updater for updating WSO2 dependencies to latest available version
 */
public class WSO2DependencyMajorUpdater extends WSO2DependencyUpdater {

    private static final Log log = LogFactory.getLog(WSO2DependencyMajorUpdater.class);

    /**
     * Retrieves the set of dependencies used in a model and set their version to latest available version
     * (version that returns from NexusRepoManagerConnector.getLatestVersion(dependency) method )
     *
     * @param pomLocation      Location of the pom file
     * @param dependencies     Set of dependencies
     * @param globalProperties Global properties (properties included in the root pom file)
     * @param localProperties  local properties (properties included in the current pom file)
     * @return org.apache.maven.model.Model with updated dependencies
     */
    protected Model updateToLatestInLocation(String pomLocation, List<Dependency> dependencies,
                                             Properties globalProperties, Properties localProperties) {

        //NOTE:- This Model object does not represent a pom.xml file.
        // It is used to return updated dependencies with update state

        List<Dependency> updatedDependencies = new ArrayList<>(dependencies);
        List<OutdatedDependency> outdatedDependencies = new ArrayList<>();
        Model model = new Model();
        for (Dependency dependency : dependencies) {
            dependency = replaceVersionFromPropertyValue(dependency, localProperties, globalProperties);
            if (isValidUpdate(dependency)) {
                String latestVersion = NexusRepoManagerConnector.getLatestVersion(dependency);
                updatedDependencies = updateDependencyList(updatedDependencies, dependency, latestVersion);
                outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies, dependency, latestVersion);
            }
        }
        localProperties = addUpdateStatus(localProperties, outdatedDependencies.size());
        model.setDependencies(updatedDependencies);
        model.setProperties(localProperties);

        //used for reporting
        OutdatedDependencyReporter outdatedDependencyReporter = new OutdatedDependencyReporter();
        outdatedDependencyReporter.setReportEntries(outdatedDependencies);

        boolean written = outdatedDependencyReporter.saveToCSV(pomLocation.substring(ConfigFileReader
                .getRootPath().length()));
        if (log.isDebugEnabled()) {
            if (written) {
                log.debug("dependency update report saved successfully");
            } else {
                log.debug("dependency update report saving failed");
            }
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

        String currentVersion = dependency.getVersion();
        if (currentVersion == null) {
            return false;
        }
        if (!dependency.getGroupId().contains(Constants.WSO2_GROUP_TAG)) {
            return false;
        }
        if (currentVersion.toLowerCase().contains(Constants.SNAPSHOT_NAME_TAG)) {
            return false;
        }

        String latestVersion = NexusRepoManagerConnector.getLatestVersion(dependency);

        if (latestVersion.length() == 0) {
            return false;
        } else if (latestVersion.equals(currentVersion)) {
            return false;
        }
        if (log.isDebugEnabled()) {

            log.debug("dependency " + dependency.getGroupId().replaceAll("[\r\n]", "")
                    + ":" + dependency.getArtifactId().replaceAll("[\r\n]", "")
                    + " updated from version " + currentVersion.replaceAll("[\r\n]", "")
                    + " to " + latestVersion.replaceAll("[\r\n]", ""));
        }
        return true;
    }

}


