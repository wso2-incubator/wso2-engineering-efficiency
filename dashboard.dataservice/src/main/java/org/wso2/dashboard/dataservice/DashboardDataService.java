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

import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.wso2.dashboard.dataservice.Database.LocalDBConnector;
import org.wso2.dashboard.dataservice.Model.ProductArea;
import org.wso2.dashboard.dataservice.ProductBuildStatus.BuildStatusFinder;

import java.sql.Connection;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Microservice for retrieving product build status
 */
@Path("/service")
public class DashboardDataService {

    @GET
    @Path("/getBuildStatus")
    public JSONObject get() {

        ArrayList<ProductArea> productAreas = LocalDBConnector.getAllProductAreas();
        for (ProductArea productArea : productAreas) {
            productArea.setComponents(LocalDBConnector.getComponentsForArea(productArea.getName()));
        }
        JSONObject result = new JSONObject();
        JsonObject productAreaJson = null;
        for (ProductArea productArea : productAreas) {
            productAreaJson = new JsonObject();
            JsonObject jsonObject =null;
            for (int day = 0; day < 7; day++) {
                jsonObject = new JsonObject();
                int state = BuildStatusFinder.getBuildStatusForDay(productArea,System.currentTimeMillis()-(day* Constants.TWENTY_FOUR_HOURS));
                System.out.println(state);
                jsonObject.addProperty("day"+String.valueOf(day),state);
            }
            productAreaJson.add(productArea.getName(),jsonObject);

        }
        result.put("data",productAreaJson);



        //productArea.setComponents(components);
        //JSONObject jsonObject = new JSONObject();
        //jsonObject.put("status", BuildStatusFinder.getBuildStatusForDay(productArea, System.currentTimeMillis()));
        //return jsonObject;
        return result;
    }


}
