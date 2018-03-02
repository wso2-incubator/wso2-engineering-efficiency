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
import org.wso2.dependencyupdater.ProductRetrieve.GithubConnector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Main Class of the application
 */
public class Application {

    private static final Log log = LogFactory.getLog(Application.class);

    public static void main(String[] args) {
        //Reading the configurations from the config file
        ConfigFileReader.readConfigFile();
        ArrayList<Component> components = getAllComponents();

        DependencyUpdater dependencyUpdater = new WSO2DependencyMinorUpdater(); // to update wso2 dependencies to latest version with no major upgrades
        //DependencyUpdater dependencyUpdater = new WSO2DependencyMajorUpdater(); // to update wso2 dependencies to latest available version

        for (Component component : components) {
            log.info(Constants.LOG_SEPERATOR);
            log.info("Component processing started :" + component.getName());

            boolean gitUpdateSuccessful = GithubConnector.retrieveComponent(component);
            long updatedTimeStamp = System.currentTimeMillis();
            boolean copySuccessful = false;
            if(gitUpdateSuccessful){
                copySuccessful = RepositoryHandler.copyProjectToTempDirectory(component);
            }

            String componentTemporaryDirectoryName = component.getName() + Constants.SUFFIX_TEMP_FILE;

            if (copySuccessful) {

                boolean componentUpdateStatus = updateComponentDependencies(dependencyUpdater, componentTemporaryDirectoryName);
                if (componentUpdateStatus) {
                    //if the component is updated, invoke a maven build
                    log.info("Component updated :" + component.getName());
                    int buildStatus = MavenInvoker.mavenBuild(componentTemporaryDirectoryName);
                    component.setStatus(buildStatus);
                    DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);

                } else {
                    //if component is not updated, try to get the latest build status for the component and save it as the current status
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
            }
            else{
                log.info("Component retrieving failed:" + component.getName());
                component.setStatus(Constants.BUILD_FAIL_CODE);
                DatabaseConnector.insertBuildStatus(component, updatedTimeStamp);
            }
            log.info("Component processing finished :" + component.getName());
            log.info(Constants.LOG_SEPERATOR);
        }
    }

    /**
     * This method retrieves component details from database
     *
     * @return list of component objects
     */
    private static ArrayList<Component> getAllComponents() {

        return DatabaseConnector.getAllComponents();
    }

    /**
     * @param dependencyUpdater      DependencyUpdater Object with set of rules to update dependencies
     * @param componentDirectoryName Name of the directory that contains the component
     * @return
     */
    private static boolean updateComponentDependencies(DependencyUpdater dependencyUpdater, String componentDirectoryName) {

        boolean updateStatus = false;
        String componentPath = ConfigFileReader.ROOT_PATH + componentDirectoryName;
        ArrayList<Model> modelList = new ArrayList<Model>();

        POMReader pomReader = new POMReader();

        Model model = pomReader.getPomModel(componentPath); //reading the root pom as a model
        if (model.getPomFile() != null) {
            Properties properties = model.getProperties();
            properties.setProperty(Constants.PROJECT_VERSION_STRING, model.getVersion());
            modelList.add(model);

            List<String> modules = model.getModules();
            for (String module : modules) {
                model = pomReader.getPomModel(componentPath + File.separator + module); //create model for each child pom mentioned in root pom
                modelList.add(model);
            }
            for (Model childModel : modelList) {
                boolean pomUpdateState = dependencyUpdater.updateModel(childModel, properties);
                log.info("pom.xml File updated :" + pomUpdateState);
                //If at least one pom file updated, updateState will set to true
                if (pomUpdateState) {
                    updateStatus = true;
                }
            }
        }
        return updateStatus;
    }
}
