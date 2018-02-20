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

package org.wso2.dependencyupdater;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.dependencyupdater.DatabaseHandler.DashboardDBConnector;
import org.wso2.dependencyupdater.DatabaseHandler.LocalDBConnector;
import org.wso2.dependencyupdater.DependencyProcessor.DependencyUpdater;
import org.wso2.dependencyupdater.DependencyProcessor.POMReader;
import org.wso2.dependencyupdater.DependencyProcessor.POMWriter;
import org.wso2.dependencyupdater.DependencyProcessor.WSO2DependencyMinorUpdater;
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.FileHandler.RepositoryHandler;
import org.wso2.dependencyupdater.Model.Product;
import org.wso2.dependencyupdater.Model.ProductComponent;
import org.wso2.dependencyupdater.ProductBuilder.MavenInvoker;
import org.wso2.dependencyupdater.ProductRetrieve.GithubConnector;
import org.apache.maven.model.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class App {

    static String MAVEN_HOME;
    private static final Log log = LogFactory.getLog(App.class);

    public static void main(String[] args) {

        LocalDBConnector localDBConnector = new LocalDBConnector();
        ArrayList<Product> productList;
        productList = getAllProducts();
        HashMap<String, Integer> statusMap = new HashMap<String, Integer>();
        for (Product product : productList) {
            boolean status = updateProduct(product);
            if (status) {
                localDBConnector.insertBuildData(product, 1);
                System.out.println("Product Build Successful :" + product.getProductName());
                statusMap.put(product.getProductName(), 1);

            } else {
                localDBConnector.insertBuildData(product, 0);
                System.out.println("Product Build Unsuccessful :" + product.getProductName());
                statusMap.put(product.getProductName(), 0);
            }
        }
    }

    /**
     * This method responsible for identifying components of a given product and getting update status for each component
     *
     * @param product product object
     * @return
     */
    private static boolean updateProduct(Product product) {

        DashboardDBConnector dashboardDBConnector = new DashboardDBConnector();
        ConfigFileReader.readConfigFile();
        GithubConnector githubConnector = new GithubConnector();
        log.info("Reading Configuration file: " + Constants.CONFIG_FILE_NAME);
        MAVEN_HOME = ConfigFileReader.getMavenHome();
        ArrayList<ProductComponent> components = product.getProductComponentsList();
        ArrayList<ProductComponent> notFoundComponents = new ArrayList<ProductComponent>();
        for (ProductComponent component : components) {
            boolean retrieved = githubConnector.retrieveComponent(component);
            if (!retrieved) {
                notFoundComponents.add(component);
            }
        }
        components.removeAll(notFoundComponents);
        ArrayList<String> componentTempFiles = RepositoryHandler.getTemporaryProductComponents(components, Constants.SUFFIX_TEMP_FILE);
        int successCount = 0;
        for (String componentTempFile : componentTempFiles) {
            updateComponentDependencies(componentTempFile);
            boolean buildStatus = MavenInvoker.mavenBuild(MAVEN_HOME, componentTempFile);
            String componentName = getComponentNameFromTempFile(componentTempFile);

            if (buildStatus) {
                successCount += 1;
                dashboardDBConnector.insertBuildStatus(1, product.getProductName(), componentName);
                log.info(componentTempFile + " Build Successful");
            } else {

                dashboardDBConnector.insertBuildStatus(0, product.getProductName(), componentName);
                log.info(componentTempFile + " Failed to build");
            }
        }
        if (successCount == components.size()) {
            return true;
        }
        return false;
    }

    /**
     * This method returns original file name from a temporary file name
     *
     * @param componentTempFile temporary file name
     * @return
     */
    private static String getComponentNameFromTempFile(String componentTempFile) {

        int tempNameLength = componentTempFile.length();
        int tempSuffixLength = Constants.SUFFIX_TEMP_FILE.length();
        int rootPathLength = Constants.ROOT_PATH.length();

        return componentTempFile.substring(rootPathLength, tempNameLength - tempSuffixLength);
    }

    /**
     * @param projectPath Component of product that need a dependency update
     * @return status indicating the success or failure of update process
     */
    private static boolean updateComponentDependencies(String projectPath) {

        boolean status;
        ArrayList<Model> modelList = new ArrayList<Model>();

        POMReader pomReader = new POMReader();
        POMWriter pomWriter = new POMWriter();
        //DependencyUpdater DependencyUpdater = new WSO2DependencyMajorUpdater(); // to update wso2 dependencies to latest available version
        DependencyUpdater DependencyUpdater = new WSO2DependencyMinorUpdater(); // to update wso2 dependencies to latest version with no major upgrades

        Model model = pomReader.getPomModel(projectPath); //create model for root pom
        if (model != null) {
            Properties properties = model.getProperties();
            properties.setProperty(Constants.PROJECT_VERSION_STRING, model.getVersion());
            modelList.add(model);

            List<String> modules = model.getModules();
            for (String module : modules) {
                model = pomReader.getPomModel(projectPath + File.separator + module); //create model for each child pom mentioned in root pom
                modelList.add(model);
            }

            ArrayList<Properties> propertiesList = new ArrayList<Properties>();
            Model updatedRootModel = model.clone();
            for (Model childModel : modelList) {
                Model updatedModel = DependencyUpdater.updateModel(childModel, properties);
                if (!childModel.getProjectDirectory().toString().equals(projectPath)) {
                    propertiesList.add(updatedModel.getProperties());
                    pomWriter.writePom(updatedModel);
                } else {
                    updatedRootModel = updatedModel;
                }
            }
            for (Properties properties1 : propertiesList) {
                for (Object property : properties1.keySet()) {
                    properties.setProperty(property.toString(), properties1.getProperty(property.toString()));
                }
            }
            updatedRootModel.setProperties(properties);
            //updatedRootModel.setVersion(updatedRootModel.getVersion()+Constants.SUFFIX_TEMP_FILE);

            status = pomWriter.writePom(updatedRootModel);
            return status;

        } else {
            return true;
        }

    }

    /**
     * read dashboard database and returns all available products.
     *
     * @return
     */
    public static ArrayList<Product> getAllProducts() {

        DashboardDBConnector dashboardDBConnector = new DashboardDBConnector();
        ArrayList<Product> products = dashboardDBConnector.getAllProducts();
        return products;
    }
}
