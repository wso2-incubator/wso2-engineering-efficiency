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

package org.wso2.dependencyupdater.DependencyProcessor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.model.Dependency;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.FileHandler.ConfigFileReader;
import org.wso2.dependencyupdater.ProductRetrieve.GitHubConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Contains methods that connect with the micro-service to resolve dependency versions
 */
public class NexusRepoManagerConnector {

    private static final Log log = LogFactory.getLog(GitHubConnector.class);

    /**
     * Identify the latest available version for a given dependency
     * Retrieves the latest minor version from a JSON Array of version
     * Example :- current version 4.1.1 ,available versions { 2.1.3, 3.5.3, 4.1.0, 4.1.1, 4.5.4, 5.1.1}
     * Select 5.1.1 as the latest minor version
     *
     * @param dependency Dependency object
     * @return String that indicates the latest available version
     */
    public static String getLatestVersion(Dependency dependency) {

        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPARATOR +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(ConfigFileReader.getAetherMicroServiceUrl() + Constants.URL_SEPARATOR + "getLatest");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Constants.UTF_8_CHARSET_NAME));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();
                return jsonObject.getString(Constants.LATEST_VERSION_KEY);
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding method not supported", e);
        } catch (IOException e) {
            log.error("Invalid request or respond", e);
        }
        return Constants.EMPTY_STRING;
    }

    /**
     * This method will provide all available versions for a given dependency.
     *
     * @param dependency Dependency object with relevant information
     * @return ArrayList of Strings which represent the all released
     */
    public static ArrayList<String> getVersionList(Dependency dependency) {

        ArrayList<String> versions = new ArrayList<>();
        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPARATOR +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(ConfigFileReader.getAetherMicroServiceUrl() + Constants.URL_SEPARATOR + "getVersions");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Constants.UTF_8_CHARSET_NAME));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();

                JSONArray versionList = jsonObject.getJSONArray(Constants.AVAILABLE_VERSIONS_KEY);
                boolean hasNewerVersionFound = false;
                for (int index = 0; index < versionList.length(); index++) {
                    String version = versionList.optString(index);
                    if (hasNewerVersionFound) {
                        versions.add(version);
                    }
                    if (version.equals(dependency.getVersion())) {
                        hasNewerVersionFound = true;
                    }
                }
                return versions;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding method not supported", e);
        } catch (IOException e) {
            log.error("Invalid request or respond", e);
        }
        return versions;
    }

    /**
     * Identifies the latest version with the same current major version component.
     * Example- version 4.3.2 will be updated to 4.4.5 even though there is an available version of 5.3.2
     *
     * @param dependency Dependency object
     * @return String indicating the latest minor version
     */
    public static String getLatestMinorVersion(Dependency dependency) {

        String latestVersion = Constants.EMPTY_STRING;
        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPARATOR +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPARATOR +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            String currentVersion = dependency.getVersion();

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(ConfigFileReader.getAetherMicroServiceUrl()
                    + Constants.URL_SEPARATOR + "getVersions");
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), Constants.UTF_8_CHARSET_NAME));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();

                JSONArray versionList = jsonObject.getJSONArray(Constants.AVAILABLE_VERSIONS_KEY);
                latestVersion = getLatestMinorVersionFromJson(currentVersion, versionList);
                return latestVersion;
            }
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding method not supported", e);
        } catch (IOException e) {
            log.error("Invalid request or respond", e);
        }
        return latestVersion;
    }

    /**
     * Retrieves the latest minor version from a JSON Array of version
     * Example :- current version 4.1.1 ,available versions { 2.1.3, 3.5.3, 4.1.0, 4.1.1, 4.5.4, 5.1.1}
     * Select 4.5.4 as the latest minor version
     *
     * @param currentVersion current version used in dependency
     * @param versionList    JSON Array of Strings indicating versions
     * @return String that indicates the latest minor version
     */
    private static String getLatestMinorVersionFromJson(String currentVersion, JSONArray versionList) {

        Optional<Integer> optionalCurrentMainVersionId = getMainVersionTagFromVersion(currentVersion);
        if (optionalCurrentMainVersionId.isPresent()) {
            boolean hasMajorVersionFound = false;
            int index = 0;
            String version;
            while (!hasMajorVersionFound && index < versionList.length()) {
                version = versionList.get(index).toString();
                Optional<Integer> optionalVersion = getMainVersionTagFromVersion(version);
                if (optionalVersion.isPresent()) {
                    if (optionalCurrentMainVersionId.get() >= optionalVersion.get()) {
                        index++;
                    } else {
                        hasMajorVersionFound = true;
                    }
                } else {
                    index++;
                }
            }
            if (hasMajorVersionFound) {

                return versionList.get(index - 1).toString();
            } else {
                if (versionList.length() != 0) {
                    return versionList.get(index - 1).toString();
                } else {
                    return currentVersion;
                }
            }

        } else {
            log.info("Failed to retrieve major version tag from current version. Not possible to compare with other versions");
            return currentVersion;
        }

    }

    /**
     * Retrieves major component of the version string
     * Example - version 5.3.1 -> 5 , version 4.2.5 -> 4
     *
     * @param version String indicating the version
     * @return Optional<Integer> that represent the major component if it can be resolved
     * @throws NullPointerException  When the version is a null string
     * @throws NumberFormatException when the version string is not representing a number
     */
    private static Optional<Integer> getMainVersionTagFromVersion(String version) {

        Integer majorVersion = null;

        try {
            majorVersion = Integer.parseInt(version.split(Constants.DOT_REGEX)[0]);
        } catch (NullPointerException | NumberFormatException e) {
            log.error("Failed to retrieve major version tag from version ", e);
        }

        return Optional.ofNullable(majorVersion);
    }
}
