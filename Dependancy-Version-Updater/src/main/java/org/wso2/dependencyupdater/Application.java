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

package org.wso2.dependencyupdater;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.model.Model;
import org.wso2.dependencyupdater.DatabaseHandler.DatabaseConnector;
import org.wso2.dependencyupdater.DependencyProcessor.DependencyUpdater;
import org.wso2.dependencyupdater.DependencyProcessor.POMReader;
import org.wso2.dependencyupdater.DependencyProcessor.WSO2DependencyMinorUpdater;
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.FileHandler.RepositoryHandler;
import org.wso2.dependencyupdater.Model.Component;
import org.wso2.dependencyupdater.ProductBuilder.MavenInvoker;
import org.wso2.dependencyupdater.ProductRetrieve.GitHubConnector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Main Class of the application
 */
public class Application {

    private static final Log log = LogFactory.getLog(Application.class);

    /**
     * Main method of the application
     *
     * @param args Default argument values for main method
     */
    public static void main(String[] args) {

        ConfigFileReader.loadConfigurations();

        ArrayList<Component> components = DatabaseConnector.getAllComponents();

        DependencyUpdater dependencyUpdater = new WSO2DependencyMinorUpdater(); // to update wso2 dependencies to latest version with no major upgrades
        //DependencyUpdater dependencyUpdater = new WSO2DependencyMajorUpdater(); // to update wso2 dependencies to latest available version

        for (Component component : components) {
            log.info(Constants.LOG_SEPARATOR + Constants.LOG_SEPARATOR);
            log.info("Component processing started :" + component.getName());

            boolean isRepoUpdateSuccessful = GitHubConnector.retrieveComponent(component);
            long updatedTimeStamp = System.currentTimeMillis();
            boolean isDirectoryCopyingSuccessful = false;
            if (isRepoUpdateSuccessful) {
                isDirectoryCopyingSuccessful = RepositoryHandler.copyProjectToTempDirectory(component);
            }

            String componentTemporaryDirectoryName = component.getName() + Constants.SUFFIX_TEMP_FILE;

            if (isDirectoryCopyingSuccessful) {

                boolean isDependencyUpdateSuccessful = updateComponent(dependencyUpdater, componentTemporaryDirectoryName);
                if (isDependencyUpdateSuccessful) {
                    //if the component dependencies are updated, invoke a maven build
                    log.info("Component updated :" + component.getName());
                    int buildStatus = MavenInvoker.mavenBuild(componentTemporaryDirectoryName);
                    component.setStatus(buildStatus);
                    DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);

                } else {
                    //if component dependencies are not updated, try to get the latest build status for the component and save it as the current status
                    log.info("Component not updated :" + component.getName());
                    int latestBuildStatus = DatabaseConnector.getLatestBuild(component);
                    if (latestBuildStatus == Constants.BUILD_NOT_AVAILABLE_CODE) {
                        int buildStatus = MavenInvoker.mavenBuild(componentTemporaryDirectoryName);
                        component.setStatus(buildStatus);
                        DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);

                    }
                    //if the component has never built before, build the component and save the status
                    else {
                        component.setStatus(latestBuildStatus);
                        DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);
                    }

                }
            } else {
                log.info("Component retrieving failed:" + component.getName());
                component.setStatus(Constants.RETRIEVE_FAILED_CODE);
                DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);
            }
            log.info("Component processing finished :" + component.getName());
            log.info(Constants.LOG_SEPARATOR + Constants.LOG_SEPARATOR);
        }
    }

    /**
     * This method responsible for reading pom files as Models and calling update for each model
     *
     * @param dependencyUpdater      DependencyUpdater Object with set of rules to update dependencies
     * @param componentDirectoryName Name of the directory that contains the component
     * @return boolean value indicating update process success
     */
    private static boolean updateComponent(DependencyUpdater dependencyUpdater, String componentDirectoryName) {

        boolean isComponentUpdated = false;
        String componentPath = ConfigFileReader.getRootPath() + componentDirectoryName;
        ArrayList<Model> modelList = new ArrayList<>();
        //reading the root pom.xml
        Model model = POMReader.getPomModel(componentPath);
        if (model.getPomFile() != null) {
            Properties properties = model.getProperties();
            properties.setProperty(Constants.PROJECT_VERSION_STRING, model.getVersion());
            modelList.add(model);

            List<String> modules = model.getModules();
            for (String module : modules) {
                //reading inner pom.xml
                model = POMReader.getPomModel(componentPath + File.separator + module); //create model for each child pom mentioned in root pom
                modelList.add(model);
            }
            for (Model pomModel : modelList) {
                boolean isPomUpdated = dependencyUpdater.updateModel(pomModel, properties);
                log.info("pom.xml File updated :" + isPomUpdated);
                //If at least one pom file updated, isComponentUpdate will set to true
                if (isPomUpdated) {
                    isComponentUpdated = true;
                }
            }
        }
        return isComponentUpdated;
    }
}
