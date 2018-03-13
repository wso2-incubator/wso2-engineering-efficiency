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
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.model.OutdatedDependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Updater for WSO2 dependencies
 */
public abstract class WSO2DependencyUpdater extends DependencyUpdater {

    private static final Log log = LogFactory.getLog(WSO2DependencyUpdater.class);

    /**
     * Rules for updating WSO2 Dependencies
     *
     * @param model            org.apache.maven.model.Model -represent the attributes of a  pom.xml
     * @param globalProperties java.util.Properties - all the properties in the root pom.xml
     * @return update operation state
     */
    public boolean updateModel(Model model, Properties globalProperties) {

        boolean isDependencyModelUpdated;
        boolean isManagementModelUpdated = false;

        Model modifiedModel = model.clone();
        String pomLocation = model.getProjectDirectory().toString();
        List<Dependency> modelDependencies = model.getDependencies();
        Properties localProperties = model.getProperties();
        //call update method for the list of dependencies
        Model dependencyModel = updateToLatestInLocation(pomLocation, modelDependencies, globalProperties,
                localProperties);

        Properties updatedLocalProperties = dependencyModel.getProperties();
        isDependencyModelUpdated = getUpdateStatusFromProperties(updatedLocalProperties);
        modifiedModel.setDependencies(dependencyModel.getDependencies());

        //If there is a DependencyManagement section, update that section too
        if (model.getDependencyManagement() != null) {
            List<Dependency> managementDependencies = model.getDependencyManagement().getDependencies();
            Model dependencyManagementModel = updateToLatestInLocation(pomLocation, managementDependencies,
                    globalProperties, localProperties);
            updatedLocalProperties = dependencyManagementModel.getProperties();

            isManagementModelUpdated = getUpdateStatusFromProperties(updatedLocalProperties);

            DependencyManagement dependencyManagement = modifiedModel.getDependencyManagement();
            dependencyManagement.setDependencies(dependencyManagementModel.getDependencies());
            modifiedModel.setDependencyManagement(dependencyManagement);

        }
        boolean isPomModelUpdated = getUpdateStatusForModel(modifiedModel, isDependencyModelUpdated,
                isManagementModelUpdated);

        if (isPomModelUpdated) {
            return POMWriter.writePom(modifiedModel);
        } else {
            return false;
        }
    }

    /**
     * Used to identify the update state of dependencies from the model
     * This method check whether the dependencies/Management dependencies updated,
     *
     * @param modifiedModel            org.apache.maven.model.Model
     * @param isDependencyModelUpdated boolean value indicating dependencies updated or not
     * @param isManagementModelUpdated boolean value indicating management dependencies updated or not
     * @return boolean value indicating model updated or not
     */
    private boolean getUpdateStatusForModel(Model modifiedModel, boolean isDependencyModelUpdated,
                                            boolean isManagementModelUpdated) {

        if (modifiedModel.getDependencyManagement() == null) {
            return isDependencyModelUpdated;
        } else {
            return isDependencyModelUpdated || isManagementModelUpdated;
        }
    }

    /**
     * Retrieve Dependency updated status from properties
     *
     * @param updatedLocalProperties java.util.Properties
     * @return boolean value indicating dependencies updated or not
     */
    private boolean getUpdateStatusFromProperties(Properties updatedLocalProperties) {

        return Boolean.valueOf(updatedLocalProperties.getProperty(Constants.UPDATE_STATUS_TEMPORARY_PROPERTY));
    }

    protected abstract Model updateToLatestInLocation(String pomLocation, List<Dependency> managementDependencies,
                                                      Properties globalProperties, Properties localProperties);

