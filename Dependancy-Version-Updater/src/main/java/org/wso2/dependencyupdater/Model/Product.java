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
package org.wso2.dependencyupdater.Model;

import java.util.ArrayList;

/**
 * TODO:Class level comment
 */
public class Product {

    String productName;
    ArrayList<ProductComponent> productComponentsList;

    public Product(String productName) {

        this.productName = productName;
        productComponentsList = new ArrayList<ProductComponent>();
    }

    public ArrayList<ProductComponent> getProductComponentsList() {

        return productComponentsList;
    }

    public void setProductComponentsList(ArrayList<ProductComponent> productComponentsList) {

        this.productComponentsList = productComponentsList;
    }

    public String getProductName() {

        return productName;
    }
}
