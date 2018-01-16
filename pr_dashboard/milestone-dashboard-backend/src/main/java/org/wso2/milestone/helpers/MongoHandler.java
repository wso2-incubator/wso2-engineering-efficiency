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

package org.wso2.milestone.helpers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

public class MongoHandler {
    private final static Logger logger = Logger.getLogger(MongoHandler.class);
    private static DB mongoDatabase = null;


    public MongoHandler(String databaseUrl,int databasePort,String databaseName){
        try {
            MongoClient mongoClient = new MongoClient(databaseUrl, databasePort);
            mongoDatabase = mongoClient.getDB(databaseName);
        }catch (UnknownHostException e){
            logger.error("Unknown Host : Can't connect to the db");
        }
    }

    /**
     * insert a single document to mongodb database collection
     * @param tableName - database collection name
     * @param object - json object which need to store as a document
     */
    public void insertToTable(String tableName, JsonObject object){
        DBCollection table = mongoDatabase.getCollection(tableName);
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("url",object.get("url").toString().replace("\"",""));
        DBCursor cursor = table.find(searchQuery);
        BasicDBObject dbObject = (BasicDBObject) JSON.parse(object.toString());

        if(cursor.count()>0){
            BasicDBObject updateObject = new BasicDBObject();
            updateObject.put("$set",dbObject);
            table.update(searchQuery,updateObject);
        }
        else {
            table.insert(dbObject);
        }

    }

    /**
     * insert array of documnets to mongodb database collection
     * @param tableName - database collection name
     * @param jsonArray - json array having json objects to store
     */
    public void insertToTable(String tableName, JsonArray jsonArray){
        for(int i=0;i<jsonArray.size();i++){
            this.insertToTable(tableName,jsonArray.get(i).getAsJsonObject());
        }
    }


    /**
     * return all the collection data
     * @param tabelName - collection name
     * @return json string of data from mongo collection
     */
    public static String getAllTableData(String tabelName){
        DBCollection table = mongoDatabase.getCollection(tabelName);
        DBCursor cursor = table.find();
        JsonArray jsonArray = new JsonArray();
        JsonParser jsonParser = new JsonParser();
        while (cursor.hasNext()){
            JsonObject jsonObject = jsonParser.parse(cursor.next().toString()).getAsJsonObject();
            jsonArray.add(jsonObject);
        }

        return jsonArray.toString();
    }


}


