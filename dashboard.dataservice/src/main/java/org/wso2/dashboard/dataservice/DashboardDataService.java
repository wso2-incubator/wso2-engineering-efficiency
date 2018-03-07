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

import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Micro-service for retrieving product build status
 */
@Path("/service")
public class DashboardDataService {

    //end point for retrieving data to display in the dashboard
    @GET
    @Path("/getBuildStatus")
    public JSONObject getBuildStatus() {

        ArrayList<ProductArea> productAreas = LocalDBConnector.getAllProductAreas();
        for (ProductArea productArea : productAreas) {
            productArea.setComponents(LocalDBConnector.getComponentsForArea(productArea.getName()));
        }
        JSONObject result = new JSONObject();
        JsonObject productAreaJson = new JsonObject();
        long currentTime = System.currentTimeMillis();
        for (ProductArea productArea : productAreas) {
            JsonObject jsonObject = new JsonObject();
            for (int day = 0; day < 7; day++) {

                int state = BuildStatusFinder.getProductAreaBuildStatusForDay(productArea, (currentTime - (Long.valueOf(day) * Constants.TWENTY_FOUR_HOURS)));
                jsonObject.addProperty("day" + String.valueOf(day), state);

            }
            jsonObject.addProperty("monthly", String.valueOf(BuildStatusFinder.getMonthlyState(productArea, currentTime)));
            jsonObject.addProperty("weekly", String.valueOf(BuildStatusFinder.getWeeklyState(productArea, currentTime)));
            jsonObject.addProperty("failed", BuildStatusFinder.getFailingComponents(productArea, currentTime));
            productAreaJson.add(productArea.getName(), jsonObject);

        }
        result.put("data", productAreaJson);
        return result;
    }

}
