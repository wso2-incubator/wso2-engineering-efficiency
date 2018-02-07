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

import org.wso2.Constants;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.wso2.Model.OutdatedDependency;
import org.wso2.ReportGenerator.OutdatedDependencyReporter;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * TODO:Class level comment
 */
public class WSO2DependencyUpdater extends DependencyUpdater {

    public Model updateModel(Model model,Properties globalProperties){

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
    private Model updateToLatestInLocation(String pomLocation,List<Dependency> dependencies, Properties globalProperties, Properties localProperties) {
        List<Dependency> updatedDependencies = getListCopy(dependencies);
        List<OutdatedDependency> outdatedDependencies = new ArrayList<OutdatedDependency>();
        OutdatedDependencyReporter outdatedDependencyReporter = new OutdatedDependencyReporter();
        Model model = new Model();
        List<Dependency> dependenciesNotFound = new ArrayList<Dependency>();
        for (Dependency dependency : dependencies) {
            String currentVersion = dependency.getVersion();
            String groupId = dependency.getGroupId();
            if (groupId.contains(Constants.WSO2_GROUP_TAG) ) {
                String latestVersion = MavenCentralConnector.getLatestVersion(dependency);
                if(latestVersion.equals(Constants.EMPTY_STRING)){
                    dependenciesNotFound.add(dependency);
                }
                else if(currentVersion!=null){
                    if(isPropertyTag(currentVersion)){
                        String versionKey = getVersionKey(currentVersion);
                        String version = getProperty(versionKey,localProperties,globalProperties);

                        if(!latestVersion.equals(version)){
                            dependency.setVersion(version);
                            updatedDependencies = updateDependecyList(updatedDependencies,dependency,latestVersion);
                            outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies,dependency,latestVersion);
                        }
                    }
                    else{
                        if(!latestVersion.equals(currentVersion)){
                            dependency.setVersion(currentVersion);
                            updatedDependencies = updateDependecyList(updatedDependencies,dependency,latestVersion);
                            outdatedDependencies = updateOutdatedDependencyList(outdatedDependencies,dependency,latestVersion);
                        }
                    }
                }
            }
        }
        model.setDependencies(updatedDependencies);
        model.setProperties(localProperties);
        System.out.println(outdatedDependencies);
        outdatedDependencyReporter.setReportEntries(outdatedDependencies);
        outdatedDependencyReporter.saveToCSV(Constants.ROOT_PATH+"/Reports/"+pomLocation.replace('/','_'));
        return model;
    }

    private List<OutdatedDependency> updateOutdatedDependencyList(List<OutdatedDependency> outdatedDependencies, Dependency dependency, String latestVersion) {
        ArrayList<String> versionList =MavenCentralConnector.getVersionList(dependency);
        OutdatedDependency outdatedDependency = new OutdatedDependency(dependency);
        outdatedDependency.setLatestVersion(latestVersion);
        outdatedDependency.setNewVersions(versionList);
        outdatedDependencies.add(outdatedDependency);
        return outdatedDependencies;
    }

    private List<Dependency> updateDependecyList(List<Dependency> dependencies,Dependency dependency, String latestVersion) {
        Dependency dependencyClone  =  dependency.clone();
        dependencyClone.setVersion(latestVersion);
        dependencies.remove(dependency);
        dependencies.add(dependencyClone);
        return dependencies;

    }

    private ArrayList<Dependency> getListCopy(List<Dependency> dependencyList) {
        ArrayList<Dependency> dependencies = new ArrayList<Dependency>();
        for (Dependency dependency : dependencyList) {
            dependencies.add(dependency);
        }
        return dependencies;
    }

    private String getProperty(String key,Properties localProperties, Properties globalProperties){
        String value = localProperties.getProperty(key);
        if(value==null){
            value = globalProperties.getProperty(key);
        }
        return value;
    }

    private String getVersionKey(String propertyKey){
        return propertyKey.substring(2,propertyKey.length()-1);
    }

    private boolean isPropertyTag(String versionTag){
        if(versionTag.startsWith(Constants.PROPERTY_START_TAG) && versionTag.endsWith(Constants.PROPERTY_END_TAG)){
            return true;
        }
        else{
            return false;
        }
    }






}
