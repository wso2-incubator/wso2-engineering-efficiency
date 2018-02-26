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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.maven.model.Dependency;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.dependencyupdater.Constants;
import org.wso2.dependencyupdater.ProductRetrieve.GithubConnector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class MavenCentralConnector {

    private static final Log log = LogFactory.getLog(GithubConnector.class);

    public static String getLatestVersion(Dependency dependency) {

        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPERATER +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(Constants.GET_LATEST_VERSION_URL);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);

            if (response.getStatusLine().getStatusCode() == Constants.SUCCESSFUL_STATUS_CODE) {
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
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
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

        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPERATER +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(Constants.GET_VERSION_LIST);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Constants.UTF_8_CHARSET_NAME));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                JSONObject jsonObject = new JSONObject(result.toString());
                rd.close();
                ArrayList<String> versions = new ArrayList<String>();
                JSONArray versionList = jsonObject.getJSONArray(Constants.AVAILABLE_VERSIONS_KEY);
                boolean newerVersionFound = false;
                for (int index = 0; index < versionList.length(); index++) {
                    String version = versionList.optString(index);
                    if (newerVersionFound) {
                        versions.add(version);
                    }
                    if (version.equals(dependency.getVersion())) {
                        newerVersionFound = true;
                    }
                }
                return versions;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public static String getLatestMinorVersion(Dependency dependency) {

        String latestVersion = "";
        try {
            String data = Constants.JSON_OBJECT_START_TAG +
                    Constants.GROUP_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getGroupId() +
                    Constants.JSON_OBJECT_ELEMENT_SEPERATER +
                    Constants.ARTIFACT_ID_TAG +
                    Constants.JSON_OBJECT_KEY_VALUE_SEPERATER +
                    dependency.getArtifactId() +
                    Constants.JSON_OBJECT_END_TAG;

            String currentVersion = dependency.getVersion();

            StringEntity entity = new StringEntity(data, ContentType.APPLICATION_JSON);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(Constants.GET_VERSION_LIST);
            request.setEntity(entity);
            HttpResponse response = httpClient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), Constants.UTF_8_CHARSET_NAME));
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
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return latestVersion;
    }

    private static String getLatestMinorVersionFromJson(String currentVersion, JSONArray versionList) {

        try {
            int currentMajorVersionId = getMajorFromVersion(currentVersion);
            boolean majorVersionFound = false;
            int index = 0;
            String version;
            while (!majorVersionFound && index < versionList.length()) {
                version = versionList.get(index).toString();
                if (currentMajorVersionId >= getMajorFromVersion(version)) {
                    index += 1;
                } else {
                    majorVersionFound = true;
                }
            }
            if (majorVersionFound) {
                return versionList.get(index - 1).toString();
            } else return currentVersion;

        } catch (NullPointerException nullPointerException) {
            return currentVersion;
        } catch (NumberFormatException numberFormatException) {
            return currentVersion;
        }
    }

    private static int getMajorFromVersion(String version) throws NullPointerException, NumberFormatException {

        return Integer.parseInt(version.split("\\.")[0]);
    }
}
