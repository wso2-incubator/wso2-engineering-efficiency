/*
 * Copyright (c) 2016, WSO2 Inc. (http://wso2.com) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.dashboard.dataservice;

import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.dashboard.dataservice.Database.LocalDBConnector;
import org.wso2.dashboard.dataservice.Model.BuildStat;
import org.wso2.dashboard.dataservice.Model.ProductArea;
import org.wso2.dashboard.dataservice.ProductBuildStatus.BuildStatusFinder;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * This is the Microservice resource class.
 * See <a href="https://github.com/wso2/msf4j#getting-started">https://github.com/wso2/msf4j#getting-started</a>
 * for the usage of annotations.
 *
 * @since 0.1-SNAPSHOT
 */
@Path("/service")
public class DashboardDataService {

    @GET
    @Path("/getBuildStatus")
    public JSONObject get() {

        ProductArea productArea = new ProductArea();
        ArrayList<String> components = new ArrayList<>();
        components.add("product-sp");

        productArea.setComponents(components);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status",BuildStatusFinder.getBuildStatusForDay(productArea,System.currentTimeMillis()));
        return jsonObject;
    }




}
