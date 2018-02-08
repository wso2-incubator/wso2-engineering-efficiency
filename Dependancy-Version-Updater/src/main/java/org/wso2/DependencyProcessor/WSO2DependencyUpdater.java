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
package org.wso2.DependencyProcessor;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.wso2.Constants;
import org.wso2.Model.OutdatedDependency;
import org.wso2.ReportGenerator.OutdatedDependencyReporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO:Class level comment
 */
public abstract class WSO2DependencyUpdater extends DependencyUpdater{

    public Model updateModel(Model model, Properties globalProperties){

        Model modifiedModel = model.clone();
        String pomLocation = model.getProjectDirectory().toString();
        List<Dependency> modelDependencies = model.getDependencies();

        Properties localProperties = model.getProperties();
        Model dependencyModel =updateToLatestInLocation(pomLocation,modelDependencies, globalProperties, localProperties);
        modifiedModel.setDependencies(dependencyModel.getDependencies());
        modifiedModel.setProperties(dependencyModel.getProperties());
        if(model.getDependencyManagement()!=null){
            List<Dependency> managementDependencies = model.getDependencyManagement().getDependencies();
            Model  dependencyManagementModel = updateToLatestInLocation(pomLocation,managementDependencies,globalProperties,localProperties);
            DependencyManagement dependencyManagement = modifiedModel.getDependencyManagement();
            dependencyManagement.setDependencies(dependencyManagementModel.getDependencies());
            modifiedModel.setDependencyManagement(dependencyManagement);
            Properties managementProperties = dependencyManagementModel.getProperties();
            for (Object property : managementProperties.keySet()) {
                modifiedModel.addProperty(property.toString(),managementProperties.getProperty(property.toString()));
            }
        }
        return modifiedModel;
    }

    protected abstract Model updateToLatestInLocation(String pomLocation, List<Dependency> managementDependencies, Properties globalProperties, Properties localProperties);

    protected String getProperty(String key, Properties localProperties, Properties globalProperties){
        String value = localProperties.getProperty(key);
        if(value==null){
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    protected String getVersionKey(String propertyKey){
        return propertyKey.substring(2,propertyKey.length()-1);
    }

    protected boolean isPropertyTag(String versionTag) {
        if(versionTag == null){
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
        ArrayList<String> versionList =MavenCentralConnector.getVersionList(dependency);
        OutdatedDependency outdatedDependency = new OutdatedDependency(dependency);
        outdatedDependency.setLatestVersion(latestVersion);
        outdatedDependency.setNewVersions(versionList);
        outdatedDependencies.add(outdatedDependency);
        return outdatedDependencies;
    }

    protected List<Dependency> updateDependencyList(List<Dependency> dependencies, Dependency dependency, String latestVersion) {
        Dependency dependencyClone  =  dependency.clone();
        dependencyClone.setVersion(latestVersion);
        dependencies.remove(dependency);
        dependencies.add(dependencyClone);
        return dependencies;
    }

}
