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
 * TODO:Class level comment
 */
public abstract class DependencyUpdater {

    /**
     * @param model      org.apache.maven.model.Model that represent the attributes of a particular pom.xml
     * @param properties java.util.Properties object with all the properties included in the root pom.xml
     * @return updated model with dependency changes
     */
    public abstract Model updateModel(Model model, Properties properties);

}
