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
package org.wso2.dependencyupdater.filehandler;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dependencyupdater.Application;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.exceptions.DependencyUpdaterRepositoryException;
import org.wso2.dependencyupdater.model.Repository;

import java.io.File;
import java.io.IOException;

/**
 * Contains methods for handling repositories
 */
public class RepositoryHandler {

    private static final Log log = LogFactory.getLog(Application.class);

    /**
     * This method copy a repository to a temporary location
     *
     * @param repository Repository that needs to be copied to a temporary location
     * @return boolean status indicating the success of copying process
     */
    public static String copyProjectToTempDirectory(Repository repository) throws DependencyUpdaterRepositoryException {

        String sourcePath = ConfigFileReader.getRootPath() + repository.getName();
        String destinationPath = sourcePath + Constants.SUFFIX_TEMP_FILE;
        File source = new File(sourcePath);
        File dest = new File(destinationPath);
        try {
            deleteDirectory(destinationPath);
            FileUtils.copyDirectory(source, dest);
            log.info("Temporary file created for " + repository.getName().replaceAll("[\r\n]", ""));
            return repository.getName() + Constants.SUFFIX_TEMP_FILE;
        } catch (IOException e) {
            throw new DependencyUpdaterRepositoryException("Directory not found for copying", e);
        }
    }

    /**
     * Method for deleting temporary project directories
     *
     * @param destination location of the Directory
     */
    private static void deleteDirectory(String destination) {

        try {
            FileUtils.deleteDirectory(new File(destination));
            log.info("Existing temporary folder deleted :" + destination.replaceAll("[\r\n]", ""));
        } catch (IOException e) {

            log.error("Directory not found for deleting", e);
        }
    }

}
