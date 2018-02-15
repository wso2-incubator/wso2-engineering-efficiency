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

package org.wso2.ProductBuilder;

import org.wso2.Constants;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.shared.invoker.*;
import org.wso2.Model.ProductComponent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Collections;


/**
 *
 */

public class MavenInvoker {


    public static boolean mavenBuild(String mavenHome, String directoryPath){

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(directoryPath));
        request.setOutputHandler(new InvocationOutputHandler() {
            public void consumeLine(String s) {

 //               System.out.println(s);
            }
        });
        request.setGoals( Collections.singletonList( Constants.MAVEN_INVOKE_COMMAND ) );
        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));
        InvocationResult invocationResult;
        try {
            invocationResult =invoker.execute(request);
            if(invocationResult.getExitCode()==0){
                return true;
            }
            else{

                System.out.println(invocationResult.getExecutionException());
                return false;
            }

        } catch (MavenInvocationException e) {
            e.printStackTrace();
        }
        return false;
    }


}