    /**
     * Identify the value for a particular key from local or global properties (two java.util.Properties object)
     *
     * @param key              String identifier for property
     * @param localProperties  java.util.Properties object
     * @param globalProperties java.util.Properties
     * @return String value for the property key
     */
    private String getProperty(String key, Properties localProperties, Properties globalProperties) {

        String value = localProperties.getProperty(key);
        if (value == null) {
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    /**
     * Retrieve property key by removing prefixes and suffixes
     * Example- ${carbon.kernel.version} -> carbon.kernel.version
     *
     * @param propertyKey String that need to modify to get key value
     * @return String property key
     */
    private String getVersionKey(String propertyKey) {

        return propertyKey.substring(2, propertyKey.length() - 1);
    }

    /**
     * Identify whether a particular String is a property key or not
     * Example - distinguish between "2.4.1" vs "${carbon.kernel.version}"
     *
     * @param versionTag String that represent the version in dependency
     * @return boolean value indicating whether a property key or not
     */
    private boolean isPropertyTag(String versionTag) {

        return versionTag != null && versionTag.startsWith(Constants.PROPERTY_START_TAG)
                && versionTag.endsWith(Constants.PROPERTY_END_TAG);
    }

    /**
     * Method to add new entries to outdated dependency list
     *
     * @param outdatedDependencies OutdatedDependency List to update
     * @param dependency           Dependency object corresponding to outdated dependency
     * @param latestVersion        Latest version available to update (Based on the DependencyUpdater implementation)
     * @return Updated list of outdated dependencies
     */
    List<OutdatedDependency> updateOutdatedDependencyList(List<OutdatedDependency> outdatedDependencies,
                                                          Dependency dependency, String latestVersion) {

        ArrayList<String> versionList = NexusRepoManagerConnector.getVersionList(dependency);
        OutdatedDependency outdatedDependency = new OutdatedDependency(dependency);
        outdatedDependency.setLatestVersion(latestVersion);
        outdatedDependency.setNewVersions(versionList);
        outdatedDependencies.add(outdatedDependency);
        return outdatedDependencies;
    }

    /**
     * This method replace a dependency from list with latest version given
     *
     * @param dependencies  List of Dependencies
     * @param dependency    Dependency object that need to update
     * @param latestVersion Latest version available for the dependency
     * @return updated list of Dependencies
     */
    List<Dependency> updateDependencyList(List<Dependency> dependencies, Dependency dependency, String latestVersion) {

        Dependency dependencyClone = dependency.clone();
        dependencyClone.setVersion(latestVersion);
        dependencies.remove(dependency);
        dependencies.add(dependencyClone);
        return dependencies;
    }

    /**
     * This method adds update state to properties list to identify whether dependencies are updated or not
     *
     * @param localProperties list of local properties
     * @param updateCount     number of dependencies updated in the update process
     * @return updated localProperties variable
     */
    Properties addUpdateStatus(Properties localProperties, int updateCount) {

        if (updateCount == 0) {
            localProperties.put(Constants.UPDATE_STATUS_TEMPORARY_PROPERTY, "false");
        } else {
            localProperties.put(Constants.UPDATE_STATUS_TEMPORARY_PROPERTY, "true");
        }
        return localProperties;
    }

    /**
     * Some dependencies refers property values as versions, This method determines whether a given dependency
     * is such dependency
     * if so, the property value will be placed as the version
     *
     * @param dependency       dependency object
     * @param localProperties  properties mentioned in the pom.xml
     * @param globalProperties properties included in the root pom.xml
     * @return updated dependency object
     */
    Dependency replaceVersionFromPropertyValue(Dependency dependency, Properties localProperties,
                                               Properties globalProperties) {

        String currentVersion = dependency.getVersion();
        if (isPropertyTag(currentVersion)) {
            String propertyKey = getVersionKey(currentVersion);
            currentVersion = getProperty(propertyKey, localProperties, globalProperties);
            if (currentVersion == null) {
                if (log.isDebugEnabled()) {
                    log.info("Property value not found for "
                            + dependency.getGroupId().replaceAll("[\r\n]", "")
                            + ":" + dependency.getArtifactId().replaceAll("[\r\n]", ""));
                }
            } else {
                dependency.setVersion(currentVersion);
            }
        }
        return dependency;
    }

}
