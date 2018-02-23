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
import org.wso2.dependencyupdater.DatabaseHandler.LocalDBConnector;
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

    static String MAVEN_HOME;

    private static final Log log = LogFactory.getLog(Application.class);

    public static void main(String[] args) {

        ConfigFileReader.readConfigFile();
        MAVEN_HOME = ConfigFileReader.getMavenHome();
        log.info("Dependency updater started");
        ArrayList<Component> components = getAllComponents();

        for (Component component : components) {
            log.info("Processing the component " + component.getName() + " started");
            GithubConnector.retrieveComponent(component);
            long currentTime = System.currentTimeMillis();
            boolean copySuccessful = RepositoryHandler.copyProjectToTempDirectory(component);
            if (copySuccessful) {
                boolean componentUpdateStatus = updateComponentDependencies(component.getName() + Constants.SUFFIX_TEMP_FILE);
                updateComponentDependencies(component.getName() + Constants.SUFFIX_TEMP_FILE);
                if (componentUpdateStatus) {
                    log.info(component.getName() + "  Component updated. Therefore building with maven");
                    int buildStatus = MavenInvoker.mavenBuild(MAVEN_HOME, component.getName() + Constants.SUFFIX_TEMP_FILE);
                    component.setStatus(buildStatus);
                    LocalDBConnector.insertBuildStatus(component, currentTime);
                } else {
                    log.info(component.getName() + "  Component not updated.");
                    int latestBuildStatus = LocalDBConnector.getLatestBuild(component);
                    if (latestBuildStatus == 2) {
                        int buildStatus = MavenInvoker.mavenBuild(MAVEN_HOME, component.getName() + Constants.SUFFIX_TEMP_FILE);
                        component.setStatus(buildStatus);
                        LocalDBConnector.insertBuildStatus(component, currentTime);
                    } else {
                        component.setStatus(latestBuildStatus);
                        LocalDBConnector.insertBuildStatus(component, currentTime);
                    }

                }
            }
            log.info("Processing the component " + component.getName() + " finished");
        }
    }

    private static ArrayList<Component> getAllComponents() {

        return LocalDBConnector.getAllComponents();
    }

    private static boolean updateComponentDependencies(String fileName) {

        boolean updateStatus = false;
        String projectPath = Constants.ROOT_PATH + fileName;
        ArrayList<Model> modelList = new ArrayList<Model>();

        POMReader pomReader = new POMReader();
        //DependencyUpdater DependencyUpdater = new WSO2DependencyMajorUpdater(); // to update wso2 dependencies to latest available version
        DependencyUpdater DependencyUpdater = new WSO2DependencyMinorUpdater(); // to update wso2 dependencies to latest version with no major upgrades

        Model model = pomReader.getPomModel(projectPath); //reading the root pom as a model
        if (model != null) {
            Properties properties = model.getProperties();
            properties.setProperty(Constants.PROJECT_VERSION_STRING, model.getVersion());
            modelList.add(model);

            List<String> modules = model.getModules();
            for (String module : modules) {
                model = pomReader.getPomModel(projectPath + File.separator + module); //create model for each child pom mentioned in root pom
                modelList.add(model);
            }
            for (Model childModel : modelList) {
                boolean pomUpdateState = DependencyUpdater.updateModel(childModel, properties);
                log.info("pom.xml File updated :" + pomUpdateState);
                if (pomUpdateState) {
                    updateStatus = true;
                }
            }
        }
        return updateStatus;
    }
}
