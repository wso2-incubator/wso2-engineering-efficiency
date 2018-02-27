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
package org.wso2.dashboard.dataservice.Model;

import java.math.BigDecimal;

/**
 * TODO:Class level comment
 */
public class BuildStat {

    int status;
    BigDecimal timestamp;

    public BuildStat(int status, BigDecimal timestamp) {

        this.status = status;
        this.timestamp = timestamp;
    }

    public int getStatus() {

        return status;
    }

    public void setStatus(int status) {

        this.status = status;
    }

    public BigDecimal getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(BigDecimal timestamp) {

        this.timestamp = timestamp;
    }

}
