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
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.Model.Component;

import java.io.File;
import java.io.IOException;

/**
 * Contains methods for communicating with github
 */
public class GitHubConnector {

    private static final Log log = LogFactory.getLog(GitHubConnector.class);

    /**
     * Method for updating components from github
     *
     * @param component component object for updating
     * @return status of update
     */
    private static boolean update(Component component) {

        boolean status = false;
        try {
            log.info("Calling git pull command for component: " + component.getName());
            status = Git.open(new File(ConfigFileReader.getRootPath() + File.separator + component.getName()))
                    .pull().call().isSuccessful();

        } catch (RefNotAdvertisedException exception) {
            log.error("Branch not found in remote repository. Therefore cannot update the existing local repository" + component.getName());
        } catch (IOException e) {
            log.error("Component directory cannot be found :" + component.getName());
        } catch (GitAPIException e) {
            log.error("Failed to execute git command " + component.getName());
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

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(ConfigFileReader.getGithubUsername(), ConfigFileReader.getGithubPassword());
        try {
            log.info("Cloning the repository to local storage: " + component.getName());
            Git.cloneRepository()
                    .setCredentialsProvider(credentialsProvider)
                    .setURI(component.getUrl())
                    .setDirectory(new File(ConfigFileReader.getRootPath() + File.separator + component.getName())).call();
            return true;

        } catch (InvalidRemoteException e) {
            log.error("Remote repository not found :" + component.getUrl());
        } catch (TransportException e) {
            log.error("Protocol error has occurred while fetching objects " + component.getUrl());
        } catch (GitAPIException e) {
            log.error("Error occurred in Git API " + component.getUrl());
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

        File repository = new File(ConfigFileReader.getRootPath() + File.separator + component.getName());
        if (repository.exists() && repository.isDirectory()) {
            log.info("Existing repository found for : " + component.getName());
            return update(component);

        } else {
            log.info("Existing repository not found for : " + component.getName());
            return clone(component);
        }
    }

}
