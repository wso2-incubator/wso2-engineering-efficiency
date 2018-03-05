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

package org.wso2.dependencyupdater.DependencyProcessor;

import org.apache.maven.model.Model;

import java.util.Properties;

/**
 * Model class for updating org.apache.maven.model.Model that represent a pom.xml file
 */
public abstract class DependencyUpdater {

    /**
     * Implementation of this method will include the set of rules for updating a org.apache.maven.model.Model
     *
     * @param model      org.apache.maven.model.Model that represent the attributes of a pom.xml
     * @param properties java.util.Properties object with all the properties included in the root pom.xml
     * @return state of update operation
     */
    public abstract boolean updateModel(Model model, Properties properties);

}
