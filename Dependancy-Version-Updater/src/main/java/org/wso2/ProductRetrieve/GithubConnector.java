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

package org.wso2.ProductRetrieve;

import org.wso2.Constants;
import org.wso2.Model.Product;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;


import java.io.File;
import java.io.IOException;


public class GithubConnector {

    Git git;




    public boolean update(Product product){
        boolean status =false;
        try {
            status =Git.open(new File(product.getSubdirectory())).pull().call().isSuccessful();


        } catch (IOException e) {
            e.printStackTrace();
        } catch (GitAPIException e){
            e.printStackTrace();
        }



        return status;
    }

    public boolean clone(Product product){
        boolean status = false;

        try {
            git = Git.cloneRepository()
                    .setURI( product.getUrl())
                    .setDirectory(new File(Constants.ROOT_PATH+File.separator+product.getName())).call();
            status = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    public boolean pullRequest(Product product){
        return true;
    }


}
