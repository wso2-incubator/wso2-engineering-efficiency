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
 * TODO:Class level comment
 */
public abstract class WSO2DependencyUpdater extends DependencyUpdater {

    private static final Log log = LogFactory.getLog(Application.class);

    public boolean updateModel(Model model, Properties globalProperties) {

        boolean dependencyModelUpdated;
        boolean managementModelUpdated = false;

        Model modifiedModel = model.clone();
        String pomLocation = model.getProjectDirectory().toString();
        List<Dependency> modelDependencies = model.getDependencies();

        Properties localProperties = model.getProperties();
        Model dependencyModel = updateToLatestInLocation(pomLocation, modelDependencies, globalProperties, localProperties);
        Properties updatedLocalProperties = dependencyModel.getProperties();
        dependencyModelUpdated = getUpdateStatusFromModel(updatedLocalProperties);
        updatedLocalProperties.remove("update.status");
        modifiedModel.setDependencies(dependencyModel.getDependencies());
        modifiedModel.setProperties(updatedLocalProperties);
        if (model.getDependencyManagement() != null) {
            List<Dependency> managementDependencies = model.getDependencyManagement().getDependencies();
            Model dependencyManagementModel = updateToLatestInLocation(pomLocation, managementDependencies, globalProperties, localProperties);
            updatedLocalProperties = dependencyManagementModel.getProperties();
            managementModelUpdated = getUpdateStatusFromModel(updatedLocalProperties);
            updatedLocalProperties.remove("update.status");
            DependencyManagement dependencyManagement = modifiedModel.getDependencyManagement();
            dependencyManagement.setDependencies(dependencyManagementModel.getDependencies());
            modifiedModel.setDependencyManagement(dependencyManagement);
            Properties managementProperties = updatedLocalProperties;
            for (Object property : managementProperties.keySet()) {
                modifiedModel.addProperty(property.toString(), managementProperties.getProperty(property.toString()));
            }

        }
        boolean status = getUpdateStatusFromProperties(modifiedModel, dependencyModelUpdated, managementModelUpdated);
        POMWriter.writePom(modifiedModel);
        return status;
    }

    private boolean getUpdateStatusFromProperties(Model modifiedModel, boolean dependencyModelUpdated, boolean managementModelUpdated) {

        if (modifiedModel.getDependencyManagement() == null) {
            return dependencyModelUpdated;
        } else {
            return dependencyModelUpdated || managementModelUpdated;
        }
    }

    private boolean getUpdateStatusFromModel(Properties updatedLocalProperties) {

        return Boolean.valueOf(updatedLocalProperties.getProperty("update.status"));
    }

    protected abstract Model updateToLatestInLocation(String pomLocation, List<Dependency> managementDependencies, Properties globalProperties, Properties localProperties);

    protected String getProperty(String key, Properties localProperties, Properties globalProperties) {

        String value = localProperties.getProperty(key);
        if (value == null) {
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    protected String getVersionKey(String propertyKey) {

        return propertyKey.substring(2, propertyKey.length() - 1);
    }

    protected boolean isPropertyTag(String versionTag) {

        if (versionTag == null) {
            return false;
        }
        if (versionTag.startsWith(Constants.PROPERTY_START_TAG) && versionTag.endsWith(Constants.PROPERTY_END_TAG)) {
            return true;
        } else {
            return false;
        }
    }

    protected ArrayList<Dependency> getListCopy(List<Dependency> dependencyList) {

        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencyList) {
            dependencies.add(dependency);
        }
        return dependencies;
    }

    protected List<OutdatedDependency> updateOutdatedDependencyList(List<OutdatedDependency> outdatedDependencies, Dependency dependency, String latestVersion) {

        ArrayList<String> versionList = MavenCentralConnector.getVersionList(dependency);
        OutdatedDependency outdatedDependency = new OutdatedDependency(dependency);
        outdatedDependency.setLatestVersion(latestVersion);
        outdatedDependency.setNewVersions(versionList);
        outdatedDependencies.add(outdatedDependency);
        return outdatedDependencies;
    }

    protected List<Dependency> updateDependencyList(List<Dependency> dependencies, Dependency dependency, String latestVersion) {

        Dependency dependencyClone = dependency.clone();
        dependencyClone.setVersion(latestVersion);
        dependencies.remove(dependency);
        dependencies.add(dependencyClone);
        return dependencies;
    }

    protected boolean isValidUpdate(Dependency dependency, Properties localProperties, Properties globalProperties) {

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
            if(log.isDebugEnabled()){
                log.info("current version is null");
            }
            return false;
        }
        if (!dependency.getGroupId().contains(Constants.WSO2_GROUP_TAG)) {
            if(log.isDebugEnabled()){
                log.info("Dependency does not belongs to wso2");
            }
            return false;
        }
        String latestVersion = MavenCentralConnector.getLatestMinorVersion(dependency);

        if (latestVersion.length() == 0) {
            if(log.isDebugEnabled()){
                log.info("latest version not found");
            }

            return false;
        } else if (latestVersion.equals(currentVersion)) {
            if(log.isDebugEnabled()){
                log.info("Already in the latest version");
            }
            return false;
        }
        log.info("Dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + " Updated from version " + currentVersion + " to " + latestVersion);
        return true;
    }

}
