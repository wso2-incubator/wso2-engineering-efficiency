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
package org.wso2.dependencyupdater.exceptions;

/**
 * Exception to throw when error occurred in configuration reading
 */
public class DependencyUpdaterConfigurationException extends Exception {

    /**
     * Constructor with only a message to display
     *
     * @param message String message about caused exception
     */
    public DependencyUpdaterConfigurationException(String message) {

        super(message);
    }

    /**
     * Constructor with both message and causing exception
     *
     * @param message String message about caused exception
     * @param cause   caught exception
     */
    public DependencyUpdaterConfigurationException(String message, Throwable cause) {

        super(message, cause);

    }

}