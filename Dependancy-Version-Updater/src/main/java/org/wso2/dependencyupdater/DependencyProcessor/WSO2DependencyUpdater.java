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
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.wso2.dependencyupdater.Application;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.OutdatedDependency;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Updater for WSO2 dependencies
 */
public abstract class WSO2DependencyUpdater extends DependencyUpdater {

    private static final Log log = LogFactory.getLog(Application.class);

    /**
     * Rules for updating WSO2 Dependencies
     *
     * @param model            org.apache.maven.model.Model that represent the attributes of a particular pom.xml
     * @param globalProperties java.util.Properties object with all the properties included in the root pom.xml
     * @return state of update operation
     */
    public boolean updateModel(Model model, Properties globalProperties) {

        boolean dependencyModelUpdated;
        boolean managementModelUpdated = false;

        Model modifiedModel = model.clone();
        String pomLocation = model.getProjectDirectory().toString();
        List<Dependency> modelDependencies = model.getDependencies();

        Properties localProperties = model.getProperties();
        Model dependencyModel = updateToLatestInLocation(pomLocation, modelDependencies, globalProperties, localProperties);

        Properties updatedLocalProperties = dependencyModel.getProperties();
        dependencyModelUpdated = getUpdateStatusFromProperties(updatedLocalProperties);
        updatedLocalProperties.remove("update.status");

        modifiedModel.setDependencies(dependencyModel.getDependencies());
        modifiedModel.setProperties(updatedLocalProperties);
        //If there is a DependencyManagement section, update that section too
        if (model.getDependencyManagement() != null) {
            List<Dependency> managementDependencies = model.getDependencyManagement().getDependencies();
            Model dependencyManagementModel = updateToLatestInLocation(pomLocation, managementDependencies, globalProperties, localProperties);
            updatedLocalProperties = dependencyManagementModel.getProperties();

            managementModelUpdated = getUpdateStatusFromProperties(updatedLocalProperties);
            updatedLocalProperties.remove("update.status");

            DependencyManagement dependencyManagement = modifiedModel.getDependencyManagement();
            dependencyManagement.setDependencies(dependencyManagementModel.getDependencies());
            modifiedModel.setDependencyManagement(dependencyManagement);

            Properties managementProperties = updatedLocalProperties;
            for (Object property : managementProperties.keySet()) {
                modifiedModel.addProperty(property.toString(), managementProperties.getProperty(property.toString()));
            }

        }
        boolean status = getUpdateStatusForModel(modifiedModel, dependencyModelUpdated, managementModelUpdated);

        return status && POMWriter.writePom(modifiedModel);
    }

    /**
     * Used to identify the update state of dependencies from the model
     * This method check whether the dependencies/Management dependencies updated,
     *
     * @param modifiedModel          org.apache.maven.model.Model
     * @param dependencyModelUpdated boolean value indicating dependencies updated or not
     * @param managementModelUpdated boolean value indicating management dependencies updated or not
     * @return boolean value indicating model updated or not
     */
    private boolean getUpdateStatusForModel(Model modifiedModel, boolean dependencyModelUpdated, boolean managementModelUpdated) {

        if (modifiedModel.getDependencyManagement() == null) {
            return dependencyModelUpdated;
        } else {
            return dependencyModelUpdated || managementModelUpdated;
        }
    }

    /**
     * Retrieve Dependency updated status from properties
     *
     * @param updatedLocalProperties java.util.Properties
     * @return boolean value indicating dependencies updated or not
     */
    private boolean getUpdateStatusFromProperties(Properties updatedLocalProperties) {

        return Boolean.valueOf(updatedLocalProperties.getProperty("update.status"));
    }

    protected abstract Model updateToLatestInLocation(String pomLocation, List<Dependency> managementDependencies, Properties globalProperties, Properties localProperties);

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

        return versionTag != null && versionTag.startsWith(Constants.PROPERTY_START_TAG) && versionTag.endsWith(Constants.PROPERTY_END_TAG);
    }

    /**
     * Method to add new entries to outdated dependency list
     *
     * @param outdatedDependencies OutdatedDependency List to update
     * @param dependency           Dependency object corresponding to outdated dependency
     * @param latestVersion        Latest version available to update (Based on the DependencyUpdater implementation)
     * @return Updated list of outdated dependencies
     */
    List<OutdatedDependency> updateOutdatedDependencyList(List<OutdatedDependency> outdatedDependencies, Dependency dependency, String latestVersion) {

        ArrayList<String> versionList = MavenCentralConnector.getVersionList(dependency);
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
     * This method validate a Dependency against a set of rules to identify whether dependency needs a update or not
     *
     * @param dependency       Dependency Object
     * @param localProperties  java.util.Properties Object with properties in the local pom.xml file
     * @param globalProperties java.util.Properties Object with properties in the root pom.xml file
     * @return boolean value indicating whether dependency should update or not
     */
    boolean isValidUpdate(Dependency dependency, Properties localProperties, Properties globalProperties) {

        String currentVersion = dependency.getVersion();
        if (isPropertyTag(currentVersion)) {
            String propertyKey = getVersionKey(currentVersion);
            currentVersion = getProperty(propertyKey, localProperties, globalProperties);
            if (currentVersion == null) {
                return false;
            }
            dependency.setVersion(currentVersion);

        }
        log.info(dependency.getGroupId() + ":" + dependency.getArtifactId() + "  " + currentVersion + "  ");
        if (currentVersion == null) {
            if (log.isDebugEnabled()) {
                log.info("current version is null");
            }
            return false;
        }
        if (!dependency.getGroupId().contains(Constants.WSO2_GROUP_TAG)) {
            if (log.isDebugEnabled()) {
                log.info("Dependency does not belongs to WSO2");
            }
            return false;
        }
        String latestVersion = MavenCentralConnector.getLatestMinorVersion(dependency);

        if (latestVersion.length() == 0) {
            if (log.isDebugEnabled()) {
                log.info("Latest version Not Found");
            }

            return false;
        } else if (latestVersion.equals(currentVersion)) {
            if (log.isDebugEnabled()) {
                log.info("Already in the latest version");
            }
            return false;
        }
        log.info("Dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + " Updated from version " + currentVersion + " to " + latestVersion);
        return true;
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
            localProperties.put("update.status", "false");
        } else {
            localProperties.put("update.status", "true");
        }
        return localProperties;
    }

}