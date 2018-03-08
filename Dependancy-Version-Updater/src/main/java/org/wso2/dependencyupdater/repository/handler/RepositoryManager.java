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
package org.wso2.dependencyupdater.repository.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.RefNotAdvertisedException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.filehandler.ConfigFileReader;
import org.wso2.dependencyupdater.model.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 */
public class RepositoryManager {

    private static final Log log = LogFactory.getLog(RepositoryManager.class);
    private Connection connection;

    public RepositoryManager() {

        try {
            String url = ConfigFileReader.getMysqlDatabaseUrl() +
                    Constants.URL_SEPARATOR + ConfigFileReader.getMysqlDatabaseName();
            this.connection = DriverManager.getConnection(url,
                    ConfigFileReader.getMysqlUsername(), ConfigFileReader.getMysqlPassword());
        } catch (SQLException e) {
            //TODO
        }

    }

    public void closeConnection() {

        try {
            connection.close();
        } catch (SQLException e) {
            //TODO
        }
    }

    /**
     * Method to insert build status to database
     *
     * @param component component object corresponding to build status
     * @param timeStamp unix timestamp of update operation
     */
    public void insertBuildStatus(Component component, long timeStamp) {

        String insertSql = "INSERT INTO ComponentBuildStatistics(Component,BuildTime,Status)VALUES(?,?,?)";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(insertSql);
            preparedStatement.setString(1, component.getName());
            preparedStatement.setBigDecimal(2, BigDecimal.valueOf(timeStamp));
            preparedStatement.setInt(3, component.getStatus());
            preparedStatement.execute();
        } catch (SQLException e) {
            log.error("Problem occurred in Database Connection", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, null);
        }
    }

    /**
     * This method returns all the components from database
     *
     * @return list of components
     */
    public ArrayList<Component> getAllComponents() {

        String selectSql = "SELECT REPO_NAME,REPO_URL FROM PRODUCT_COMPONENT_MAP";
        ArrayList<Component> components = new ArrayList<>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(selectSql);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Component component = new Component(resultSet.getString("REPO_NAME"),
                        resultSet.getString("REPO_URL"));
                components.add(component);
            }
        } catch (SQLException e) {
            log.error("Problem occurred when connecting to  database ", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, resultSet);

        }
        return components;

    }

    /**
     * Method for retrieving the latest available build status data for a particular component
     *
     * @param component Component object
     * @return integer indicating the latest build status (0 - Build Failed, 1 - Build Successful, 2 - Not available)
     */
    public int getLatestBuild(Component component) {

        String selectSql = "SELECT * FROM ComponentBuildStatistics WHERE Component=? GROUP BY BuildTime DESC LIMIT 1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connection.prepareStatement(selectSql);
            preparedStatement.setString(1, component.getName());
            resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                return Constants.BUILD_NOT_AVAILABLE_CODE;
            } else {
                return Integer.parseInt(resultSet.getString("Status"));
            }

        } catch (SQLException e) {
            log.error("Problem occurred in Database Connection", e);
        } finally {
            closeDatabaseAttributes(preparedStatement, resultSet);

        }
        return Constants.BUILD_NOT_AVAILABLE_CODE;

    }

    /**
     * Method used for closing database attributes such as prepared statement or result set
     *
     * @param preparedStatement prepared statement object that need closing
     * @param resultSet         result set object that need closing
     */
    private void closeDatabaseAttributes(PreparedStatement preparedStatement, ResultSet resultSet) {

        try {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            log.error("Problem occurred in closing database attributes", e);
        }
    }

    /**
     * Method for updating components from github
     *
     * @param component component object for updating
     * @return status of update
     */
    private boolean update(Component component) {

        boolean isUpdateSuccessful = false;
        try {
            log.info("Calling git pull command for component: "
                    + component.getName().replaceAll("[\r\n]", ""));
            isUpdateSuccessful = Git.open(new File(ConfigFileReader.getRootPath()
                    + File.separator + component.getName()))
                    .pull().call().isSuccessful();

        } catch (RefNotAdvertisedException exception) {
            log.error("Branch not found in remote repository. Therefore cannot update the existing local repository"
                    + component.getName().replaceAll("[\r\n]", ""));
        } catch (IOException e) {
            log.error("Component directory cannot be found :"
                    + component.getName().replaceAll("[\r\n]", ""));
        } catch (GitAPIException e) {
            log.error("Failed to execute git command "
                    + component.getName().replaceAll("[\r\n]", ""));
        }

        return isUpdateSuccessful;
    }

    /**
     * Method for cloning new component to the local environment
     *
     * @param component component object for cloning
     * @return status of clone
     */
    private boolean clone(Component component) {

        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(
                ConfigFileReader.getGithubUsername(), ConfigFileReader.getGithubPassword());
        try {
            log.debug("Cloning the repository to local storage: "
                    + component.getName().replaceAll("[\r\n]", ""));
            Git.cloneRepository()
                    .setCredentialsProvider(credentialsProvider)
                    .setURI(component.getUrl())
                    .setDirectory(new File(ConfigFileReader.getRootPath()
                            + File.separator + component.getName())).call();
            return true;

        } catch (InvalidRemoteException e) {
            log.error("Remote repository not found :"
                    + component.getUrl().replaceAll("[\r\n]", ""), e);
        } catch (TransportException e) {
            log.error("Protocol error has occurred while fetching objects "
                    + component.getUrl().replaceAll("[\r\n]", ""), e);
        } catch (GitAPIException e) {
            log.error("Error occurred in Git API "
                    + component.getUrl().replaceAll("[\r\n]", ""), e);
        }
        return false;
    }

    /**
     * Method responsible for retrieving components from github. Calls Update or
     * clone method based on the existence of a repository
     *
     * @param component Component for retrieving
     * @return Status of retrieving process
     */
    public boolean retrieveComponent(Component component) {

        log.info("Retrieving component: " + component.getName().replaceAll("[\r\n]", ""));

        File repository = new File(ConfigFileReader.getRootPath() + File.separator + component.getName());
        if (repository.exists() && repository.isDirectory()) {
            log.info("Existing repository found for : "
                    + component.getName().replaceAll("[\r\n]", ""));
            return update(component);

        } else {
            log.info("Existing repository not found for : "
                    + component.getName().replaceAll("[\r\n]", ""));
            return clone(component);
        }
    }

}
