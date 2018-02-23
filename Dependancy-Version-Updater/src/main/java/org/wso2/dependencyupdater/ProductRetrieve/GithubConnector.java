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
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.Model.Component;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;

public class GithubConnector {

    private static final Log log = LogFactory.getLog(GithubConnector.class);
    private static Git git;

    public static boolean update(Component component) {

        boolean status = false;
        try {
            log.info("Calling git pull for component: " + component.getName());
            status = Git.open(new File(Constants.ROOT_PATH + File.separator + component.getName())).pull().call().isSuccessful();

        } catch (RefNotAdvertisedException exception) {
            log.error("Branch not found in remote repository. Therefore cannot update the existing local repository");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return status;
    }

    private static boolean clone(Component component) {

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider("dimuthnc", "Priyadarshani@143");
        try {
            log.info("Cloning the repository to local storage: " + component.getName());
            git = Git.cloneRepository()
                    .setCredentialsProvider(credentialsProvider)
                    .setURI(component.getUrl())
                    .setDirectory(new File(Constants.ROOT_PATH + File.separator + component.getName())).call();
            return true;

        } catch (InvalidRemoteException e) {
            log.info("Remote repository not found :" + component.getName());

        } catch (TransportException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean retrieveComponent(Component component) {

        log.info("Retrieving component: " + component.getName());

        File repository = new File(Constants.ROOT_PATH + File.separator + component.getName());
        if (repository.exists() && repository.isDirectory()) {
            log.info("Existing repository found for : " + component.getName());
            update(component);
            return true;
        } else {
            log.info("Existing repository not found for : " + component.getName());
            boolean cloned = clone(component);
            if (!cloned) {
                return false;
            }
            return true;
        }
    }



}
