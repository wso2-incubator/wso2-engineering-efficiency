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

package org.wso2.dependencyupdater.product.builder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.filehandler.ConfigFileReader;
import org.wso2.dependencyupdater.filehandler.MavenOutputHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

/**
 * Contains methods for Invoking maven commands
 */

public class MavenInvoker {

    private static final Log log = LogFactory.getLog(MavenInvoker.class);

    /**
     * Method to invoke maven clean install command
     *
     * @param directoryName Directory Name for project
     * @return Build Status
     */
    public static int mavenBuild(final String directoryName) {

        String directoryPath = ConfigFileReader.getRootPath() + directoryName;
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(directoryPath));
        File logFile = new File(ConfigFileReader.getReportPath() +
                getComponentName(directoryName) + File.separator + Constants.MAVEN_LOG_FILE_NAME);
        logFile.getParentFile().mkdirs();

        try {
            InvocationOutputHandler outputHandler = new MavenOutputHandler(logFile);
            request.setOutputHandler(outputHandler);
        } catch (FileNotFoundException e) {
            log.error("Location for log files not found", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding format not supported", e);
        }

        request.setGoals(Collections.singletonList(Constants.MAVEN_INVOKE_COMMAND));
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(ConfigFileReader.getMavenHome()));
        InvocationResult invocationResult;
        try {
            invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() == 0) {
                log.info("Maven build Successful :"
                        + getComponentName(directoryName).replaceAll("[\r\n]", ""));
                return Constants.BUILD_SUCCESS_CODE;
            } else {
                log.info("Maven build Failed :"
                                + getComponentName(directoryName).replaceAll("[\r\n]", ""),
                        invocationResult.getExecutionException());
            }

        } catch (MavenInvocationException e) {

            log.error("Failed to invoke :" + Constants.MAVEN_INVOKE_COMMAND,e);
        }
        return Constants.BUILD_FAIL_CODE;
    }

    /**
     * This method determines original component  name from the temporary file name
     *
     * @param temporaryFileName Temporary file name
     * @return Repository Name
     */
    private static String getComponentName(String temporaryFileName) {
        //removes the suffix string added to create the temporary file name
        return temporaryFileName.substring(0, temporaryFileName.length() - Constants.SUFFIX_TEMP_FILE.length());
    }

}

