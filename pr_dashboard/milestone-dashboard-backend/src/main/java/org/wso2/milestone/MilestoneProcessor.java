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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.wso2.milestone;


import com.google.gson.JsonArray;
import org.apache.log4j.Logger;
import org.wso2.milestone.gitobjects.Milestone;
import org.wso2.milestone.gitobjects.Repository;
import org.wso2.milestone.helpers.MongoHandler;

import java.util.ArrayList;


public class MilestoneProcessor {
    private final static Logger logger = Logger.getLogger(MilestoneProcessor.class);
    private static String gitAccessToken = null;


    private MilestoneProcessor(String gitToken) {
        if (gitAccessToken == null) {
            gitAccessToken = gitToken;
        }
    }


    /**
     * Get git token
     *
     * @return git token as string
     */
    public static String getGitToken() {
        return gitAccessToken;
    }

    public static void main(String[] args) {
        String git_token = "fgfdg";
        MilestoneProcessor milestoneProcessor = new MilestoneProcessor(git_token);
        ArrayList<Repository> repoArray = new ArrayList<>();
        Repository repository = new Repository("product-ei", "wso2", "Enterprise Integrator");
        repoArray.add(repository);
        ArrayList<Milestone> milestones = milestoneProcessor.getMilestones(repoArray);
        JsonArray returnArray = milestoneProcessor.getJsonArrayOfMilestones(milestones);
        MongoHandler mongoHandler = new MongoHandler("localhost", 27017, "product_milestone");
        mongoHandler.insertToTable("milestones", returnArray);


    }

    /**
     * get milestone list from all repositories
     *
     * @param repoArray - the repository arraylist to get milestones
     * @return array list of milestones
     */
    private ArrayList<Milestone> getMilestones(ArrayList<Repository> repoArray) {
        ArrayList<Milestone> milestonesAll = new ArrayList<>();
        for (Repository repository : repoArray) {
            milestonesAll.addAll(repository.getMilestones());
        }
        return milestonesAll;
    }

    /**
     * create json array of milestones
     *
     * @param milestones - list of milestones
     * @return json array of milestones
     */
    private JsonArray getJsonArrayOfMilestones(ArrayList<Milestone> milestones) {
        JsonArray jsonArray = new JsonArray();
        for (Milestone milestone : milestones) {
            jsonArray.add(milestone.getJsonObject());
        }
        logger.info("The milestone data string generated");
        return jsonArray;
    }
}

