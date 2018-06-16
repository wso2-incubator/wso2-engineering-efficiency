/*
Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
WSO2 Inc. licenses this file to you under the Apache License,
Version 2.0 (the "License"); you may not use this file except
in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package org.wso2.engineering.efficiency.patch.analysis.exceptions;

/**
 * Exceptions that occur while dealing with content from either Jira, Pmt or email creation
 */
public class PatchAnalysisConfigurationException extends PatchAnalysisException {

    /**
     * Constructs an Exception with the specified detail message
     * and cause for the email not being set up
     *
     * @param message The detailed message.
     * @param cause   the cause.
     */
    public PatchAnalysisConfigurationException(String message, Throwable cause) {

        super(message, cause);
    }
}

