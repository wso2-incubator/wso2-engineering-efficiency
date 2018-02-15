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
package org.wso2.FileHandler;

import org.apache.commons.io.FileUtils;
import org.wso2.Constants;
import org.wso2.Model.ProductComponent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * TODO:Class level comment
 */
public class RepositoryHandler {


    public static ArrayList<String> getTemporaryProductComponents(ArrayList<ProductComponent> components,String suffix){
        ArrayList<String> tempPathList =  new ArrayList<String>();

        for (ProductComponent component : components) {
            String projectPath =Constants.ROOT_PATH+File.separator+component.getName();
            deleteFile(projectPath + suffix);
            copyProjectToTempDirectory(projectPath,projectPath+ suffix);
            tempPathList.add(projectPath+suffix);
        }
        return tempPathList;
    }


    private static boolean copyProjectToTempDirectory(String sourcePath,String destinationPath){
        File source = new File(sourcePath);
        File dest = new File(destinationPath);
        try {
            FileUtils.copyDirectory(source, dest);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean deleteFile(String destination){
        try {
            FileUtils.deleteDirectory(new File(destination));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }








}
