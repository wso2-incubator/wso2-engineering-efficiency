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

package org.wso2.dependencyupdater.ProductBuilder;

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

import java.io.File;
import java.util.Collections;

/**
 *
 */

public class MavenInvoker {

    private static final Log log = LogFactory.getLog(MavenInvoker.class);

    public static boolean mavenBuild(String mavenHome, String buidFileName) {

        String directoryPath = Constants.ROOT_PATH + buidFileName;
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(directoryPath));
        request.setOutputHandler(new InvocationOutputHandler() {
            public void consumeLine(String s) {

            }
        });
        request.setGoals(Collections.singletonList(Constants.MAVEN_INVOKE_COMMAND));
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));
        InvocationResult invocationResult;
        try {
            invocationResult = invoker.execute(request);
            if (invocationResult.getExitCode() == 0) {
                log.info(buidFileName + " Build Successful");
                return true;
            } else {
                log.info(buidFileName + " Build Failed");
                return false;
            }

        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }

}

