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
import org.wso2.dependencyupdater.dependency.processor.DependencyUpdater;
import org.wso2.dependencyupdater.dependency.processor.POMReader;
import org.wso2.dependencyupdater.dependency.processor.WSO2DependencyMajorUpdater;
import org.wso2.dependencyupdater.exceptions.DependencyUpdaterConfigurationException;
import org.wso2.dependencyupdater.exceptions.DependencyUpdaterRepositoryException;
import org.wso2.dependencyupdater.filehandler.ConfigFileReader;
import org.wso2.dependencyupdater.filehandler.RepositoryHandler;
import org.wso2.dependencyupdater.model.Repository;
import org.wso2.dependencyupdater.product.builder.MavenInvoker;
import org.wso2.dependencyupdater.report.generator.EmailGenerator;
import org.wso2.dependencyupdater.repository.handler.RepositoryManager;

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

        try {
            ConfigFileReader.loadConfigurations();
            RepositoryManager repositoryManager = new RepositoryManager();
            EmailGenerator emailGenerator = new EmailGenerator();

            ArrayList<Repository> repositories = repositoryManager.getAllComponents();

            // to update wso2 dependencies to latest version with no major upgrades
            //DependencyUpdater dependencyUpdater = new WSO2DependencyMinorUpdater();

            // to update wso2 dependencies to latest available version
            DependencyUpdater dependencyUpdater = new WSO2DependencyMajorUpdater();

            for (Repository repository : repositories) {
                if (log.isDebugEnabled()) {
                    log.debug(Constants.LOG_SEPARATOR + Constants.LOG_SEPARATOR);
                    log.debug("Repository processing started :" + repository.getName()
                            .replaceAll("[\r\n]", ""));
                }

                try {
                    boolean isRepoUpdateSuccessful = repositoryManager.retrieveComponent(repository);
                    long updatedTimeStamp = System.currentTimeMillis();
                    if (isRepoUpdateSuccessful) {
                        String componentTemporaryDirectoryName = RepositoryHandler
                                .copyProjectToTempDirectory(repository);
                        boolean isDependencyUpdateSuccessful = updateComponent(dependencyUpdater,
                                componentTemporaryDirectoryName);
                        if (isDependencyUpdateSuccessful) {
                            //if the repository dependencies are updated, invoke a maven build
                            if (log.isDebugEnabled()) {
                                log.debug("Repository updated :" + repository.getName()
                                        .replaceAll("[\r\n]", ""));
                            }
                            int buildStatus = MavenInvoker.mavenBuild(componentTemporaryDirectoryName);
                            repository.setStatus(buildStatus);
                            repositoryManager.insertBuildStatus(repository, updatedTimeStamp);
                        } else {
                            //if repository dependencies are not updated,
                            // try to get the latest build status for the repository and save it as the current status
                            if (log.isDebugEnabled()) {
                                log.debug("Repository not updated :" + repository.getName()
                                        .replaceAll("[\r\n]", ""));
                            }
                            int latestBuildStatus = repositoryManager.getLatestBuild(repository);
                            if (latestBuildStatus == Constants.BUILD_NOT_AVAILABLE_CODE) {
                                int buildStatus = MavenInvoker.mavenBuild(componentTemporaryDirectoryName);
                                repository.setStatus(buildStatus);
                                repositoryManager.insertBuildStatus(repository, updatedTimeStamp);
                            } else {
                                //if the repository has never built before, build the repository and save the status
                                repository.setStatus(latestBuildStatus);
                                repositoryManager.insertBuildStatus(repository, updatedTimeStamp);
                            }
                        }
                        RepositoryHandler.deleteDirectory(ConfigFileReader.getRootPath()
                                +File.separator+componentTemporaryDirectoryName);
                        emailGenerator.sendEmail("dimuthcse@gmail.com",
                                ConfigFileReader.getReportPath()+repository.getName(),updatedTimeStamp);
                    } else {
                        if (log.isDebugEnabled()) {
                            log.debug("Repository retrieving failed:" + repository.getName()
                                    .replaceAll("[\r\n]", ""));
                        }
                        repository.setStatus(Constants.RETRIEVE_FAILED_CODE);
                        repositoryManager.insertBuildStatus(repository, updatedTimeStamp);
                    }
                    if (log.isDebugEnabled()) {
                        log.debug("Repository processing finished :" + repository.getName()
                                .replaceAll("[\r\n]", ""));
                    }
                } catch (DependencyUpdaterRepositoryException e) {
                    log.error("Error occurred in updating the repository " + repository, e);
                }
                if (log.isDebugEnabled()) {
                    log.debug(Constants.LOG_SEPARATOR + Constants.LOG_SEPARATOR);
                }
            }
            repositoryManager.closeConnection();
        } catch (DependencyUpdaterConfigurationException e) {
            log.error("Error occurred in reading configuration file", e);
        }

    }

    /**
     * Method contains procedure for updating a component with all of its pom files
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
                model = POMReader.getPomModel(componentPath + File.separator + module);
                //create model for each child pom mentioned in root pom
                modelList.add(model);
            }
            for (Model pomModel : modelList) {
                boolean isPomUpdated = dependencyUpdater.updateModel(pomModel, properties);
                //If at least one pom file updated, isComponentUpdate will set to true
                if (isPomUpdated) {
                    isComponentUpdated = true;
                }
            }
        }
        return isComponentUpdated;
    }
}
