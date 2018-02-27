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

package org.wso2.dependencyupdater.ProductRetrieve;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.Model.Component;

import java.io.File;
import java.io.IOException;

/**
 * Contains methods for communicating with github
 */
public class GithubConnector {

    private static final Log log = LogFactory.getLog(GithubConnector.class);
    private static Git git;

    /**
     * Method for updating components from github
     *
     * @param component component object for updating
     * @return status of update
     */
    private static boolean update(Component component) {

        boolean status = false;
        try {
            String logMessage = "Calling git pull command for component: " + component.getName();
            log.info(logMessage);
            status = Git.open(new File(Constants.ROOT_PATH + File.separator + component.getName())).pull().call().isSuccessful();

        } catch (RefNotAdvertisedException exception) {
            String errorMessage = "Branch not found in remote repository. Therefore cannot update the existing local repository" + component.getName();
            log.error(errorMessage);
        } catch (IOException e) {
            String errorMessage = "Component directory cannot be found :" + component.getName();
            log.error(errorMessage);
        } catch (GitAPIException e) {
            String errorMessage = "Failed to execute git command " + component.getName();
            log.error(errorMessage);
        }

        return status;
    }

    /**
     * Method for cloning new component to the local environment
     *
     * @param component component object for cloning
     * @return status of clone
     */
    private static boolean clone(Component component) {

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(ConfigFileReader.GITHUB_USERNAME, ConfigFileReader.GITHUB_PASSWORD);
        try {
            String logMessage = "Cloning the repository to local storage: " + component.getName();
            log.info(logMessage);
            git = Git.cloneRepository()
                    .setCredentialsProvider(credentialsProvider)
                    .setURI(component.getUrl())
                    .setDirectory(new File(Constants.ROOT_PATH + File.separator + component.getName())).call();
            return true;

        } catch (InvalidRemoteException e) {
            String errorMessage = "Remote repository not found :" + component.getUrl();
            log.error(errorMessage);

        } catch (TransportException e) {
            String errorMessage = "Protocol error has occurred while fetching objects " + component.getUrl();
            log.error(errorMessage);
        } catch (GitAPIException e) {
            String errorMessage = "Error occurred in Git API " + component.getUrl();
            log.error(errorMessage);
        }
        return false;
    }

    /**
     * Method responsible for retrieving components from github. Calls Update or clone method based on the existence of a repository
     *
     * @param component Component for retrieving
     * @return Status of retrieving process
     */
    public static boolean retrieveComponent(Component component) {

        log.info("Retrieving component: " + component.getName());

        File repository = new File(Constants.ROOT_PATH + File.separator + component.getName());
        if (repository.exists() && repository.isDirectory()) {
            String logMessage = "Existing repository found for : " + component.getName();
            log.info(logMessage);
            return update(component);

        } else {

            String logMessage = "Existing repository not found for : " + component.getName();
            log.info(logMessage);
            return clone(component);
        }
    }

}
